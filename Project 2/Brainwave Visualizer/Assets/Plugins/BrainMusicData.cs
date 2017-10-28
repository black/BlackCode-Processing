using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using System.Xml.Serialization;
using System.Text.RegularExpressions;

/**
 * Struct to encapsulate information about a song.
 */
[XmlRoot("song")]
public struct Song {
  public string title;

  public string album;

  public string artist;

  /**
   * Determine whether two instances of Song are equivalent.
   */
  public static bool operator ==(Song s1, Song s2){
    return s1.title == s2.title &&
           s1.album == s2.album &&
           s1.artist == s2.artist;
  }

  /**
   * Determine whether two instances of Song are not equivalent.
   */
  public static bool operator !=(Song s1, Song s2){
    return !(s1 == s2);
  }

  /**
   * Determine whether a generic object instance is equivalent to
   * the current Song instance.
   */
  public override bool Equals(object s1){
    if(!(s1 is Song))
      return false;

    return this == (Song)s1;
  }

  /**
   * Generate a hash of the Song. 
   *
   * This is done by XORing the hash codes for each property of a Song.
   */
  public override int GetHashCode(){
    return title.GetHashCode() ^ album.GetHashCode() ^ artist.GetHashCode();
  }

  public override string ToString(){
    return artist + " - " + album + " - " + title;
  }

  [XmlIgnore]
  public string DataFileName {
    get { 
      // sanitize the file name - only allow word characters, spaces, and dashes
      Regex r = new Regex("[^\\w -]");

      return r.Replace(ToString(), "") + ".xml"; 
    }
    set { }
  }
}

/**
 * ORM wrapper to associate headset output with a particular song.
 */
[XmlRoot("brainMusicData")]
public class BrainMusicData : IBrainwaveDataPlayer {
  // the song that the data is associated with
  public Song song;

  // the headset data
  [XmlArray("dataPoints")]
  [XmlArrayItem("thinkGearData", typeof(ThinkGearData))]
  public List<ThinkGearData> brainwaveData;

  // the date the data set was created
  public DateTime createdAt;

  [XmlIgnore]
  public int currentIndex = 0;

  /*
   * Constructors
   */

  /**
   * No-argument constructor that just populates the Song with empty strings.
   */
  public BrainMusicData() : this("", "", ""){}

  public BrainMusicData(string title, string album, string artist){
    song.title = title;
    song.album = album;
    song.artist = artist;

    brainwaveData = new List<ThinkGearData>();

    createdAt = DateTime.Now;
  }

  public BrainMusicData(Song copy) : this(copy.title, copy.album, copy.artist){}

  [XmlIgnore]
  public int dataPoints {
    get { return brainwaveData.Count; }
  }

  [XmlIgnore]
  public double duration {
    get { 
      if(brainwaveData.Count == 0)
        return 0.0;
      else
        return brainwaveData[brainwaveData.Count - 1].elapsedTime; 
    }
  }

  /**
   * This method finds the closest ThinkGearData associated with a particular
   * time offset.
   *
   * This is done by "walking" through the array (either upwards or downwards)
   * until we find two adjacent timestamps that the method argument can be sandwiched
   * between. Then, we just return the ThinkGearData associated with the lower
   * bound timestamp.
   *
   * This method returns null if a search wasn't possible (i.e. if there was no
   * ThinkGearData recorded)
   */
  public ThinkGearData DataAt(double elapsedTime){
    int lowerBound = currentIndex;
    int upperBound = currentIndex + 1;

    // check to see whether a search should even be done. don't search the array
    // if there are less than two timestamped elements
    if(brainwaveData.Count < 2)
      return brainwaveData.Count == 0 ? new ThinkGearData() : brainwaveData[0];

    // loop while we haven't walked off the end of the array
    while(lowerBound >= 0){
      // we've found the right data, so let's stop incrementing the pointer and return
      if(elapsedTime >= brainwaveData[lowerBound].elapsedTime && 
         (upperBound >= brainwaveData.Count || elapsedTime < brainwaveData[upperBound].elapsedTime))
        break;
      else if(elapsedTime < brainwaveData[lowerBound].elapsedTime){
        lowerBound--;
        upperBound--;
      }
      else if(elapsedTime > brainwaveData[upperBound].elapsedTime){
        lowerBound++;
        upperBound++;
      }
    }

    currentIndex = lowerBound < 0 ? 0 : lowerBound;

    return brainwaveData[currentIndex]; 
  }

  /**
   * Find the most recent BrainMusicData instance based on the passed Song instance.
   */
  public static BrainMusicData Load(string path){
    // if the file doesn't exist, then there's no brainwave data for that song
    if(!File.Exists(path))
      return null;
    // deserialize the contents of the file
    else {
      Stream stream = new FileStream(path, FileMode.Open, FileAccess.Read, FileShare.None);
      XmlSerializer xs = new XmlSerializer(typeof(BrainMusicData));
      
      BrainMusicData data = null;

      try {
        data = (BrainMusicData)xs.Deserialize(stream);
      }
      catch(FileNotFoundException e){
        Debug.Log(e);
      }

      stream.Close();

      return data;
    }
  }

  /**
   * Adds a new ThinkGearData to the list of ThinkGearDatas.
   */
  public void Add(ThinkGearData data){
    brainwaveData.Add(data);
  }

  public override string ToString(){
    string output = song.ToString();

    for(int i = 0; i < brainwaveData.Count; i++)
      output += "\n" + brainwaveData[i];

    return output;
  }

  public void Write(string path){
    Stream stream = new FileStream(path,
                                   FileMode.Create, 
                                   FileAccess.Write,
                                   FileShare.None);
    XmlSerializer xs = new XmlSerializer(typeof(BrainMusicData));
    xs.Serialize(stream, this);
    stream.Close();
  }
}
