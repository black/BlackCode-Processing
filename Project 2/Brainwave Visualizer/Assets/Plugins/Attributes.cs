using UnityEngine;
using System.Collections;

public class Attributes : MonoBehaviour {

  public enum ESenseTypes {
    Attention,
    Meditation
  }

  public ESenseTypes eSenseType = ESenseTypes.Attention;
  public int eSenseValue = 0;

  public GameObject selectedObject;

  private DataController client;

	// Use this for initialization
	void Start () {
    client = GameObject.Find("ThinkGear").GetComponent<DataController>();

	  InvokeRepeating("UpdateESenseValue", 1, 0.5f);
	}
	
	void UpdateESenseValue(){
    switch(eSenseType){
      case ESenseTypes.Attention:
        eSenseValue = client.headsetData.attention;
        break;
      case ESenseTypes.Meditation:
        eSenseValue = client.headsetData.meditation;
        break;
    }
  }
}
