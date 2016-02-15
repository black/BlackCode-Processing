public static class KeeperInterceptBall extends State implements Constants {

  private static KeeperInterceptBall instance = null;

  public static KeeperInterceptBall state() {
    if (instance == null) {
      instance = new KeeperInterceptBall();
      instance.name = "Intercept ball";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    GoalKeeper keeper = (GoalKeeper) owner;
    keeper.AP().pursuitOn(keeper.team.pitch.ball);
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    GoalKeeper keeper = (GoalKeeper) owner;

    if (keeper.tooFarFromGoalMouth() && !keeper.isClosestPlayerOnPitchToBall()) {
      keeper.FSM().changeState(KeeperReturnHome.state());
      return;
    }
    if (keeper.isBallWithinRange()) {
      keeper.team.pitch.ball.trap(keeper);
      keeper.FSM().changeState(KeeperPutBallBackInPlay.state());
      return;
    }
  }

  public void exit(BaseEntity owner) {
    GoalKeeper keeper = (GoalKeeper) owner;
    keeper.AP().pursuitOff();
  }


  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}



public static class KeeperPutBallBackInPlay extends State implements Constants {

  private static KeeperPutBallBackInPlay instance = null;

  public static KeeperPutBallBackInPlay state() {
    if (instance == null) {
      instance = new KeeperPutBallBackInPlay();
      instance.name = "Put ball into play";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    GoalKeeper keeper = (GoalKeeper) owner;
    keeper.team.setControllingPlayer(keeper);
    keeper.team.FSM().changeState(TeamDefending.state());
    keeper.team.getOtherTeam().FSM().changeState(TeamDefending.state());
    keeper.team.sendFieldPlayersToRegion();
    keeper.team.getOtherTeam().sendFieldPlayersToRegion();
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    GoalKeeper keeper = (GoalKeeper) owner;
    Ball ball = keeper.team.pitch.ball;
    Vector2D ballPos = ball.pos();
    Vector2D ballTarget = new Vector2D();

    if (!keeper.isThreatened()) {
      PlayerBase receiver = keeper.team.findBestPass(keeper, ballTarget, MaxPassingForce, KeeperMinPassDistance);
      if (receiver != null) {
        keeper.team.addNoiseToKick(ballPos, ballTarget);
        Vector2D kickDirection = Vector2D.sub(ballTarget, ballPos);
        keeper.team.pitch.ball.kick(keeper, kickDirection, MaxPassingForce);
        Dispatcher.dispatch(0, keeper.ID(), receiver.ID(), RECEIVE_BALL, ballTarget);
        keeper.FSM().changeState(KeeperTendGoal.state());
        return;
      }
      else {
        // can't find receiver kick ball towards one of the defenders anyway
        // to prevent lockup
      }
    }
    keeper.velocity(Vector2D.ZERO);
  }

  public void exit(BaseEntity owner) {
    GoalKeeper keeper = (GoalKeeper) owner;
    keeper.team.pitch.ball.realease();
  }


  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}



public static class KeeperReturnHome extends State implements Constants {

  private static KeeperReturnHome instance = null;

  public static KeeperReturnHome state() {
    if (instance == null) {
      instance = new KeeperReturnHome();
      instance.name = "Return home";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    GoalKeeper keeper = (GoalKeeper) owner;
    keeper.targetPos.set(keeper.team.pitch.getRegion(keeper.regionToUse).centre);
    keeper.targetPos.add(MathUtils.randomClamped(), MathUtils.randomClamped());
    keeper.AP().arriveOn(keeper.targetPos, SBF.FAST);
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    GoalKeeper keeper = (GoalKeeper) owner;
    if (keeper.team.pitch.getRegion(keeper.regionToUse).contains(keeper.pos(), 0.8)) {
      keeper.targetPos.set(keeper.pos());
      keeper.FSM().changeState(KeeperTendGoal.state());
      return;
    }
  }

  public void exit(BaseEntity owner) {
    GoalKeeper keeper = (GoalKeeper) owner;
    keeper.AP().arriveOff();
  }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}



public static class KeeperTendGoal  extends State implements Constants {

  private static KeeperTendGoal instance = null;

  public static KeeperTendGoal state() {
    if (instance == null) {
      instance = new KeeperTendGoal();
      instance.name = "Tend Goal";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    GoalKeeper keeper = (GoalKeeper) owner;
    keeper.targetPos.set(keeper.getRearInterposeTarget());
    keeper.AP().arriveOn(keeper.targetPos, SBF.FAST);
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    GoalKeeper keeper = (GoalKeeper) owner;
    keeper.targetPos.set(keeper.getRearInterposeTarget());
    keeper.AP().arriveOn(keeper.targetPos, SBF.FAST);

    if (keeper.isBallWithinRange()) {//&& keeper.team.getReceivingPlayer() == null) {
      keeper.team.pitch.ball.trap(keeper);
      keeper.FSM().changeState(KeeperPutBallBackInPlay.state());
      return;
    }

    // If within intercept range and the other team are in control go for it
    if (keeper.isBallWithinInterceptRange() && !keeper.team.isInControl()) {
      keeper.FSM().changeState(KeeperInterceptBall.state());
      return;
    }

    if (keeper.isBallWithinInterceptRange() && keeper.team.isInControl() && keeper.team.getReceivingPlayer() == null) {
      keeper.FSM().changeState(KeeperInterceptBall.state());
      return;
    }

    if (keeper.tooFarFromGoalMouth() && keeper.team.isInControl()) {
      keeper.FSM().changeState(PlayerReturnToRegion.state());
      return;
    }
  }

  public void exit(BaseEntity owner) {
    GoalKeeper keeper = (GoalKeeper) owner;
    keeper.AP().arriveOff();
  }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}



public static class PlayerGlobal extends State implements Constants {

  private static PlayerGlobal instance = null;

  public static PlayerGlobal state() {
    if (instance == null)
      instance = new PlayerGlobal();
    return instance;
  }

  public void enter(BaseEntity owner) {
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    PlayerBase player = (PlayerBase) owner;  
    if (player.isControllingPlayer() && player.isBallWithinReceivingRange())
      player.maxSpeed(PlayerMaxSpeedWithBall);
    else
      player.maxSpeed(PlayerMaxSpeedWithoutBall);
  }

  public void exit(BaseEntity owner) { }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    PlayerBase player = (PlayerBase) owner;
    Vector2D v = null;
    switch(tgram.msg) {
    case RECEIVE_BALL:
      // Get position ball was kicked to
      v = (Vector2D) tgram.extraInfo[0];
      player.targetPos.set(v);
      player.FSM().changeState(PlayerReceiveBall.state());
      break;
    case SUPPORT_ATTACKER:
      if (player.FSM().isUsingState(PlayerSupportAttacker.state()))
        return true;
      player.FSM().changeState(PlayerSupportAttacker.state());
      return true;  
    case PLAYER_PREPARE_FOR_KICKOFF:
      player.FSM().changeState(PlayerPrepareForKickOff.state());
      return true;
    case GO_TO_REGION:
      player.FSM().changeState(PlayerReturnToRegion.state());
      return true;
    case WAIT:
      player.FSM().changeState(PlayerWait.state());
      return true;
    case PASS_TO_ME:
      // Only need to consider this if the field player is within kicking distance 
      //        if(player.isFieldPlayer() && player.isBallWithinKickingRange()){
      if (player.team.getReceivingPlayer() == null && player.isBallWithinKickingRange()) {
        // Get the player sending this message
        FieldPlayer receiver = (FieldPlayer)tgram.extraInfo[0];
        player.team.pitch.ball.kick(player, Vector2D.sub(receiver.pos(), player.team.pitch.ball.pos()), MaxPassingForce);
        Dispatcher.dispatch(0, player.ID(), receiver.ID(), RECEIVE_BALL, receiver.pos().get());
        player.FSM().changeState(PlayerWait.state());
        player.findSupport();
      }
      return true;
    }
    return false;
  }
}



public static class PlayerChaseBall extends State {

  private static PlayerChaseBall instance = null;

  public static PlayerChaseBall state() {
    if (instance == null) {
      instance = new PlayerChaseBall();
      instance.name = "Chase ball";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.AP().seekOn(player.team.pitch.ball.pos());
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    PlayerBase player = (PlayerBase) owner;
    // If player in kick range change to kick ball state
    if (player.isBallWithinKickingRange()) {
      player.FSM().changeState(PlayerKickBall.state());
      return;
    }
    // If this player is the closest to the ball continue chasing it
    if (player.isClosestTeamMemberToBall()) {
      player.AP().seekOn(player.team.pitch.ball.pos());
      return;
    }
    // If player is not closest to ball he should return back to current region
    player.FSM().changeState(PlayerReturnToRegion.state());
  }

  public void exit(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.AP().seekOff();
  }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}



public static class PlayerDribble extends State implements Constants {

  private static PlayerDribble instance = null;

  public static PlayerDribble state() {
    if (instance == null) {
      instance = new PlayerDribble();
      instance.name = "Dribble";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.team.setControllingPlayer(player);
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    PlayerBase player = (PlayerBase) owner;
    Vector2D direction;
    double dot = player.team.teamHeading.dot(player.heading());
    if (dot < 0) {  // Facing wrong direction so turn round
      direction = new Vector2D(player.heading());
      double angle = -1 * FastMath.QUARTER_PI * direction.sign(player.team.teamHeading);
      direction = Transformations.vec2DRotateAroundOrigin(direction, angle);
      player.team.pitch.ball.kick(player, direction, MaxDribbleTurnForce);
    }
    else {
      // So not turning so dribble up the field towards the opponents goal Change this 
      // direction so it is more aligned with the opponents goal
      if (player.isInHotRegion()) {
        Goal goal = player.team.getOtherGoal();
        double y = goal.centre.y;
        if (player.pos().y < GOAL_LOW_Y)
          y = GOAL_LOW_Y;
        else if (player.pos().y > GOAL_HIGH_Y)
          y = GOAL_HIGH_Y;
        direction = new Vector2D(goal.centre.x, y);  
        direction.sub(player.pos());
      }
      else {
        direction = new Vector2D(player.team.teamHeading);
      }
      player.team.pitch.ball.kick(player, direction, MaxDribbleForce);
    }
    player.FSM().changeState(PlayerChaseBall.state());
  }

  public void exit(BaseEntity owner) { }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}


public static class PlayerKickBall extends State implements Constants {

  private static PlayerKickBall instance = null;

  public static PlayerKickBall state() {
    if (instance == null) {
      instance = new PlayerKickBall();
      instance.name = "Kick ball";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    // Make everyone know this is the controlling player
    player.team.setControllingPlayer(player);
    // If we are not ready to kick then change to ChaseBall state
    if (!player.team.pitch.ball.readyToBeKickedBy(player))
      player.FSM().changeState(PlayerChaseBall.state());
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    PlayerBase player = (PlayerBase) owner;
    Ball ball = player.team.pitch.ball;
    Vector2D ballPos = ball.pos();

    // Calculate the normalised vector to the ball
    Vector2D toBall = Vector2D.sub(ballPos, player.pos());
    toBall.normalize();
    // Calculate the dot product of the heading and toball vector
    double dot = player.heading().dot(toBall);

    // If there is a receiving player assigned or the ball is in the hands
    // of a goalkeeper or the player is facing away from the ball 
    // change to ChaseBall state.
    if (player.team.getReceivingPlayer() != null || player.team.pitch.ball.keeperHasBall() || dot < 0) {
      player.FSM().changeState(PlayerChaseBall.state());
      return;
    }

    // See if we have a shot at goal
    double power = (0.6 + 0.4 * dot) * MaxShootingForce;
    Vector2D ballTarget = new Vector2D(); // populate this with the shot target
    // The probability of a shot increases as we get closer to opponents goal
    double prob = player.isInHotRegion() ? 0.8 : player.isInHotHalf() ? 0.6 : 0.05;
    if ( Math.random() < prob && player.team.canShoot(ballPos, power, ballTarget)) {
      player.team.addNoiseToKick(ballPos, ballTarget);
      Vector2D kickDirection = Vector2D.sub(ballTarget, ballPos);
      ball.kick(player, kickDirection, power);
      player.FSM().changeState(PlayerWait.state());
      player.findSupport();
      return;
    }

    // Attempt to pass the ball if we are threatened and a suitable receiver can be found
    power = (0.4 + 0.6 * dot) * MaxPassingForce;
    if (player.isThreatened()) {
      PlayerBase receiver = player.team.findBestPass(player, ballTarget, power, FielderMinPassDistance);
      if (receiver != null) {
        player.team.addNoiseToKick(ballPos, ballTarget);
        Vector2D kickDirection = Vector2D.sub(ballTarget, ballPos);
        ball.kick(player, kickDirection, power);
        Dispatcher.dispatch(0, player.ID(), receiver.ID(), RECEIVE_BALL, ballTarget);
        player.FSM().changeState(PlayerWait.state());
        player.findSupport();
        return;
      }
    }
    // Either we are not threatened or if threatened unable to find a suitable pass
    // so dribble
    player.findSupport();
    player.FSM().changeState(PlayerDribble.state());
  }

  public void exit(BaseEntity owner) {
  }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}


public static class PlayerLeavesPitch  extends State implements Constants {

  private static PlayerLeavesPitch instance = null;

  public static PlayerLeavesPitch state() {
    if (instance == null) {
      instance = new PlayerLeavesPitch();
      instance.name = "Leave Pitch";
    }
    return instance;
  }


  public void enter(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.targetPos.set(player.offFieldPosition);
    player.heading(offPitchHeading);
    player.AP().arriveOn(player.targetPos, SBF.FAST);
  }


  public void execute(BaseEntity owner, double deltaTime, World world) {
    PlayerBase player = (PlayerBase) owner;
    if (player.isAtTarget()) {
      Dispatcher.dispatch(0, player.ID(), player.team.ID(), AT_TARGET);
      player.moveTo(player.targetPos);
      player.FSM().changeState(PlayerWait.state());
    }
  }


  public void exit(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.AP().arriveOff();
  }


  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}

public static class PlayerPrepareForKickOff extends State implements Constants {

  private static PlayerPrepareForKickOff instance = null;

  public static PlayerPrepareForKickOff state() {
    if (instance == null) {
      instance = new PlayerPrepareForKickOff();
      instance.name = "Prep for Kickoff";
    }
    return instance;
  }


  public void enter(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.regionToUse = player.defendRegion;
    player.targetPos.set(player.team.pitch.getRegion(player.regionToUse).centre);
    player.targetPos.add(MathUtils.randomClamped(), MathUtils.randomClamped());
    player.headingAtRest(player.team.teamHeading);
    player.AP().arriveOn(player.targetPos, SBF.FAST);
  }


  public void execute(BaseEntity owner, double deltaTime, World world) {
    PlayerBase player = (PlayerBase) owner;
    if (player.isGoalKeeper()) { // Goal keeper
      if (player.isNearTarget()) {
        Dispatcher.dispatch(0, player.ID(), player.team.ID(), AT_TARGET);
        player.FSM().changeState(KeeperTendGoal.state());
      }
    }
    else { // Field player
      if (player.isAtTarget()) {
        Dispatcher.dispatch(0, player.ID(), player.team.ID(), AT_TARGET);
        player.moveTo(player.targetPos);
        player.FSM().changeState(PlayerWait.state());
      }
    }
  }


  public void exit(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.AP().arriveOff();
  }


  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}

public static class PlayerReceiveBall extends State implements Constants {


  private static PlayerReceiveBall instance = null;

  public static PlayerReceiveBall state() {
    if (instance == null) {
      instance = new PlayerReceiveBall();
      instance.name = "Receive ball";
    }
    return instance;
  }


  public void enter(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.team.setReceivingPlayer(player);
    player.team.setControllingPlayer(player);

    // There are two types of receive behaviour. One uses arrive to direct
    // the receiver to the position sent by the passer in its telegram. The
    // other uses the pursuit behavior to pursue the ball. 
    // This statement selects between them dependent on the probability
    // ChanceOfUsingArriveTypeReceiveBehavior, whether or not an opposing
    // player is close to the receiving player, and whether or not the receiving
    // player is in the opponents 'hot region' (the third of the pitch closest
    // to the opponent's goal
    if (player.isInHotRegion() || 
      Math.random() < ChanceOfUsingArriveTypeReceiveBehavior &&
      !player.team.isOpponentWithinRadius(player.pos(), PassThreatDistance)) {
      player.AP().arriveOn(player.targetPos, SBF.FAST);
    }
    else {
      player.AP().pursuitOn( player.team.pitch.ball);
    }
  }


  public void execute(BaseEntity owner, double deltaTime, World world) {
    PlayerBase player = (PlayerBase) owner;

    // If the player is close enough to the ball or his team loses control
    // then chase ball
    if (player.isBallWithinReceivingRange() || !player.team.isInControl()) {
      player.FSM().changeState(PlayerChaseBall.state());
    }

    if (player.isAtTarget()) {
      player.AP().arriveOff().pursuitOff();
      player.trackBall();
      player.velocity(0, 0);
    }
  }


  public void exit(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.AP().arriveOff().pursuitOff();
    player.team.setReceivingPlayer(null);
  }


  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}

public static class PlayerReturnToRegion extends State implements Constants {


  private static PlayerReturnToRegion instance = null;

  public static PlayerReturnToRegion state() {
    if (instance == null) {
      instance = new PlayerReturnToRegion();
      instance.name = "To Region";
    }
    return instance;
  }


  public void enter(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.targetPos.set(player.team.pitch.getRegion(player.regionToUse).centre);
    player.targetPos.add(MathUtils.randomClamped(), MathUtils.randomClamped());
    player.AP().arriveOn(player.targetPos, SBF.FAST);
  }


  public void execute(BaseEntity owner, double deltaTime, World world) {
    PlayerBase player = (PlayerBase) owner;
    if (player.team.pitch.ballInPlay) {
      // If this is the nearest player to the ball and no receiver has been allocated
      // and it is not in the possession of a goalkeeper then chase it
      if (player.isClosestTeamMemberToBall() && player.team.getReceivingPlayer() == null && !player.team.pitch.ball.keeperHasBall()) {
        if (player.isGoalKeeper())
          player.FSM().changeState(KeeperInterceptBall.state());
        else
          player.FSM().changeState(PlayerChaseBall.state());
        return;
      }

      if (player.team.pitch.getRegion(player.regionToUse).contains(player.pos(), 0.5)) {
        player.targetPos.set(player.pos());
        if (player.isGoalKeeper())
          player.FSM().changeState(KeeperTendGoal.state());
        else
          player.FSM().changeState(PlayerWait.state());
        return;
      }
    }
    // Game is not on
    if (!player.team.pitch.ballInPlay && player.isAtTarget()) {
      if (player.isGoalKeeper())
        player.FSM().changeState(KeeperTendGoal.state());
      else
        player.FSM().changeState(PlayerWait.state());
    }
  }


  public void exit(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.AP().arriveOff();
  }


  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}


public static class PlayerSupportAttacker extends State implements Constants {


  private static PlayerSupportAttacker instance = null;

  public static PlayerSupportAttacker state() {
    if (instance == null) {
      instance = new PlayerSupportAttacker();
      instance.name = "Support Attacker";
    }
    return instance;
  }


  public void enter(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    // Find the best supporting position
    player.AP().arriveOn(player.team.getBestSupportngSpot(), SBF.FAST);
  }


  public void execute(BaseEntity owner, double deltaTime, World world) {
    PlayerBase player = (PlayerBase) owner;
    // If team is not in control then go to current region
    if (!player.team.isInControl() || player != player.team.getSupportingPlayer()) {
      player.FSM().changeState(PlayerReturnToRegion.state());
      return;
    }
    // Update the best supporting position if required
    player.targetPos.set(player.team.getBestSupportngSpot());
    player.AP().arriveOn(player.targetPos, SBF.FAST);
    // See if this player has a possible shot at goal
    if (player.team.canShoot(player.pos(), MaxShootingForce, null)) {
      player.team.requestPass(player);
    }
    if (player.isAtTarget()) {
      player.AP().arriveOff();
      player.velocity(0, 0);
      player.trackBall();
      if (!player.isThreatened()) {
        player.team.requestPass(player);
      }
    }
    else if (!player.AP().isArriveOn()) {
      player.AP().arriveOn();
    }
  }


  public void exit(BaseEntity owner) {
    PlayerBase player = (PlayerBase) owner;
    player.team.setSupportingPlayer(null);
    player.AP().arriveOff();
  }


  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}


public static class PlayerWait extends State implements Constants {


  private static PlayerWait instance = null;

  public static PlayerWait state() {
    if (instance == null) {
      instance = new PlayerWait();
      instance.name = "Wait";
    }
    return instance;
  }


  public void enter(BaseEntity owner) {
    //PlayerBase player = (PlayerBase) owner;
  }


  public void execute(BaseEntity owner, double deltaTime, World world) {
    PlayerBase player = (PlayerBase) owner;
    if (!player.isNearTarget()) {
      player.AP().arriveOn();
      return;
    }
    player.AP().arriveOff();
    player.velocity(0, 0);
    if (player.team.pitch.ballInPlay)
      player.trackBall();

    if (player.team.pitch.ballInPlay) {
      // If our team is in control and the player is ahead of the attacker then ask for a pass
      if (player.team.isInControl() && !player.isControllingPlayer() && player.isAheadOfAttacker()) {
        player.team.requestPass(player);
        return;
      }
      // if closest player to ball and there is no assigned receiver and 
      // neither goalkeeper has the ball then chase it
      if (player == player.team.getPlayerClosestToBall() && 
        player.team.getReceivingPlayer() == null &&
        !player.team.pitch.ball.keeperHasBall()) {
        player.FSM().changeState(PlayerChaseBall.state());
        return;
      }
    }
  }

  public void exit(BaseEntity owner) {
  }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}
