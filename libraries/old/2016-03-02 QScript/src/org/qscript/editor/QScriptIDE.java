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

package org.qscript.editor;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.qscript.Argument;
import org.qscript.Script;
import org.qscript.Solver$;
import org.qscript.Variable;
import org.qscript.events.EvaluationErrorEvent;
import org.qscript.events.HaltExecutionEvent;
import org.qscript.events.OutputEvent;
import org.qscript.events.ResumeEvent;
import org.qscript.events.ScriptEvent;
import org.qscript.events.ScriptFinishedEvent;
import org.qscript.events.StoreUpdateEvent;
import org.qscript.events.SyntaxErrorEvent;
import org.qscript.events.TraceEvent;
import org.qscript.events.WaitEvent;
import org.qscript.eventsonfire.EventHandler;

/**
 * A Java Swing QScript editor <br>
 * 
 * There are no third party library dependencies for this editor. <br>
 * A very simple QScript IDE. Allows to to trace through the evaluation of 
 * a QScript expression or algorithm.
 * 
 * @author Peter Lager
 */
@SuppressWarnings("serial")
public class QScriptIDE extends javax.swing.JFrame implements TableModelListener {

	public static final int EDIT = 1;
	public static final int PAUSED = 2;
	public static final int RUNNING = 3;

	private static QScriptIDE that;
	public static final String newline = "\n";
	public static final String lineSep = System.getProperty("line.separator");
	protected static final int MAX_CHARACTERS = 50000;
	protected AbstractDocument doc;
	protected StyledDocument styledDoc;

	protected Script script = new Script("");

	protected SimpleAttributeSet[] attrs;
	protected DataStoreModel model;
	protected int mode;

	// The data store
	protected HashMap<String, Variable> store = new HashMap<String, Variable>();


	/**
	 * Creates new QScript Editor
	 */
	public QScriptIDE() {
		initComponents();
		// Always scroll script window when text is added
		DefaultCaret caret = (DefaultCaret) txaOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		styledDoc = txaScript.getStyledDocument();
		if (styledDoc instanceof AbstractDocument) {
			doc = (AbstractDocument) styledDoc;
			doc.setDocumentFilter(new DocumentSizeFilter(MAX_CHARACTERS));
		} else {
			System.err.println("Code pane's document isn't an AbstractDocument!");
			System.exit(-1);
		}
		attrs = initAttributes();
		// Data store for variables
		model = new DataStoreModel();
		storeTable.setModel(model);
		model.addTableModelListener(this);

		// Setup in the initial script
		initDocument(3);
		// The document listener will clear the attributes when the contents change
		styledDoc.addDocumentListener(new ScriptChangeListener());

		// Scripting events are to be sent to this object
		script.addListener(this);
		setMode(EDIT);
	}

	/**
	 * Set the UI elements according to current mode
	 * @param newMode
	 */
	private void setMode(int newMode) {
		if (mode != newMode) {
			mode = newMode;
			switch (mode) {
			case EDIT:
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
				btnPause.setEnabled(false);
				btnResume.setEnabled(false);
				break;
			case RUNNING:
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				btnPause.setEnabled(true);
				btnResume.setEnabled(false);
				break;
			case PAUSED:
				btnStart.setEnabled(false);
				btnStop.setEnabled(false);
				btnPause.setEnabled(false);
				btnResume.setEnabled(true);
				break;
			}
		}
	}

	/**
	 * Used to clear any highlights added when tracing
	 */
	public void clearAllHighlights() {
		styledDoc.setCharacterAttributes(0, styledDoc.getLength() - 1, attrs[0], true);
	}

