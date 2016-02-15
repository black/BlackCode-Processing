public class Pitch extends Artefact implements Constants {

  World world;
  PApplet app;
  PFont font0, font1;

  float length = (float) PITCH_LENGTH;
  float width = (float) PITCH_WIDTH;

  StopWatch sw = new StopWatch();

  Ball ball;

  Team[] teams = new Team[2];
  int[][] teamColors;
  String[] teamNames;
  float[] nameWidths;
  int[] teamScore = new int[2];
  int[] teamColorIdx = {
    -1, -1
  };

  boolean showSpots = false;

  // Pitch regions for players and region IDs for each team
  Domain[] regions;
  int[][] playerRegionIDs = { 
    { 
      1, 3, 6, 5, 8, 1, 4, 12, 8, 14
    }
    , // pitch side 0
    { 
      16, 12, 9, 14, 11, 16, 9, 3, 13, 5
    }  // pitch side 1
  };

  // Supporting spots on pitch
  Vector2D[][] sspots = new Vector2D[2][30];

  boolean ballInPlay = false;
  boolean playersOffPitch = true;

  public Pitch(PApplet papp, World world, float length, float width) {
    super(null, null, length, width);
    app = papp;
    font0 = app.createFont("Sans Serif", 24);
    font1 = app.createFont("Sans Serif", 18);
    this.world = world;

    addFSM();
    createRegions();
    createSupportingSpotPositions();

    teamColors = new int[6][];
    teamColors[0] = new int[] { 
      color(255, 0, 0), color(255, 200, 200), color(96, 0, 0), color(128, 0, 0)
    };
    teamColors[1] = new int[] { 
      color(0, 0, 255), color(200, 200, 255), color(0, 0, 96), color(0, 0, 128)
    };
    teamColors[2] = new int[] { 
      color(255, 0, 255), color(255, 200, 255), color(96, 0, 96), color(128, 0, 128)
    };
    teamColors[3] = new int[] { 
      color(255, 128, 0), color(255, 192, 64), color(166, 82, 0), color(238, 118, 0)
    };
    teamColors[4] = new int[] { 
      color(0, 255, 255), color(200, 255, 255), color(0, 96, 96), color(0, 128, 128)
    };
    teamColors[5] = new int[] { 
      color(230), color(190), color(96), color(128)
    };

    teamNames = new String[] {
      "Red Socks", "Blue Bottles", "Purple Hearts", "Orange Pips", "Cyan Eyeds", "Grey Beards"
    };
    nameWidths = new float[teamNames.length];
    app.textFont(font0);
    for (int i = 0; i < teamNames.length; i++)
      nameWidths[i] = app.textWidth(teamNames[i]);

    createBall();
    world.add(ball);

    teams[0] = new Team(app, this, world, 0);
    teams[1] = new Team(app, this, world, 1);

    newTeams();

    // Create the renderer for the pitch
    renderer(new PitchRenderer(app, "grassb.jpg"));

    addFSM();
    FSM().setGlobalState(PitchGlobal.state());
    world.add(this);
  }

  /**
   * Make sure we have two different teams
   */
  public void newTeams() {
    int t0 = teamColorIdx[0];
    int t1 = teamColorIdx[1];
    int nt0, nt1;
    do {
      nt0 = (int) app.random(teamColors.length);
    } 
    while (nt0 == t0 || nt0 == t1);
    do {
      nt1 = (int) app.random(teamColors.length);
    } 
    while (nt1 == nt0 || nt1 == t0 || nt1 == t1);
    teamColorIdx[0] = nt0;
    teamColorIdx[1] = nt1;
    teams[0].setTeamColors(teamColors[teamColorIdx[0]]);
    teams[1].setTeamColors(teamColors[teamColorIdx[1]]);
    teamScore[0] = teamScore[1] = 0;
  }

  private void createRegions() {
    regions = new Domain[18];
    double sizex = PITCH_LENGTH/6;
    double sizey = PITCH_WIDTH/3;
    double px, py;
    for (int i = 0; i < regions.length; i++) {
      px = (i/3) * sizex;
      py = (i%3) * sizey;
      regions[i] = new Domain(px, py, px+sizex, py+sizey);
    }
  }

  public Domain getRegion(int nbr) {
    return regions[nbr];
  }

  public int getRegionID(int side, int playerNo, int teamState) {
    int p = (teamState == ATTACKING_STATE) ? 5 : 0;
    return playerRegionIDs[side][playerNo + p];
  }

  private void createSupportingSpotPositions() {
    double deltaX = PITCH_LENGTH * 0.5 * 0.12;
    double deltaY = PITCH_WIDTH * 0.12;
    double startX = PITCH_LENGTH * 0.5 * 0.26;
    double startY = PITCH_WIDTH * 0.20;
    for (int sp = 0; sp < sspots[0].length; sp++) {
      int r = sp / 5;
      int c = sp % 5;
      sspots[1][sp] = new Vector2D(startX + c * deltaX, startY + r * deltaY);
      sspots[0][sp] = new Vector2D(PITCH_LENGTH - sspots[1][sp].x, PITCH_WIDTH - sspots[1][sp].y);
    }
  }

  public Team getTeam(int pitch_side) {
    return teams[pitch_side];
  }

  public boolean isValidBallPos(Vector2D p) {
    double br = ball.colRadius();
    return p.x - br > 0 && p.x + br < PITCH_LENGTH && p.y - br > 0 && p.y + br < PITCH_WIDTH;
  }

  private void createBall() {
    ball = new Ball(this);
    ball.renderer(new CirclePic(app, 2 * BALL_RADIUS, color(0), color(255), 1.2f));
    ball.Z(-2);
    ball.toCentreSpot(PITCH_LENGTH/2, PITCH_WIDTH/2);
    ball.toCentreSpot(PITCH_LENGTH/2, PITCH_WIDTH/2);
    world.add(ball);
  }

  // Called from Game class
  public void setTeamColors(int team, int col) {
    teamColorIdx[team] = col;
    teams[team].setTeamColors(teamColors[col]);
  }

  /**
   * 
   * The pitch renderer implemented as an inner class.
   * @author Peter Lager
   *
   */
  public class PitchRenderer extends PicturePS implements Constants {
    PImage grass;
    int lineCol;
    float lineWidth;

    public PitchRenderer(PApplet papp, String imgFilename) {
      super(papp);
      grass = app.loadImage(imgFilename);
      lineCol = color(255, 255, 255);
      lineWidth = 3;
    }


    public void draw(BaseEntity owner, float posX, float posY, float velX, 
    float velY, float headX, float headY, float etime) {
      app.pushStyle();
      app.pushMatrix();
      app.rectMode(PApplet.CORNER);
      app.ellipseMode(PApplet.CENTER);
      app.image(grass, 0, 0);
      app.noFill();
      app.stroke(lineCol);
      app.strokeWeight(lineWidth);
      app.rect(0, 0, PITCH_LENGTH, PITCH_WIDTH);
      app.fill(lineCol);
      app.ellipse(PITCH_LENGTH/2, PITCH_WIDTH/2, 5, 5);
      app.noFill();
      app.ellipse(PITCH_LENGTH/2, PITCH_WIDTH/2, 160, 160);
      // Goals
      app.line(PITCH_LENGTH/2, 0, PITCH_LENGTH/2, PITCH_WIDTH);
      app.rect(0, GOAL_WIDTH, GOAL_DEPTH, GOAL_WIDTH);
      app.rect(PITCH_LENGTH - GOAL_DEPTH, GOAL_WIDTH, 30, GOAL_WIDTH);

      app.fill(teamColors[teamColorIdx[0]][0]);
      app.textFont(font0);
      app.text(teamNames[teamColorIdx[0]], 0, -30);
      app.textFont(font1);
      app.text(teamScore[0], 50, -8);

      app.fill(teamColors[teamColorIdx[1]][0]);
      app.textFont(font0);
      app.text(teamNames[teamColorIdx[1]], (float) (PITCH_LENGTH - nameWidths[teamColorIdx[1]]), -30);
      app.textFont(font1);
      app.text(teamScore[1], (float) (PITCH_LENGTH) - 50 - app.textWidth(""+ teamScore[1]), -8);

      if (showSpots)
        for (Team t : teams)
          t.drawSupportingSpots();

      String clockText;
      if (!playersOffPitch) {
        app.noStroke();
        app.fill(255);
        float gtime = (float) sw.getRunTime();
        float d = TIMER_LENGTH  - TIMER_LENGTH * gtime / MATCH_TIME;
        app.rect((PITCH_LENGTH - TIMER_LENGTH)/2, -54, d, 7);
        app.noFill();
        app.stroke(0);
        app.strokeWeight(1);
        app.rect((PITCH_LENGTH - TIMER_LENGTH)/2, -54, TIMER_LENGTH, 7);

        int mins = (int) (gtime / 60);
        float secs = gtime - 60 * mins;
        clockText = PApplet.nf(mins, 2) + ":" + PApplet.nf(secs, 2, 1);
      }
      else
        clockText = "GAME OVER";

      app.fill(0);
      app.text(clockText, (float) (PITCH_LENGTH - app.textWidth(clockText))/2, -26);

      app.popMatrix();
      app.popStyle();
    }
  }
}


