public interface IBrainwaveDataPlayer {
  ThinkGearData DataAt(double time);

  int dataPoints {
    get;
  }

  double duration {
    get;
  }
}
