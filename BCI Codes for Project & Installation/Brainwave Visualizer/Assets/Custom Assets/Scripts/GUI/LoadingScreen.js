var splashDisplay : Texture2D;
var skin : GUISkin;
var skinJp : GUISkin;

private var tempSkin : GUISkin;

private var taglineStyle : GUIStyle;

private var subtitleStyle : GUIStyle;

function Start(){
  OnLanguageChanged();
}

function OnLanguageChanged(){
  tempSkin = Localizer.Skins["main"]; 

  taglineStyle = tempSkin.GetStyle("LoadingTagline");

  if(Localizer.appLanguage != Localizer.Language.en)
    subtitleStyle = tempSkin.GetStyle("loadingsubtitle");
}

function OnGUI(){
  GUI.color = Color.white;
  GUILayout.BeginVertical(GUILayout.Height(Screen.height));
  GUILayout.FlexibleSpace();

  GUILayout.BeginHorizontal(GUILayout.Width(Screen.width));
  GUILayout.FlexibleSpace();

  GUILayout.BeginHorizontal(GUILayout.Width(splashDisplay.width));
  GUILayout.FlexibleSpace();
  GUILayout.BeginVertical();
  GUILayout.Label(splashDisplay);

  if(Localizer.appLanguage == Localizer.Language.jp){
    GUILayout.BeginHorizontal();
    GUILayout.FlexibleSpace();
    GUILayout.Label(Localizer.Content["loading"]["visualizer"], subtitleStyle);
    GUILayout.EndHorizontal();
  }

  GUILayout.EndVertical();
  GUILayout.FlexibleSpace();
  GUILayout.EndHorizontal();

  GUILayout.FlexibleSpace();
  GUILayout.EndHorizontal();

  GUILayout.Space(55);

  GUILayout.BeginHorizontal(GUILayout.Width(Screen.width));
  GUILayout.FlexibleSpace();
  GUILayout.Label(Localizer.Content["loading"]["tagline"], taglineStyle);
  GUILayout.FlexibleSpace();
  GUILayout.EndHorizontal();

  GUILayout.FlexibleSpace();
  GUILayout.EndVertical();
}