public class Goal extends Artefact implements Constants {

  public final Vector2D centre;

  public Goal(PApplet papp, Team team, Pitch pitch, int pitchSide) {
    double x = (pitchSide == 0) ? KEEPER_TEND_DIST : PITCH_LENGTH - KEEPER_TEND_DIST;
    double y = PITCH_WIDTH/2;
    pos.set(x, y);
    centre = new Vector2D(x, y);

    this.renderer( new GoalRenderer(papp, team, GOAL_WIDTH, GOAL_DEPTH));
  }

  public class GoalRenderer extends PicturePS {

    Team team;
    float depth = 0;
    float width = 0;

    public GoalRenderer(PApplet papp, Team team, double width, double depth) {
      super(papp);
      this.team = team;
      this.width = (float) width;
      this.depth = (float) depth;
    }

    public void draw(BaseEntity owner, float posX, float posY, float velX, 
    float velY, float headX, float headY, float etime) {
      app.pushStyle();
      app.pushMatrix();
      app.translate(posX, posY);

      app.rectMode(PApplet.CENTER);

      app.noFill();
      app.stroke(team.teamColors[0]);
      app.strokeWeight(2.0f);
      app.rect(0, 0, depth, width);

      app.popMatrix();
      app.popStyle();
    }
  }
}


