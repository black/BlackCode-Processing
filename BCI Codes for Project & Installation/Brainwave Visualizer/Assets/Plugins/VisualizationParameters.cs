using UnityEngine;
using System.Collections;

public class VisualizationParameters : MonoBehaviour {
  public string visualizationName;
  
  public bool showCenterPoint = false;   // whether or not to show the center point
  public int cameraDistance = 7;           // distance of the line from the camera
  
  public int lineSmoothness = 50;          // distance of control points from anchor point
  public float lineDamping = 1.0f;					// the lineDamping of the line's movement
  public float lineWidth = 0.05f;
  public Material lineMaterial;            // the material to use for the line
  public Color[] lineColors = new Color[8];               // the colors to be used
  public float lineViewportReduction = 1.8f;        // how much to reduce the main curve by
  
  public float captureColorMultiplicative = 0.3f;   // multiplication constant for the color of old lines

  public int captureHistorySize = 20;      // number of lines to keep updating at any given time
  public float captureInterval = 0.05f;
  public bool growCapturedCurves = false;
  public float growthSpeed = 100.0f;         // the growth speed of the expanding lines
}
