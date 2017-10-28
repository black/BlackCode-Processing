using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using Jayrock.Json;
using Jayrock.Json.Conversion;

public class Localizer : MonoBehaviour {

  // languages supported by the localizer
  public enum Language {
    invalid = -1,
    en = 0,           // english
    jp,           // japanese
    zh_cn,         // chinese (prc)
    zh_tw, // chinese (taiwan)
    ko
  }

  [System.Serializable]
  public class Localization {
    public Language language;
    public TextAsset data;
    public SkinHash[] skins;
  }
  
  [System.Serializable]
  public class SkinHash {
    public string name;
    public GUISkin skin;
  }

  public static Language appLanguage = Language.en;
  public Localization[] localizations;

  public static bool isFirstTimeLoading = true;
  
  // we create a static variable to make it easy for in-game classes to access
  // localized data
  public static Dictionary<string, Dictionary<string, string>> Content = 
                            new Dictionary<string, Dictionary<string, string>>();

  public static Dictionary<string, GUISkin> Skins = new Dictionary<string, GUISkin>();

  public static Localizer singleton;

  void Awake(){
    appLanguage = (Language)PlayerPrefs.GetInt("appLanguage", (int)Language.invalid);

    isFirstTimeLoading = appLanguage == Language.invalid;

    if(isFirstTimeLoading){
      appLanguage = Language.en;
      PlayerPrefs.SetInt("appLanguage", (int)Language.en);
    }

    LoadLocalizedAssets();

    singleton = this;
	}

  /**
   * If a language is set via this method, the Localizer component *must* be
   * reloaded before changes will take place. This normally entails a restart of
   * the game.
   */
  public static void SetLanguage(Language l){
    Debug.Log(l);
    PlayerPrefs.SetInt("appLanguage", (int)l);
    appLanguage = l;
    singleton.LoadLocalizedAssets(); 

    GameHelper.SendMessageToAll("OnLanguageChanged", null, SendMessageOptions.DontRequireReceiver);
  }

  public static void ResetLanguage(){
    PlayerPrefs.DeleteKey("appLanguage");
  }

  public void LoadLocalizedAssets(){
    // figure out which localization to use
    Localization l = System.Array.Find(localizations, delegate(Localization lo){
      return lo.language == appLanguage;
    });

    // load the GUISkins into the hash/dictionary for usage elsewhere
    foreach(SkinHash s in l.skins){
      if(Skins.ContainsKey(s.name))
        Skins.Remove(s.name);

      Skins.Add(s.name, s.skin);
    }

    // import the JSON file as a dictionary (key/value data struct)
    IDictionary primary = (IDictionary)JsonConvert.Import(typeof(IDictionary), l.data.text);

    // shape the non-generic dictionary into a generic one, so that it's easier
    // to use 
    foreach(string pKey in primary.Keys){
      if(Content.ContainsKey(pKey))
        Content.Remove(pKey);

      Content.Add(pKey, new Dictionary<string, string>());

      IDictionary secondary = (IDictionary)primary[pKey];

      foreach(string sKey in secondary.Keys){
        Content[pKey].Add(sKey, (string)secondary[sKey]);
      }
    }

  }
}
