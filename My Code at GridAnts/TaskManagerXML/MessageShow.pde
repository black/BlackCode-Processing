class Message {
  int id;
  String date = " ";
  String message = " ";
  Message(int id, String date, String message) {
    this.id = id;
    this.date = date;
    this.message = message;
  }
  void show() {
    textSize(12);
    text(id+" "+ date, 0, 10);
    textSize(16);
    text(message, 0, 24);
  }
}

