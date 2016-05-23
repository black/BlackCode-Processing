/*
 * Finger detection class.
 * (c) Antonio Molinaro 2011 
 * http://code.google.com/p/blobscanner/.
 */

class FingerDetector {
  BoundingBox bBox;
  //WARNING: Do I even need a PVector here? Arrays might be far cheaper
  public PVector hand, wrist, a, b,c,d, pinky,thumb, prevThumb, prevPinky, prevHand, prevWrist; 
  public PVector[] prevFingers;
  public PVector foreFinger, midFinger, ringFinger, pinkyFinger;
  public float initialWristZ, initialHandLength;
  
  private float w, h, handLength;
  PImage out;
  
  FingerDetector(PVector hand, PVector wrist){
    this.hand=hand;
    this.wrist=wrist;
    
    initialWristZ =wrist.z;
    initialHandLength = PVector.sub(hand,wrist).mag();
  }
  
  public void update(PImage _out, PVector _hand, PVector _wrist){
      //println("Got to beginning of update");
      this.prevHand = hand;
      this.prevWrist = wrist;
      
      this.out=_out;
      if(_hand!= new PVector()){
        this.hand = _hand;
      }
      if(_wrist!=new PVector()){
        this.wrist = _wrist;
      }
      
      
      w=out.width;
      h=out.height;
      
      PVector handVector = PVector.sub(hand,wrist);
      
      handLength= handVector.mag();
      
      //println("Got past setting of variables");
      //if( hand!=null && wrist !=null){
       // PVector handVec = PVector.sub(hand, wrist);
       // handVec.mult(1.0/4.0);
       // hand.add(handVec);
        PVector handVec = PVector.sub(hand, wrist);
        handVec.mult(1.0/4.0);
        wrist.add(handVec);
        
        //ONLY USE FOR DEMONSTRATION SO THE POINTS CAN BE DRAWN
        initBoundingBox();
        //out.updatePixels();
      //}
      
    }
    
