using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Text;

public enum MindSetVersions {
  Firmware1_6 = 0,
  Firmware1_7,
  Firmware1_7_9,
  ASIC
}

public class MindSetOutput : IBrainwaveDataPlayer {
  private MindSetVersions firmwareVersion = MindSetVersions.ASIC;
  
  public MindSetOutput(MindSetVersions version){
    firmwareVersion = version;
  }

  public MindSetOutput(){
    firmwareVersion = MindSetVersions.ASIC;
  }

  public ThinkGearData DataAt(double secondsFromBeginning){
      return new ThinkGearData(secondsFromBeginning,
                               (int)ThinkGear.GetValue(ThinkGear.DATA_MEDITATION),
                               (int)ThinkGear.GetValue(ThinkGear.DATA_ATTENTION),
                               (int)ThinkGear.GetValue(ThinkGear.DATA_POOR_SIGNAL),
                               ThinkGear.GetValue(ThinkGear.DATA_DELTA),
                               ThinkGear.GetValue(ThinkGear.DATA_THETA),
                               ThinkGear.GetValue(ThinkGear.DATA_ALPHA1),
                               ThinkGear.GetValue(ThinkGear.DATA_ALPHA2),
                               ThinkGear.GetValue(ThinkGear.DATA_BETA1),
                               ThinkGear.GetValue(ThinkGear.DATA_BETA2),
                               ThinkGear.GetValue(ThinkGear.DATA_GAMMA1),
                               ThinkGear.GetValue(ThinkGear.DATA_GAMMA2),
                               ThinkGear.GetValue(ThinkGear.DATA_RAW));
  }

  public int dataPoints {
    get { return -1; }
  }

  public double duration {
    get { return 0.0; }
  }
}
