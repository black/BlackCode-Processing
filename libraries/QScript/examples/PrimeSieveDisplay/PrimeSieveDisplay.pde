/**
 This sketch demonstrates the how to enable and process 
 script events using the QScript library.
 
<p>For detailed information about how to use this library 
please visit :  </p>
http://www.lagers.org.uk/qscript/ <br>

 created 2014 by Peter Lager
 */

import org.qscript.eventsonfire.*;
import org.qscript.events.*;
import org.qscript.editor.*;
import org.qscript.*;
import org.qscript.operator.*;
import org.qscript.errors.*;


Script script;

public void setup() {
  size(400, 400);
  background(0, 0, 64);
  fill(0, 0, 255);
  noStroke();

  String[] lines = { 
    "maxPrime = 40000", 
    "println('Prime numbers < ' + maxPrime)", 
    "lastPrime = 2", 
    "n = 3", 
    "REPEAT", 
    "  rootN = int(sqrt(n))", 
    "  notPrime = false;", 
    "  i = 3", 
    "  WHILE(i <= rootN && NOT(notPrime))", 
    "    notPrime = (n % i == 0)", 
    "    i = i + 1", 
    "  WEND", 
    "  IF(notPrime == false)", 
    "    lastPrime = n", 
    "  ENDIF", 
    "  n = n + 2", 
    "UNTIL(n >= maxPrime)"
  };

  script = new Script(lines);
  // Change the time limit from 5 (default value) to 20 seconds
  script.setTimeLimit(20);
  // Tell the script to send script events to this sketch
  script.addListener(this);
  // Evaluate the script
  Solver$.evaluate(script);
}

public void draw() {
}

@EventHandler
public void handleScriptEvents(ScriptEvent event) {
  if (event instanceof TraceEvent) {
    // This will not happen because tracing is not enabled
  } else if (event instanceof HaltExecutionEvent) {
    if (event.etype == ErrorType.MAX_TIME_EXCEEDED)
      println("Script timed out might want to give it more time");
  } else if (event instanceof ScriptFinishedEvent) {
    println("Time: " + script.getRunTime());
    println("=== DONE ===");
  } else if (event instanceof OutputEvent) {
    // no print or println functions so nothing to do
  } else if (event instanceof StoreUpdateEvent) {
    Variable var = (Variable) event.extra[0];
    if (var.getIdentifier().equals("lastPrime")) {
      int pn = var.toInteger();
      int x = (pn % 200) *2;
      int y = (pn / 200) * 2;
      rect(x, y, 2, 2);
    }
  } else if (event instanceof WaitEvent) {
    
  } else if (event instanceof ResumeEvent) {
    
  } else if (event instanceof SyntaxErrorEvent) {
    //Hopefully this won't be needed
  } else if (event instanceof EvaluationErrorEvent) {
    //Hopefully this won't be needed
  }
}
