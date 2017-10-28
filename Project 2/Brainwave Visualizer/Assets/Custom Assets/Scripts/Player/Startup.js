private var weaponController : WeaponController;

function Start(){
  weaponController = gameObject.GetComponent(WeaponController);
  weaponController.activeWeapon = WeaponController.Weapons.Ignite;
}
