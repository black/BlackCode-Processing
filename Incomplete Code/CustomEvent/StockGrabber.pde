class StockGrabber implements Subject {
  ArrayList<Observer> observers;
  float ibmPrice;
  float aaplPrice;
  float googPrice;

  StockGrabber() {
    observers = new ArrayList();
  }

  void register(Observer newObserver) {
    observers.add(newObserver);
  }

  void unregister(Observer deleteObserver) {
    int observerIndex = observers.indexOf(deleteObserver);
    println(observerIndex+"deleted");
    observers.remove(observers);
  }

  void notifyObserver() {
    for (Observer observer : observers) {
      observer.update(ibmPrice, aaplPrice, googPrice);
    }
  }

  /*--setter methods----*/
  void setIBMPrice(float newIBMPrice) {
    this.ibmPrice = newIBMPrice;
    notifyObserver();
  }

  void setAAPLPrice(float newAAPLPrice) {
    this.aaplPrice = newAAPLPrice;
    notifyObserver();
  }

  void setGOOGPrice(float newGOOGPrice) {
    this.googPrice = newGOOGPrice;
    notifyObserver();
  }
}

