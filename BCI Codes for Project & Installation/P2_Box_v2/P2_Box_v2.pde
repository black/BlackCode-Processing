
int i=3,j=0;
int[] m=new int[4];
int[] n=new int[4];
int g=0;    
void setup()
{
  size(500,500);
  smooth();
}

void draw()
{ 
  background(24);
  noFill();
  stroke(0); 
  strokeWeight(4);
  translate(160,210);
  ellipse(50,20,20,20);
  ellipse(75,20,20,20);
  ellipse(100,20,20,20);
  ellipse(125,20,20,20);
  if(keyPressed){
    g=g+1;
}
  
  if(i>-1){
              stroke(255);
              ellipse(i*25+50,20,20,20);
              if(g>0)
              {
                  m[i]=1;
                  text("1 "+i,i*25+49, 50 );
                  g=0;
               }
              else{
                      m[i]=0;   
                      text("0 "+i,i*25+49,50 );    
                   }
              }
             
    if(i==-1)
   { 
     arrayCopy(m,n);
     println( n[0]+" "+n[1] +" "+ n[2] +" " +n[3]);
     delay(300);
     i=3;
   }
             
             
 if(j==50)
   {
     i=i-1;
     j=0;
   }
   j++;
      
}
