/**
 * Component to make objects burnable. Objects now have a health, and will
 * set on fire when health is below a threshold. The object will also self-
 * destruct at the appropriate time.
 */
 
#pragma strict

var sparksSound : AudioClip;
var fireSound : AudioClip;
var explosionSound : AudioClip;

var maxHealth : float = 100.0;
var fireThreshold : float = 30.0;
var fire : Transform;
var explosion : Transform;
var sparks : Transform;
var selfDestructThreshold : float = 15;
var rechargeRate : float = 2.0;
var selfDestructRate : float = 2.0;
var onFire : boolean = false;

private var isExploded : boolean = false;
private var health : float;
private var activeFire : Transform;
private var lastBurn : float = -10.0;

function Awake(){
  onFire = false;
  sparks = Instantiate(sparks, transform.position, Quaternion.identity);
  sparks.transform.parent = transform;
  sparks.particleEmitter.emit = false;
  health = maxHealth;
  
  gameObject.AddComponent(AudioSource);
  audio.rolloffFactor = 0.8;
  audio.volume = 0.4;
}

function FixedUpdate(){
  if((Time.time - lastBurn) < 1.0 && !audio.isPlaying){
    audio.clip = sparksSound;
    audio.Play();
  }
  else if((Time.time - lastBurn) >= 1.0 && audio.clip == sparksSound)
    audio.Stop();
    
  
  // set the object on fire
  if(health < fireThreshold && !onFire){
    if(fire){
      activeFire = Instantiate(fire, transform.position, Quaternion.identity);
      activeFire.transform.parent = transform;
    }

    onFire = true;
    audio.clip = fireSound;
    audio.loop = true;
    audio.Play();
  }
  // get rid of the fire
  else if(health > fireThreshold && onFire){
    Destroy(activeFire.gameObject);
    onFire = false;
    audio.Stop();
  }
  
  // kill off the object from the game world
  if(health <= 0){
    if(!isExploded){
      if(explosion){
        Instantiate(explosion, transform.position, Quaternion.identity);
        isExploded = true;
      
        audio.Stop();
        audio.volume = 1.5;
        audio.loop = false;
        audio.PlayOneShot(explosionSound);
      }
      
      // now disable all the renderers for the meshes
      var renderers = GetComponentsInChildren(Renderer);
      
      for(var objectRenderer : Renderer in renderers)
        objectRenderer.enabled = false;
      
      DelayedDestroy(0.9);
    }
  }
  else if(health <= selfDestructThreshold)
    health -= selfDestructRate * Time.deltaTime;
  // recharge health
  else if(health < maxHealth)
    health += rechargeRate * Time.deltaTime;
}

function TriggerBurn(forceMagnitude : float){
  health -= (forceMagnitude / 10.0) * Time.deltaTime;
  sparks.particleEmitter.Emit((forceMagnitude - 10.0) / 15.0);
  
  lastBurn = Time.time;
}

function DelayedDestroy(delay : float){
  yield WaitForSeconds(delay);
  
  for (var go : GameObject in FindObjectsOfType(GameObject))
		go.SendMessage("OnObjectExploded", SendMessageOptions.DontRequireReceiver);
  
  Destroy(activeFire.gameObject);
  Destroy(gameObject);
}