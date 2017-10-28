using UnityEngine;
using System.Collections;

public class WeaponController : MonoBehaviour {

  //public int activeWeapon;
  public Weapons activeWeapon;

  public enum Weapons {
    Inactive,
    Push,
    Pull,
    Lift,
    Ignite
  }

  private Attributes playerAttributes;

  void Awake(){
    playerAttributes = (Attributes)gameObject.GetComponent("Attributes");
  }

	// Use this for initialization
	void Start () {
	  InvokeRepeating("UpdateESenseType", (float)1.0, (float)0.1);
	}
	
	void UpdateESenseType(){
	  if(activeWeapon == Weapons.Push || activeWeapon == Weapons.Pull || activeWeapon == Weapons.Ignite)
	    playerAttributes.eSenseType = Attributes.ESenseTypes.Attention;
	  else if(activeWeapon == Weapons.Lift)
	    playerAttributes.eSenseType = Attributes.ESenseTypes.Meditation; 
	  else
	    playerAttributes.eSenseType = Attributes.ESenseTypes.Attention; 
	}
}
