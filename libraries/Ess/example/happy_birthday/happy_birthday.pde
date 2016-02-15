import krister.Ess.*;
AudioChannel myChannel;
TriangleWave myWave;
Envelope myEnvelope;
int numNotes = 200;
int noteDuration = 500;
float[] rawSequence ={293.6648, 293.6648, 329.62756, 329.62756,
391.9955, 369.99445, 293.6648, 293.6648, 329.62756, 293.6648,
439.99997, 391.9955, 293.6648, 293.6648, 587.3294, 493.8834,
391.9955, 369.99445, 392.62756, 523.25116, 523.25116, 493.8834,
391.9955, 439.99997, 391.9955};
void setup()
{
  size(100,100);
  Ess.start(this);
  myChannel = new AudioChannel();
  myChannel.initChannel(myChannel.frames(rawSequence.length * noteDuration));
  int current = 0;
  myWave = new TriangleWave(480, 0.3);
  EPoint[] myEnv = new EPoint[3];
  myEnv[0] = new EPoint(0,0);
  myEnv[1] = new EPoint(0.25,1);
  myEnv[2] = new EPoint(2,0);
  myEnvelope = new Envelope(myEnv);
  int time = 0;
  for(int i = 0; i<rawSequence.length; i++)
  {
    myWave.frequency = rawSequence[current];
    int begin = myChannel.frames(time);
    int e = int(noteDuration*0.8);
    int end = myChannel.frames(e);
    myWave.generate(myChannel, begin, end);
    myEnvelope.filter(myChannel, begin, end);
    current++;
    time += noteDuration;
  }
  myChannel.play();
}
void draw() {}
public void stop()
{
  Ess.stop();
  super.stop();
}
