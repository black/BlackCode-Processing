static public class PitchGameOn extends State implements Constants {

  private static PitchGameOn instance = null;  

  public static PitchGameOn state() {
    if (instance == null) {
      instance = new PitchGameOn();
      instance.name = "Game On";
    }
    return instance;
  }


  public void enter(BaseEntity owner) {
    Pitch pitch = (Pitch) owner;
    pitch.ballInPlay = true;
  }

  public void execute(BaseEntity owner, double deltaTime, World world) { }

  public void exit(BaseEntity owner) {
    Pitch pitch = (Pitch) owner;
    pitch.ballInPlay = false;
  }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    Pitch pitch = (Pitch) owner;
    switch(tgram.msg) {
    case TEAMS_PREPARE_FOR_KICK_OFF:
      pitch.FSM().changeState(PitchPrepareForKickOff.state());
      return true;
    case GOAL_SCORED:
      int teamToScore = (Integer) tgram.extraInfo[0];
      pitch.teamScore[teamToScore]++;
      pitch.FSM().changeState(PitchPrepareForKickOff.state());
    }
    return false;
  }
}


static public class PitchGlobal extends State implements Constants {

  private static PitchGlobal instance = null;  

  public static PitchGlobal state() {
    if (instance == null) {
      instance = new PitchGlobal();
      instance.name = "Game On";
    }
    return instance;
  }

  public void enter(BaseEntity owner) { }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    Pitch pitch = (Pitch) owner;
    if (pitch.sw.getRunTime() >= MATCH_TIME) {
      pitch.sw.reset();
      pitch.FSM().changeState(PitchPlayersOff.state());
    }
  }

  public void exit(BaseEntity owner) { }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    return false;
  }
}

public static class PitchPlayersOff extends State implements Constants {

  private static PitchPlayersOff instance = null;  

  public static PitchPlayersOff state() {
    if (instance == null) {
      instance = new PitchPlayersOff();
      instance.name = "Players Off";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    Pitch pitch = (Pitch) owner;
    pitch.ballInPlay = false;
    pitch.playersOffPitch = true;
    pitch.teams[0].sendTeamOffPitch();
    pitch.teams[1].sendTeamOffPitch();
    pitch.ball.toCentreSpot(PITCH_LENGTH/2, PITCH_WIDTH/2);
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    Pitch pitch = (Pitch) owner;
    if (pitch.teams[0].allPlayersReachedTarget() && pitch.teams[1].allPlayersReachedTarget()) {
      // Make sure we only do this once
      pitch.teams[0].nbrReachedTarget = 0;
      pitch.teams[1].nbrReachedTarget = 0;

      pitch.ball.toCentreSpot(PITCH_LENGTH/2, PITCH_WIDTH/2);
      // Select 2 new teams for the next match
      pitch.newTeams();
      Dispatcher.dispatch(2000, pitch.ID(), pitch.ID(), TEAMS_PREPARE_FOR_KICK_OFF);
    }
  }


  public void exit(BaseEntity owner) { }


  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    Pitch pitch = (Pitch) owner;
    switch(tgram.msg) {
    case TEAMS_PREPARE_FOR_KICK_OFF:
      pitch.FSM().changeState(PitchPrepareForKickOff.state());
      return true;
    }
    return false;
  }
}


public static class PitchPrepareForKickOff extends State implements Constants {

  private static PitchPrepareForKickOff instance = null;  

  public static PitchPrepareForKickOff state() {
    if (instance == null) {
      instance = new PitchPrepareForKickOff();
      instance.name = "Prepare for kickoff";
    }
    return instance;
  }

  public void enter(BaseEntity owner) {
    Pitch pitch = (Pitch) owner;
    pitch.ballInPlay = false;
    pitch.teams[0].FSM().changeState(TeamPrepareForKickOff.state());
    pitch.teams[1].FSM().changeState(TeamPrepareForKickOff.state());
    pitch.ball.toCentreSpot(PITCH_LENGTH/2, PITCH_WIDTH/2);
  }

  public void execute(BaseEntity owner, double deltaTime, World world) {
    Pitch pitch = (Pitch) owner;
    if (pitch.teams[0].allPlayersReachedTarget() && pitch.teams[1].allPlayersReachedTarget()) {
      Dispatcher.dispatch(1000, pitch.ID(), pitch.ID(), TEAMS_READY_FOR_KICK_OFF);
    }
  }

  public void exit(BaseEntity owner) {
  }

  public boolean onMessage(BaseEntity owner, Telegram tgram) {
    Pitch pitch = (Pitch) owner;
    switch(tgram.msg) {
    case TEAMS_READY_FOR_KICK_OFF:
      if (pitch.FSM().isPreviousState(PitchPlayersOff.state()))
        pitch.sw.reset();
      pitch.playersOffPitch = false;
      pitch.FSM().changeState(PitchGameOn.state());
      return true;
    }
    return false;
  }
}
