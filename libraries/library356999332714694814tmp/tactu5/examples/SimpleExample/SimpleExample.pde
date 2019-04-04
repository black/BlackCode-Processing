import tactu5.*;
import tactu5.Tactu5SimpleSequencer;

// creating an instance of Tactu5
Tactu5 tactu5;

// creating an aggregator instance
Aggregator aggregator;

void setup() {
       
  
       Tactu5Utilities tactu5utilities = new Tactu5Utilities();
  
       // define various frequencies
  
        float freqA = 440.000;
        float freqC = tactu5utilities.noteToFreq ( T5Notes.C, 4 );
        float freqE = tactu5utilities.noteToFreq ( T5Notes.E, 4 );
        float freqG = tactu5utilities.noteToFreq ( T5Notes.G, 3 );             
        

        // declaring some notes
        
        Note noteA = new Note(freqA,200,1,1,100,false);
        Note noteC = new Note(freqC,200,1,1,100,false);
        Note noteE = new Note(freqE,200,1,1,100,false);
        Note noteG = new Note(freqG,200,1,1,100,false);
        
        // declaring new chords
        
        Cluster chord_1 = new Cluster();
        Cluster chord_2 = new Cluster();
        
        
        chord_1.addNote(noteA);
        chord_1.addNote(noteC);
        chord_1.addNote(noteE);
        
        chord_2.addNote(noteC);
        chord_2.addNote(noteG);
        chord_2.addNote(noteE);
        
        // creating a chord container
        
        ClusterSequence chordSequence = new ClusterSequence();
        
        chordSequence.addCluster(chord_1);
        chordSequence.addCluster(chord_2);
        chordSequence.addCluster(chord_1);
        chordSequence.addCluster(chord_2);
        
        aggregator = new Aggregator();
        
        //add the sequence to aggregator
        
        aggregator.addClusterSequence(chordSequence);
        
        // initializing and feed internal sequencer, boolean value inidicate if it will loop
        
        tactu5 = new Tactu5(this,aggregator.getScore(),true);
       
        // start sequencer
        tactu5.start();
  
  
  }
  
void draw() {
    
    // do something
    
}

void noteReceiver(Note n){
  
  // send data to a synth
  
 println(n.getFrequency());
 println(n.getDuration());
 println(n.getSustain());
 println(n.getPan());
  
  
  
}
void stop() {
  
  // close Tactu5 sequencer
  tactu5.closeSequencer();
  super.stop();
  
}
