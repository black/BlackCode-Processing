int s = 100, t=1;
float[] data = new float[s]; 
String saveMe[] = new String[s];
ArrayList<Float> datax = new ArrayList(); 
void setup() {
  size(300, 200);
}

void draw() {
  background(-1);
  for (int i=0; i<data.length; i++) {
    float x = random(0, 1);
    datax.add(x);
  }

  for (int i=0; i<datax.size (); i++) {
    float k = (float) datax.get(i);
    data[i] = k;
  }

  for (int k = 0; k < data.length; k++)
  {
    saveMe[k] = str(data[k]);
    rect(k*4, height, 4, -data[k]*100);
  }
  saveStrings("data.txt", saveMe);
  noLoop();
}

