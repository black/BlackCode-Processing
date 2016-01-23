using UnityEngine;
using System.Collections;

public class CustomTransform {
	/**
	 * Rotate a vector about the origin over "angle" radians.
	 */
  public static Vector3 Rotate(float angle, Vector3 initial){
    return new Vector3(initial.x * Mathf.Cos(-angle) - initial.y * Mathf.Sin(-angle),
                       initial.x * Mathf.Sin(-angle) + initial.y * Mathf.Cos(-angle),
                       0);
  }

  /**
   * Generate a point in a cubic Bezier curve, where p0 and p3 are the anchor points,
   * p1 and p2 are the control points, and t is the parameter.
   */
  public static Vector3 Bezier(Vector3 p0, Vector3 p1, Vector3 p2, Vector3 p3, float t){
    return Mathf.Pow(1 - t, 3) * p0 + (3 * t * Mathf.Pow(1 - t, 2)) * p1 + 
           (3 * Mathf.Pow(t, 2) * (1 - t)) * p2 + Mathf.Pow(t, 3) * p3;
  }
}
