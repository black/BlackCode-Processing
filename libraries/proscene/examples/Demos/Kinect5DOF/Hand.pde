public class Hand {
  PVector point;
  color col;

  public Hand() {
    point=new PVector(0, 0, 0);
  }
  
  public Hand(color inColor) {
    point=new PVector(0, 0, 0);
    col=inColor;
  }
  
  public PVector getPoint() {
    return point;
  }
  
  public void setPoint(PVector inPoint) {
    point=inPoint;
  }
  
  public void draw() {
    fill(col);
    float maxZAllowed=1800;
    float minZAllowed=1000;
    float maxRadius=15;
    float minRadius=5;
    float rad=abs((maxRadius-((point.z-minZAllowed)*((maxRadius-minRadius)/(maxZAllowed-minZAllowed)))));
    ellipse(point.x, point.y, rad, rad);
  }
}