public class Team extends Artefact implements Constants {
  // Determines which side of the pitch is their side
  // LEFT = 0 # RIGHT = 1
  int pitchSide;
  Vector2D teamHeading;

  protected PApplet app;
  protected Pitch pitch;
  Goal goal;

  int[] teamColors;

  PlayerBase[] players = new PlayerBase[5];
  int nbrReachedTarget = 5;

  private PlayerBase controllingPlayer;
  private PlayerBase receivingPlayer;
  private PlayerBase supportingPlayer;
  private PlayerBase playerClosestToBall;

  double distToBallSq = Double.MAX_VALUE;

  double[] sspotScores = new double[30];
  final Vector2D[] sspotPositions;
  Vector2D bestSupportingPosition = null;

  long timeSinceSupportCalculated;

  Vector2D[] passes = new Vector2D[6];

  public boolean isInControl() {
    return controllingPlayer != null;
  }

  public void calculateClosestPlayerToBall() {
    distToBallSq = Double.MAX_VALUE;
    for (PlayerBase p : players) {
      double d2 = Vector2D.distSq(p.pos(), pitch.ball.pos());
      p.distToBallSq = d2;
      if (d2 < distToBallSq) {
        distToBallSq = d2;
        playerClosestToBall = p;
      }
    }
  }

  public void setControllingPlayer(PlayerBase player) {
    // If we are taking control then the opposing team are losing it.
    if (player != null) {
      getOtherTeam().controllingPlayer = null;
      //      getOtherTeam().teamLosesControl();
    }
    resetSupportSpotScores();
    controllingPlayer = player;
  }

  public PlayerBase getControllingPlayer() {
    return controllingPlayer;
  }

  /**
   * @return the receivingPlayer
   */
  public PlayerBase getReceivingPlayer() {
    return receivingPlayer;
  }

  /**
   * @param receivingPlayer the receivingPlayer to set
   */
  public void setReceivingPlayer(PlayerBase receivingPlayer) {
    this.receivingPlayer = receivingPlayer;
  }

  /**
   * @return the supportingPlayer
   */
  public PlayerBase getSupportingPlayer() {
    return supportingPlayer;
  }

  /**
   * @param supportingPlayer the supportingPlayer to set
   */
  public void setSupportingPlayer(PlayerBase supportingPlayer) {
    this.supportingPlayer = supportingPlayer;
  }

  /**
   * @return the playerClosestToBall
   */
  public PlayerBase getPlayerClosestToBall() {
    return playerClosestToBall;
  }

  /**
   * @param playerClosestToBall the playerClosestToBall to set
   */
  public void setPlayerClosestToBall(PlayerBase playerClosestToBall) {
    this.playerClosestToBall = playerClosestToBall;
  }

  public void resetSupportSpotScores() {
    for (int sp = 0; sp < sspotPositions.length; sp++)
      sspotScores[sp] = 1.0;
    // This will force a recalculation if a new sopporting player is calculated
    timeSinceSupportCalculated = -SupportSpotUpdateInterval;
  }

  public Vector2D getBestSupportngSpot() {
    long currTime = System.currentTimeMillis();
    // If we have not calculated the supporting spot 
    if (bestSupportingPosition == null || currTime - timeSinceSupportCalculated >= SupportSpotUpdateInterval) {
      bestSupportingPosition = new Vector2D();
      timeSinceSupportCalculated = currTime;
      double bestScoreSoFar = 0.0;
      for (int sp = 0; sp < sspotPositions.length; sp++) {
        sspotScores[sp] = 1.0;
        if (controllingPlayer != null) {
          // Is it safe to pass to this spot
          if (isPassSafeFromAllOpponents(controllingPlayer.pos(), sspotPositions[sp], null, MaxPassingForce))
            sspotScores[sp] += Spot_CanPassScore;
          // Is it possible to take a shot at goal from this position
          if (canShoot(controllingPlayer.pos(), MaxShootingForce, null))
            sspotScores[sp] += Spot_CanScoreFromPositionScore;
          // If we have a supporting player see how far away from supporting spot
          if (supportingPlayer != null) {
            final double optimalDistance = 200;
            double dist = Vector2D.dist(controllingPlayer.pos(), sspotPositions[sp]);
            double temp = Math.abs(optimalDistance - dist);
            if (temp < dist)
              sspotScores[sp] += Spot_DistFromControllingPlayerScore * (optimalDistance - temp) / optimalDistance;
          }
        }
        if (sspotScores[sp] > bestScoreSoFar) {
          bestScoreSoFar = sspotScores[sp];
          bestSupportingPosition.set(sspotPositions[sp]);
        }
      }
    }
    return bestSupportingPosition;
  }

