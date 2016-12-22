Person rootPerson;
void setup() {
  size(300, 300);
  rootPerson = new Person(1, 1, "MyName", "Male", 100, 100);
}

void draw() {
  background(-1);
  rootPerson.displayPerson();
  addChildrens(rootPerson);
  showChlidrens(rootPerson);
}

void addChildrens(Person parent) {
  if (dist(mouseX, mouseY, parent.x, parent.y)<10) {
    rootPerson.displayInfo();
    if (mousePressed) {
      int gen = parent.gen + 1;
      int id = (int)random(1000);
      String name = randomName((int)random(5, 10));
      String gender = (random(100)<50)?"male":"female";
      int xx = parent.x + 40*((random(50)<25)?-1:1);
      int yy = parent.y + 40*((random(50)<25)?-1:1);
      parent.newChild(parent.gen+1, id, name, gender, xx, yy);
      mousePressed =false;
    }
  }
}

void showChlidrens(Person parent) {
  for (int i=0; i<parent.children.size (); i++) {
    Person child = parent.children.get(i);
    child.displayPerson();
    connectParentChild(parent.x, parent.y, child.x, child.y);
  }
}

void connectParentChild(int px, int py, int cx, int cy) {
  stroke(0);
  line(px, py, cx, cy);
}

String randomName(int texlen) {
  String Name = "";
  for (int i=0; i<texlen; i++) {
    Name = Name+char(int(random(65, 90)));
  }
  return Name;
}

