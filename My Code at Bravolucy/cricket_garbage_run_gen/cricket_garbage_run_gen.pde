int s = 50, t=6;
int[] data = new int[s]; 
String saveMe[] = new String[s];
ArrayList<Integer> datax = new ArrayList(); 
void setup() {
  size(300, 300);
}

void draw() {
  background(-1);
  for (int i=0;i<data.length;i++) {
    int x =(int)random(0, t+1);
    datax.add(x);
    //    for (int j=0;j<datax.size();j++) {
    //      int t = (int) datax.get(j);
    //      if (t!=data[i]) {
    //        data[i] = x;
    //        fill(0);
    //        text(x, i*20, height/2);
    //      }
    //    }
  }
  println(datax.size());
  for(int i=0;i<datax.size();i++){
    int k = (int) datax.get(i);
    data[i] = k;
  }
  
  //data = sort(data);
  int l=0;
  for (int k = 0; k < data.length; k++)
  {
   l = l + data[k];
    saveMe[k] = str(l);
    // l = l+ data[k];
    exit();
  }
  //  if (l/20==50) { // average
  //    println(l/20);
  //    exit();
  //  }

  saveStrings("data.txt", saveMe);
}

