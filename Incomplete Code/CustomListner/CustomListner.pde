//import java.util.*;

Initiater initiater;
Responder responder;

void setup() {
  size(400, 400);
  initiater = new Initiater();
  responder = new Responder();
}

void draw() {
  background(-1);
  initiater.addListener(responder); 
  initiater.sayHello();
}

// An interface to be implemented by everyone interested in "Hello" events
interface HelloListener {
  void someoneSaidHello();
}

// Someone who says "Hello"
class Initiater {
  private List<HelloListener> listeners = new ArrayList<HelloListener>();

  public void addListener(HelloListener toAdd) {
    listeners.add(toAdd);
  }

  public void sayHello() {
    System.out.println("Hello!!");

    // Notify everybody that may be interested.
    for (HelloListener hl : listeners)
      hl.someoneSaidHello();
  }
}

// Someone interested in "Hello" events
class Responder implements HelloListener {

  public void someoneSaidHello() {
    System.out.println("Hello there...");
  }
}

