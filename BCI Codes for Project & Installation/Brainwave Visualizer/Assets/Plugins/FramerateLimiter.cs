using UnityEngine; 
using System.Collections; 
using System.Threading; 

public class FramerateLimiter : MonoBehaviour { 
  public float frameRate = 30.0f; 
  public bool enableFrameRateDisplay = false;
    
  private string frameRateString = "";
  private float deltaTime = 1.0f;

  void Awake(){
    Application.targetFrameRate = (int)frameRate;

    InvokeRepeating("UpdateFrameRate", 0.0f, 1.0f);
  }

  void Update(){
    deltaTime = Time.deltaTime;
  }
    
  // Update is called once per frame 
  void LateUpdate () { 
    Application.targetFrameRate = (int)frameRate;
  } 

  void OnGUI(){
    if(enableFrameRateDisplay)
      GUILayout.Label(frameRateString);
  }

  void UpdateFrameRate(){
    frameRateString = "Frame rate: " + 1.0f / deltaTime;
  }
} 

