using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using System.Xml.Serialization;

public class RecordedRawOutput : IBrainwaveDataPlayer {
  public struct RawDataPair {
    public double timestamp;
    public double value;
  }

  [XmlRoot("dataContainer")]
  public class DataContainer {
    [XmlArray("rawDataPairs")]
    [XmlArrayItem("rawDataPair", typeof(RawDataPair))]
    public RawDataPair[] dataPairs;
  }

  private System.Random r;
  private DateTime startTime;
  private DateTime currentTime;
  private DateTime lastTime;
  private DataContainer container;

  private int currentIndex = 0;
  private double timeOffset = 0.0;

  [XmlIgnore]
  public int dataPoints {
    get { return -1; }
  }

  [XmlIgnore]
  public double duration {
    get { return 0.0; }
  }

  public RecordedRawOutput(){
    r = new System.Random();
    startTime = DateTime.Now;
    lastTime = DateTime.Now;

    container.dataPairs = new RawDataPair[0];
  }

  public RecordedRawOutput(string filename){
    r = new System.Random();
    startTime = DateTime.Now;
    lastTime = DateTime.Now;

    if(File.Exists(filename)){
      // grab the XML serialized data from a file
      FileStream stream = new FileStream(filename, FileMode.Open, FileAccess.Read, FileShare.Read);
      XmlSerializer xs = new XmlSerializer(typeof(DataContainer));

      // then try to deserialize it
      try {
        container = (DataContainer)xs.Deserialize(stream);
      }
      catch(FileNotFoundException e){
        container.dataPairs = new RawDataPair[0];
      }
      catch(Exception e){
        Debug.Log("General exception: " + e);
      }

      /*
      stream.Close();
      */
    }
    else {
      container.dataPairs = new RawDataPair[0];
    }
  }

  public ThinkGearData DataAt(double secondsFromBeginning){
    currentTime = DateTime.Now;
    
    double elapsedTime = currentTime.Subtract(startTime).TotalSeconds; 
    double deltaTime = currentTime.Subtract(lastTime).TotalSeconds;

    lastTime = currentTime;

    return new ThinkGearData(secondsFromBeginning,
                             (int)(Math.Sin(elapsedTime / 3.0f) * 50.0f + 50.0f),
                             (int)(Math.Cos(elapsedTime / 3.0f) * 50.0f + 50.0f), 
                             //0,
                             //0,
                             0,
                             RandomValue(), RandomValue(),
                             RandomValue(), RandomValue(),
                             RandomValue(), RandomValue(),
                             RandomValue(), RandomValue(),
                             (float)RawValueFor(deltaTime));
  }

  private double RawValueFor(double deltaTime){
    int nextIndex = GetNextIndex(currentIndex); 
    
    while(true){
      if(currentIndex + 1 == container.dataPairs.Length){
        currentIndex = 0;
        timeOffset -= container.dataPairs[container.dataPairs.Length - 1].timestamp;
      }
      else if(timeOffset >= container.dataPairs[currentIndex].timestamp &&
              timeOffset < container.dataPairs[nextIndex].timestamp){
        break;
      }
      else {
        currentIndex = GetNextIndex(currentIndex);
      }

      nextIndex = GetNextIndex(currentIndex); 
    }

    timeOffset += deltaTime;

    return container.dataPairs[currentIndex].value;
  }

  private int GetNextIndex(int currentIndex){
    return (currentIndex + 1) % container.dataPairs.Length;
  }

  private float RandomValue(){
    return (float)r.NextDouble();
  }
}
