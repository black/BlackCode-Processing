/*
 * Copyright (c) 2014 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.qscript;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.qscript.errors.ErrorType;
import org.qscript.errors.EvaluationException;
import org.qscript.errors.SyntaxException;
import org.qscript.events.SyntaxErrorEvent;
import org.qscript.operator.Operator;
import org.qscript.operator.OperatorSet;

/**
 * This will scan the original text line by line and split it into tokens.
 * 
 * @author Peter Lager
 *
 */
class Tokenizer {

	private enum TokenType {

		COMMENT 		( "[#]+.*" ),
		WHITESPACE 		( "\\s+" ),
		FLOAT1 			( "[+-]?[0-9]*\\.[0-9]*([Ee][+-]?[0-9]+)?" ),
		FLOAT2 			( "[+-]?[0-9]+([Ee][+-]?[0-9]+)+" ),
		INTEGER			( "[+-]?[0-9]+" ),
		LITERAL_STRING	( "[']{1}[.[^']]*[']{1}" ),
		VARIABLE		( "[$]?[a-zA-Z][a-zA-Z0-9_]*" ),
		OPERATOR		( OperatorSet.get().getRegex() ) ;

		public Pattern pattern;

		private TokenType(String regex){
			this.pattern = Pattern.compile("^(" + regex + ")"); 
		}

		public void setPattern(String regex){
			this.pattern = Pattern.compile("^(" + regex + ")"); 
		}

	}

	// This array defines the order to use in case we want 
	// something different than that returned by enum.values()
	private static TokenType[] tokenOrder = new TokenType[] { 
		TokenType.COMMENT,
		TokenType.WHITESPACE, 
		TokenType.FLOAT1, 
		TokenType.FLOAT2, 
		TokenType.INTEGER, 
		TokenType.LITERAL_STRING, 
		TokenType.VARIABLE, 
		TokenType.OPERATOR
	};

	private static OperatorSet opSet;


	/**
	 * Tokenise the script lines
	 * @param script the script object to be evaluated
	 * @param lines the expression 
	 * @return
	 * @throws SyntaxException
	 * @throws EvaluationException
	 */
	public static LinkedList<Token> tokenize(Script script, String[] lines) throws SyntaxException {
		// Get latest operator set and associated regex
		opSet = OperatorSet.get();
		if(opSet.hasChanged())
			TokenType.OPERATOR.setPattern(opSet.getRegex());
		// The list of tokens in infix order
		LinkedList<Token> tokens = new LinkedList<Token>();

		// Now tokenise line by line
		for(int i = 0; i < lines.length; i++){
			tokens.addAll(tokenizeLine(script, lines[i], i));        /////////  switcher
			// Add statement separator if not the last line and we have some tokens
			if(i < lines.length - 1 && !tokens.isEmpty()){
				Token token = tokens.peekLast();
				Operator op = token.isOperator() ? (Operator)token : null;
				if(op == null || !op.getSymbol().equals(";")){
					Token t = OperatorSet.get().getOperator(";");
					t.setTextPosition(i, 0, 0);
					tokens.add(t);				
				}
			}
		}
		confirmUnaryNumbers(tokens, lines);
		//printTokens("Tokeniser", tokens);
		validateInfix(script, tokens);
		return tokens;
	}

	/**
	 * First cut of this method simply looks for invalid operator names
	 * @param script
	 * @param infix
	 * @throws SyntaxException
	 */
	private static void validateInfix(Script script, LinkedList<Token> infix) throws SyntaxException {
		Token errorOnToken = null, prevToken = null;
		ErrorType etype = null;
		for(Token token : infix){
			if(token.isOperator()){
				Operator op = (Operator) token;
				if(op.getSymbol().equals("(")){
					if(prevToken != null && !prevToken.isOperator()){
						etype = ErrorType.OPERATOR_EXPECTED;
						errorOnToken = prevToken;
						break;
					}
				}
			}
			prevToken = token;
		}
		if(errorOnToken != null){
			script.fireEvent(SyntaxErrorEvent.class, etype, errorOnToken);
			script.throwSyntaxException(etype, errorOnToken);			
		}
	}

