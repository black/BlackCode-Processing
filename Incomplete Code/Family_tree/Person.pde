class Person {
  ArrayList<Person> children = new ArrayList();
  color c;
  int id, gen, x, y;
  String name, gender;
  Person(int gen, int id, String name, String gender, int x, int y ) {
    this.gen  = gen;
    this.id = id;
    this.name = name;
    this.gender = gender;
    this.x = x;
    this.y = y;
    c = (color)random(#000000);
  }
  void displayPerson() {
    noStroke();
    fill(c);
    ellipse(x, y, 20, 20);
  }
  void displayInfo() {
    fill(c);
    text("Name:"+name+"\nGender:"+gender+"\nGeneration:"+gen + "\nChildren:"+children.size(), mouseX, mouseY);
  }
  void newChild(int gen, int id, String name, String gender, int x, int y ) {
    children.add(new Person(gen, id, name, gender, x, y));
  }
}

