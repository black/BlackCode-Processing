using UnityEngine;
using System.Collections;

[System.Serializable]
public class GraphBar {
  public string name;
  public Color color;
  public float velocity;
  public float lerpedValue;

  public GraphData data;
  public int topBarCounter;

  public GraphBar(){
    data = new GraphData(20);
    name = "";
  }
}
