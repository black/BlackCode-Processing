var audioCutoffThreshold : float = 0.05;


function Update () {
  var yPos = transform.position.y;
  
  if(yPos > audioCutoffThreshold){
    audio.volume = Mathf.Clamp((yPos - audioCutoffThreshold), 0.0, 2.0);
    audio.pitch = Mathf.Clamp((yPos - audioCutoffThreshold) + 0.4, 0.5, 1.2);
  }
  else {
    audio.volume = 0;
    audio.pitch = 1.0;
  }
}

@script RequireComponent(AudioSource)