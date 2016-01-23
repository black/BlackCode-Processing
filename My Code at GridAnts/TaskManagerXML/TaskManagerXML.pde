XML xml;
XML[] children;
DateStamp D = new DateStamp();
String saveText = " ";

void setup() {
  size(300, 600);
  updateXML();
}
void updateXML() {
  xml = loadXML("TaskManager.xml");
  children = xml.getChildren("Task");
}
void draw() {
  background(-1);
  saveButton();
  updateXML();
  for (int i = 0; i < children.length; i++) {
    int id = children[i].getInt("id");
    String date = children[i].getString("date");
    String thetask = children[i].getContent();
    Message M = new Message(id, date, thetask);
    fill((i%2==0)?#FF8181:#F70000);
    rect(0, i*35, width, 40);
    fill(0);
    pushMatrix();
    translate(10, i*37);
    M.show();
    popMatrix();
  }

  text(saveText, 10, 80);

  if (save) {
    XML newChild = xml.addChild("Task");
    newChild.setInt("id", children.length );
    newChild.setString("date", D.createStamp());
    newChild.setContent(saveText);
    saveXML(xml, "TaskManager.xml");
    save =false;
    saveText = " ";
  }
}

