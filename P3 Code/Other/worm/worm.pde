
Tentacle[] tent;
int MAX_TENTS = 9; 

void setup()
{
  size(640, 480);
 // frameRate(30);
  smooth();

  tent = new Tentacle[MAX_TENTS];

  for (int i = 0; i < MAX_TENTS; i++)
  {
    tent[i] = new Tentacle(1, width, height);
  }
}
void draw()
{
  background(255);

  for (int i = 0; i < MAX_TENTS; i++)
  {
    tent[i].move(width, height);
    tent[i].display();
  }
}


class Tentacle
{

  int direction;            // tiene conto della direzione del movimento dei vermi (1, 2, 3 e 4)

  int numNodes;            // numero di nodi che compongono il verme
  PVector[] nodes;         // vettore di PVector incaricati di memorizzare le posizioni dei diversi nodi

  float init_theta;        // angolo iniziale
  float theta;             // l'angolo usato per disegnare il node[0]
  float tv;                // variabile di supporto per una somma incrementale

  float head;              // ampiezza del movimento - è legata anche alla velocità di movimento
  float girth;             // più grande è, più lunghi i segmenti che costituiscono il verme
  float speedCoefficient;  // maggiore il coefficiente di velocità, più velocemente il verme si muoverà nello spazio
  float friction;          // simula la visosità dell'acqua. Quando presente il movimento è più naturale

  float muscleRange;       // è un valore in gradi e viene utilizzato per calcolare il muscleTheta, assieme a muscleFreq
  float muscleFreq;        // definisce gli istanti di campionamento su di un seno spaziale
  float muscleTheta;       // l'angolo usato per disegnare il node[1]
  float cnt;               // variabile di supporto per contenere la somma incrementale dei muscleFreq

  float xpos, ypos;        // tengono conto della poszione del verme
  int margin_w, margin_h;  // tengono conto dell'area visibile e di quella non visibile
  float body;              // valore usato per scalare lo spessore corporeo del verme


  // COSTRUTTORE //////////////////////////////////////////////////////////////
  Tentacle(int dir_, int width_, int height_)
  {
    margin_w = 200;
    margin_h = 200;

    direction = dir_;

    numNodes = 27;
    nodes = new PVector[numNodes];
    for (int i=0; i < numNodes; i++)
    {
      nodes[i] = new PVector(0, 0);
    }

    init(direction, width_, height_);
  }

  // INIT /////////////////////////////////////////////////////////////////////
  void init(int dir_, int w_, int h_)
  {
    switch (dir_)
    {
    case 1:
      init_theta = 0;
      xpos = 0 - random(100, 200);
      ypos = random(h_);
      break;

    case 2:
      init_theta = 90;
      xpos = random(w_);
      ypos = 0 - random(100, 200);
      break;

    case 3:
      init_theta = 180;
      xpos = w_ + random(100, 200);
      ypos = h_;
      break;    

    case 4:
      init_theta = 270;
      xpos = random(w_);
      ypos = h_ + random(100, 200);
      break;

    default:
      init_theta = 270;
      xpos = random(w_);
      ypos = h_ + random(100, 200);
      break;
    }

    theta = init_theta;

    body = random(13, 50);                      // varia tra 13 - 50
    head = 2 + random(4);                       // varia tra 2 - 6
    girth = 8 + random(8);                      // varia tra 8 - 16
    speedCoefficient = 0.09 + (random(10)/50);  // varia tra 0,09 - 0,29
    friction = 0.9 + (random(10)/100);          // varia tra 0,9 - 1;

    muscleRange = 20 + random(50);              // valore in gradi tra 20 - 70
    muscleFreq = 5 + random(23);                // valore in gradi tra 5 - 28
  }

  // MOVE /////////////////////////////////////////////////////////////////////
  void move(int _width, int _height)
  {
    tv += 0.3 * random(-1, 1);
    theta += tv;
    tv *= friction;

    nodes[0].x = head * cos(radians(theta));
    nodes[0].y = head * sin(radians(theta));

    cnt += muscleFreq;
    muscleTheta = muscleRange * sin( radians(cnt) );

    nodes[1].x = (-1) * head * cos(radians(theta + muscleTheta));
    nodes[1].y = (-1) * head * sin(radians(theta + muscleTheta));


    for (int i = 2; i < numNodes; ++i)
    {
      float dx = nodes[i].x - nodes[i-2].x;
      float dy = nodes[i].y - nodes[i-2].y;

      float d = sqrt(dx * dx + dy * dy);

      nodes[i].x = nodes[i-1].x + (dx/d)*girth;
      nodes[i].y = nodes[i-1].y + (dy/d)*girth;

      if (i == 2)
      {
        xpos -= dx*speedCoefficient;
        ypos -= dy*speedCoefficient;

        if ( ((xpos + margin_w)<0) || (xpos - margin_w > _width) || ((ypos + margin_h) < 0) || (ypos - margin_h > _height) )
          init(direction, _width, _height);
      }
    }
  }


  // DISPLAY //////////////////////////////////////////////////////////////////
  void display()
  {
    pushMatrix();
    translate(xpos, ypos);
    for (int i = 2; i < 27; i++)
    {
      stroke(60, map(i, 2, numNodes, 160, 0), 0, map(i, 2, numNodes, 255, 60));
      strokeWeight( ( (numNodes-i)*(numNodes-i) )/body );
      line(nodes[i-1].x, nodes[i-1].y, nodes[i].x, nodes[i].y);
    }
    popMatrix();
  }
}

