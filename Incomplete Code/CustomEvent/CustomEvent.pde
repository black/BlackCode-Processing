StockGrabber stockGrabber;
StockObserver observer1, observer2;
void setup() {
  size(300, 300); 
  stockGrabber = new StockGrabber();

  observer1 = new StockObserver(stockGrabber);
  stockGrabber.setIBMPrice(197.00);
  stockGrabber.setAAPLPrice(677.60);
  stockGrabber.setGOOGPrice(676.40);
}

void draw() {
  background(-1);
}

void mousePressed() {
  stockGrabber.setAAPLPrice(mouseX);
}

