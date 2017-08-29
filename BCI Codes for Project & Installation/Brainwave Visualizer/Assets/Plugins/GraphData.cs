using System;
using System.Collections;
using System.Collections.Generic;

public class GraphData {

  private List<float> history;

  private float _averageValue = 0.0f;
  private float _averageMagnitude = 0.0f;
  private float _runningTotal = 0.0f;

  public GraphData(int historySize){
    history = new List<float>(historySize);
    
    // make sure there's at least one element, so
    // the average value & magnitude calculations
    // don't go haywire
    Add(0.0f);
  }

  public float AverageValue {
    get { return _averageValue; }
  }

  public float AverageMagnitude {
    get { return _averageMagnitude; }
  }

  public float LatestValue {
    get { return history[history.Count - 1]; }
  }

  /**
   * Produces a result that is, on average, between -1 and 1
   */
  public float NormalizedValue {
    get { return _averageMagnitude == 0.0f ? 0.0f : (history[history.Count - 1] - _averageValue) / _averageMagnitude; }
  }

  public void Add(float data){
    // handle the case where the input number is infinity (e.g. log(0))
    if(float.IsInfinity(data)){
      Add(0.0f);
      return;
    }

    // remove the oldest element(s) from the running total until the 
    // appropriate capacity is reached
    while(history.Count >= history.Capacity){
      _runningTotal -= history[0];
      history.RemoveAt(0);
    }

    // add new data to the list
    history.Add(data);
    _runningTotal += data;

    /*
     * now calculate the average magnitude and value
     */
    _averageMagnitude = 0;

    _averageValue = _runningTotal / history.Count;

    //foreach(float f in history)
    for(int i = 0; i < history.Count; i++){ 
      _averageMagnitude += Math.Abs(history[i] - _averageValue);
    }

    _averageMagnitude /= history.Count;
  }
}