public class Ball extends MovingEntity implements Constants {

  Pitch pitch;
  PlayerBase owner = null;

  PlayerBase lastkickedBy = null;
  long lastKickedAt = 0;

  double lowGoalY, highGoalY;

  public Ball(Pitch pitch) {
    super("ball", 
    new Vector2D(PITCH_LENGTH/2, PITCH_WIDTH/2), // Position on pitch
    BALL_RADIUS, // Collision radius
    new Vector2D(0, 0), // Velocity
    Float.MAX_VALUE, // Maximum velocity
    new Vector2D(0, 1), // Heading
    BALL_MASS, // Ball mass
    1, // Max turning rate
    Float.MAX_VALUE                  // Maximum force
    );
    this.pitch = pitch;
  }

  public void kick(PlayerBase kicker, Vector2D dir, double force) {
    lastkickedBy = kicker;
    lastKickedAt = System.currentTimeMillis();  
    dir.normalize();
    dir.mult(force/mass);
    velocity.set(dir);
    owner = null;
  }

  public boolean readyToBeKickedBy(PlayerBase kicker) {
    if (lastkickedBy == kicker && System.currentTimeMillis() - lastKickedAt  < PlayerKickInterval)
      return false;  
    return true;
  }

  public PlayerBase getLastKickedBy() {
    return lastkickedBy;
  }

  public double timeToCoverDistance(Vector2D from, Vector2D to, double force) {
    //this will be the velocity of the ball in the next time step *if*
    //the player was to make the pass. 
    double speed = force / mass;

    //calculate the velocity at B using the equation
    //
    //  v^2 = u^2 + 2as
    //

    //first calculate s (the distance between the two positions)
    double DistanceToCover =  Vector2D.dist(from, to);

    double term = speed * speed + 2.0 * DistanceToCover * FRICTION_MAG;

    //if  (u^2 + 2as) is negative it means the ball cannot reach point B.
    if (term <= 0.0) return -1.0;

    double v = Math.sqrt(term);

    //it IS possible for the ball to reach B and we know its speed when it
    //gets there, so now it's easy to calculate the time using the equation
    //
    //    t = v-u
    //        ---
    //         a
    //
    return (v-speed)/FRICTION_MAG;
  }

  public Vector2D futurePosition(double time) {
    //using the equation s = ut + 1/2at^2, where s = distance, a = friction
    //u=start velocity

    // start by calculating the ut term, which is a vector
    Vector2D s = Vector2D.mult(velocity, time);

    //calculate the 1/2at^2 term, which is scalar
    double half_a_t_squared = 0.5 * FRICTION_MAG * time * time;

    //turn the scalar quantity into a vector by multiplying the value with
    //the normalized velocity vector (because that gives the direction)
    Vector2D dir = Vector2D.normalize(velocity);
    dir.mult(half_a_t_squared);

    // Calculate the estimated position
    s.add(dir);
    //the predicted position is the balls position plus these two terms
    return s;
  }

  public void trap(PlayerBase owner) {
    this.owner = owner;
    velocity.set(Vector2D.ZERO);
  }

  public void realease() {
    owner = null;
  }

  public boolean keeperHasBall() {
    return owner != null && owner.isGoalKeeper();
  }

  public void toCentreSpot(double x, double y) {
    pos.set(x, y);
    velocity.set(0, 0);
    heading.set(0, 1);
  }

  public void update(double deltaTime, World world) {
    super.update(deltaTime, world);
    if (velocity.lengthSq() > deltaTime * FRICTION_MAG_SQ) {
      Vector2D decel = Vector2D.normalize(velocity);
      decel.mult(deltaTime * FRICTION_MAG);
      velocity.add(decel);
    }
    else
      velocity.set(0, 0);
    // Now check edge conditions
    boolean goalScored = false;
    if (pos.x - BALL_RADIUS < 0) {
      velocity.x = Math.abs(velocity.x);
      goalScored = pos.y >= GOAL_LOW_Y && pos.y <= GOAL_HIGH_Y;
    }
    else if (pos.x + BALL_RADIUS > PITCH_LENGTH) {
      velocity.x = -Math.abs(velocity.x);
      goalScored = pos.y >= GOAL_LOW_Y && pos.y <= GOAL_HIGH_Y;
    }
    if (pos.y - BALL_RADIUS < 0)
      velocity.y = Math.abs(velocity.y);
    else if (pos.y + BALL_RADIUS > PITCH_WIDTH)
      velocity.y = -Math.abs(velocity.y);
    if (goalScored) {
      velocity.set(0, 0);
      int teamNo = pos.x < PITCH_LENGTH/2 ? 1 : 0;
      Dispatcher.dispatch(0, entityID, pitch.ID(), GOAL_SCORED, teamNo);
    }
  }
}
