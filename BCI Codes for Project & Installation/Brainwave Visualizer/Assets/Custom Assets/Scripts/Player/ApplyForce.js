// the cutoff distance for push/pull interactions. the unit is in-game length units,
// which may not necessarily reflect real-world units like m or ft.
var pushPullCutoffDistance : float = 60.0;

var thresholdReduction : float = 15.5;
var exceedThresholdReduction : float = 10.2;

private var playerAttributes : Attributes;
private var weaponController : WeaponController;

private var eSenseVelocity : float;
private var eSenseValue : float;

private var reductionFactor : float = 15;

function Awake(){
  playerAttributes = GetComponent(Attributes);
  weaponController = GetComponent(WeaponController);
}

/**
 * This is a callback method within Unity3D to handle physics interactions.
 *
 * Refer to http://unity3d.com/support/documentation/ScriptReference/MonoBehaviour.FixedUpdate.html
 * for details
 */
function FixedUpdate(){
  // grab the player's eSense value. depending on the player's selected weapon,
  // this value will reflect either the eSense Meditation value (for LIFT), or
  // the eSense Attention value (for PUSH and PULL).
  eSenseValue = Mathf.SmoothDamp(eSenseValue, playerAttributes.eSenseValue, eSenseVelocity, 1.1);
  
  // grab the player's selected object
  var selectedObject = playerAttributes.selectedObject;
  
  if(selectedObject && (weaponController.activeWeapon != WeaponController.Weapons.Inactive)){
    
    var force : Vector3 = Vector3.zero;
    
    // figure out the direction of the selected object from the player
    var direction : Vector3 = selectedObject.transform.position - transform.position;
    
    // grab the mass of the object. the unit of mass is an in-game quantity -- it does
    // not reflect real-world mass values (like kg or lbs).
    var mass : float = selectedObject.rigidbody.mass;
    
    // figure out what the active weapon is (either PUSH, PULL, or LIFT)
    switch(weaponController.activeWeapon){
      case WeaponController.Weapons.Push:
        // if the object is within the sphere of influence
        if(direction.magnitude < pushPullCutoffDistance)
          // apply a force on the object away from the player
          force = Vector3.Normalize(direction) * (eSenseValue / 15.0);
          
        break;
      case WeaponController.Weapons.Pull:
        // if the object is within the sphere of influence
        if(direction.magnitude < pushPullCutoffDistance)
          // apply a force on the object towards the player
          force = Vector3.Normalize(-direction) * (eSenseValue / 15.0);
          
        break;
      case WeaponController.Weapons.Lift:
      
        /**
         * The following code implements a scheme where the lift force is
         * reduced with the square of the distance, but only if the vertical
         * distance from the player exceeds a certain threshold. The lift force
         * is reduced by a constant factor otherwise.
         *
         * This prevents the selected object from lifting too high beyond the 
         * player's viewport.
         */
        reductionFactor = (direction.y < 1.5) ? thresholdReduction : direction.y * exceedThresholdReduction;
        
        // set the force, applying all scaling and shifting factors (determined by trial and error)
        // we also apply a small bob to the movement, to get the slight "wobble" at the threshold of lift
        force = Vector3.up * (mass * 1.25  * (eSenseValue + 50.0) / reductionFactor + Mathf.Sin(Time.time * 9.0));
        break;
      case WeaponController.Weapons.Ignite:
        selectedObject.SendMessage("TriggerBurn", eSenseValue, SendMessageOptions.DontRequireReceiver);
        break;
      default:
        break;
    }
    
    // finally, apply the force to the object
    // Refer to http://unity3d.com/support/documentation/ScriptReference/Rigidbody.AddForce.html
    // for details.
    selectedObject.rigidbody.AddForce(force);
  }
}

@script RequireComponent(Attributes)
@script RequireComponent(WeaponController)
