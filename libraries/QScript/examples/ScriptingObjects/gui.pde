// The variables declarations and all the code
// to create the G4P GUI interface
GPanel panel;
GTextArea editor;
GOption heloBlue, heloGold;
GButton btnBlue, btnGold;
GLabel errorStatus, blueActivity, goldActivity;

public void createGUI() {
  G4P.messagesEnabled(false);
  panel = new GPanel(this, 0, -20, 600, 210, "Script Editor");
  panel.setDraggable(false);
  // text editor
  editor = new GTextArea(this, 4, 20, 292, 186, G4P.SCROLLBARS_BOTH);
  editor.setText(blueCode);
  editor.setFont(new Font("Monospaced", Font.PLAIN, 10));
  // RHS
  GToggleGroup heloTog = new GToggleGroup();
  heloBlue = new GOption(this, 450, 22, 65, 16, "Blue");
  heloBlue.setLocalColorScheme(G4P.CYAN_SCHEME);
  heloBlue.setOpaque(true);
  heloGold = new GOption(this, 525, 22, 65, 16, "Gold");
  heloGold.setLocalColorScheme(G4P.GOLD_SCHEME);
  heloGold.setOpaque(true);
  heloTog.addControls(heloBlue, heloGold);
  heloBlue.setSelected(true);
  GLabel lblPick = new GLabel(this, 310, 22, 130, 16, "View the script for - ");
  lblPick.setOpaque(true);

  GLabel lblSetCode = new GLabel(this, 310, 62, 280, 16, "Use this script with helicopter -");
  lblSetCode.setOpaque(true);
  btnBlue = new GButton(this, 310, 82, 130, 16, "Blue");
  btnBlue.setLocalColorScheme(G4P.CYAN_SCHEME);
  btnGold = new GButton(this, 460, 82, 130, 16, "Gold");
  btnGold.setLocalColorScheme(G4P.GOLD_SCHEME);

  errorStatus = new GLabel(this, 310, 112, 280, 48, "");
  errorStatus.setOpaque(true);

  panel.addControls(editor, heloBlue, heloGold, lblPick, lblSetCode, btnBlue, btnGold, errorStatus);
  panel.setAlpha(240);

  blueActivity = new GLabel(this, 20, height - 24, 260, 20);
  blueActivity.setOpaque(true);
  blueActivity.setLocalColorScheme(G4P.CYAN_SCHEME);
  goldActivity = new GLabel(this, 320, height - 24, 260, 20);
  goldActivity.setOpaque(true);
  goldActivity.setLocalColorScheme(G4P.GOLD_SCHEME);
}