	//UNKNOWN_OPERATOR
	/**
	 * Tokenise a single line from the original script lines
	 * @throws SyntaxException 
	 */
	private static LinkedList<Token> tokenizeLine(Script script, String line, int lineNbr) throws SyntaxException {
		LinkedList<Token> tokens = new LinkedList<Token>();
		int pos = 0;

		while (!line.equals("")) {
			boolean match = false;
			for (TokenType type : tokenOrder) {
				Matcher m = type.pattern.matcher(line);
				if (m.find()) {
					match = true;
					String fragment = m.group();
					line = m.replaceFirst("");
					Token token = getToken(opSet, type, fragment, lineNbr, pos, fragment.length());
					if(token != null)
						tokens.add(token);
					pos += fragment.length();
					break;
				}
			}
			if (!match) {
				ErrorType etype = ErrorType.SYNTAX_ERROR;
				Token bestGuess = new Argument(null);
				bestGuess.setTextPosition(lineNbr, pos, 1);
				if(line.startsWith("'"))
					etype = ErrorType.UNTERMINATED_STRING;
				script.fireEvent(SyntaxErrorEvent.class,  etype, bestGuess);
				script.throwSyntaxException(etype, bestGuess);
			}
		}
		return tokens;
	}


	/**
	 * Create a token from a matched sequence of characters. If the characters are 'spaces' then return null.
	 */
	private static Token getToken(OperatorSet opSet, TokenType type, String sequence, int line, int pos, int width){
		Token token = null;
		switch(type){
		case OPERATOR:
			token = opSet.getOperator(sequence);
			break;
		case FLOAT1:
		case FLOAT2:
			token = new Argument(Double.parseDouble(sequence));
			break;
		case INTEGER:
			token = new Argument(Integer.parseInt(sequence));
			break;
		case LITERAL_STRING:
			sequence = sequence.substring(1, sequence.length() - 1);
			token = new Argument(sequence);
			break;
		case VARIABLE:
			// If the variable name starts with the name of an operstor  then
			// check that it is not an operator 
			if(opSet.hasOperator(sequence))
				token = opSet.getOperator(sequence);
			else
				token = new Variable(sequence);
			break;
		case WHITESPACE:
		case COMMENT:
			break;
		}
		if(token != null){
			token.setTextPosition(line,  pos, width);
		}
		return token;
	}

	/**
	 * Converts the unary +- to the equivalent operator and inserts it into
	 * the infix list
	 */
	private static void confirmUnaryNumbers(LinkedList<Token> tokens, String[] lines){
		int idx = tokens.size()-1;
		char first, last;
		while(idx > 0){
			Token curr = tokens.get(idx);
			if(curr.isArgument()){
				first = lines[curr.line].charAt(curr.charStart);
				if(first == '+' || first == '-'){
					Token prev = tokens.get(idx-1);
					last = lines[prev.line].charAt(prev.charStart + prev.charWidth - 1);
					if("+-/*(=,^<>%&|;".indexOf(last) < 0) {
						// Need to strip off unary sign and make it an operator
						Token t = first == '-' ? opSet.getOperator("+") : opSet.getOperator("+");
						t.setTextPosition(curr.line, curr.charStart, 1);
						tokens.add(idx, t);
						curr.charStart++;
						curr.charWidth--;
					}
				}
			}
			idx--;
		}
	}


	static void printTokens(String title, List<Token> tokens){
		System.out.println("-------------------------------------------------------------------");
		System.out.println(title);
		System.out.println("Line    Pos     Width    Token");
		for(Token token : tokens){
			System.out.println(token.getLine() + "\t" + token.getCharStart() + "\t" + token.getCharWidth() + "\t" + token.forListing());
		}
		System.out.println("-------------------------------------------------------------------");
	}

	/** Prevent instantiation */
	private Tokenizer() {	}

}
