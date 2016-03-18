int x=0;
void setup() {
  size(300, 300);
} 
void draw() {
  background(-1);  
  fill(0);
  text(x + " ", width>>1, height>>1);
}
float dx=0;
void mouseMoved() { 
  x++;
} 

