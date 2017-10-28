private var controller : GameController;

private var headerStyle : GUIStyle;

private var tempSkin : GUISkin;

function Start(){
  OnLanguageChanged();

  controller = GetComponent(GameController);
  /*
  headerStyle = new GUIStyle();
  headerStyle.font = titleFont;
  headerStyle.normal.textColor = Color.white;
  headerStyle.margin.top = 0;
  headerStyle.padding.top = 0;
  headerStyle.margin.bottom = 15;
  headerStyle.margin.left = 15;
  */
}

function OnLanguageChanged(){
  tempSkin = Localizer.Skins["main"];

  headerStyle = tempSkin.GetStyle("GameHeader");
}

function OnGUI(){
  GUI.skin = tempSkin;

  GUILayout.BeginArea(Rect(50, 10, 150, 260));
  
  GUILayout.BeginVertical();
  
  var modeString : String = controller.mode == GameController.MEDITATION ? "Float" : "Burn";
  
  if(controller.mode == GameController.MEDITATION){
    GUILayout.Label(Localizer.Content["game"]["cfloattime"]);
    GUILayout.Label(controller.currentTime.ToString("f") + "s", headerStyle);
    
    GUILayout.Label(Localizer.Content["game"]["bfloattime"]);
    GUILayout.Label((controller.mode == GameController.MEDITATION ? controller.bestFloatTime : controller.bestBurnTime).ToString("f") + "s", headerStyle);
  }
  else {
    GUILayout.Label(Localizer.Content["game"]["cburntime"]);
    GUILayout.Label(controller.currentTime.ToString("f") + "s", headerStyle);
    
    GUILayout.Label(Localizer.Content["game"]["bburntime"]);
    GUILayout.Label((controller.mode == GameController.MEDITATION ? controller.bestFloatTime : controller.bestBurnTime).ToString("f") + "s", headerStyle);
  }
  
  GUILayout.Space(30);
  
  GUILayout.BeginVertical(GUILayout.Width(60));
  
  if(GUILayout.Button(Localizer.Content["game"]["float"], GUILayout.Height(30))){
    controller.mode = GameController.MEDITATION;
    controller.DestroyActive();
  }
  else if(GUILayout.Button(Localizer.Content["game"]["burn"], GUILayout.Height(30))){
    controller.mode = GameController.ATTENTION;
    controller.DestroyActive();
  }

  GUILayout.EndVertical();
  
  GUILayout.EndVertical();
  GUILayout.EndArea();
  
  // display current / max height if float mode
  if(controller.mode == GameController.MEDITATION){
    GUILayout.BeginArea(Rect(210, 10, 150, 260));
    
    GUILayout.BeginVertical();
    GUILayout.Label(Localizer.Content["game"]["cfloatht"]);
    GUILayout.Label(controller.currentFloatHeight.ToString("f") + "m", headerStyle);
    
    GUILayout.Label(Localizer.Content["game"]["bfloatht"]);
    GUILayout.Label(controller.bestFloatHeight.ToString("f") + "m", headerStyle);
    GUILayout.EndVertical();
    GUILayout.EndArea();
  }
}

@script RequireComponent(GameController)