    public PVector thumbDetection(){
      
    //STEP ONE: CHECK PREVIOUS THUMB NEAR EXACT LOCATION
    //Use new width Checker: CHANGE
    if(prevThumb!=null && prevThumb.x>0){
      
      //Using the top one makes it more likely to track, but also less accurate
      PVector potentialThumb = thumbCheckerWithinDistance(prevThumb,5);
      //PVector potentialThumb = closestWhiteTo(prevThumb,2);
      
      //IF THUMB IN SAME PLACE, RETURN THAT
      if(potentialThumb!=null && potentialThumb.x>0){
        prevThumb = potentialThumb;
        return potentialThumb;
        
      }
    
      //STEP TWO: DETERMINE IF THERE'S A THUMB WHERE THE HAND HAS JUST MOVED
      else{
        
        PVector handMvmt = PVector.sub(hand, prevHand);
        PVector projectedThumb = PVector.add(handMvmt, prevThumb);
        //Change: Use new width checker, but only has to do it once per the line somehow
        potentialThumb = thumbCheckerWithinDistance(projectedThumb,5);
        //potentialThumb = closestWhiteTo(prevThumb,5);
        if(potentialThumb!=null && potentialThumb.x>0){
          prevThumb = potentialThumb;
          return potentialThumb;
          
        }
        else{
          //TRYING: ANGLE BASED, IN CASE OF ROTATION. 
          PVector prevArm = PVector.sub(prevHand, prevWrist);
          PVector currArm = PVector.sub(hand,wrist);
          
          float angle = PVector.angleBetween(prevArm,currArm);
          
          PVector prevThumbWrist = PVector.sub(prevThumb,prevWrist);
          
          float xTemp = prevThumbWrist.x;
          float yTemp = prevThumbWrist.y;
          
          prevThumbWrist.x = xTemp*cos(angle)-yTemp*sin(angle);
          prevThumbWrist.y = xTemp*sin(angle)+yTemp*cos(angle);
          
          potentialThumb = thumbCheckerWithinDistance(prevThumbWrist,5);
          
          if(potentialThumb!=null && potentialThumb.x>0){
            prevThumb = potentialThumb;
            return potentialThumb;
          }
                
        }
        
        
      }
      
      //If got here, prev thumb is lost
      prevThumb=null;
      return thumbDetection();
    }
    
    //BIG CHANGE: Make this only check if there's a thumb (of correct height only, width is too expensive)
    else{
      //LATER MOVE THE BOUNDING BOX TO HERE
      //initBoundingBox();
      
      //Try doing the one off thumbChecker first
      PVector potentialThumbBoundingBox = boundingBoxDetection();
      if(potentialThumbBoundingBox!=null && potentialThumbBoundingBox.x>0){
        prevThumb = potentialThumbBoundingBox;
        if(thumbChecker(potentialThumbBoundingBox)){
          println("Got an actual thumb for");
        }
        
        return potentialThumbBoundingBox;
      }
      else{
        potentialThumbBoundingBox = advancedBoundingBoxDetection();
        
        if(potentialThumbBoundingBox!=null && potentialThumbBoundingBox.x>0){
          prevThumb=potentialThumbBoundingBox;
          return potentialThumbBoundingBox;
        }
        else{
          //If that doesn't work then do this stuff
          
          PVector thumb = thumbPotentials(a,b);
          PVector thumb2 = thumbPotentials(c,d);
          if(thumb!=null && thumb2!=null){
            PVector thumbToHandDist = PVector.sub(thumb,hand);
            PVector thumb2ToHandDist = PVector.sub(thumb2,hand);
            
            if(thumbToHandDist.mag()>thumb2ToHandDist.mag()){
              return thumb2;
            }
            else{
              return thumb;
            }
          }
          else if(thumb!=null){
            prevThumb=thumb;
            return thumb;
          }
          else if(thumb2!=null){
            prevThumb=thumb2;
            return thumb2;
          }
          
          
        }
      }
      
    }
    return null;
  }
  
  
  
  
  /**
  ----------------------
  //HELPER METHODS
  ----------------------
  */
  //WARNING:
  //Gonna need to have the lines set up, but does it require removing the palm? 
  void initBoundingBox(){
    PVector handPar = PVector.sub(hand, wrist);
    //Trying to get rid of this and see what happens
    //handPar.normalize();
    
    //THIS SHOULD TOTALLY FUCK SHIT UP
    //But it didn't. What. I don't think mult is acceptable in this version
    //handPar.mult(2);
    
    //handPar.x*=2;
    //handPar.y*=2;
    
    
    PVector wristPerp = new PVector();
    PVector wristPerp4 = new PVector();
    
    float perpX = handPar.x;
    float perpY = handPar.y;
    
    wristPerp.x = -1*perpY;
    wristPerp.y = perpX;
    
    wristPerp4.x= perpY;
    wristPerp4.y=-1*perpX;
    
    //Checks that they aren't of length 0
    //TRYING: REMOVING THIS
    //if(wristPerp.mag()>0){
      wristPerp.add(wrist);
      wristPerp4.add(wrist);
      
      
     //println("After");
      PVector hand2 = new PVector();
      hand2.x=wristPerp.x;
      hand2.y=wristPerp.y;
      
      hand2.sub(wrist);
      hand2.add(hand);
      
      PVector hand3 = new PVector();
      hand3.x=wristPerp4.x;
      hand3.y=wristPerp4.y;
      
      hand3.sub(wrist);
      hand3.add(hand);
      
      a=wristPerp;
      b=hand2;
      c =wristPerp4;
      d=hand3;
      
      
      bBox = new BoundingBox(wristPerp, hand2, hand3, wristPerp4);
      if(wristPerp.x>0 || wristPerp.y>0 || wristPerp.x<w || wristPerp.y<h){
       // blackenPalm();
      } 
      
    //}  
  }
  
