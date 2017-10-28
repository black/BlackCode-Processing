using UnityEngine;
using System.Collections;

public class LayoutHelper {
  public const int ORIGINAL_WIDTH = 1024;
  public const int ORIGINAL_HEIGHT = 640;
  private const float EXPECTED_RATIO = (ORIGINAL_WIDTH + 0.0f) / (ORIGINAL_HEIGHT + 0.0f);
  
  public static Matrix4x4 PlacementMatrix(){
    return Matrix4x4.TRS(TranslationVector(), Quaternion.identity, ScalingVector());
  }
  
  public static Vector3 TranslationVector(){
    float aspectRatio = (Screen.width + 0.0f) / (Screen.height + 0.0f);

    Vector3 translation = Vector3.zero;
    
    if(aspectRatio < EXPECTED_RATIO)
      translation.y = (Screen.height - (Screen.width / EXPECTED_RATIO)) / 2;
    else
      translation.x = (Screen.width - (Screen.height * EXPECTED_RATIO)) / 2;
    
    return translation;
  }
  
  public static Vector3 ScalingVector(){
    float aspectRatio = (Screen.width + 0.0f) / (Screen.height + 0.0f);
    
    Vector3 scaling = Vector3.one;
    
    if(aspectRatio < EXPECTED_RATIO)
      scaling.x = scaling.y = (Screen.width + 0.0f) / (ORIGINAL_WIDTH + 0.0f);
    else
      scaling.x = scaling.y = (Screen.height + 0.0f) / (ORIGINAL_HEIGHT + 0.0f);
      
    return scaling;
  }
}
