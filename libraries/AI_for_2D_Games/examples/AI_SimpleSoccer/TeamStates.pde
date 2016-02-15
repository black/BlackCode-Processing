public static class TeamAttacking extends State implements Constants {

  private static TeamAttacking instance = null;

  public static TeamAttacking state() {
    if (instance == null) {
      instance = new TeamAttacking();
      instance.name = "Attacking";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    Team team = (Team) owner;
    team.changePlayerHomePositions(ATTACKING);
    PlayerBase[] players = team.players;
    for (int i = 1; i < players.length; i++) {
      if (players[i].FSM().isCurrentState(PlayerWait.state()))
        players[i].FSM().changeState(PlayerReturnToRegion.state());
    }
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    Team team = (Team) owner;
    if (!team.isInControl())
      team.FSM().changeState(TeamDefending.state());
  }

  public void exit(BaseEntity owner) { }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}



public static class TeamDefending extends State implements Constants {

  private static TeamDefending instance = null;

  public static TeamDefending state() {
    if (instance == null) {
      instance = new TeamDefending();
      instance.name = "Defending";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    Team team = (Team) owner;
    team.changePlayerHomePositions(DEFENDING);
    PlayerBase[] players = team.players;
    for (int i = 1; i < players.length; i++) {
      if (players[i].FSM().isCurrentState(PlayerWait.state()))
        players[i].FSM().changeState(PlayerReturnToRegion.state());
    }
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    Team team = (Team) owner;
    if (team.isInControl())
      team.FSM().changeState(TeamAttacking.state());
  }

  public void exit(BaseEntity owner) { }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}



public static class TeamGlobal extends State implements Constants {

  private static TeamGlobal instance = null;

  public static TeamGlobal state() {
    if (instance == null)
      instance = new TeamGlobal();
    return instance;
  }


  public void enter(BaseEntity owner) { }


  public void execute(BaseEntity owner, double deltaTime, World world) {
    Team team = (Team) owner;
    if (team.pitch.ballInPlay) {
      team.calculateClosestPlayerToBall();
    }
  }

  public void exit(BaseEntity owner) { }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    Team team = (Team) owner;
    switch(tgram.msg) {
    case AT_TARGET:
      if (team.nbrReachedTarget == team.players.length)
        team.nbrReachedTarget = 1;
      else
        team.nbrReachedTarget++;
      break;
    }
    return false;
  }
}



public static class TeamPrepareForKickOff extends State implements Constants {

  private static TeamPrepareForKickOff instance = null;

  public static TeamPrepareForKickOff state() {
    if (instance == null) {
      instance = new TeamPrepareForKickOff();
      instance.name = "Prepare for kickoff";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    Team team = (Team) owner;
    team.setControllingPlayer(null);
    team.setPlayerClosestToBall(null);
    team.setReceivingPlayer(null);
    team.setSupportingPlayer(null);
    team.nbrReachedTarget = 0;
    for (PlayerBase p : team.players) {
      Dispatcher.dispatch(0, team.ID(), p.ID(), PLAYER_PREPARE_FOR_KICKOFF);
    }
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    Team team = (Team) owner;
    if (team.allPlayersReachedTarget()) {
      team.FSM().changeState(TeamDefending.state());
    }
  }

  public void exit(BaseEntity owner) { }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}



public static class KeeperGlobal extends State implements Constants {

  private static KeeperGlobal instance = null;

  public static KeeperGlobal state() {
    if (instance == null) {
      instance = new KeeperGlobal();
      instance.name = "Keeper global";
    }
    return instance;
  }

  public void enter(BaseEntity owner) { }

  public void execute(BaseEntity owner, double deltaTime, World world) { }

  public void exit(BaseEntity owner) { }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    GoalKeeper keeper = (GoalKeeper) owner;
    switch(tgram.msg) {
    case GO_TO_REGION:
      keeper.FSM().changeState(PlayerReturnToRegion.state());
      return true;
    case RECEIVE_BALL:
      keeper.FSM().changeState(KeeperInterceptBall.state());
      return true;
    case PLAYER_PREPARE_FOR_KICKOFF:
      keeper.FSM().changeState(PlayerPrepareForKickOff.state());
      return true;
    case TEND_GOAL:      
      keeper.FSM().changeState(KeeperTendGoal.state());
      break;
    }
    return false;
  }
}
