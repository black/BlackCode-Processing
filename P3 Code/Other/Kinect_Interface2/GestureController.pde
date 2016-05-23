class GestureController{
  //Right Stuff (And head)
  PVector head, rHand, rWrist, rElbow, rShoulder, rHip;
  //Left Stuff
  PVector lHand, lWrist, lElbow, lShoulder, lHip;
  PVector prevLeftHand= null;
  PVector prevRightHand=null;
  float leftHandMvmt = 0.0f;
  float rightHandMvmt = 0.0f;
  
  final int DETECT_LEFTARM_STRAIGHT=0;
  final int DETECT_LEFTARM_BENT=1;
  final int DETECT_LEFTARM_FRONT=2;
  final int DETECT_LEFTHAND_IN=3;
  final int DETECT_LEFTHAND_OUT=4;
  final int DETECT_LEFTHAND_DOWN=5;
  //Above shoulder
  final int DETECT_LEFTHAND_UP=6;
  final int DETECT_LEFTHAND_AT_MOUTH=7;
  
  final int DETECT_RIGHTARM_STRAIGHT=8;
  final int DETECT_RIGHTARM_BENT=9;
  final int DETECT_RIGHTARM_FRONT=10;
  final int DETECT_RIGHTHAND_IN=11;
  final int DETECT_RIGHTHAND_OUT=12;
  final int DETECT_RIGHTHAND_DOWN=13;
  //About shoulder
  final int DETECT_RIGHTHAND_UP=14;
  final int DETECT_RIGHTHAND_AT_MOUTH=15;
  
  final int DETECT_HANDS_TOGETHER=16;
  
  int complexGestureStart=17;
  
  //Complex gestures
  final int DETECT_SWORD_SWING=17;
  final int DETECT_HOLDING_REINS=18;
  final int DETECT_BOW_N_ARROW=19;
  final int DETECT_AIR_GUITAR=20;
  final int DETECT_HARP=21;
  final int DETECT_CROCODILE_ARMS=22;
  final int DETECT_FLYING=23;
  final int DETECT_WAND_TWIRL=24;
  final int DETECT_CASTING_MAGIC=25;
  final int DETECT_CRYSTAL_BALL=26;
  final int DETECT_PROTECT_TREASURE=27;
  final int DETECT_EAT_OR_DRINK=28;
  
  final int DETECT_INTERP_PUNCH_AT=29;
  final int DETECT_INTERP_SMOKE_PONDER=30;
  final int DETECT_INTERP_ARMS_CROSSED=31;
  final int DETECT_INTERP_CUFFED=32;
  final int DETECT_INTERP_TABLE_WIPER=33;
  final int DETECT_INTERP_LOW_DRAW=34;
  final int DETECT_INTERP_HIGH_DRAW=35;
  final int DETECT_INTERP_SWEEP=36;
  final int DETECT_INTERP_FIDDLE_WITH_SMALL_THING=37;
  final int DETECT_INTERP_STAND_LEGS=38;
  final int DETECT_INTERP_BOTH_HANDS_BY_CHEST=39;
  final int DETECT_INTERP_ARM_BY_BICEP=40;
  final int DETECT_INTERP_HANDS_TOGETHER=41;
  final int DETECT_INTERP_PLAY_CARD=42;
  final int DETECT_INTERP_ONE_HAND_UP=43;
  final int DETECT_MOVING_RUSH_AT=44;
  
  final int DETECT_SIGNAL_TYPES=45;
  
  public boolean[] detectSignals = new boolean[DETECT_SIGNAL_TYPES];
  public double[] confidence = new double[DETECT_SIGNAL_TYPES];
  private boolean leftXMvmt, leftYMvmt, leftZMvmt, rightXMvmt, rightYMvmt, rightZMvmt;  

  GestureController(PVector[] skeleton){
    head=skeleton[0];
    rHand = skeleton[1];
    rWrist=skeleton[2];
    rElbow=skeleton[3];
    rShoulder=skeleton[4];
    rHip=skeleton[5];
    lHand = skeleton[6];
    lWrist=skeleton[7];
    lElbow=skeleton[8];
    lShoulder=skeleton[9];
    lHip = skeleton[10];
  }  
  
  
  String[] update(PVector[] skeleton){
    setSkeleton(skeleton);
    resetGestures();
    
    if(prevRightHand!=null && prevLeftHand!=null){
      simpleGestureChecker();
      complexGesturesChecker();
      chrisGesturesChecker();
    }
    
    prevRightHand=rHand;
    prevLeftHand=lHand;
    
    setConfidence();
    
    return completedGestures();
  }
  
  //Determines the facing, potentially for use with Chris' stuff (prolly will be)
  void facingChecker(){
    float shoulderAngle = (float) atan2(rShoulder.z-lShoulder.z,rShoulder.x-lShoulder.x);
    
    if (shoulderAngle < -0.35f){
      println("Facing left");
    }
    else if (shoulderAngle > 0.35f){
      println("Facing right");
    }
    else{
      println("Facing Camera");
    }
  }
  
  void setConfidence(){
    for(int i =0; i<confidence.length; i++){
      if(confidence[i]>0){
        confidence[i]-=0.1;
      }
    }
    
    for(int i =0; i<detectSignals.length; i++){
      if(detectSignals[i]==true){
        confidence[i]=1.0;
      }
    }
    
    
  }
  
  //Sets all to false
  void resetGestures(){
    for(int i =0; i<detectSignals.length; i++){
      detectSignals[i]=false;
    }
    
    leftXMvmt=false;
    leftYMvmt=false;
    leftZMvmt=false;
    rightXMvmt=false;
    rightYMvmt=false;
    rightZMvmt=false;
  }
  
  /**
    Returns string of completed gestures
  */
  
  String[] completedGestures(){
    String[] gestures = new String[DETECT_SIGNAL_TYPES];
    
    for(int i =0; i<DETECT_SIGNAL_TYPES; i++){
      if(detectSignals[i]|| confidence[i]>0.6){
        String gestureName = "";
        
        switch(i){
          /*
          case DETECT_LEFTARM_STRAIGHT: gestureName= "Left Arm Straight";
            break;
          case DETECT_LEFTARM_BENT: gestureName="Left Arm Bent";
            break;
          case DETECT_LEFTARM_FRONT: gestureName="Left Arm Front";
            break;
          case DETECT_LEFTHAND_IN: gestureName = "Left Hand In";
            break;
          case DETECT_LEFTHAND_OUT: gestureName = "Left Hand Out";
            break;
          case DETECT_LEFTHAND_UP: gestureName = "Left Hand Up";
            break;
          case DETECT_LEFTHAND_AT_MOUTH: gestureName = "Left Hand At Mouth";
            break;
          case DETECT_RIGHTARM_STRAIGHT: gestureName= "Right Arm Straight";
            break;
          case DETECT_RIGHTARM_BENT: gestureName="Right Arm Bent";
            break;
          case DETECT_RIGHTARM_FRONT: gestureName="Right Arm Front";
            break;
          case DETECT_RIGHTHAND_IN: gestureName = "Right Hand In";
            break;
          case DETECT_RIGHTHAND_OUT: gestureName = "Right Hand Out";
            break;
          case DETECT_RIGHTHAND_UP: gestureName = "Right Hand Up";
            break;
          case DETECT_RIGHTHAND_AT_MOUTH: gestureName = "Right Hand At Mouth";
            break;
          case DETECT_HANDS_TOGETHER: gestureName = "Hands Together";
            break;
          */  
            
          case DETECT_SWORD_SWING: gestureName = "Sword Swing";
            break;
          case DETECT_HOLDING_REINS: gestureName="Holding Reins";
            break;
          case DETECT_BOW_N_ARROW: gestureName = "Bow n' Arrow";
            break;
          case DETECT_AIR_GUITAR: gestureName = "Air Guiar";
            break;
          case DETECT_HARP: gestureName = "Harp";
            break;
          case DETECT_CROCODILE_ARMS: gestureName = "Crocodile Arms";
            break;
          case DETECT_FLYING: gestureName = "Flying";
            break;
          case DETECT_WAND_TWIRL: gestureName = "Wand Twirl";
            break;
          case DETECT_CASTING_MAGIC: gestureName = "Casting Magic";
            break;
          case DETECT_CRYSTAL_BALL: gestureName = "Scrying";
            break;
          case DETECT_PROTECT_TREASURE: gestureName ="Protect Treasure";
            break;
          case DETECT_EAT_OR_DRINK: gestureName = "Eating or Drinking";
            break;
          
          case DETECT_INTERP_PUNCH_AT: gestureName = "Punching";
            break;
          case DETECT_INTERP_SMOKE_PONDER: gestureName= "Smoking/Pondering";
            break;
          case DETECT_INTERP_ARMS_CROSSED: gestureName= "Arms crossed";
            break;
          case DETECT_INTERP_CUFFED: gestureName = "Handcuffed";
            break;
          case DETECT_INTERP_TABLE_WIPER: gestureName = "Table Wiping";
            break;
          case DETECT_INTERP_LOW_DRAW: gestureName= "Low Draw";
            break;
          case DETECT_INTERP_HIGH_DRAW: gestureName= "High Draw";
            break;
          case DETECT_INTERP_SWEEP: gestureName = "Sweeping";
            break;
          case DETECT_INTERP_FIDDLE_WITH_SMALL_THING: gestureName ="Fiddling";
            break;
          case DETECT_INTERP_ARM_BY_BICEP: gestureName= "Hand by biceps";
            break;
          case DETECT_INTERP_STAND_LEGS: gestureName ="Standing";
            break;
          case DETECT_INTERP_BOTH_HANDS_BY_CHEST: gestureName = "Both hands by chest";
            break;
          case DETECT_INTERP_PLAY_CARD: gestureName = "Play Card";
            break;
          case DETECT_INTERP_ONE_HAND_UP: gestureName = "One hand up";
            break;
          case DETECT_MOVING_RUSH_AT: gestureName = "Moving rush at";
            break;
          //default: gestureName = "N/A";
          //  break;
        }
        gestures[i]=gestureName;
      }
      
    }
    return gestures;
  }
  
  
  /**
    Establishes all different gestural parts
    as either true or false
  */
  void simpleGestureChecker(){
    PVector rUpperArm=PVector.sub(rShoulder, rElbow);
    PVector rLowerArm = PVector.sub(rHand, rElbow);
    
    PVector lUpperArm = PVector.sub(lShoulder, lElbow);
    PVector lLowerArm = PVector.sub(lHand, lElbow);
    
    float rAngle = PVector.angleBetween(rUpperArm, rLowerArm);
    float lAngle = PVector.angleBetween(lUpperArm, lLowerArm);
    
    rAngle = degrees(rAngle);
    lAngle = degrees(lAngle);
    
    //println("Right angle: "+rAngle);
    //println("Left angle: "+lAngle);
    
    if(lAngle>150){
      detectSignals[DETECT_LEFTARM_STRAIGHT]=true;
    }
    else if(lAngle<115){
      detectSignals[DETECT_LEFTARM_BENT]=true;
    }
    
    if(rAngle>150){
      detectSignals[DETECT_RIGHTARM_STRAIGHT]=true;
    }
    else if(rAngle<115){
      detectSignals[DETECT_RIGHTARM_BENT]=true;
    }
    
    //If right hand is more left than right shoulder
    if(rShoulder.x>rHand.x && rHand.x>lShoulder.x){
      detectSignals[DETECT_RIGHTHAND_IN]=true;
    }
    
    //If left hand is more right than left shoulder
    if(lShoulder.x<lHand.x && lHand.x<rShoulder.x){
      detectSignals[DETECT_LEFTHAND_IN]=true;
    }
    
    if(rShoulder.x<rElbow.x && rElbow.x<rHand.x){
      detectSignals[DETECT_RIGHTHAND_OUT]=true;
    }
    if(lShoulder.x>lElbow.x && lElbow.x>lHand.x){
      detectSignals[DETECT_LEFTHAND_OUT]=true;
    }
    
    if(lShoulder.y<lHand.y){
      detectSignals[DETECT_LEFTHAND_UP]=true;
    }
    
    if(rShoulder.y<rHand.y){
      detectSignals[DETECT_RIGHTHAND_UP]=true;
    }
    
    if(lHand.y<lElbow.y && lElbow.y<lShoulder.y){
      detectSignals[DETECT_LEFTHAND_DOWN]=true;
    }
    if(rHand.y<rElbow.y && rElbow.y<rShoulder.y){
      detectSignals[DETECT_RIGHTHAND_DOWN]=true;
    }
    
    float xRDist = abs(head.x-rHand.x);
    float yRDist = abs(head.y-rHand.y);
    
    float xLDist = abs(head.x-lHand.x);
    float yLDist = abs(head.y-lHand.y);
    
    if(xRDist<100 && yRDist<200){
      detectSignals[DETECT_RIGHTHAND_AT_MOUTH]=true;
    }
    
    if(xLDist<100 && yLDist<200){
      detectSignals[DETECT_LEFTHAND_AT_MOUTH]=true;
    }
    
    float xHandDist = abs(rHand.x-lHand.x);
    float yHandDist = abs(rHand.y-lHand.y);
    
    //println("X Hand Dist: "+xHandDist);
    //println("Y Hand Dist: "+yHandDist);
    
    if(xHandDist< 140 && yHandDist <140){
      detectSignals[DETECT_HANDS_TOGETHER]=true;
    }
    
    if(lHand.z<lElbow.z && lElbow.z<lShoulder.z){
      detectSignals[DETECT_LEFTARM_FRONT]=true;
    }
    
    if(rHand.z<rElbow.z && rElbow.z<rShoulder.z){
      detectSignals[DETECT_RIGHTARM_FRONT]=true;
    }
    
    PVector rHandDiff = PVector.sub(rHand, prevRightHand);
    PVector lHandDiff = PVector.sub(lHand, prevLeftHand);
    
    PVector rDiffNorm = new PVector (rHandDiff.x,rHandDiff.y,rHandDiff.z);
    PVector lDiffNorm = new PVector (lHandDiff.x,lHandDiff.y,lHandDiff.z);
    
    rDiffNorm.normalize();
    lDiffNorm.normalize();
    
    //Mostly in x direction
    if(abs(lDiffNorm.x)>0.8){
      leftXMvmt=true;
    }
    //Mostly in y direction
    else if(abs(lDiffNorm.y)>0.8){
      leftYMvmt=true;
    }
    //Mostly in z direction
    else if(abs(lDiffNorm.z)>0.8){
      leftZMvmt=true;
    }
    
    //Mostly in x direction
    if(abs(rDiffNorm.x)>0.8){
      rightXMvmt=true;
    }
    else if(abs(rDiffNorm.y)>0.8){
      rightYMvmt=true;
    }
    else if(abs(rDiffNorm.z)>0.8){
      rightZMvmt=true;
    }
    
    rightHandMvmt = rHandDiff.mag();
    leftHandMvmt = lHandDiff.mag();
  }
  
  /**
    Establishes the complex gestures
    (Gestures made up of various simple gestures)
    as either true or false.
  */
  void complexGesturesChecker(){
    
    PVector leftHandToElbow = PVector.sub(lHand,rElbow);
    PVector rightHandToElbow = PVector.sub(rHand,lElbow);
    
    boolean leftNearRightElbow = leftHandToElbow.mag()<250.0f;
    boolean rightNearLeftElbow = rightHandToElbow.mag()<250.0f;
    
    float handMvmtDiff = abs(rightHandMvmt-leftHandMvmt);
    float notMuchMvmt=32.0f;
    
    boolean handsClose = PVector.sub(lHand,rHand).mag()<200.0f;
    
    detectSignals[DETECT_SWORD_SWING]= ((detectSignals[DETECT_LEFTARM_FRONT] && leftHandMvmt>65.0f && rightHandMvmt<notMuchMvmt && !detectSignals[DETECT_RIGHTHAND_UP])
    || (detectSignals[DETECT_RIGHTARM_FRONT] && rightHandMvmt>65.0f && leftHandMvmt<notMuchMvmt && !detectSignals[DETECT_LEFTHAND_UP])) && !detectSignals[DETECT_HANDS_TOGETHER];
    
    detectSignals[DETECT_HOLDING_REINS]= (detectSignals[DETECT_HANDS_TOGETHER] && detectSignals[DETECT_LEFTHAND_IN] && detectSignals[DETECT_RIGHTHAND_IN] &&
    rightHandMvmt>0 && handMvmtDiff<notMuchMvmt && rightYMvmt && leftYMvmt);    
    
    /*
    println("Left hand movement: "+leftHandMvmt);
    println("Right hand movement: "+rightHandMvmt);
    println("Right Y Movement: "+rightYMvmt);
    println("Left Y Movement: "+leftYMvmt);
    */
    
    //Works only on the side, never facing straight forward
    detectSignals[DETECT_BOW_N_ARROW]= 
    ((detectSignals[DETECT_LEFTARM_FRONT] && detectSignals[DETECT_LEFTHAND_OUT] && detectSignals[DETECT_LEFTARM_STRAIGHT] && (detectSignals[DETECT_RIGHTARM_BENT] || detectSignals[DETECT_RIGHTHAND_IN]) && !detectSignals[DETECT_RIGHTHAND_DOWN] 
    && !detectSignals[DETECT_LEFTHAND_DOWN] && rightHandMvmt>30.0f && leftHandMvmt<2*notMuchMvmt && !handsClose && !leftNearRightElbow && !rightNearLeftElbow) 
    || 
    (detectSignals[DETECT_RIGHTARM_FRONT] && detectSignals[DETECT_RIGHTHAND_OUT] && detectSignals[DETECT_RIGHTARM_STRAIGHT] && (detectSignals[DETECT_LEFTARM_BENT] || detectSignals[DETECT_LEFTHAND_IN]) && !detectSignals[DETECT_LEFTHAND_DOWN] 
    && !detectSignals[DETECT_RIGHTHAND_DOWN] && leftHandMvmt>30.0f && rightHandMvmt<2*notMuchMvmt) && !handsClose && !leftNearRightElbow && !rightNearLeftElbow);
    //||
    //((detectSignals[DETECT_HANDS_TOGETHER] && leftZMvmt)
    //||
    //(detectSignals[DETECT_HANDS_TOGETHER] &&rightZMvmt));
    
    detectSignals[DETECT_AIR_GUITAR]= (detectSignals[DETECT_LEFTARM_BENT] && detectSignals[DETECT_LEFTHAND_IN] && !detectSignals[DETECT_RIGHTHAND_IN] && detectSignals[DETECT_RIGHTHAND_UP] && detectSignals[DETECT_RIGHTHAND_OUT])
    || (detectSignals[DETECT_RIGHTARM_BENT] && detectSignals[DETECT_RIGHTHAND_IN] && !detectSignals[DETECT_LEFTHAND_IN] && detectSignals[DETECT_LEFTHAND_UP] && detectSignals[DETECT_LEFTHAND_OUT]);
    
    //Both on one side, that side arm bent
    detectSignals[DETECT_HARP]= (detectSignals[DETECT_LEFTHAND_OUT] && detectSignals[DETECT_LEFTHAND_UP] && !detectSignals[DETECT_RIGHTHAND_IN] && rHand.x<lShoulder.x && rHand.x>lHand.x)
    || (detectSignals[DETECT_RIGHTHAND_OUT] && detectSignals[DETECT_RIGHTHAND_UP] && !detectSignals[DETECT_LEFTHAND_IN] && lHand.x>rShoulder.x && lHand.x<rHand.x);
    
    //All sorts of perfect
    float handXDiff = lHand.x-rHand.x;
    
    detectSignals[DETECT_CROCODILE_ARMS]= (detectSignals[DETECT_LEFTARM_FRONT] && detectSignals[DETECT_RIGHTARM_FRONT] && detectSignals[DETECT_LEFTHAND_IN] && 
    detectSignals[DETECT_RIGHTHAND_IN] && (leftYMvmt||rightYMvmt) && lHand.y>lHip.y && rHand.y>rHip.y && handXDiff<50.0f && !detectSignals[DETECT_HANDS_TOGETHER]);
    
    detectSignals[DETECT_FLYING]= (detectSignals[DETECT_LEFTARM_STRAIGHT] && detectSignals[DETECT_RIGHTARM_STRAIGHT] && detectSignals[DETECT_LEFTHAND_OUT] && detectSignals[DETECT_RIGHTHAND_OUT]
    && leftYMvmt && rightYMvmt);
    
    detectSignals[DETECT_WAND_TWIRL]= ((detectSignals[DETECT_LEFTARM_FRONT] && leftHandMvmt>notMuchMvmt && rightHandMvmt<notMuchMvmt/2 && !detectSignals[DETECT_HANDS_TOGETHER]
    && !leftNearRightElbow && !rightNearLeftElbow && !detectSignals[DETECT_LEFTHAND_DOWN] && detectSignals[DETECT_RIGHTHAND_DOWN])
    || 
    (detectSignals[DETECT_RIGHTARM_FRONT] && rightHandMvmt>notMuchMvmt && !detectSignals[DETECT_LEFTHAND_UP] && leftHandMvmt<notMuchMvmt/2 && !detectSignals[DETECT_HANDS_TOGETHER]
    && !leftNearRightElbow && !rightNearLeftElbow && !detectSignals[DETECT_LEFTHAND_DOWN] && detectSignals[DETECT_LEFTHAND_DOWN]));
    
    float yHandDiff = abs(rHand.y-lHand.y);
    
    detectSignals[DETECT_CASTING_MAGIC]= (detectSignals[DETECT_LEFTARM_FRONT] && detectSignals[DETECT_RIGHTARM_FRONT] && detectSignals[DETECT_LEFTARM_BENT] && detectSignals[DETECT_RIGHTARM_BENT]
    && !handsClose && yHandDiff<100);
    
    detectSignals[DETECT_CRYSTAL_BALL]= ((detectSignals[DETECT_LEFTARM_BENT] || detectSignals[DETECT_RIGHTARM_BENT]) && detectSignals[DETECT_LEFTHAND_IN] && detectSignals[DETECT_RIGHTHAND_IN] &&
    (yHandDiff>200));
        
    //Works fairly well, maybe use left/right ZMvmt
    detectSignals[DETECT_PROTECT_TREASURE]=(!detectSignals[DETECT_LEFTHAND_UP] && !detectSignals[DETECT_LEFTHAND_IN] && detectSignals[DETECT_RIGHTARM_FRONT] && detectSignals[DETECT_RIGHTHAND_IN] && leftHandMvmt<notMuchMvmt && rightHandMvmt>55 &&
    rightZMvmt ) ||
    (!detectSignals[DETECT_RIGHTHAND_UP] && !detectSignals[DETECT_RIGHTHAND_IN] && detectSignals[DETECT_LEFTARM_FRONT] && detectSignals[DETECT_LEFTHAND_IN] && rightHandMvmt<notMuchMvmt && leftHandMvmt>55 &&
    leftZMvmt);
    
    detectSignals[DETECT_EAT_OR_DRINK]= (detectSignals[DETECT_LEFTHAND_AT_MOUTH] && detectSignals[DETECT_RIGHTHAND_DOWN] && !detectSignals[DETECT_RIGHTHAND_IN]) ||
    (detectSignals[DETECT_RIGHTHAND_AT_MOUTH] && detectSignals[DETECT_LEFTHAND_DOWN] && ! detectSignals[DETECT_RIGHTHAND_IN]);
    
    }
  
  
  void chrisGesturesChecker(){
    float waggleLeftHand = abs(lHand.x-prevLeftHand.x);
    waggleLeftHand*=0.9f;
    
    float waggleRightHand = abs(rHand.x-prevRightHand.x);
    waggleRightHand*=0.9f;
    
    //println("WaggleLeftHand: "+waggleLeftHand);
    //println("WaggleRightHand: "+waggleRightHand);
    
    detectSignals[DETECT_INTERP_PUNCH_AT]=((detectSignals[DETECT_LEFTHAND_OUT] && detectSignals[DETECT_RIGHTHAND_DOWN] && waggleLeftHand > 160.0f) ||
    (detectSignals[DETECT_LEFTHAND_DOWN] && detectSignals[DETECT_RIGHTHAND_OUT] && waggleRightHand > 160.0f)) && !detectSignals[DETECT_HANDS_TOGETHER];
    
    detectSignals[DETECT_INTERP_SMOKE_PONDER] = (detectSignals[DETECT_LEFTHAND_AT_MOUTH] && detectSignals[DETECT_RIGHTHAND_DOWN]) ||
    (detectSignals[DETECT_LEFTHAND_DOWN] && detectSignals[DETECT_RIGHTHAND_AT_MOUTH]);
    
    PVector leftHandToElbow = PVector.sub(lHand,rElbow);
    PVector rightHandToElbow = PVector.sub(rHand,lElbow);
    
    boolean leftNearRightElbow = leftHandToElbow.mag()<300.0f;
    boolean rightNearLeftElbow = rightHandToElbow.mag()<300.0f;
    
    
    detectSignals[DETECT_INTERP_ARMS_CROSSED] = leftNearRightElbow && rightNearLeftElbow;
    
    boolean handsTogether = PVector.sub(rHand,lHand).mag()<120.0f;
    float leftHandHeightFromWaist = abs(lHand.y - lHip.y);
    float rightHandHeightFromWaist = abs(rHand.y - rHip.y);
    float leftHandHeightFromShoulders = abs(lHand.y-lShoulder.y);
    float rightHandHeightFromShoulders = abs(rHand.y-rShoulder.y);
    
    detectSignals[DETECT_INTERP_CUFFED]= handsTogether && (leftHandHeightFromWaist < 100.0f) &&
    (rightHandHeightFromWaist < 100.0f);
    
    detectSignals[DETECT_INTERP_TABLE_WIPER]= (leftHandHeightFromWaist < 220.0f && detectSignals[DETECT_RIGHTHAND_DOWN] 
    && detectSignals[DETECT_LEFTARM_BENT] && waggleLeftHand > 35.0f) ||
    (rightHandHeightFromWaist < 220.0f && detectSignals[DETECT_LEFTHAND_DOWN]
    && detectSignals[DETECT_RIGHTARM_BENT] && waggleRightHand > 35.0f);
    
    //Let's try this one:
    detectSignals[DETECT_INTERP_LOW_DRAW]=(detectSignals[DETECT_RIGHTHAND_DOWN] && waggleLeftHand < 20.0f &&
    detectSignals[DETECT_LEFTHAND_OUT] && leftHandHeightFromWaist<150.0f) 
    ||
    (detectSignals[DETECT_LEFTHAND_DOWN] && waggleRightHand < 20.0f && detectSignals[DETECT_RIGHTHAND_OUT] 
    && rightHandHeightFromWaist<150.0f); 
    
    //Let's try this one: 
    detectSignals[DETECT_INTERP_HIGH_DRAW] = (detectSignals[DETECT_LEFTHAND_OUT] && detectSignals[DETECT_RIGHTHAND_DOWN]
    && waggleLeftHand < 20.0f && leftHandHeightFromWaist>400.0f)
    ||
    (detectSignals[DETECT_RIGHTHAND_OUT] && detectSignals[DETECT_LEFTHAND_DOWN] && waggleRightHand < 20.0f 
    && rightHandHeightFromWaist>400.0f);
    
    //Nope
    detectSignals[DETECT_INTERP_SWEEP] =((leftHandHeightFromShoulders < 250.0f && rightHandHeightFromWaist < 150.0f && waggleRightHand > 30.0f) ||
    (rightHandHeightFromShoulders < 250.0f && leftHandHeightFromWaist < 150.0f && waggleLeftHand > 30.0f))
    && (detectSignals[DETECT_LEFTARM_BENT] && detectSignals[DETECT_RIGHTARM_BENT]);
    
    
    detectSignals[DETECT_INTERP_FIDDLE_WITH_SMALL_THING] =handsTogether && detectSignals[DETECT_LEFTARM_BENT] && detectSignals[DETECT_RIGHTARM_BENT];
    
    //GOING TO NEED TO PASS IN LEG INFO AS WELL FOR THIS TO WORK
    detectSignals[DETECT_INTERP_STAND_LEGS] =false;
    
    //NEED TORSO INFO FOR DIS
    detectSignals[DETECT_INTERP_BOTH_HANDS_BY_CHEST] = false;
    
    
    detectSignals[DETECT_INTERP_ARM_BY_BICEP] =(leftNearRightElbow && detectSignals[DETECT_RIGHTHAND_DOWN]) ||
    (rightNearLeftElbow && detectSignals[DETECT_LEFTHAND_DOWN]);
    
    detectSignals[DETECT_INTERP_HANDS_TOGETHER] = handsTogether;
    
    //Needs a leftArmAtPartner/rightArmAtParnet
    detectSignals[DETECT_INTERP_PLAY_CARD] =(detectSignals[DETECT_RIGHTHAND_IN] && waggleLeftHand > 35.0f&& waggleRightHand<20.0f && lHand.y > 0.0f) ||
    (detectSignals[DETECT_LEFTHAND_IN] && waggleRightHand > 35.0f && waggleLeftHand<20.0f && rHand.y > 0.0f);
    
    detectSignals[DETECT_INTERP_ONE_HAND_UP] =(detectSignals[DETECT_LEFTHAND_UP] && detectSignals[DETECT_RIGHTHAND_DOWN]) ||
    (detectSignals[DETECT_LEFTHAND_DOWN] && detectSignals[DETECT_RIGHTHAND_UP]);
    
    //NEED LEG INFO FOR THIS
    detectSignals[DETECT_MOVING_RUSH_AT] = false;
  }
  
  void setSkeleton(PVector[] skeleton){
    head=skeleton[0];
    rHand = skeleton[1];
    rWrist=skeleton[2];
    rElbow=skeleton[3];
    rShoulder=skeleton[4];
    rHip=skeleton[5];
    lHand = skeleton[6];
    lWrist=skeleton[7];
    lElbow=skeleton[8];
    lShoulder=skeleton[9];
    lHip = skeleton[10];
  }
  
  
}