  /**
  * Finds closest white pixel to previous, within a measure
  *
  */
 public PVector closestWhiteTo(PVector prev, int maximumDistance){
      
      int maxDist = maximumDistance;
      int index = (int)(prev.x+prev.y*w);
      if(index>0 && index<out.pixels.length){
        //If rgb all 255, it's white
        if((out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
           return prev;
        }
      }
      
      for(int x=(-1*maxDist+(int)prev.x); x<=(maxDist+(int)prev.x); x++){
        for(int y=(-1*maxDist+(int)prev.y); y<=(maxDist+(int)prev.y); y++){
          index= (int)(x+y*w);
          
          if(index>0 && index<out.pixels.length){
            //If all colors on 255, it's white
            if((out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
              return new PVector(x,y);
            }
          }
        }
      }
      
      return null;
  }
  
  /**
  * Determines if a past in Vector could be a thumb based on how quickly it hits black pixels on either side
  *
  */
  public boolean thumbChecker(PVector possibleThumb){
    if(hand!=null && possibleThumb!=null){
      //println("Checker- Hand: "+hand +", Wrist: "+wrist);
      if(thumbHeightChecker(possibleThumb) && (thumbNewWidthChecker(possibleThumb)) && sameSideAsWrist(possibleThumb)){
        return true;
      }
      else{
        return false;
      }
    }
    else{
      return false;
    }
  }
  
  /**
  * Based on formula found here: 
  * http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/
  *
  * P3: possibleThumb
  * P2: hand
  * P1: wrist
  */
  boolean thumbHeightChecker(PVector possibleThumb){
    
    float u = (possibleThumb.x-wrist.x)*(hand.x-wrist.x)+(possibleThumb.y-wrist.y)*(hand.y-wrist.y);
    
    u=u/(handLength*handLength);
    
    float closestXOnLine = wrist.x+u*(hand.x-wrist.x);
    float closestYOnLine = wrist.y+u*(hand.y-wrist.y);
    
    PVector closestPnt = new PVector(closestXOnLine,closestYOnLine);
    
    //Trying to change this back from hand
    //WARNING: IT'S A LOT LESS LIKELY TO FIND THUMBS LIKE THIS, THOUGH IT'S MORE PRECISE
    //MOVING TO handlength/2 DOWN BELOW HELPS A LOT BUT REINTRODUCES THAT IMPRECISION
    float thumbHeight = PVector.sub(possibleThumb,closestPnt).mag();
    //Changing this: from handLength to 3*handLength/4.0
    if(thumbHeight>((handLength/2.0)) && thumbHeight<handLength*1.5f){
      return true;
    }
    else{
      return false;
    }
  }
  
  boolean thumbNewWidthChecker(PVector possibleThumb){
     float thumbWidth = handLength/8;
          
     float u = (possibleThumb.x-wrist.x)*(hand.x-wrist.x)+(possibleThumb.y-wrist.y)*(hand.y-wrist.y);
    
    
    
    
    u=u/(handLength*handLength);
    
    float closestXOnLine = wrist.x+u*(hand.x-wrist.x);
    float closestYOnLine = wrist.y+u*(hand.y-wrist.y);
    
    PVector closestPnt = new PVector(closestXOnLine,closestYOnLine);
    
    //println("Checker- Hand: "+hand +", Wrist: "+wrist);
    //println("ClosestPnt: "+closestPnt);
    //println("possibleThumb: "+possibleThumb);
    
    
    PVector distToClosest = PVector.sub(possibleThumb,closestPnt);
    
    
    //These things are the points closest to the hand and wrist in terms of the possible thumb
    PVector handClosest = PVector.add(hand, distToClosest);
    //Might have to extend wrist back a bit to make it work
    PVector wristClosest = PVector.add(wrist,distToClosest);
    
    PVector newWristClosest = PVector.sub(handClosest,wristClosest);
    
    //Made up number, trying 1.4 at first
    newWristClosest.mult(-1.4f);
    
    //reset closest wrist to be a bit lower than the "wrist"
    wristClosest = newWristClosest;
    
    PVector diff = PVector.sub(handClosest, wristClosest);
    
    float distTotal = diff.mag();
    
    diff.normalize();
    
    
    int whiteCnt =0;
    boolean firstWhite = false;
    
    int index = 0;//(int)movePnt.x+(int)movePnt.y*(int)w;

    //println("Possible thumb: "+possibleThumb);
    
    PVector movePnt = new PVector();
    
    while(movePnt.mag()<distTotal){
      
      movePnt.add(diff);
      
      PVector checkPnt = PVector.add(wristClosest,movePnt);
      //println("movePnt: "+movePnt);
      index = (int)checkPnt.x+(int)checkPnt.y*(int)w;
      if(index>0 && index<w*h){
        if(!firstWhite){
          
          //if(brightness(out.pixels[index])==255 ){
          if((out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
            firstWhite=true;
            //println("Got first white");
            whiteCnt++;
          }
          
        }
        else{
         if((out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){

            whiteCnt++;
            
            
          }
          else if((out.pixels[index] >> 8 & 0xFF)==0 && (out.pixels[index] >> 16 & 0xFF)==0 && (out.pixels[index] & 0xFF)==0 ){
             if(whiteCnt<thumbWidth*2 && whiteCnt>thumbWidth/2){
               return true;
             }
             else{
               return false;
             }
          }
          
        }
      }      
    }
    
    return false;
  }
  //Checks if it's the correct width
  //Added in a necessary amount of white points to hit before hitting black bit
  //Added maximum number of white points it could have hit before hitting black bit
  /**
  boolean thumbWidthChecker(PVector possibleThumb){
     float thumbWidth = handLength/6;
     println("");
     println("Thumb Width: "+thumbWidth);
          
     float u = (possibleThumb.x-wrist.x)*(hand.x-wrist.x)+(possibleThumb.y-wrist.y)*(hand.y-wrist.y);
    
    u=u/(handLength*handLength);
    
    float closestXOnLine = wrist.x+u*(hand.x-wrist.x);
    float closestYOnLine = wrist.y+u*(hand.y=wrist.y);
    
    PVector closestPnt = new PVector(closestXOnLine,closestYOnLine);
    
    PVector thumbWrist = PVector.sub(possibleThumb,closestPnt);
    
    PVector leftPerp = new PVector(thumbWrist.y,-1*thumbWrist.x);
    PVector rightPerp = new PVector(-1*thumbWrist.y, thumbWrist.x);
    
    boolean left=false;
    boolean right=false;
    
    //Set up the increments
    leftPerp.normalize();
    rightPerp.normalize();
    
    
    //println("Normalized left perp: "+leftPerp);
    PVector leftIncrement = new PVector(leftPerp.x, leftPerp.y);
    PVector rightIncrement = new PVector(rightPerp.x, rightPerp.y);
    
    leftPerp.add(possibleThumb);
    rightPerp.add(possibleThumb);
    
    PVector leftCheck = PVector.sub(leftPerp, possibleThumb);
    
    int whiteCnt = 0;
    while(leftCheck.mag()<thumbWidth){
      int index = (int)(leftPerp.x+leftPerp.y*w);
      if(index>0 && index<out.pixels.length){
        //If colors all black, thing is black
        if((out.pixels[index] >> 16 & 0xFF)==0 && (out.pixels[index] >> 8 & 0xFF)==0 && (out.pixels[index] & 0xFF)==0 ){
          if(whiteCnt>thumbWidth/4 && whiteCnt<thumbWidth){
            left=true;
          }
          else{
            left=false;
          }
        }
        else if((out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
          whiteCnt++;
          println("Left white Count: "+whiteCnt);
        }
      }
      
      leftPerp.add(leftIncrement);
      leftCheck = PVector.sub(leftPerp, possibleThumb);
    }
    
    PVector rightCheck = PVector.sub(rightPerp, possibleThumb);
    whiteCnt = 0;
    while(rightCheck.mag()<thumbWidth){
      int index = (int)(rightPerp.x+rightPerp.y*w);
      if(index>0 && index<out.pixels.length){
        //If colors all black, thing is black
        if((out.pixels[index] >> 16 & 0xFF)==0 && (out.pixels[index] >> 8 & 0xFF)==0 && (out.pixels[index] & 0xFF)==0 ){
          if(whiteCnt>thumbWidth/4 && whiteCnt<thumbWidth){
            right=true;
          }
          else{
            right=false;
          }
        }
        else if((out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
          whiteCnt++;
          println("Right white Count: "+whiteCnt);
        }
      }
      
      rightPerp.add(rightIncrement);
      rightCheck = PVector.sub(rightPerp, possibleThumb);
    }
    
    //Has to be both to be tue
    boolean answer = left && right;
    
    return answer;
  }
  */
  //Exactly like the thing above, except instead of just checking for white, it checks if that found white point passes the thumb check
 public PVector thumbCheckerWithinDistance(PVector prev, int maximumDistance){
   int maxDist = maximumDistance;
      
      int thumbCnt = 0;
      int index = (int)(prev.x+prev.y*w);
      if(index>0 && index<out.pixels.length){
        //If rgb all 255, it's white
        if(brightness(out.pixels[index])==255 ){//if((out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255){
          if(thumbChecker(prev)){
        
           return prev;
          }
        }
      }
      //Let's see if there's a reason it's trending toward the hand
      
      //Flipping these helps, but still get the getting too close to the wrist/hand line
      for(int y=(-1*maxDist+(int)prev.y); y<=(maxDist+(int)prev.y); y++){
        for(int x=(-1*maxDist+(int)prev.x); x<=(maxDist+(int)prev.x); x++){
          index= (int)(x+y*w);
          
          if(index>0 && index<out.pixels.length){
            //If all colors on 255, it's white
            if(brightness(out.pixels[index])==255 ){//if((out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
              PVector possThumb =new PVector(x,y); 
              if(thumbChecker(possThumb)){
                //Checks to see if it's "confident" enough that a thumb is there
                
                //REMOVING THE CONFIDENCE CHECK AS I DON'T THINK IT'S APPROPRIATE 
                //if(thumbCnt>maximumDistance){
                return possThumb;
                //}
                //thumbCnt++;
                
              }
            }
          }
        }
      }
      
      return null;
 }  

  //WARNING: HUGE OF HUGELY UNECESSARY THUMB DETECTION
  PVector oldThumbDetection(){
    PVector thumb1 = null;
    PVector thumb2 = null;
    
    float length1 = 0.0f; 
    float length2= 0.0f;

    
    if(a!=null && b!=null && c!=null && d!=null){
      
      //println("All abcd vectors non null. Should be every frame");
      
      if(a.x>0 && b.x>0 && a.y>0 && b.x>0){
        //println("All ab vectors in range, should be every frame.");
        thumb1 = thumbPotentials(a,b);
        if(thumb1!=null){
          length1 = thumb1.z;
          thumb1.z=0.0f;
        }
        
      }
      if(c.x>0 && d.x>0 && c.y>0 && d.y>0){
        //println("All cd vectors in range");
        thumb2 = thumbPotentials(c,d);
        if(thumb2!=null){
          length2 = thumb2.z;
          thumb2.z = 0.0f;
        }
      }
    }
    
    if(thumb1!=null && thumb2!=null){
      //println("Both were non-null");
      if(thumbChecker(thumb1) && thumbChecker(thumb2)){
        //println("Both were in thumb range");
        if(length1>length2){
          thumb=thumb1;
          pinky=thumb2;
          return thumb1;
        }
        else{
          pinky=thumb1;
          thumb=thumb2;
          return thumb2;
        }
      }
      else if(thumbChecker(thumb1)){
        //println("Thumb1 was in thumb range");
        thumb=thumb1;
        return thumb1;
      }
      else if(thumbChecker(thumb2)){
        //println("Thumb2 was in thumb range");
        thumb=thumb2;
        return thumb2;
      }
    }
    else if(thumb1!=null){
      //println("Thumb1 was non-null");
      if(thumbChecker(thumb1)){
        //print("Thumb1 was in range");
        return thumb1;
      }
    }
    else if(thumb2!=null){
      //println("Thumb2 was non-null");
      if(thumbChecker(thumb2)){
        //print("Thumb2 was in range");
        return thumb2;
      }
    }
    
    if(prevThumb!=null){
      PVector thumb3 =closestWhiteTo(prevThumb,20);
      if(thumb3!=null){
        return thumb3;
      }
    }
    
    
    
   
    return null;
  }
  
  
  
  /**
  Finger grabber
  Horizontal lines drawn perpindicularly from the hand-wrist line
  
  */
  PVector[] pickOutFingers(){
    //b and d are closest to the hand
   
    PVector[] fingers = new PVector[4];
    int pointer = 0; 
    
    if(b==null || d==null){
      return fingers;
    }
    
    PVector startPos = PVector.sub(b,hand);
    PVector endPos = PVector.sub(d,hand);
     
    //Make it wider
    startPos.mult(2);
    endPos.mult(2);
    
    startPos.add(hand);
    endPos.add(hand);
    PVector increment = PVector.sub(hand,wrist);
    increment.normalize();
    
    float difference = 0.0f;
    
    float savedX=0.0f;
    float savedY=0.0f;
    while(difference<handLength*2 && pointer<fingers.length){
      startPos.add(increment);
      endPos.add(increment);
      difference++;
      
      PVector movePnt = new PVector(startPos.x,startPos.y);
      
      PVector diff = PVector.sub(endPos, startPos);
      float distance = diff.mag();
      diff.normalize();
      
      boolean white=false;
      boolean black=false;
      int whiteCnt = 0;
      
      while(PVector.sub(movePnt, startPos).mag()<distance && pointer<fingers.length){
        movePnt.add(diff);
        
        
        int index = (int)movePnt.x+(int)movePnt.y*(int)w;
        if(index>=0 &&out.pixels.length>index){
          if((out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
            
            //If previously black
            if(black){
              black = false;
            }
            //If previously white
            if(white){
              whiteCnt++;
              savedX = movePnt.x;
              savedY = movePnt.y;
            }            
            white=true;
          }
          else if((out.pixels[index] >> 8 & 0xFF)==0 && (out.pixels[index] >> 16 & 0xFF)==0 && (out.pixels[index] & 0xFF)==0 ){
            
            //If previously white
            if(white){
              //If white count is less than 3, you found a finger!
              if(whiteCnt<3 && whiteCnt>0){
                //If not a duplicate
                if(!duplicate(fingers,new PVector(savedX,savedY))){
                  fingers[pointer] = new PVector(savedX,savedY);
                  pointer++;
                }
              }
              
              white=false;
            }
            black=true;
            
            
          }
        }
      }
      
    }
    //Incase I want to do something with this later
    prevFingers=fingers;
    return fingers;
  }
  
  
  PVector thumbPotentials(PVector startPos, PVector endPos){
    //Reset wristClosest
    PVector wristClosest = PVector.sub(endPos,startPos);
    
    //Made up number, trying 1.4 at first
    wristClosest.mult(-1.4f);
    
    //reset closest wrist to be a bit lower than the "wrist"
    startPos = wristClosest;
    
    
    //Start Pos resetting
    PVector newStartPos = PVector.sub(startPos,wrist);
    newStartPos.mult(0.5f);
    newStartPos.add(wrist);
    
    startPos = newStartPos;
    
    PVector newEndPos = PVector.sub(endPos,hand);
    newEndPos.mult(0.5f);
    newEndPos.add(hand);
    
    endPos = newEndPos;
    
    
    PVector startIncrement = PVector.sub(startPos, wrist);
    PVector endIncrement = PVector.sub(endPos, hand);
    
    PVector difference = PVector.sub(endPos, startPos);
    float handLength = difference.mag();
    
    PVector distToHand = new PVector(endIncrement.x, endIncrement.y);
    
    startIncrement.normalize();
    endIncrement.normalize();
    
    //Added this so it goes twice as fast
    startIncrement.mult(2);
    endIncrement.mult(2);
    
    PVector thumbPossible = null;
    
    float distExtended = 0.0f;
    
    //Moving this as it should be the same every time
    //If this doesn't work, move it back inside the while loop
    PVector diff = PVector.sub(endPos,startPos);
    float distance = diff.mag();
    diff.normalize();
    boolean firstWhite;
    
    
    //Extending to full handLength
    while(distToHand.mag() <(handLength)){
      
      startPos.add(startIncrement);
      endPos.add(endIncrement);
      //Added this so it goes twice as fast
      distExtended+=2;
      
      distToHand = PVector.sub(endPos, hand);
     
      PVector movePnt = new PVector(startPos.x,startPos.y);
      
      int whiteCnt = 0;
      float savedX = 0.0f;
      float savedY = 0.0f;
      firstWhite=false;
      int index=0;
      
      //SEE IF THIS FIXED THE thumb in fingers issue
      diff = PVector.sub(endPos,startPos);
      distance = diff.mag();
      diff.normalize();
      
     
      
      //TRYING TO DO A DISTANCE LOWERING SUCH THAT IT DOESN'T GET THE FINGERS
      while(PVector.sub(movePnt, startPos).mag() <distance){
        
        movePnt.add(diff);
        index = (int)movePnt.x+(int)movePnt.y*(int)w; 
        if(index>0 && index<w*h){ 
          if(!firstWhite){
            
            if((out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
              firstWhite=true;
              whiteCnt++;
            }
            
          }
          else{
           if((out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
           
              whiteCnt++;
              
              PVector thumbPossibleMaybe = new PVector(movePnt.x,movePnt.y);
              
              if(thumbChecker(thumbPossibleMaybe)){
                thumbPossible=thumbPossibleMaybe;
              }
            }
            else if((out.pixels[index] >> 8 & 0xFF)==0 && (out.pixels[index] >> 16 & 0xFF)==0 && (out.pixels[index] & 0xFF)==0 ){
              //Trying 10 for now
               if(whiteCnt<handLength/10 && whiteCnt>2){
                 //println("White cnt when thumbed: "+whiteCnt);
                 return thumbPossible;
               }
               else{
                 return null;
               }
            }
            
          }
        }
        
      }
     
    }
    
    
    return null;
  }
  
  //Advanced bounding box-based detection
  PVector advancedBoundingBoxDetection(){
    
    PVector aExtended = PVector.sub(a,b);
    aExtended.mult(1.5f);
    aExtended.add(b);
    
    PVector bExtended = PVector.sub(b,a);
    bExtended.mult(1.5f);
    bExtended.add(a);
    
    PVector cExtended = PVector.sub(c,d);
    cExtended.mult(1.5f);
    cExtended.add(d);
    
    PVector dExtended = PVector.sub(d,c);
    dExtended.mult(1.5f);
    dExtended.add(c);
    
    PVector abThumb = whitePntLineCheck(a,b);
    PVector cdThumb = whitePntLineCheck(c,d);
    
    
    if(abThumb!=null && cdThumb!=null){
      if(thumbChecker(abThumb) && !thumbChecker(cdThumb)){
        return abThumb;
      }
      else if(!thumbChecker(abThumb) && thumbChecker(cdThumb)){
        return cdThumb;
      }
      else if(thumbChecker(abThumb) && thumbChecker(cdThumb)){
        
        return abThumb;        
      }
    }
    else if(abThumb!=null){
      return abThumb;
    }
    else if(cdThumb!=null){
      return cdThumb;
    }
    
    return null;
  }
  
  PVector whitePntLineCheck(PVector a, PVector b){
    PVector differenceBetween = PVector.sub(b,a);
    
   PVector trackerPnt = new PVector(a.x,a.y);
   
   float maxDist = differenceBetween.mag();
   
   differenceBetween.normalize();
   PVector increment = new PVector(differenceBetween.x,differenceBetween.y);
   
   boolean hitWhite=false;
   int count = 0;
   int index=0;
   
   while(PVector.sub(trackerPnt,a).mag()<maxDist){
     index = (int)trackerPnt.x+(int)trackerPnt.y*(int)w;
      
     if(index>0 && index<w*h){
       if(!hitWhite){
         if((out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
            
           hitWhite=true;
           count++;
         }
         
       }
       else{
         if((out.pixels[index] >> 8 & 0xFF)==255 && (out.pixels[index] >> 16 & 0xFF)==255 && (out.pixels[index] & 0xFF)==255 ){
            
           hitWhite=true;
           count++;
         }
         else if((out.pixels[index] >> 8 & 0xFF)==0 && (out.pixels[index] >> 16 & 0xFF)==0 && (out.pixels[index] & 0xFF)==0 ){
            
           if(count<handLength/2){
             return trackerPnt;
           }
           else{
             hitWhite=false;
             count=0;
           }
         }  
       }
     }    
     
     trackerPnt.add(increment);
   }
   
   return null;
  }
  

  //Current issue with using closest white is I get it going to other four points when i can't see a white even within the space
  //of the circle
  PVector boundingBoxDetection(){
    
    int indexA,indexB,indexC,indexD;
    int count = 0;
    PVector potentialThumb=null;
    if(a!=null && b!=null && c!=null && d!=null){
      indexA = (int)a.x+(int)a.y*(int)w;
      indexB = (int)b.x+(int)b.y*(int)w; 
      indexC = (int)c.x+(int)c.y*(int)w; 
      indexD = (int)d.x+(int)d.y*(int)w;
      
      //Getting rid of thumbNewWidthChecker, doesn't fix error of it going off and getting the side of the hand briefly
      //And stops it from getting bent thumbs
      
      if(indexA>0 && indexA<w*h){
        //if(closestWhiteTo(a,1)!=null){
        if((out.pixels[indexA] >> 8 & 0xFF)==255 && (out.pixels[indexA] >> 16 & 0xFF)==255 && (out.pixels[indexA] & 0xFF)==255 ){
          //if(thumbNewWidthChecker(new PVector(a.x, a.y))){
            count++;
            potentialThumb = new PVector(a.x, a.y);
          //}
        }
      }
      
      if(indexB>0 && indexB<w*h){
        //if(closestWhiteTo(b,1)!=null){
        if((out.pixels[indexB] >> 8 & 0xFF)==255 && (out.pixels[indexB] >> 16 & 0xFF)==255 && (out.pixels[indexB] & 0xFF)==255 ){
          //if(thumbNewWidthChecker(new PVector(b.x,b.y))){
            count++;
            potentialThumb =new PVector(b.x,b.y);
          //}
        }
      }
      
      if(indexC>0 && indexC<w*h){
        //if(closestWhiteTo(c,1)!=null){
        if((out.pixels[indexC] >> 8 & 0xFF)==255 && (out.pixels[indexC] >> 16 & 0xFF)==255 && (out.pixels[indexC] & 0xFF)==255 ){
          //if(thumbNewWidthChecker(new PVector(c.x,c.y))){
            count++;
            potentialThumb =new PVector(c.x,c.y);
          //}
        }
      }
      
      if(indexD>0 && indexD<w*h){
        //if(closestWhiteTo(d,1)!=null){
        if((out.pixels[indexD] >> 8 & 0xFF)==255 && (out.pixels[indexD] >> 16 & 0xFF)==255 && (out.pixels[indexD] & 0xFF)==255 ){
          //if(thumbNewWidthChecker(new PVector(d.x,d.y))){
            count++;
            potentialThumb =new PVector(d.x,d.y);
          //}
        }
      }
      
      if(count==1){
        return potentialThumb;
      }
    }
    
    return null;
  }
  
  
    //Assumes finger is non-null
  boolean duplicate(PVector[] fingers, PVector finger){
    //Might want to make this measure based on handLength or sumting
    int measure = 10;
    boolean duplicate = false;
    for(int i = 0; i<fingers.length; i++){
      if(fingers[i] !=null){
        if(fingers[i].x+measure>finger.x && fingers[i].x-measure<finger.x){
          if(fingers[i].y+measure>finger.y && fingers[i].y-measure<finger.y){
            duplicate=true;
          }
        }
      }
    }
    if(thumb!=null){
      if(thumb.x+measure>finger.x && thumb.x-measure<finger.x){
        if(thumb.y+measure>finger.y && thumb.y-measure<finger.y){
          duplicate=true;
        }
      }
    }
    
    if(pinky!=null){
      if(pinky.x+measure>finger.x && pinky.x-measure<finger.x){
        if(pinky.y+measure>finger.y && thumb.y-measure<finger.y){
          duplicate=true;
        }
      }
    }
    
    
    return duplicate;
  }
 
 
 
 
 //THUMB CHECKER: SHOULDN"T BE TOO NEAR WRIST
 //Assumes non null being passed into it
 
 //TESTING: Try seeing if hand will do anything
 boolean notTooCloseToWrist(PVector possibleThumb){
   PVector wristToThumb = PVector.sub(possibleThumb,wrist);
   PVector handToThumb = PVector.sub(possibleThumb,hand);
   
   float distanceToWrist = wristToThumb.mag();
   float distanceToHand = handToThumb.mag();
   
   
   if(distanceToWrist>handLength/4 && distanceToHand>handLength/4){
     return true;
   }
   else{
     return false;
   }
 }
 
 
 //THUMB CHECKER, make sure that the perpindicular hand/wrist line (centered on the hand)
 //does not seperate the thumb and wrist, but that both points exist on the same line
 //Returns +1 if positive, -1 if negative and 0 if 0
 
 //Based on this stackoverflow question: http://stackoverflow.com/questions/1560492/how-to-tell-whether-a-point-is-to-the-right-or-left-of-a-line
 boolean sameSideAsWrist(PVector possibleThumb){
   int possibleThumbSide = sign((b.x-d.x)*(possibleThumb.y-d.x)-(b.y-d.y)*(possibleThumb.x-d.x));
   int wristSide = sign((b.x-d.x)*(wrist.y-d.x)-(b.y-d.y)*(wrist.x-d.x));
   
   if(wristSide!=0 && possibleThumbSide!=0){
     if(wristSide==possibleThumbSide){
       return true;
     }
     else{
       return false;
     }
   }
   return true;
 }
 
 
 
 int sign(float x){
   if(x>0){
     return 1;
   }
   else if(x<0){
     return -1;
   }
   else{
     return 0;
   }
 }
}




