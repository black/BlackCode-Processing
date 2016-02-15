public interface Constants {

  // Player messages
  int RECEIVE_BALL         = 101;
  int PASS_TO_ME          = 102;
  int SUPPORT_ATTACKER      = 103;
  int PLAYER_PREPARE_FOR_KICKOFF  = 104;
  int GO_TO_REGION        = 105;
  int TEND_GOAL          = 107;
  int AT_TARGET          = 108;
  int WAIT            = 199;

  // Team messages;
  int PREPARE_FOR_KICKOFF      = 201;
  int DEFENDING          = 202;
  int ATTACKING          = 203;

  // Pitch messages
  int TEAMS_PREPARE_FOR_KICK_OFF  = 301;
  int TEAMS_READY_FOR_KICK_OFF  = 302;
  int GOAL_SCORED          = 303;
  int STOP_GAME          = 304;

  // State IDs
  int PREPARE_FOR_KICKOFF_STATE  = 901;
  int DEFENDING_STATE        = 902;
  int ATTACKING_STATE        = 903;

  int GOALKEEPER          = 1001;
  int ATTACKER          = 1002;
  int DEFENDER          = 1003;

  float BALL_RADIUS        = 4.5f;
  double BALL_MASS        = 1.0;

  float PLAYER_RADIUS        = 5.5f;
  double PLAYER_MASS        = 1.0;


  float PITCH_LENGTH        = 600;
  float PITCH_WIDTH         = 300;
  float GOAL_WIDTH        = 100;
  float GOAL_DEPTH        = 30;
  float GOAL_HIGH_Y        = (PITCH_WIDTH + GOAL_WIDTH)/2 - BALL_RADIUS;
  float GOAL_LOW_Y        = (PITCH_WIDTH - GOAL_WIDTH)/2 + BALL_RADIUS;
  float KEEPER_TEND_DIST      = GOAL_DEPTH/2;

  float MATCH_TIME        = 120;
  float TIMER_LENGTH        = 200;

  //these values tweak the various rules used to calculate the support spots
  double Spot_CanPassScore            = 2.0;
  double Spot_CanScoreFromPositionScore      = 1.0;
  double Spot_DistFromControllingPlayerScore    = 2.0;
  //    double Spot_ClosenessToSupportingPlayerScore   = 0.1;
  //    double Spot_AheadOfAttackerScore        = 1.0;

  // The maximum time between recalculation of the support spots
  long SupportSpotUpdateInterval          = 1000;

  //the chance a player might take a random pot shot at the goal
  double ChancePlayerAttemptsPotShot        = 0.05;

  //this is the chance that a player will receive a pass using the arrive
  //steering behaviour, rather than Pursuit
  double ChanceOfUsingArriveTypeReceiveBehavior  = 0.5;


  double FRICTION_MAG                = -15.0;
  double FRICTION_MAG_SQ              = FRICTION_MAG * FRICTION_MAG;

  // The keeper has to be this close to the ball to be able to interact with it
  double KeeperInBallRange            = PLAYER_RADIUS + BALL_RADIUS; // was 10
  double KeeperInBallRangeSq            = KeeperInBallRange * KeeperInBallRange;
  double PlayerNearTargetRange          = 20.0;
  double PlayerNearTargetRangeSq          = PlayerNearTargetRange * PlayerNearTargetRange;
  double PlayerAtTargetRange            = 1.0;
  double PlayerAtTargetRangeSq          = PlayerAtTargetRange * PlayerAtTargetRange;

  // Player has to be this close to the ball to be able to kick it. The higher
  // the value this gets, the easier it gets to tackle. 
  double PlayerKickingDistance          = PLAYER_RADIUS + BALL_RADIUS + 2; // 12.0;
  double PlayerKickingDistanceSq          = PlayerKickingDistance * PlayerKickingDistance;

  // The minimum time (milliseconds) allowed between kicks by the same player
  long PlayerKickInterval              = 125;

  float PlayerMaxForce              = 1000;
  double PlayerMaxSpeedWithBall          = 36; // 30
  double PlayerMaxSpeedWithoutBall        = 66; // 60
  double KeeperMaxSpeedWithoutBall        = 45; // 60
  double PlayerMaxTurnRate            = 7.0;

  //when an opponents comes within this range the player will attempt to pass
  //the ball. Players tend to pass more often, the higher the value
  double PlayerComfortZone            = 50.0;
  double PlayerComfortZoneSq            = PlayerComfortZone * PlayerComfortZone;

  //in the range zero to 1.0. adjusts the amount of noise added to a kick,
  //the lower the value the worse the players get.
  double PlayerKickingAccuracy          = 0.95;

  //the number of times the SoccerTeam::CanShoot method attempts to find
  //a valid shot
  int NumAttemptsToFindValidStrike        = 5;

  // Forces that can be applied when kicking the ball
  double MaxShootingForce              = 220;
  double BaseForce                = 180;
  double MaxPassingForce              = BaseForce;
  double MaxDribbleForce              = BaseForce/4.6;
  double MaxDribbleTurnForce            = BaseForce/5;

  //the minimum distance a receiving player must be from the passing player
  double FielderMinPassDistance          = 120;
  //the minimum distance a player must be from the goalkeeper before it will
  //pass the ball
  double KeeperMinPassDistance          = 60.0;
  // Pass threat distance
  double PassThreatDistance            = 70.0;

  // when the ball becomes within this distance of the goalkeeper he
  // changes state to intercept the ball
  double GoalKeeperInterceptRange          = 80.0;
  double GoalKeeperInterceptRangeSq        = GoalKeeperInterceptRange * GoalKeeperInterceptRange;

  //how close the ball must be to a receiver before he starts chasing it
  double BallWithinReceivingRange          = 50.0;
  double BallWithinReceivingRangeSq        = BallWithinReceivingRange * BallWithinReceivingRange;

  Vector2D offPitchHeading = new Vector2D(0, -1);
}