  /**
   * Get the best supporting player
   * @return
   */
  public PlayerBase getBestSupportingAttacker() {
    double closestSoFar = Double.MAX_VALUE;
    PlayerBase bestPlayer = null;
    for (int i = 1; i < players.length; i++) {
      if (players[i].getPlayerType() == ATTACKER && players[i] != controllingPlayer) {
        double dist = Vector2D.distSq(players[i].pos(), getBestSupportngSpot());
        if (dist < closestSoFar) {
          closestSoFar = dist;
          bestPlayer = players[i];
        }
      }
    }
    return bestPlayer;
  }

  /**
   * Find the best pass target position and a receiver.
   * 
   * @param passer the player with the ball
   * @param passTarget the target position to calculate
   * @param power
   * @param minPassingDistance depends on whether a goalie or field player
   * @return null if can't find a good pass
   */
  public PlayerBase findBestPass(PlayerBase passer, Vector2D passTarget, double power, double minPassingDistance) {
    double closestToGoalSoFar = Double.MAX_VALUE;
    double minPassingDistanceSq = minPassingDistance * minPassingDistance;
    PlayerBase receiver = null;
    Vector2D target = new Vector2D();
    // Prevent  passing back to goalie from opponents half
    int startI = passer.isInHotHalf() ? 1 : 0; 
    for (int i = startI; i < players.length; i++) {
      if (getBestPassToReceiver(passer, players[i], target, power)) {
        if (players[i] != passer && Vector2D.distSq(passer.pos(), players[i].pos()) > minPassingDistanceSq) {
          double distToGoalLine = Math.abs(target.x - getOtherGoalCenter().x);
          if (distToGoalLine < closestToGoalSoFar) {
            closestToGoalSoFar = distToGoalLine;
            receiver = players[i];
            passTarget.set(target);
          }
        }
      }
    }
    return receiver;
  }

  /**
   * Calculates the best pass direction to another player. Returns true if it finds one
   * @param passer
   * @param receiver
   * @param passTarget
   * @param power
   * @return
   */
  private boolean getBestPassToReceiver(PlayerBase passer, PlayerBase receiver, Vector2D passTarget, double power) {
    // Get the receiver and ball position since we use these a lot
    Vector2D ballPos = pitch.ball.pos();
    Vector2D receiverPos = receiver.pos();
    // Calculate the time for the ball to reach receiver if he doesn't move
    double time = pitch.ball.timeToCoverDistance(ballPos, receiverPos, power);
    // If less than zero then can't reach
    if (time < 0) return false;

    // Calculate the intercept range for the receiver
    double interceptRange = time * receiver.maxSpeed();
    double scalingFactor = 0.3;
    interceptRange *= scalingFactor;

    // Calculate the intercept points either side of the receiver (returns 0/2/4 points
    double[] pts = Geometry2D.tangents_to_circle(ballPos.x, ballPos.y, receiverPos.x, receiverPos.y, interceptRange);
    int nbrPassesToTest = pts.length / 2 + 1;
    // Get an array of points to test
    switch(nbrPassesToTest) {
    case 1:
      passes[0] = receiverPos;
      break;
    case 2:
      passes[0] = new Vector2D(pts[0], pts[1]);
      passes[1] = receiverPos;
      break;
    case 3:
      passes[0] = new Vector2D(pts[0], pts[1]);
      passes[1] = new Vector2D(pts[2], pts[3]);
      passes[2] = receiverPos;
      break;
    default:
      System.out.println("Team: getBestPassToReceiver() error");
    }

    // this pass is the best found so far if it is:
    //
    //  1. Further upfield than the closest valid pass for this receiver
    //     found so far
    //  2. Within the playing area
    //  3. Cannot be intercepted by any opponents

    double closestSoFar = Float.MAX_VALUE;
    boolean bResult = false;

    for (int i = 0; i < nbrPassesToTest; i++) {
      double dist = Math.abs(passes[i].x - getOtherGoalCenter().x);
      if (dist < closestSoFar && pitch.isValidBallPos(passes[i]) &&
        isPassSafeFromAllOpponents(ballPos, passes[i], receiver, power)) {
        closestSoFar = dist;
        passTarget.set(passes[i]);
        bResult = true;
      }
    }
    return bResult;
  }

