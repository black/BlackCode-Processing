JSONArray Children(JSONArray ChildList, JSONObject Child) {
  int id = ChildList.size(); 
  ChildList.setJSONObject(id, Child);
  return ChildList;
}

JSONObject Person(int id, String Name, String Gender, String Color, String ChildList) {
  JSONObject person = new JSONObject();
  person.setInt("id", id); 
  person.setString("Name", Name);
  person.setString("Gender", Gender);
  person.setString("Color", Color);
  person.setString("ChildList", ChildList);
  return person;
}
