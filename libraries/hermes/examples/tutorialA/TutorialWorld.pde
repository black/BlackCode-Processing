/**
 * Tutorial World
 * A World filled with squares
 */
class TutorialWorld extends World {
  int _squareNum;

  TutorialWorld(int squareNum/*, int portIn, int portOut*/) {
    super(/*portIn, portOut*/);
    _squareNum = squareNum;
  }

  void setup() {
    for (int i = 0; i < _squareNum; i++) {
      int x = (int) random(WINDOW_WIDTH - 50);
      int y = (int) random(WINDOW_HEIGHT - 50);
      register(new GlitchySquare(x, y));
    }
  }
  
  void draw() {
    background(0);
    super.draw();
  }
}