	/**
	 * Hightlight the bit that is currently being evaluated
	 */
	public void setHighlight(int lineNo, int charPos, int charWidth, int style) {
		if (lineNo >= 0 && charPos >= 0 && charWidth > 0) {
			// Clear all existing styles
			styledDoc.setCharacterAttributes(0, styledDoc.getLength() - 1, attrs[0], true);
			// Calculate new style position
			int caretPos = getDocumentPos(lineNo, charPos);
			txaScript.setCaretPosition(caretPos);
			// Apply new style
			styledDoc.setCharacterAttributes(caretPos, charWidth, attrs[style], true);
		}
	}

	/**
	 * Clear the data store
	 */
	private void emptyStore(){
		store.clear();
		model.clear();
		model.fireTableDataChanged();    	
	}

	/**
	 *  This will handle all events fired by during parsing and evaluating of the script
	 * @param event the event to process
	 */
	@EventHandler
	public void onScriptEvent(ScriptEvent event) {
		if (event instanceof TraceEvent && cbxTrace.isSelected()) {
			setHighlight(event.lineNo, event.pos, event.width, 2);
		} else if (event instanceof SyntaxErrorEvent) {
			setMode(EDIT);
			lblStatus.setText(event.getMessage());
			setHighlight(event.lineNo, event.pos, event.width, 1);
		} else if (event instanceof EvaluationErrorEvent) {
			setMode(EDIT);
			lblStatus.setText(event.getMessage());
			System.out.println("Highlight " + System.currentTimeMillis());
			setHighlight(event.lineNo, event.pos, event.width, 1);
			// script.stop();
		} else if (event instanceof HaltExecutionEvent) {
			setMode(EDIT);
			lblStatus.setText(event.getMessage());
			setHighlight(event.lineNo, event.pos, event.width, 3);
			script.stop();
		} else if (event instanceof ScriptFinishedEvent) {
			setMode(EDIT);
			lblStatus.setText(event.getMessage());
			clearAllHighlights();
		} else if (event instanceof OutputEvent) {
			txaOutput.append(event.extra[0].toString());
		} else if (event instanceof StoreUpdateEvent) {
			Variable var = (Variable) event.extra[0];
			if(var != null){
				store.put(var.getIdentifier(),  var);
				model.updateStoreVariable((Variable[]) store.values().toArray(new Variable[store.size()]));
				model.fireTableDataChanged();
			}
		} else if (event instanceof WaitEvent) {
			setHighlight(event.lineNo, event.pos, event.width, 3);
			Argument arg = (Argument) event.extra[0];
			int time = arg.toInteger();
			if (time == 0) {
				lblStatus.setText("Waiting for you to resume ... ");
			} else {
				lblStatus.setText("Waiting for " + time + " milliseconds");
			}
			setMode(PAUSED);
		} else if (event instanceof ResumeEvent) {
			lblStatus.setText("Resuming ... ");
			setMode(RUNNING);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		pnlScriptSelectButtons = new javax.swing.JPanel();
		btnQuadratic = new javax.swing.JButton();
		btnFibonacci = new javax.swing.JButton();
		btnPrimes = new javax.swing.JButton();
		pnlStatus = new javax.swing.JPanel();
		lblStatusTitle = new javax.swing.JLabel();
		lblStatus = new javax.swing.JLabel();
		pnlTraceBar = new javax.swing.JPanel();
		lblDelayTitle = new javax.swing.JLabel();
		sdrDelay = new javax.swing.JSlider();
		pnlScriptControls = new javax.swing.JPanel();
		btnStart = new javax.swing.JButton();
		btnStop = new javax.swing.JButton();
		btnPause = new javax.swing.JButton();
		btnResume = new javax.swing.JButton();
		cbxTrace = new javax.swing.JCheckBox();
		mainSplit = new javax.swing.JSplitPane();
		pnlCode = new javax.swing.JPanel();
		lblCodeTitle = new javax.swing.JLabel();
		jspCode = new javax.swing.JScrollPane();
		txaScript = new javax.swing.JTextPane();
		debugSplit = new javax.swing.JSplitPane();
		jPanel1 = new javax.swing.JPanel();
		lblVarsTitle = new javax.swing.JLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		storeTable = new javax.swing.JTable();
		jPanel2 = new javax.swing.JPanel();
		lblOutputTitle = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		txaOutput = new javax.swing.JTextArea();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		pnlScriptSelectButtons.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

		btnQuadratic.setText("Solve Quadratic Formula");
		btnQuadratic.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnQuadraticActionPerformed(evt);
			}
		});

		btnFibonacci.setText("Fibonacci Series");
		btnFibonacci.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnFibonacciActionPerformed(evt);
			}
		});

		btnPrimes.setText("Prime Number Sieve");
		btnPrimes.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPrimesActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout pnlScriptSelectButtonsLayout = new javax.swing.GroupLayout(pnlScriptSelectButtons);
		pnlScriptSelectButtons.setLayout(pnlScriptSelectButtonsLayout);
		pnlScriptSelectButtonsLayout.setHorizontalGroup(
				pnlScriptSelectButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pnlScriptSelectButtonsLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(btnQuadratic, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(btnFibonacci, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(btnPrimes)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		pnlScriptSelectButtonsLayout.setVerticalGroup(
				pnlScriptSelectButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlScriptSelectButtonsLayout.createSequentialGroup()
						.addGap(0, 0, Short.MAX_VALUE)
						.addGroup(pnlScriptSelectButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(btnQuadratic)
								.addComponent(btnFibonacci)
								.addComponent(btnPrimes)))
				);

		pnlStatus.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

		lblStatusTitle.setText("STATUS");

		lblStatus.setBackground(new java.awt.Color(255, 204, 204));
		lblStatus.setOpaque(true);

		javax.swing.GroupLayout pnlStatusLayout = new javax.swing.GroupLayout(pnlStatus);
		pnlStatus.setLayout(pnlStatusLayout);
		pnlStatusLayout.setHorizontalGroup(
				pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pnlStatusLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblStatusTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap())
				);
		pnlStatusLayout.setVerticalGroup(
				pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlStatusLayout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(lblStatusTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addContainerGap())
				);

		pnlTraceBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

		lblDelayTitle.setText("Trace Delay");

		sdrDelay.setMaximum(500);
		sdrDelay.setMinimum(50);
		sdrDelay.setValue(150);
		sdrDelay.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				sdrDelayStateChanged(evt);
			}
		});

		javax.swing.GroupLayout pnlTraceBarLayout = new javax.swing.GroupLayout(pnlTraceBar);
		pnlTraceBar.setLayout(pnlTraceBarLayout);
		pnlTraceBarLayout.setHorizontalGroup(
				pnlTraceBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pnlTraceBarLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblDelayTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(sdrDelay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap())
				);
		pnlTraceBarLayout.setVerticalGroup(
				pnlTraceBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTraceBarLayout.createSequentialGroup()
						.addContainerGap(10, Short.MAX_VALUE)
						.addGroup(pnlTraceBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(lblDelayTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(sdrDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap())
				);

		pnlScriptControls.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

		btnStart.setText("Start");
		btnStart.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnStartActionPerformed(evt);
			}
		});

		btnStop.setText("Stop");
		btnStop.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnStopActionPerformed(evt);
			}
		});

		btnPause.setText("Pause");
		btnPause.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPauseActionPerformed(evt);
			}
		});

		btnResume.setText("Resume");
		btnResume.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnResumeActionPerformed(evt);
			}
		});

		cbxTrace.setSelected(true);
		cbxTrace.setText("Trace");
		cbxTrace.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cbxTraceActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout pnlScriptControlsLayout = new javax.swing.GroupLayout(pnlScriptControls);
		pnlScriptControls.setLayout(pnlScriptControlsLayout);
		pnlScriptControlsLayout.setHorizontalGroup(
				pnlScriptControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pnlScriptControlsLayout.createSequentialGroup()
						.addComponent(btnStart)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(btnStop)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(btnPause)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(btnResume)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(cbxTrace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap())
				);
		pnlScriptControlsLayout.setVerticalGroup(
				pnlScriptControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pnlScriptControlsLayout.createSequentialGroup()
						.addGap(0, 0, Short.MAX_VALUE)
						.addGroup(pnlScriptControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(btnStart)
								.addComponent(btnStop)
								.addComponent(btnPause)
								.addComponent(btnResume)))
								.addGroup(pnlScriptControlsLayout.createSequentialGroup()
										.addContainerGap()
										.addComponent(cbxTrace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGap(2, 2, 2))
				);

		mainSplit.setDividerLocation(322);

		pnlCode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

		lblCodeTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblCodeTitle.setText("Script");

		jspCode.setViewportView(txaScript);

		javax.swing.GroupLayout pnlCodeLayout = new javax.swing.GroupLayout(pnlCode);
		pnlCode.setLayout(pnlCodeLayout);
		pnlCodeLayout.setHorizontalGroup(
				pnlCodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCodeLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(pnlCodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jspCode)
								.addComponent(lblCodeTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
								.addContainerGap())
				);
		pnlCodeLayout.setVerticalGroup(
				pnlCodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pnlCodeLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblCodeTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jspCode, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
						.addContainerGap())
				);

		mainSplit.setLeftComponent(pnlCode);

		debugSplit.setDividerLocation(222);
		debugSplit.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

		jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

		lblVarsTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblVarsTitle.setText("Variables");

		storeTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object [][] {
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null}
				},
				new String [] {
						"Title 1", "Title 2", "Title 3", "Title 4"
				}
				));
		jScrollPane3.setViewportView(storeTable);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(
				jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(lblVarsTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
						.addContainerGap())
				);
		jPanel1Layout.setVerticalGroup(
				jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addComponent(lblVarsTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
						.addContainerGap())
				);

		debugSplit.setTopComponent(jPanel1);

		jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

		lblOutputTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblOutputTitle.setText("Output");

		txaOutput.setEditable(false);
		txaOutput.setColumns(20);
		txaOutput.setRows(5);
		jScrollPane2.setViewportView(txaOutput);

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(
				jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(lblOutputTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(jPanel2Layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
						.addContainerGap())
				);
		jPanel2Layout.setVerticalGroup(
				jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup()
						.addComponent(lblOutputTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
						.addContainerGap())
				);

		debugSplit.setRightComponent(jPanel2);

		mainSplit.setRightComponent(debugSplit);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(mainSplit)
								.addComponent(pnlScriptSelectButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(pnlStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(pnlTraceBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(pnlScriptControls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addContainerGap())
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(mainSplit)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(pnlScriptControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(pnlTraceBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(pnlStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(pnlScriptSelectButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
				);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void btnQuadraticActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuadraticActionPerformed
		initDocument(1);
	}//GEN-LAST:event_btnQuadraticActionPerformed

	private void cbxTraceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxTraceActionPerformed
		changeTraceMode(((javax.swing.JCheckBox) evt.getSource()).isSelected());
	}//GEN-LAST:event_cbxTraceActionPerformed

	private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
		emptyStore();
		lblStatus.setText("");
		txaOutput.setText("");
		script.setCode(txaScript.getText().split(lineSep));
		script.traceDelay(sdrDelay.getValue());
		if(cbxTrace.isSelected())
			script.traceModeOn();
		else
			script.traceModeOff();

		Solver$.evaluate(script);
		setMode(RUNNING);
	}//GEN-LAST:event_btnStartActionPerformed

	private void btnFibonacciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFibonacciActionPerformed
		initDocument(2);
	}//GEN-LAST:event_btnFibonacciActionPerformed

	private void btnPrimesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrimesActionPerformed
		initDocument(3);
	}//GEN-LAST:event_btnPrimesActionPerformed

	private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
		setMode(EDIT);
		script.stop();
	}//GEN-LAST:event_btnStopActionPerformed

	private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
		setMode(PAUSED);
		script.waitFor(0);
	}//GEN-LAST:event_btnPauseActionPerformed

	private void btnResumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResumeActionPerformed
		setMode(RUNNING);
		script.resume();
	}//GEN-LAST:event_btnResumeActionPerformed

	private void sdrDelayStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sdrDelayStateChanged
		script.traceDelay(sdrDelay.getValue());
	}//GEN-LAST:event_sdrDelayStateChanged

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(QScriptIDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(QScriptIDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(QScriptIDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(QScriptIDE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				that = new QScriptIDE();
				that.setVisible(true);
			}
		});
	}
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnFibonacci;
	private javax.swing.JButton btnPause;
	private javax.swing.JButton btnPrimes;
	private javax.swing.JButton btnQuadratic;
	private javax.swing.JButton btnResume;
	private javax.swing.JButton btnStart;
	private javax.swing.JButton btnStop;
	private javax.swing.JCheckBox cbxTrace;
	private javax.swing.JSplitPane debugSplit;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jspCode;
	private javax.swing.JLabel lblCodeTitle;
	private javax.swing.JLabel lblDelayTitle;
	private javax.swing.JLabel lblOutputTitle;
	private javax.swing.JLabel lblStatus;
	private javax.swing.JLabel lblStatusTitle;
	private javax.swing.JLabel lblVarsTitle;
	private javax.swing.JSplitPane mainSplit;
	private javax.swing.JPanel pnlCode;
	private javax.swing.JPanel pnlScriptControls;
	private javax.swing.JPanel pnlScriptSelectButtons;
	private javax.swing.JPanel pnlStatus;
	private javax.swing.JPanel pnlTraceBar;
	private javax.swing.JSlider sdrDelay;
	private javax.swing.JTable storeTable;
	private javax.swing.JTextArea txaOutput;
	private javax.swing.JTextPane txaScript;
	// End of variables declaration//GEN-END:variables

	/**
	 * Change the trace mode. If switching off restart the script
	 * @param traceOn
	 */
	private void changeTraceMode(boolean traceOn) {
		if(traceOn)
			script.traceModeOn();
		else {
			script.traceModeOff();
			if (mode == PAUSED){
				script.resume();
				setMode(RUNNING);			
			}
		}
		pnlTraceBar.setEnabled(traceOn);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		storeTable.repaint();
	}

	public int getDocumentPos(int lineNo, int pos) {
		String text = txaScript.getText();
		String eol = System.getProperty("line.separator");
		int eolLen = eol.length();

		int linecount = lineNo;
		int lineposcount = 0, docposcount = 0;;
		while (linecount > 0) {
			lineposcount = text.indexOf(eol, lineposcount) + eolLen;
			linecount--;
		}
		// In memory all eol are treated as one character so if the eol sepeartor
		// is more than one character in length (i.e. WIndows where it is 2)
		// then make the correction
		docposcount = pos + lineposcount - lineNo * (eolLen - 1);
		return docposcount;
	}

	private SimpleAttributeSet[] initAttributes() {
		//Hard-code some attributes.
		SimpleAttributeSet[] attrs = new SimpleAttributeSet[4];

		attrs[0] = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attrs[0], "Monospaced");
		StyleConstants.setFontSize(attrs[0], 12);

		attrs[1] = new SimpleAttributeSet(attrs[0]);
		StyleConstants.setBold(attrs[1], true);
		StyleConstants.setForeground(attrs[1], Color.red);
		StyleConstants.setBackground(attrs[1], new Color(255, 200, 200));

		attrs[2] = new SimpleAttributeSet(attrs[0]);
		StyleConstants.setBold(attrs[2], true);
		StyleConstants.setForeground(attrs[2], Color.blue);
		StyleConstants.setBackground(attrs[2], new Color(200, 200, 255));

		attrs[3] = new SimpleAttributeSet(attrs[0]);
		StyleConstants.setForeground(attrs[3], new Color(8, 96, 8));
		StyleConstants.setBackground(attrs[3], new Color(200, 255, 200));

		return attrs;
	}

	protected void setCode(String[] codeLines) {
		try {
			for (String line : codeLines) {
				doc.insertString(doc.getLength(), line + '\n', attrs[0]);
			}
		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text.");
		}
	}

	/**
	 * Setup up the document using one of the predefined scripts
	 * @param id
	 */
	private void initDocument(int id) {
		String desc;
		String[] lines;
		emptyStore();
		switch (id) {
		case 1:
			desc = "This sketch calculates the roots of a \n";
			desc += "quadratic equation of the form \n";
			desc += "ax^2 + bx + c.\n";
			desc += "It will show the values of the two real\n";
			desc += "roots if they exist otherwise you will \n";
			desc += "be informed that they are complex. This \n";
			desc += "sketch demonstrates use of the IF-THEN-ELSE \n";
			desc += "construct.n";
			lines = new String[]{
					"a=1; b=-7; c=-30",
					"numer = b*b - 4*a*c",
					"IF(numer < 0)",
					"  println('Complex Roots')",
					"ELSE",
					"  numer = sqrt(numer); denom = 2*a",
					"  root1= (-1 * b + numer)/denom",
					"  root2= (-1 * b - numer)/denom",
					"  println(root1 + '  &  ' + root2)",
					"ENDIF"
			};
			break;
		case 2:
			desc = "This sketch displays the first few terms\n";
			desc += "in the Fibonacci series. Change the value\n";
			desc += "in the first line to set the number of \n";
			desc += "terms to display.\n";
			lines = new String[]{
					"nbrTerms = 12",
					"n1 = 1; n2 = 1 ; count = 2",
					"print(n1 + ' ' + n2 + ' ')",
					"WHILE(nbrTerms > 2)",
					"  next = n1 + n2",
					"  print(next + ' ') ",
					"  count = count + 1",
					"  IF(count % 10 == 0) ",
					"    println('')",
					"  ENDIF",
					"  n1 = n2; n2 = next",
					"  nbrTerms = nbrTerms - 1",
					"WEND",
					"println(''); END(next)"
			};
			break;
		case 3:
		default:
			desc = "This sketch calculates and displays all the\n";
			desc += "prime numbers up to a user specified limit. \n";
			desc += "Change the value in the first line to set\n";
			desc += "another limit.";
			lines = new String[]{
					"maxPrime = 90",
					"println('Prime numbers <= ' + maxPrime)",
					"println('2')",
					"n = 3",
					"REPEAT",
					"  rootN = int(sqrt(n))",
					"  notPrime = false",
					"  i = 3",
					"  WHILE(i <= rootN && NOT(notPrime))",
					"    notPrime = (n % i == 0)",
					"    i = i + 1",
					"  WEND",
					"  IF(notPrime == false)",
					"    println(n)",
					"  ENDIF",
					"  n = n + 2",
					"UNTIL(n > maxPrime)"
			};
		}
		txaScript.setText("");
		txaOutput.setText(desc);
		script.setCode(lines);
		setCode(lines);
	}
	
	/**
	 * The sole purpose of this is to clear any highlight if the document (script) is changed.
	 * @author peter
	 *
	 */
	class ScriptChangeListener implements DocumentListener {
 
		@Override
		public void insertUpdate(DocumentEvent e) {
			if (mode == EDIT) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						styledDoc.setCharacterAttributes(0, doc.getLength() - 1, attrs[0], true);
					}
				});
			}
		}
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			if (mode == EDIT) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						styledDoc.setCharacterAttributes(0, doc.getLength() - 1, attrs[0], true);
					}
				});
			}
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			// Nothing to do here			
		}
		
    }
}
