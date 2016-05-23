public class PFrame extends Frame {
    public SecondaryApplet s;  
  
    public PFrame() {
        setBounds(100,100,200,400);
        s = new SecondaryApplet();
        add(s);
        s.init();
        show();
    }
    
    public PFrame(String name) {
        this();
        this.setTitle(name);
    }
    
    public PFrame(String name, int w, int h){
      setBounds(100,100,w,h);
        s = new SecondaryApplet(w,h);
        add(s);
        s.init();
        show();
      this.setTitle(name);
    }
    
}
