import ijeoma.motion.ICallback;
import ijeoma.motion.Motion;
import ijeoma.motion.tween.Tween; 

Tween t;

float s = 0;

public void setup() {
  smooth();

  Motion.setup(this);

  // t = new Tween(100).add(this, "s", width).onBegin("onBegin")
  // .onEnd("onEnd").onChange(this, "onChange").play();
  t = new Tween(100).add(this, "s", width).onBegin(new ICallback() {
    public void run(Object t) {
      println(t+" begin");
    }
  }
  ).onEnd(new ICallback() {
    public void run(Object t) {
      println(t+" end");
    }
  } 
  ).onChange(new ICallback() {
    public void run(Object t) {
      println(t+" change");
    }
  } 
  ).play();
}

public void draw() {
  background(255);

  String time = (int) t.getTime() + " / " + (int) t.getDuration();

  noStroke();
  fill(0);
  rectMode(CENTER);
  rect(width / 2, height / 2, s, s);

  fill(255, 0, 0);
  text(time, width - textWidth(time) - 10, height - 10);
}
 
public void keyPressed() {
  t.play();
}

