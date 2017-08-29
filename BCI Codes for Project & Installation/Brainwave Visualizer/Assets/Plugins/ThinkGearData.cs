[System.Serializable]
public struct ThinkGearData {
  public double elapsedTime;

  // eSense values
  public int meditation;
  public int attention;

  // headset statuses
  public int poorSignalValue;

  // EEG values
  public float delta;
  public float theta;
  public float lowAlpha;
  public float highAlpha;
  public float lowBeta;
  public float highBeta;
  public float lowGamma;
  public float highGamma;

  // raw headset value
  public float raw;

  public ThinkGearData(double time, int meditation, int attention, int poorSignalValue, float delta, float theta,
                       float lowAlpha, float highAlpha, float lowBeta, float highBeta,
                       float lowGamma, float highGamma, float raw){
    this.elapsedTime = time;
    this.meditation = meditation;
    this.attention = attention;
    this.poorSignalValue = poorSignalValue;
    this.delta = delta;
    this.theta = theta;
    this.lowAlpha = lowAlpha;
    this.highAlpha = highAlpha;
    this.lowBeta = lowBeta;
    this.highBeta = highBeta;
    this.lowGamma = lowGamma;
    this.highGamma = highGamma;

    this.raw = raw;
  }

  public override string ToString(){
    return "PoorSig: " + poorSignalValue + "\n" + 
           "Med: " + meditation + "\n" + 
           "Att: " + attention + "\n" + 
           "Delta: " + delta + "\n" + 
           "Theta: " + theta + "\n" + 
           "Low Alpha: " + lowAlpha + "\n" + 
           "High Alpha: " + highAlpha + "\n" +
           "Low Beta: " + lowBeta + "\n" +
           "High Beta: " + highBeta + "\n" +
           "Low Gamma: " + lowGamma + "\n" + 
           "High Gamma: " + highGamma + "\n" +
           "Raw: " + raw;
  }
}

