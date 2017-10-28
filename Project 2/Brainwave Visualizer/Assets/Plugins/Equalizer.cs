using UnityEngine;
using System.Collections;

public class Equalizer : MonoBehaviour {

  public Texture2D background;
  public Texture2D element;
  public Rect normalizedRect;
  public Camera linkedCamera;

  public int elementGap = 0;

  public GraphBar[] bars;

  private float scaling;
  private Camera usedCamera;
  private Rect cameraRect;
  private Rect backgroundRect;
  private Rect graphRect;

  private float elementWidth;
  private float elementHeight;
  private float barGap;
  private float startGap;

  private float scaledElementGap;

	void Awake(){
    usedCamera = (linkedCamera != null ? linkedCamera : Camera.main);
  }

  void Start(){
    InvokeRepeating("Relayout", 0.0f, 0.5f);
  }

  void Relayout(){
    cameraRect = usedCamera.pixelRect;
    scaling = normalizedRect.width * cameraRect.width / 700.0f;
    
    backgroundRect = RectForTexture(background);
    
    elementWidth = 63.0f * scaling;
    elementHeight = Mathf.Round(element.height * elementWidth / element.width);
    
    scaledElementGap = Mathf.Round(elementGap * scaling);
    
    barGap = 3.0f * scaling;
    startGap = 11.0f * scaling * normalizedRect.height;
    
    graphRect = new Rect(backgroundRect.x + startGap, backgroundRect.y, backgroundRect.width - startGap, backgroundRect.height - (30 * scaling));
  }

  void Update(){
    foreach(GraphBar bar in bars)
      bar.lerpedValue = Mathf.SmoothDamp(bar.lerpedValue, 
                                         (bar.data.NormalizedValue * 0.2f) + 0.5f, 
                                         ref bar.velocity, 
                                         0.9f);
  }

  void OnGUI(){
    GUI.depth = 5;
  
    GUI.DrawTexture(backgroundRect, background);
    
    // draw the bars
    for(int i = 0; i < bars.Length; i++){
      GraphBar bar = bars[i];

      float offset = 0;
      
      GUI.color = bar.color * new Color(1.0f, 1.0f, 1.0f, 0.75f);

      // figure out the actual bar height
      float height = bar.data.LatestValue == 0 ? 0 : Mathf.Clamp(graphRect.height * bar.lerpedValue, 0.0f, graphRect.height);
      
      // figure out element count
      int elementCount = (int)(height / (elementHeight + scaledElementGap));

      // now draw a series of individual equalizer elements on top of each other 
      for(int j = 0; j < elementCount; j++){
        offset += elementHeight + scaledElementGap;

        GUI.DrawTexture(new Rect(graphRect.x + i * (elementWidth + barGap), 
                                 graphRect.y + graphRect.height - offset - ((elementHeight + scaledElementGap) / 2), 
                                 elementWidth, elementHeight), 
                        element);
      }
    }
  }

  public void AddData(float[] data){
    // first, add the data to the graph bar holders
    for(int i = 0; i < bars.Length; i++){
      bars[i].data.Add(data[i]);
    }

    // if the data point is 0, assume off-head and don't do anything, otherwise figure out the
    // highest bar
    if(data[0] != 0.0f){
      int highestIndex = 0;
      float currentHighest = 0;

      // next, figure out which was the highest bar
      for(int i = 0; i < bars.Length; i++){
        if(bars[i].data.NormalizedValue > currentHighest){
          currentHighest = bars[i].data.NormalizedValue;
          highestIndex = i;
        }
      }

      bars[highestIndex].topBarCounter += 1;
    }
  }

  public void ResetBarCounters(){
    for(int i = 0; i < bars.Length; i++){
      bars[i].topBarCounter = 0;
    }
  }

  Rect RectForTexture(Texture2D texture){
    float aspectRatioFactor= (float)Screen.width / (float)Screen.height / 1.77778f;
   
    float shiftedScreenHeight = Screen.height * aspectRatioFactor;
    float offset = (Screen.height - shiftedScreenHeight) / 2.0f;
    
    return new Rect(cameraRect.x + (cameraRect.width * normalizedRect.x), 
                (cameraRect.y * aspectRatioFactor) + ((cameraRect.height * aspectRatioFactor) * normalizedRect.y) + offset,
                texture.width * scaling, texture.height * scaling);
  }
}
