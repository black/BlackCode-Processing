class PushDetectorLab extends XnVPushDetector {
  // XnVPushDetector pushDetector;

  PushDetectorLab() {    
    RegisterPush(this);
    RegisterDeactivate(this);
    RegisterPrimaryPointCreate(this);
    // RegisterPointCreate(this);
    RegisterPointUpdate(this);
    RegisterPointDestroy(this);

    // RegisterStabilized(this);
  }

  void onPush(float vel, float angle) {
    println(">>>>>>>>> PUSH v:" + vel + "a: " + angle);
    //   println("ID = " +  GetPrimaryID()+" immduration: " + GetPushImmediateDuration()+" immoffset: "+GetPushImmediateOffset()  );
  }

  void onPointUpdate(XnVHandPointContext cxt) {
    /*
    println("PUSH onPointUpdate " + cxt.getFTime() +":"+cxt.getNUserID()+"("+cxt.getNID()  +") "+ 
     cxt.getPtPosition().getX() + " " +
     cxt.getPtPosition().getY()+" " +
     cxt.getPtPosition().getZ());
     */
  }



  void onPrimaryPointCreate(XnVHandPointContext pContext, XnPoint3D ptFocus)
  {
    println(">>>>>>>>> PUSH onPrimaryPointCreate:");
  }

  void onPointDestroy(int nID)
  {
    println(">>>>>>>>> PUSH onPointDestroy: " + nID);
  }
}

