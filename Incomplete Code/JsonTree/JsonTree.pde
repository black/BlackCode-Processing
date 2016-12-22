JSONArray Family; 

void setup() { 
  size(300, 500);
  Family = new JSONArray();
} 
void draw() {
  background(-1);
} 

void mousePressed() {
  if (mouseButton==LEFT) {
    String Name  = randomName((int)random(5, 10));
    String gender = (random(100)<50)?"male":"female";
    int id = Family.size();
    JSONObject person = Person(id, Name, gender, "Red", ""); 
    Family.setJSONObject(id, person);
  } else if (mouseButton==RIGHT) {
  }
}

void keyPressed() { 
  println(Family);
}




String randomName(int texlen) {
  String Name = "";
  for (int i=0; i<texlen; i++) {
    Name = Name+char(int(random(65, 90)));
  }
  return Name;
}

int getGeneration(JSONObject person) {
  //int depth  =
  return 0;
}