  private boolean isPassSafeFromOpponent(Vector2D from, Vector2D target, PlayerBase receiver, PlayerBase opp, double passingForce) {
    Vector2D toTarget = Vector2D.sub(target, from);
    toTarget.normalize();
    Vector2D perp = toTarget.getPerp();
    Vector2D localPosOpp = Transformations.pointToLocalSpace(opp.pos(), toTarget, perp, from);
    if (localPosOpp.x < 0)
      return true;

    if (Vector2D.distSq(from, target) < Vector2D.distSq(opp.pos(), from)) {
      if (receiver != null)
        return (Vector2D.distSq(target, opp.pos()) > Vector2D.distSq(target, receiver.pos()));
      else 
        return true;
    }
    double timeForBall = pitch.ball.timeToCoverDistance(Vector2D.ZERO, new Vector2D(localPosOpp.x, 0), passingForce);
    double reach = opp.maxSpeed() * timeForBall + pitch.ball.colRadius() + opp.colRadius();

    return (Math.abs(localPosOpp.y) >= reach);
  }

  private boolean isPassSafeFromAllOpponents(Vector2D from, Vector2D target, 
  PlayerBase receiver, double passingForce) {
    PlayerBase[] opponents = getOtherPlayers();
    for (PlayerBase opp : opponents) {
      if (!isPassSafeFromOpponent(from, target, receiver, opp, passingForce))
        return false;
    }
    return true;
  }

  public void requestPass(PlayerBase player) {
    // Only 10% chance of request sent
    if (Math.random() > 0.01 || controllingPlayer == null)
      return;
    if (isPassSafeFromAllOpponents(controllingPlayer.pos(), player.pos(), player, MaxPassingForce)) {
      Dispatcher.dispatch(0, player.ID(), controllingPlayer.ID(), PASS_TO_ME, player);
    }
  }

  boolean canShoot(Vector2D fromPos, double force, Vector2D shotTarget) {
    int numAttempts = NumAttemptsToFindValidStrike;

    Vector2D target = new Vector2D(pitchSide == 0 ? PITCH_LENGTH : 0, 0);
    double minY = (PITCH_WIDTH - GOAL_WIDTH)/2 + pitch.ball.colRadius();
    double maxY = (PITCH_WIDTH + GOAL_WIDTH)/2 - pitch.ball.colRadius();

    while (numAttempts-- > 0) {
      target.y = MathUtils.randomInRange(minY, maxY);
      double time = pitch.ball.timeToCoverDistance(fromPos, target, force);
      if (time >= 0 && isPassSafeFromAllOpponents(fromPos, target, null, force)) {
        if (shotTarget == null)
          shotTarget = new Vector2D(target);
        else
          shotTarget.set(target);
        return true;
      }
    }
    return false;
  }

  void addNoiseToKick(Vector2D ballPos, Vector2D ballTarget) {
    double displacement = (Math.PI - Math.PI*PlayerKickingAccuracy) * MathUtils.randomClamped();
    Vector2D toTarget = Vector2D.sub(ballTarget, ballPos);
    //      MathUtils.rotateVectorAboutOrigin(toTarget, displacement);
    toTarget = Transformations.vec2DRotateAroundOrigin(toTarget, displacement);
    toTarget.add(ballPos);
    ballTarget.set(toTarget);
  }


  public boolean isOpponentWithinRadius(Vector2D pos, double rad) {
    PlayerBase[] opponents = getOtherPlayers();
    double radSq = rad * rad;
    for (PlayerBase opp : opponents) {
      if (Vector2D.distSq(pos, opp.pos()) < radSq)
        return true;
    }
    return false;
  }

  public Team getOtherTeam() {
    return pitch.getTeam(1-pitchSide);
  }

  public PlayerBase[] getOtherPlayers() {
    return getOtherTeam().players;
  }

