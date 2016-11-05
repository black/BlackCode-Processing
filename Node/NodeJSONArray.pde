class NodeJSONArray {
  JSONArray parent;
  NodeJSONArray(int gen, JSONObject child) {
    parent.setJSONObject(gen, child);
  }
}