# OneDollar-Unistroke-Recognizer

Implementation of the [$1 Gesture Recognizer](http://depts.washington.edu/aimgroup/proj/dollar/), a two-dimensional template based gesture recognition, for [Processing](http://processing.org/).

## About

The [$1 Gesture Recognizer](http://depts.washington.edu/aimgroup/proj/dollar/) is a research project by Wobbrock, Wilson and Li of the University of Washington and Microsoft Research. It describes a simple algorithm for accurate and fast recognition of drawn gestures.

Gestures can be recognised at any position, scale, and under any rotation. The system requires little training, achieving a 97% recognition rate with only one template for each gesture.

> Wobbrock, J.O., Wilson, A.D. and Li, Y. (2007). [Gestures without libraries, toolkits or training: A $1 recognizer for user interface prototypes](http://faculty.washington.edu/wobbrock/pubs/uist-07.1.pdf). Proceedings of the ACM Symposium on User Interface Software and Technology (UIST '07). Newport, Rhode Island (October 7-10, 2007). New York: ACM Press, pp. 159-168.

## Download

* [OneDollarUnistrokeRecognizer.zip v0.2.2](https://raw.github.com/voidplus/onedollar-unistroke-recognizer/master/download/OneDollarUnistrokeRecognizer.zip)

## Installation

Unzip and put the extracted *OneDollarUnistrokeRecognizer* folder into the libraries folder of your Processing sketches. Reference and examples are included in the *OneDollarUnistrokeRecognizer* folder.


## Usage

Import the library and create a instance:

```java
import de.voidplus.dollar.*;

OneDollar one = new OneDollar(this);
```

Add templates, which will be compare with your candidates:

```java
one.add("circle", new Integer[] {127,141,124,140 /* x1,y1, x2,y2 ... */ });

// one.remove("circle");
```
Bind callbacks, which will execute by success:

```java
one.bind("circle","callback_name");

// one.unbind("circle");
```

Implement the callbacks:

```java
void callback_name(String gesture, int x, int y, int c_x, int c_y){
  println("Detected gesture: "+gesture+" (Position: X: "+x+" / Y: "+y+", Centroid: X: "+c_x+" / Y: "+c_y+")");
}
```

Input data via unique IDs:

```java
void mousePressed(){ one.start(100); }  // 100 = ID
void mouseDragged(){ one.update(100, mouseX, mouseY); }
void mouseReleased(){ one.end(100); }
```

Draw the move of candidates:

```java
one.draw();
```

Run the gesture recognition:

```java
one.check();
```

For extended instructions look into the wiki: [**Usage**](https://github.com/voidplus/onedollar-unistroke-recognizer/wiki/Usage)


## Examples

* [Simple](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e0_simple/e0_simple.pde)
* [Basic](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e1_basic/e1_basic.pde)
* [Callbacks](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e2_several_callbacks/e2_several_callbacks.pde)
* [Binding](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e3_local_binding/e3_local_binding.pde)
* [Gestures](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e4_more_gestures/e4_more_gestures.pde)
* [Settings](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e5_settings/e5_settings.pde)


## Snapshot

![Snapshots](https://raw.github.com/voidplus/onedollar-unistroke-recognizer/master/reference/p5snap2.png)


## Tested

System:

* OSX
* Windows

Processing Version:

* 1.5.1
* 2.0b5
* 2.0b6
* 2.0b7
* 2.0b8

## Dependencies

None.

## Questions?

Don't be shy and feel free to contact me via [Twitter](https://twitter.com/darius_morawiec).

## License

The library is Open Source Software released under the [MIT License](https://raw.github.com/voidplus/onedollar-unistroke-recognizer/master/MIT-LICENSE.txt). It's developed by [Darius Morawiec](http://voidplus.de).