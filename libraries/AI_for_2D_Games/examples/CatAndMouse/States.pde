public class CatWanderState extends State {

  public void enter(BaseEntity user) {
    Cat c = (Cat)user;
    c.maxSpeed(CAT_WANDER_SPEED);
    c.AP().wanderOn();
  }

  public void execute(BaseEntity user, double deltaTime, World world) {
    Cat c = (Cat)user;
    c.lookForMouse();
    if (c.chasing != null)
      c.FSM().changeState(chaseMouseState);
  }

  public void exit(BaseEntity user) {
    Cat c = (Cat)user;
    c.AP().wanderOff();
  }

  public boolean onMessage(BaseEntity user, Telegram tgram) {
    return false;
  }
} // End of CatWanderState class


public class ChaseMouseState extends State {

  public void enter(BaseEntity user) {
    Cat c = (Cat)user;
    c.maxSpeed(CAT_CHASE_SPEED);
    Dispatcher.dispatch(500, c.ID(), c.chasing.ID(), AFTER_YOU);
    c.AP().pursuitOn(c.chasing);
  }

  public void execute(BaseEntity user, double deltaTime, World world) {
    Cat c = (Cat)user;
    if (Vector2D.distSq(c.pos(), c.chasing.pos()) < killDistSq) {
      c.FSM().changeState(killMouseState);
    }
    else if (!c.canSee(world, c.chasing.pos())) {
      c.FSM().changeState(seekMouseState);
    }
  }

  public void exit(BaseEntity user) {
    Cat c = (Cat)user;
    c.AP().pursuitOff();
  }

  public boolean onMessage(BaseEntity user, Telegram tgram) {
    return false;
  }
} // End of ChaseMouseState class


public class KillMouseState extends State {

  public void enter(BaseEntity user) {
    Cat c = (Cat)user;
    c.velocity(0, 0);
    c.miceKilled++;
    // Send mouse a message to die
    Dispatcher.dispatch(0, c.ID(), c.chasing.ID(), DIE);
    // Slight pause before back to wander
    Dispatcher.dispatch(2000, c.ID(), c.ID(), NEXT_MOUSE);
  }

  public void execute(BaseEntity user, double deltaTime, World world) {
  }

  public void exit(BaseEntity user) {
  }

  public boolean onMessage(BaseEntity user, Telegram tgram) {
    Cat c = (Cat)user;
    switch(tgram.msg) {
    case NEXT_MOUSE:
      if (c.miceKilled < NBR_MICE)
        c.FSM().changeState(catWanderState);
      else
        reset();
      return true;
    }      
    return false;
  }
} // End of CatWaitState class


public class SeekMouseState extends State {

  public void enter(BaseEntity user) {
    Cat c = (Cat)user;
    c.maxSpeed(CAT_SEEK_SPEED);
    c.adjustLastKnownPosition();
    c.AP().arriveOn(c.lastKnownPos);
  }

  public void execute(BaseEntity user, double deltaTime, World world) {
    Cat c = (Cat)user;
    // Can we see a mouse to chase
    c.lookForMouse();
    if (c.chasing != null) {
      c.FSM().changeState(chaseMouseState);
    }
    else if (c.AP().arriveDistance() < 20) {
      c.FSM().changeState(catWanderState);
    }
  }

  public void exit(BaseEntity user) {
    Cat c = (Cat)user;
    c.AP().arriveOff();
  }

  public boolean onMessage(BaseEntity user, Telegram tgram) {
    return false;
  }
} // End of SeekMouseState class


public class MouseGlobalState extends State {

  public void enter(BaseEntity user) {
  }

  public void execute(BaseEntity user, double deltaTime, World world) {
  }

  public void exit(BaseEntity user) {
  }

  public boolean onMessage(BaseEntity user, Telegram tgram) {
    Mouse m = (Mouse)user;
    switch(tgram.msg) {
    case DIE:
      m.alive = false;
      m.velocity(0, 0);
      m.AP().allOff();
      m.die(world, 0.5);
      return true;
    }      
    return false;
  }
} // End of MouseGlobalState class


public class MouseWanderState extends State {

  public void enter(BaseEntity user) {
    Mouse m = (Mouse)user;
    m.maxSpeed(MOUSE_WANDER_SPEED);
    m.AP().wanderOn();
  }

  public void execute(BaseEntity user, double deltaTime, World world) {
    Mouse m = (Mouse)user;
    if (m.canSee(world, cat.pos()))
      m.FSM().changeState(evadeCatState);
  }

  public void exit(BaseEntity user) {
    Mouse m = (Mouse)user;
    m.AP().wanderOff();
  }

  public boolean onMessage(BaseEntity user, Telegram tgram) {
    Mouse m = (Mouse)user;
    switch(tgram.msg) {
    case AFTER_YOU:
      m.FSM().changeState(evadeCatState);
      return true;
    }      
    return false;
  }
} // End of MouseWanderState class


public class EvadeCatState extends State {

  public void enter(BaseEntity user) {
    Mouse m = (Mouse)user;
    m.maxSpeed(MOUSE_EVADE_SPEED);
    if (Vector2D.dist(m.pos(), cat.pos()) > 100)
      m.AP().hideOn(cat);
    else
      m.AP().evadeOn(cat);
  }

  public void execute(BaseEntity user, double deltaTime, World world) {
    Mouse m = (Mouse)user;
    if (!cat.canSee(world, m.pos()))
      Dispatcher.dispatch(1000, m.ID(), m.ID(), SAFE_HERE);
  }

  public void exit(BaseEntity user) {
    Mouse m = (Mouse)user;
    m.AP().hideOff();
    m.AP().evadeOff();
  }

  public boolean onMessage(BaseEntity user, Telegram tgram) {
    Mouse m = (Mouse)user;
    switch(tgram.msg) {
    case SAFE_HERE:
      m.FSM().changeState(mouseWanderState);
      return true;
    }      
    return false;
  }
} // End of MouseWanderState class


