/**
 <h1>QScript algorithm editor</h1>
 <p>There are 3 predefined scripts demonstrating solutions to simple math 
 algorithms. To execute one of these scripts click on one of the green 
 buttons, then click on the Init button, this will parse and tokenise 
 the script. </p>
 
 <p>In the event of a syntax error it will be highlighted in red and a 
 description will appear in the pink status bar. </p>
 
 <p>It is not possible to run the script until it has been successfully 
 tokenised. At that time you can click on the start button, slect whether you 
 want to trace the progress of the evaluation and if tracing the speed of 
 evaluation.</p>
 
 <p>To run your own scripts simply type them into the script editor and then
 initialise and run them.</p>
 
 <p>For detailed information about how to use this library 
please visit the 
<a href="http://www.lagers.org.uk/qscript/">website</a></p>
 
 created 2014 by Peter Lager
 */

import g4p_controls.*;

import org.qscript.eventsonfire.*;
import org.qscript.events.*;
import org.qscript.editor.*;
import org.qscript.*;
import org.qscript.operator.*;
import org.qscript.errors.*;

import java.awt.Font;
import java.util.*;

final int EDIT = 1;
final int RUNNING = 3;

int currMode;

Script script;
String code[];
boolean codeChanged = false;
TreeMap<String, String> store = new TreeMap<String, String>();
LinkedList<ScriptEvent> event_queue = new LinkedList<ScriptEvent>();

StringBuilder output = new StringBuilder();

public void setup() {
  size(600, 570, JAVA2D);
  createGUI();
  customGUI();
  // Set the script to start with
  script = new Script("");
  setScript(1);
  // Set the script trace and trace delays based on initial GUI settings
  script.traceDelay(sdrDelay.getValueI());
  setTraceMode(cbxTrace.isSelected());

  // Scripting events are to be sent to this object
  script.addListener(this);

  goToMode(EDIT);
  registerMethod("pre", this);
}

// Switch IDE between running and editing modes
public void goToMode(int mode) {
  switch(mode) {
  case EDIT:
    currMode = mode;
    btnStart.setAlpha(255);
    btnStop.setAlpha(120);
    break;
  case RUNNING:
    currMode = mode;
    btnStart.setAlpha(120);
    btnStop.setAlpha(255);
    break;
  }
}  


// Clear the data store of all variables
public void clearVars() {
  store.clear();
  txaVars.setText("");
  if (script != null)
    script.clearVariables();
}

// Hide/show trace controls depending on whether the
// trace is switched on or off.
public void setTraceMode(boolean traceOn) {
  if (script != null) {
    if (traceOn) {
      lblDelayTitle.setVisible(true);
      sdrDelay.setVisible(true);
      btnPause.setVisible(true);
      btnPause.setAlpha(255);
      btnResume.setVisible(true);
      btnResume.setAlpha(120);
      script.traceModeOn();
    } else {
      lblDelayTitle.setVisible(false);
      sdrDelay.setVisible(false);
      btnPause.setVisible(false);
      btnPause.setAlpha(255);
      btnResume.setVisible(false);
      btnResume.setAlpha(120);
      script.traceModeOff();
      script.resume();
      txaScript.clearStyles();
    }
  }
}

/**
 * Since events are fired asynchronously this method might be called whilst G4P
 * is drawing its controls causing the program to crash. To avoid this we will
 * add the event to a queue and process it during Processing's event loop.
 */
@EventHandler
public void onScriptEvent(ScriptEvent event) {
  if (event instanceof HaltExecutionEvent && event.etype == ErrorType.STOPPED_BY_USER)
    event_queue.addFirst(event);
  else
    event_queue.addLast(event);
}

/**
 * This method has been registered with Processing so will be called just 
 * before the draw method. It will process a maximum of 20 events in the FIFO
 * queue, then allow the draw method to execute.
 * Since the script can generate hundreds of events per frame we have to
 * cap the number processed if we want the GUI to be responsive.
 */
public void pre() {
  int count = 0;
  while (!event_queue.isEmpty () && count++ < 20)
    performEventAction(event_queue.removeFirst());
}

/**
 * This method performs the actions needed by any particular type of script 
 * event. 
 */
public void performEventAction(ScriptEvent event) {
  if (event instanceof TraceEvent && cbxTrace.isSelected()) {
    txaScript.clearStyles();
    txaScript.addStyle(G4P.BACKGROUND, 0xFF0000CC, event.lineNo, event.pos, event.pos + event.width);
    txaScript.addStyle(G4P.FOREGROUND, 0xFFFFFF00, event.lineNo, event.pos, event.pos + event.width);
    txaScript.moveCaretTo(event.lineNo, event.pos);
  } else if (event instanceof SyntaxErrorEvent || event instanceof EvaluationErrorEvent) {
    lblStatus.setText(event.getMessage());
    txaScript.clearStyles();
    txaScript.addStyle(G4P.BACKGROUND, 0xFFFF0000, event.lineNo, event.pos, event.pos + event.width);
    txaScript.addStyle(G4P.FOREGROUND, 0xFFFFFFFF, event.lineNo, event.pos, event.pos + event.width);
    txaScript.addStyle(G4P.WEIGHT, 4, event.lineNo, event.pos, event.pos + event.width);
    txaScript.moveCaretTo(event.lineNo, event.pos);
    goToMode(EDIT);
  } else if (event instanceof HaltExecutionEvent) {
    lblStatus.setText(event.getMessage());
    event_queue.clear();
    txaScript.clearStyles();
    txaScript.addStyle(G4P.BACKGROUND, 0xFFFF0000, event.lineNo, event.pos, event.pos + event.width);
    txaScript.addStyle(G4P.FOREGROUND, 0xFFFFFFFF, event.lineNo, event.pos, event.pos + event.width);
    txaScript.addStyle(G4P.WEIGHT, 4, event.lineNo, event.pos, event.pos + event.width);
    goToMode(EDIT);
  } else if (event instanceof ScriptFinishedEvent) {
    lblStatus.setText(event.getMessage());
    event_queue.clear();
    txaScript.clearStyles();
    goToMode(EDIT);
  } else if (event instanceof OutputEvent) {
    output.append(event.extra[0]);      
    txaOutput.setText(output.toString(), 2000);
  } else if (event instanceof StoreUpdateEvent) {
    Variable var = (Variable) event.extra[0];
    store.put(var.getIdentifier(), var.toString());
    StringBuilder s = new StringBuilder();
    for (String id : store.keySet ()) {
      if (id.length() > 12) {
        s.append(id.substring(0, 9));
        s.append("...   ");
      } else {
        s.append(id);
        s.append("               ".substring(0, 15 - id.length()));
      }
      s.append(store.get(id));
      s.append("\n");
    }
    txaVars.setText(s.toString());
  } else if (event instanceof WaitEvent) {
    Argument arg = (Argument) event.extra[0];
    int time = arg.toInteger();
    if (time == 0)
      lblStatus.setText("Waiting for you to resume ... ");
    else
      lblStatus.setText("Waiting for " + time + " milliseconds");
  }
}

public void draw() {
  background(230, 230, 255);
}

// Use this method to add additional statements
// to customise the GUI controls
public void customGUI() {
  //  lblStatus.setTextAlign(GAlign.LEFT, null);
  txaVars.setFont(new Font("Monospaced", Font.PLAIN, 11));
  txaOutput.setFont(new Font("Dialog", Font.PLAIN, 11));
  txaScript.setFont(new Font("Monospaced", Font.PLAIN, 12));
  txaVars.setTextEditEnabled(false);
  txaOutput.setTextEditEnabled(false);
}
