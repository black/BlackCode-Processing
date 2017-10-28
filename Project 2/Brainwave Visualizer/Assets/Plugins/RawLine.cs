using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class RawLine : MonoBehaviour {
  public Rect normalizedRect;
  public Camera linkedCamera;
  public Material lineMaterial;
  public Color color;

  public float maxValue;
  public float minValue;

  public float targetValue;

  public int lineResolution = 200;
  public int pointHistorySize = 200;
  
  private float increment;
  private LineRenderer line;
  private Rect lineBounds;
  private Rect cameraRect;
  private float scaling;
  private List<float> vertexList;


  private float lineVelocity = 0.0f;

  private Camera usedCamera;

  private GraphData data;

  void Awake(){
    GameObject lineObject = new GameObject("Raw Line");

    vertexList = new List<float>(lineResolution);

    data = new GraphData(pointHistorySize);

    /**
     * Initialize the point lists
     */

    for(int i = 0; i < lineResolution; i++)
      vertexList.Add(0.0f);

    line = lineObject.AddComponent(typeof(LineRenderer)) as LineRenderer;

    usedCamera = linkedCamera != null ? linkedCamera : Camera.main;

    InvokeRepeating("PushVertex", 0.0f, 0.015f);
  }

  void Start(){
    line.material = lineMaterial;
    line.SetColors(color, color);
    line.SetVertexCount(lineResolution);

    InvokeRepeating("Relayout", 0.0f, 0.5f);
  }

  /*
  void OnStartedRecording(){
    line.enabled = this.enabled = false;
  }
  
  void OnStoppedRecording(){
    line.enabled = this.enabled = true;
  }
  */

  void OnStartedPlaying(){
    line.enabled = this.enabled = false;
  }

  void OnStoppedPlaying(){
    line.enabled = this.enabled = true;
  }

  private void Relayout(){
    cameraRect = usedCamera.pixelRect;
    scaling = normalizedRect.width * cameraRect.width / 700.0f;
    
    line.SetWidth(0.03f * scaling, 0.03f * scaling);
    
    float aspectRatioFactor = ((Screen.width + 0.0f) / (Screen.height + 0.0f)) / 1.77778f;
   
    float shiftedScreenHeight = Screen.height * aspectRatioFactor;
    float offset = (Screen.height - shiftedScreenHeight) / 2.0f;
    
    lineBounds = new Rect(cameraRect.x + (cameraRect.width * normalizedRect.x), 
                (cameraRect.y * aspectRatioFactor) + ((cameraRect.height * aspectRatioFactor) * normalizedRect.y) + offset,
                549 * scaling, 390 * scaling);
                
    increment = (lineBounds.width + 0.0f) / (lineResolution + 0.0f);
  }

  private void PushVertex(){
    if(vertexList.Count >= lineResolution)
      vertexList.RemoveRange(0, vertexList.Count + 1 - lineResolution);

    data.Add(targetValue);

    float yValue = scaling * Mathf.SmoothDamp(vertexList[vertexList.Count - 1], 
                                              Mathf.Clamp(7.0f * data.NormalizedValue, -170, 170),
                                              ref lineVelocity, 0.02f);
  
    // now add the calculated plotted point
    vertexList.Add(yValue);
  }

  void Update(){
    for(int i = 0; i < lineResolution; i++){
      Vector3 vertex = linkedCamera.ScreenToWorldPoint(new Vector3(lineBounds.x + (i * increment), 
                                                                   vertexList[i] + lineBounds.y + (lineBounds.height * 2.5f / 2.0f),
                                                                   5 * scaling));

      line.SetPosition(i, vertex);
    }
  }
}
