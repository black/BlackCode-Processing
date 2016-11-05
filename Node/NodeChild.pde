class NodeChild {
  JSONObject node;
  NodeChild(int i, String name) {
    node = new JSONObject();
    node.setInt("ID", i); 
    node.setString("nodeName", name);
  }
}