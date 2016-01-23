using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using System;

public class FakeMindSetOutput : IBrainwaveDataPlayer {
  private System.Random r;
  private DateTime startTime;
  private DateTime currentTime;

  public FakeMindSetOutput(){
    r = new System.Random();
    startTime = DateTime.Now;
  }

  public ThinkGearData DataAt(double secondsFromBeginning){
    currentTime = DateTime.Now;
    
    double deltaSeconds = currentTime.Subtract(startTime).TotalSeconds; 

    return new ThinkGearData(secondsFromBeginning,
                             (int)(Math.Sin(deltaSeconds / 3.0f) * 49.0f + 51.0f),
                             (int)(Math.Cos(deltaSeconds / 3.0f) * 49.0f + 51.0f), 
                             0, 
                             RandomValue(), RandomValue(),
                             RandomValue(), RandomValue(),
                             RandomValue(), RandomValue(),
                             RandomValue(), RandomValue(),
                             512.0f * (float)r.NextDouble());
  }

  public int dataPoints {
    get { return -1; }
  }

  public double duration {
    get { return 0.0; }
  }

  private float RandomValue(){
    return (float)r.NextDouble();
  }
}
