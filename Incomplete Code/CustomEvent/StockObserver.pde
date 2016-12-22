class StockObserver implements Observer {
  float ibmPrice;
  float aaplPrice;
  float googPrice;

  int observerIDTracker = 0;
  int observerID;

  Subject stockGrabber;

  StockObserver(Subject stockGrabber) {
    this.stockGrabber = stockGrabber;
    this.observerID  = ++observerIDTracker;
    println("New Obser" + this, observerID);
    stockGrabber.register(this);
  }

  void update(float ibmPrice, float aaplPrice, float googPrice) {

    this.ibmPrice = ibmPrice;
    this.aaplPrice = aaplPrice;
    this.googPrice = googPrice;

    printThePrices();
  }

  void printThePrices() {
    println(observerID + "\nIBM: " + ibmPrice +"\nAPPLE: " + aaplPrice +"\nGOOGLE: " + googPrice);
  }
}

