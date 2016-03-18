public void setScript(int sn) {
  String fname = null;
  StringList codeLines = new StringList();
  String desc = "";
  switch(sn) {
  case 1:
    fname = "quadratic.txt";
    break;
  case 2:
    fname = "fibonacci.txt";
    break;
  case 3:
    fname = "primes.txt";
    break;
  }
  if (fname == null)
    return;
  String[] lines = loadStrings(fname);
  int ln = 0;
  while (ln < lines.length) {
    if (lines[ln].startsWith("##Desc")) {
      ln++;
      while (!lines[ln].startsWith ("##") && ln <lines.length)
        desc += lines[ln++] + " ";
    }
    if (lines[ln].startsWith("##Code")) {
      ln++;
      while (!lines[ln].startsWith ("##") && ln <lines.length)
        codeLines.append(lines[ln++]);
    }
    ln++;
  }
  code = codeLines.array();
  script.setCode(code);
  txaScript.setText(code);
  txaOutput.setText(desc, 240);
  txaVars.setText("");
  lblStatus.setText("");
  codeChanged = true;
  goToMode(EDIT);
}
