/**
Jasmine Library Example

Examples of algorithm evaluations.

created by Peter Lager 2014
*/

import org.quark.jasmine.*;

void setup() {
  Compile.init();
  isPrime();
  println("===============================================\n");
  sumRange();
  println("===============================================\n");
  fibboSeries();
  println("===============================================\n");
  calcinterest();
  println("===============================================\n");
}

void calcinterest() {
  String[] lines = {
    "# Calculate future value of an  investement", 
    "# Interest can be simple or compound", 
    "# pv = present value, fv = future value", 
    "# r = % interest rate, t = years to invest", 
    "r = r / 100 # convert to proportion", 
    "if(simple == TRUE) then fv = pv*(1+r*t); else; fv = pv*(1+r)^t; endif", 
    "print(fv)",
  };
  Algorithm a = Compile.algorithm(lines, false);
  a.eval("simple", Jasmine.TRUE, "pv", 1000, "r", 10, "t", 2);
  a.eval("simple", Jasmine.FALSE, "pv", 1000, "r", 10, "t", 2);
}

void isPrime() {
  String[] lines = {
    "# Test to see if n is an odd prime number", 
    "root = floor(sqrt(n)); notPrime = FALSE; p = 3", 
    "while(p <= root && notPrime == FALSE)", 
    "  if(n % p == 0); notPrime = TRUE; endif", 
    "  p = p + 1", 
    "wend"
  };

  Algorithm a = Compile.algorithm(lines, false);
  a.eval("n", 15);
  boolean isPrime = a.answer("notPrime").toBoolean();
  int n = a.answer("n").toInteger();
  print("the number " + n + " is");
  if (isPrime) {
    print(" not");
  }
  println(" a prime number");
}


void sumRange() {
  String[] lines = {
    "# Sum of all numbers within an inclusive range ", 
    "from = min(n0, n1); to = max(n0, n1); sum = 0", 
    "for(i = from, i <= to, i = i + 1); sum = sum + i; fend",
  };
  Algorithm a = Compile.algorithm(lines, false);
  a.eval("n0", 15, "n1", -13);
  int s = a.answer("sum").toInteger();
  println("Sum of range is " + s);
}

void fibboSeries() {
  String[] lines = {
    "# Show the Fibonacci  series until 'limit' is reached", 
    "f0 = 1; f1 = 1; print(f0); print(f1)", 
    "repeat", 
    "  f2 = f0 + f1; print(f2)", 
    "  f0 = f1; f1 = f2", 
    "until(f2 >= limit)",
  };
  Algorithm a = Compile.algorithm(lines, false);
  a.eval("limit", 15);
}
