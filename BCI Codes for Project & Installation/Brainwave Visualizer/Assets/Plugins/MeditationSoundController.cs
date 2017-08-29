using UnityEngine;
using System.Collections;

[RequireComponent(typeof(AudioSource))]

public class MeditationSoundController : MonoBehaviour {

  public AudioClip meditationClip;

  public bool soundEnabled = true;

  private DataController thinkGearClient;
  private float targetValue;
  private float currentValue;
  private float valueVelocity;

	void Start () {
	  audio.clip = meditationClip;

    thinkGearClient = (DataController)GameObject.Find("ThinkGear").GetComponent(typeof(DataController));

    InvokeRepeating("UpdateEsense", 0.0f, 1.0f);
	}
	
	void Update () {
    currentValue = Mathf.SmoothDamp(currentValue, targetValue, ref valueVelocity, 1.0f);

    if(!soundEnabled){
      if(audio.isPlaying)
        audio.Stop();

      return;
    }

    // the volume of the audio is 0 if med is lower than 70.
    // between 70 and 90, volume will increase smoothly from 0 to 1.
    // above 90, volume is 1. 
    if(currentValue < 70 && audio.isPlaying)
      audio.Stop();
    else if(currentValue >= 70 && !audio.isPlaying)
      audio.Play();

    audio.volume = currentValue < 70 ? 0 : currentValue < 90 ? 1.0f - ((90.0f - currentValue) / 20.0f) : 1.0f;
	}

  void UpdateEsense() {
    targetValue = thinkGearClient.headsetData.meditation;
  }
}
