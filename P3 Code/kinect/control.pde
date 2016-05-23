
void keyPressed(){
  switch(keyCode){
    case LEFT:
      rotY += 0.1;
      break;
    case RIGHT:
      rotY -= 0.1;
      break;
    case UP:
      if(keyEvent.isShiftDown()){
        zoomF += 0.02;
      }  
      else{
        rotX += 0.1;
      }
      break;
    case DOWN:
      if(keyEvent.isShiftDown()){
        zoomF -= 0.02;
        if(zoomF < 0.04){
          zoomF = 0.04;
        }
      }
      else{
        rotX -= 0.1;
      }
      break;
  }
}

