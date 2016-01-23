int s = 20, t=100; // t is the range of the random numbers form 0-t
                   // s = numbers of random numbers (How many random number you want to generate)
int[] data = new int[s]; 
String saveMe[] = new String[s];
ArrayList<Integer> datax = new ArrayList(); 
void setup() {
  size(300, 100);
}

void draw() {
  background(-1);
  
  // generate randomo numbers
  for (int i=0;i<data.length;i++) {
    int x =(int)random(0, t+1);
    datax.add(x);
    for (int j=0;j<datax.size();j++) {
      int t = (int) datax.get(j);
      if (t!=data[i]) {
        data[i] = x;
        //--just for looking coool! :) 
        fill(0); 
        text(x, i*20, height/2);
      }
    }
  }
  
  //--Sort the data if you want or commment it
  data = sort(data); 
  //---- save the data in string array for wrting in a text file
  int l=0;
  for (int k = 0; k < data.length; k++)
  {
    saveMe[k] = str(data[k]);
    l = l+ data[k];
  }
  
  //----Take the average of all 20 values and exits from the program
  if (l/20==50) { 
    println(l/20);
    saveStrings("data.txt", saveMe);
    exit();
  }
}