  public Vector2D getOwnGoalCenter() {
    return goal.centre;
  }

  public Vector2D getOtherGoalCenter() {
    return getOtherTeam().goal.centre;
  }

  public Goal getOtherGoal() {
    return getOtherTeam().goal;
  }

  /**
   * Change region to use depending on whether the team is attacking or defending.
   * It does not order the players back to these regions. 
   * @param stateID
   */
  public void changePlayerHomePositions(int stateID) {
    for (PlayerBase p : players)
      p.changePlayerHomePositions(stateID);
  }

  public void sendTeamOffPitch() {
    controllingPlayer = null;
    playerClosestToBall = null;
    receivingPlayer = null;
    supportingPlayer = null;
    nbrReachedTarget = 0;
    FSM().changeState(TeamDefending.state());

    for (int i = 0; i < players.length; i++) {
      players[i].FSM().changeState(PlayerLeavesPitch.state());
      //Dispatcher.dispatch( (i == 0) ? 500 : 0, entityID, players[i].ID(), GO_OFF_PITCH);
    }
  }

  public void sendFieldPlayersToRegion() {
    for (int i = 1; i < players.length; i++) {
      players[i].FSM().changeState(PlayerReturnToRegion.state());
    }
  }

  public boolean allPlayersReachedTarget() {
    return nbrReachedTarget == players.length;
  }

  public Team(PApplet papp, Pitch pitch, World world, int pitch_side) {
    addFSM();

    this.pitchSide = pitch_side;
    this.app = papp;
    this.pitch = pitch;

    timeSinceSupportCalculated = System.currentTimeMillis();
    sspotPositions = pitch.sspots[pitchSide];
    //      sspotPositions = pitch.getSupportingSpots(pitchSide);
    for (int sp = 0; sp < sspotPositions.length; sp++)
      sspotScores[sp] = 1;

    // Calculate the team heading 
    int dir = (1 - 2 * pitchSide);
    teamHeading = new Vector2D(dir, 0);

    // Create goal
    goal = new Goal(papp, this, pitch, pitch_side);
    goal.Z(-5);
    world.add(goal);

    //      // Create players
    int x = (int) (PITCH_LENGTH/2 - dir * 20);
    players[0] = new GoalKeeper("0", 
    new Vector2D(x, PITCH_WIDTH + 10), // Position
    PLAYER_RADIUS, // Collision radius
    new Vector2D(0, 0), // Velocity
    KeeperMaxSpeedWithoutBall, // Maximum velocity
    offPitchHeading, // Heading
    PLAYER_MASS, // Mass
    PlayerMaxTurnRate, // Max turn rate
    PlayerMaxForce, // Max force
    pitchSide, // 0 or 1
    this, // the players team
    0                  // player position in team
    );

    for (int i = 1; i < players.length; i++)
      players[i] = new FieldPlayer("" + i, 
      new Vector2D(x, PITCH_WIDTH + 15 + i * 50), // Position
      PLAYER_RADIUS, // Collision radius
      new Vector2D(0, 0), // Velocity
      PlayerMaxSpeedWithoutBall, // Maximum velocity
      offPitchHeading, // Heading
      PLAYER_MASS, // Mass
      PlayerMaxTurnRate, // Max turn rate
      PlayerMaxForce, // Max force
      pitchSide, // 0 or 1
      this, // the players team
      i                  // player position in team
      );
    for (int i = 0; i < players.length; i++) {
      world.add(players[i]);
    }
    FSM().setGlobalState(TeamGlobal.state());
    FSM().setCurrentState(TeamDefending.state());
    world.add(this);
  }

  public void setTeamColors(int[] cols) {
    teamColors = cols;
    PersonPic ppic = new PersonPic(app, 2 * PLAYER_RADIUS, teamColors[1], teamColors[2], teamColors[0], 1);
    for (int i = 0; i < players.length; i++)
      players[i].renderer(ppic);
  }

  public void drawSupportingSpots() {
    if (controllingPlayer != null) {
      app.noStroke();
      app.fill(teamColors[3]);
      for (int sp = 0; sp < sspotPositions.length; sp++)
        app.ellipse((float)sspotPositions[sp].x, (float)sspotPositions[sp].y, 
        2+(float)sspotScores[sp], 2+(float)sspotScores[sp]);
    }
  }
}