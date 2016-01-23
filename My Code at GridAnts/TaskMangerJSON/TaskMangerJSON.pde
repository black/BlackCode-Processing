DateStamp D = new DateStamp();
String saveText = " ";
JSONArray jsonLoad;
JSONObject tempTask;
void setup() {
  size(400, 400, P3D);
  tempTask = new JSONObject();
  jsonLoad = loadJSONArray("data/data.json");
}

void draw() {
  background(-1);
  saveButton();
  fill(0);
  text(saveText, 10, height-140 );
  fill(#F2830C);
  //----------------
  for (int i = 0; i < jsonLoad.size (); i++) {
    JSONObject tempJSONObject = jsonLoad.getJSONObject(i); 
    int id = tempJSONObject.getInt("id");
    String date = tempJSONObject.getString("date");
    String task = tempJSONObject.getString("message");
    text(id + ", " + date + ", " + task, 10, 15+40*i);
  }
  //-----------------
  if (save) {
    tempTask.setInt("id", jsonLoad.size());
    tempTask.setString("date", D.createStamp());
    tempTask.setString("message", saveText);
    jsonLoad.append(tempTask);
    saveJSONArray(jsonLoad, "data/data.json");
    save = false;
  }
}

