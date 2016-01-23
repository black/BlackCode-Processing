public static var MEDITATION = 0;
public static var ATTENTION = 1;

var meditationSphere : Transform;
var explodingBarrel : Transform;
var spawnPoint : Transform;
     
var mouseOrbit : MouseOrbit;
var player : Attributes;

/*
 * The meanings of these values are overloaded depending on the active
 * mode. 
 *
 * When in MEDITATION mode, bestTime refers to the longest time
 * spent aloft, and currentTime refers to the current time spent aloft.
 *
 * When in ATTENTION mode, bestTime refers to the shortest time it
 * took to explode the barrel, and currentTime refers to the time it has
 * taken so far to burn the barrel.
 */

var bestFloatTime : float = 0.0;
var bestBurnTime : float = 100.0;
var currentTime : float = 0.0;

var bestFloatHeight : float = 0.0;
var currentFloatHeight : float = 0.0;

private var weaponController : WeaponController;

var mode : int = ATTENTION;

private var selectedObject : Transform;

function Start(){
  weaponController = GameObject.Find("Player").GetComponent(WeaponController);
  weaponController.activeWeapon = WeaponController.Weapons.Ignite;
  
  InvokeRepeating("ReloadObject", 0.0, 0.2);
  
  selectedObject = player.selectedObject.transform;
}

function Update () {
  if(mode == MEDITATION){
    if(selectedObject && selectedObject.position.y > 0.05)
      currentTime += Time.deltaTime;
    else
      currentTime = 0;
      
    if(selectedObject && currentTime > bestFloatTime)
      bestFloatTime = currentTime;
      
    if(selectedObject)
      currentFloatHeight = selectedObject.transform.position.y;
    
    if(currentFloatHeight > bestFloatHeight)
      bestFloatHeight = currentFloatHeight;
  }
  else if(mode == ATTENTION) {
    if(selectedObject){
      currentTime += Time.deltaTime;
    }
  }
}

function ReloadObject(){
  if(player.selectedObject == null)
    LoadActive();
}

function LoadActive(){
  if(mode == MEDITATION){
    selectedObject = Instantiate(meditationSphere, spawnPoint.position, Quaternion.identity);
    weaponController.activeWeapon = WeaponController.Weapons.Lift;
  }
  else {
    weaponController.activeWeapon = WeaponController.Weapons.Ignite;
    selectedObject = Instantiate(explodingBarrel, spawnPoint.position, Quaternion.identity);
  }
  
  player.selectedObject = selectedObject.gameObject;
  mouseOrbit.target = selectedObject;
  
  currentTime = 0.0;
}

function OnObjectExploded(){
  if(currentTime < bestBurnTime)
    bestBurnTime = currentTime;
    
  currentTime = 0.0;
}

function DestroyActive(){
  Destroy(player.selectedObject);
}
