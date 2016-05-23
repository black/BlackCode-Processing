public class SecondaryApplet extends PApplet {
    int w, h;
    public PImage display;
    public PVector thumb, prevThumb, hand, a, b, c, d;
    public PVector[] fingers;
    
    
    public SecondaryApplet(){
      super();
    }
    
    public SecondaryApplet(int w, int h){
      super();
      this.w=w;
      this.h=h;
    }
    
    
    public void setup() {
        size(w,h);
        //smooth();
        //noLoop();
    }
    
    
    public void draw() {
      fill(0,0,0);
      rect(0,0,w,h);
      //fill(255,0,0);
      //rect(0,0,w/2,h/2);
      if(display!=null){
        image(display,0,0);
      }
      
      
      if(a!=null && b!=null && c!=null && d!=null){
        /**
        fill(255,0,0);
        ellipse(a.x,a.y,10,10);
        fill(255,255,0);
        ellipse(b.x,b.y,10,10);
        fill(255,0,255);
        ellipse(c.x,c.y,10,10);
        fill(255,255,255);
        ellipse(d.x,d.y,10,10);
        */
      }
      
      if(fingers!=null){
        for(int i =0; i<fingers.length; i++){
          if(fingers[i]!=null){
            fill(255,140,0);
            ellipse(fingers[i].x,fingers[i].y,10,10);
          }
        }
      }
      
      
      if(hand!=null){
       // fill(20,255,20);
       // rect(hand.x,hand.y,15,15);
      }
      
      if(thumb!=null){
        fill(0,255,0);
        ellipse(thumb.x,thumb.y,15,15);
      }
      
      if(prevThumb!=null){
       // fill(100,100,255);
       // ellipse(prevThumb.x,prevThumb.y,15,15);
      }
    }
    
    
    public void setDisplay(PImage other){
      display = other;
      display.updatePixels();
    }
} 
