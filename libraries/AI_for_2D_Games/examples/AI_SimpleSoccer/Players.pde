public abstract class PlayerBase extends Vehicle implements Constants {

  // These are fixed for the duration
  final Team team;
  final int type;
  final int number;

  // These need to be changed if the pitch side changes
  int defendRegion;
  int attackRegion;
  int regionToUse;
  // this also depends on pitch side
  Vector2D offFieldPosition = new Vector2D();

  Vector2D targetPos = new Vector2D();

  double distToBallSq = Double.MAX_VALUE;

  public boolean isControllingPlayer() {
    return this == team.getControllingPlayer();
  }

  public boolean isBallWithinReceivingRange() {
    return Vector2D.distSq(pos, team.pitch.ball.pos()) < BallWithinReceivingRangeSq;
  }

  public boolean isBallWithinKickingRange() {
    return(Vector2D.distSq(pos, team.pitch.ball.pos()) <= PlayerKickingDistanceSq);
  }

  public boolean isClosestTeamMemberToBall() {
    return this == team.getPlayerClosestToBall();
  }

  public boolean isPositionInFrontOfPlayer(Vector2D p) {
    Vector2D toSubject = Vector2D.sub(p, pos);
    return toSubject.dot(heading) > 0;
  }

  public boolean isThreatened() {
    PlayerBase[] opponents = team.getOtherPlayers();
    for (PlayerBase opponent : opponents) {
      if (isPositionInFrontOfPlayer(opponent.pos()) &&
        Vector2D.distSq(pos, opponent.pos()) < PlayerComfortZoneSq)
        return true;
    }
    return false;
  }

  public void findSupport() {
    // If there is no current supporting player get one.
    if (team.getSupportingPlayer() == null) {
      team.setSupportingPlayer(team.getBestSupportingAttacker());
      Dispatcher.dispatch(0, entityID, team.getSupportingPlayer().entityID, SUPPORT_ATTACKER);
    }
    else { // We have a supporting player See if we have have a new best supporting player
      PlayerBase bsp = team.getBestSupportingAttacker();
      // is this a new supporting player
      if (bsp != null && bsp != team.getSupportingPlayer()) {
        // Send old supporting player home
        Dispatcher.dispatch(0, entityID, team.getSupportingPlayer().entityID, GO_TO_REGION);
        //Send message for player to take over supporting position
        Dispatcher.dispatch(0, entityID, bsp.entityID, SUPPORT_ATTACKER);
        // Set new supporting player and
        team.setSupportingPlayer(bsp);
      }
    }
  }

  // Is in the opponents third of the pitch
  public boolean isInHotRegion() {
    return Math.abs(team.goal.centre.x - pos.x) > PITCH_LENGTH * 0.60;
  }

  public boolean isInHotHalf() {
    return Math.abs(team.goal.centre.x - pos.x) > PITCH_LENGTH * 0.45;
  }

  public boolean isAheadOfAttacker() {
    if (team.getControllingPlayer() == null)
      return false;
    double goalx = team.getOtherGoalCenter().x;
    return Math.abs(pos.x - goalx) < Math.abs(team.getControllingPlayer().pos().x - goalx);
  }

  public boolean isClosestPlayerOnPitchToBall() {
    return (this == team.getPlayerClosestToBall() && distToBallSq < team.getOtherTeam().distToBallSq);
  }

  public boolean isAtTarget() {
    return (Vector2D.distSq(pos, targetPos) <  PlayerAtTargetRangeSq);
  }

  public boolean isNearTarget() {
    return (Vector2D.distSq(pos, this.targetPos) <  PlayerNearTargetRangeSq);
  }

  public void trackBall() {
    Vector2D ballPos = team.pitch.ball.pos();
    headingAtRest(Vector2D.sub(ballPos, pos));
  }

  public abstract boolean isGoalKeeper();

  public abstract boolean isFieldPlayer();

  public void changePlayerHomePositions(int stateID) {
    if (stateID == DEFENDING)
      regionToUse = defendRegion;
    else
      regionToUse = attackRegion;
  }

  public int getPlayerType() {
    return type;
  }

  public PlayerBase(String name, Vector2D position, double radius, 
  Vector2D velocity, double max_speed, Vector2D heading, double mass, 
  double max_turn_rate, double max_force, int side, Team team, int playerNumber) {

    super(name, position, radius, velocity, max_speed, heading, mass, 
    max_turn_rate, max_force);
    addFSM();
    this.team = team;
    this.number = playerNumber;
    defendRegion = team.pitch.getRegionID(side, number, DEFENDING_STATE);
    attackRegion = team.pitch.getRegionID(side, number, ATTACKING_STATE);

    switch(number) {
    case 0:
      type = GOALKEEPER;
      break;
    case 1:
    case 2:
      type = DEFENDER;
      break;
    default:
      type = ATTACKER;
    }
    offFieldPosition.set(position);
    headingAtRest(heading);
    FSM().setCurrentState(PlayerWait.state());
    targetPos.set(position);
    AP(new AutoPilot());
  }
}


public class GoalKeeper extends PlayerBase implements Constants {

  Vector2D rearInterposeTarget = new Vector2D();

  public GoalKeeper(String name, Vector2D position, double radius, 
  Vector2D velocity, double max_speed, Vector2D heading, double mass, 
  double max_turn_rate, double max_force, int side, Team team, int playerNumber) {

    super(name, position, radius, velocity, max_speed, heading, mass, 
    max_turn_rate, max_force, side, team, playerNumber);
    FSM().setGlobalState(KeeperGlobal.state());
  }

  public boolean isBallWithinRange() {
    return Vector2D.distSq(pos, team.pitch.ball.pos()) <= KeeperInBallRangeSq;
  }

  public boolean isBallWithinInterceptRange() {
    return Vector2D.distSq(team.getOwnGoalCenter(), team.pitch.ball.pos()) <= GoalKeeperInterceptRangeSq;
  }

  public boolean tooFarFromGoalMouth() {
    return Vector2D.distSq(pos, getRearInterposeTarget()) > GoalKeeperInterceptRangeSq;
  }

  public Vector2D getRearInterposeTarget() {
    double x = team.goal.centre.x;
    double y = (PITCH_WIDTH - GOAL_WIDTH)/2 + (team.pitch.ball.pos().y * GOAL_WIDTH / PITCH_WIDTH);
    rearInterposeTarget.set(x, y);
    return rearInterposeTarget;
  }

  public boolean isGoalKeeper() {
    return true;
  }

  public boolean isFieldPlayer() {
    return false;
  }
}


public class FieldPlayer extends PlayerBase {

  public FieldPlayer(String name, Vector2D position, double radius, 
  Vector2D velocity, double max_speed, Vector2D heading, double mass, 
  double max_turn_rate, double max_force, int side, Team team, int playerNumber) {

    super(name, position, radius, velocity, max_speed, heading, mass, 
    max_turn_rate, max_force, side, team, playerNumber);
    FSM().setGlobalState(PlayerGlobal.state());
  }

  public boolean isGoalKeeper() {
    return false;
  }

  public boolean isFieldPlayer() {
    return true;
  }
}
