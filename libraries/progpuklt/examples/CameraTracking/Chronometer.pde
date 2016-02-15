// Simple class to compute frames-per-second.
class Chronometer
{
    int fcount;
    int lastmillis;
    int interval;
    float fps;
    float time;
    boolean updated;

    Chronometer()
    {
        lastmillis = 0;
        fcount = 0;  
        interval = 5;
        updated = false;        
    }

    Chronometer(int t)
    {
        lastmillis = 0;
        fcount = 0;  
        interval = t;
        updated = false;
    }
    
    void inc()
    {
      fcount++;    
    }
  
    void update()
    {
      
       int t = millis();
       if (t - lastmillis > interval * 1000)
       {
           fps = float(fcount) / interval;
           time = float(t) / 1000;
           fcount = 0;
           lastmillis = t;
           updated = true;
       }
       else updated = false;
    }
    
    void printfps()
    {
        if (updated) println("FPS: " + fps);
    }
}

