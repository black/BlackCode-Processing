/**
 * Represents the program frame
 * Keeps balls from escaping frame
 */
class BoxGroup extends Group<Box> {
  BoxGroup(World world) {
    super(world);
    Box boite = new Box();
      getWorld().register(boite);
    this.add(boite);
  }
}
