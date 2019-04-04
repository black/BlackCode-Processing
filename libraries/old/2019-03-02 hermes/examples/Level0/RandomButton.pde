/**
 * When pressed, randomizes the vector field on the canvas
 */
class RandomButton extends Being {
  
  Canvas _c;
  PostOffice _po;
  boolean _hover;
  boolean _keyPressed = false;
  
  RandomButton(Canvas c, PostOffice po) {
    super(new HRectangle(randomButtonX,randomButtonY,randomButtonSide,randomButtonSide));
    _c = c;
    _hover = false;
    _po = po;
  }
  
  /**
   * Tells canvas to randomize vector field on key press or mouse press
   */
  void receive(KeyMessage m) {
    if(m.isPressed() && !_keyPressed) {
      _c.randomize();
      _keyPressed = true;
    }
    else {
      _keyPressed = false;
    }
  }
  
  void receive(MouseMessage m) {
    _hover = true;
    if(m.getAction() == POCodes.Click.PRESSED) {
      _c.randomize();
    }
  }
  
  
  boolean getHover() {return _hover;}
  void setHover(boolean hover) {_hover = hover;}
  
  void draw() {
    if(_hover) {
      fill(buttonHover);
      _hover = _po.isMouseInRegion(this.getShape());
    }
    else {
      noFill();
    }
    getShape().draw();
    //Draw star in the center
    translate(randomButtonSide/2,randomButtonSide/2);
    for(int i = 0; i < starNum; i++) {
      rotate(PI/starNum);
      line(randomButtonSide/4,randomButtonSide/4,-randomButtonSide/4,-randomButtonSide/4);
    }
  }
}


