package tactu5;

/*
 * TACTU5 by Alessandro Capozzo  
 * www.abstract-codex.net
 */
public interface T5Notes {
	  
	  // frequency hardcoded in italian
	  
	  float SI_DIESIS = 32.7032f;
	  float DO = 32.7032f;
	  float DO_DIESIS = 34.6478f;
	  float RE_BEMOLLE = 34.6478f;
	  float RE = 36.7081f;
	  float RE_DIESIS = 38.8909f;
	  float MI_BEMOLLE = 38.8909f;
	  float MI = 41.203453f;
	  float FA_BEMOLLE = 41.2034f;
	  float MI_DIESIS = 43.6535f;
	  float FA = 43.6535f;
	  float FA_DIESIS = 46.2493f;
	  float SOL_BEMOLLE = 46.2493f;
	  float SOL = 48.9994f;
	  float SOL_DIESIS = 51.9131f;
	  float LA_BEMOLLE = 51.9131f;
	  float LA = 55.0000f;
	  float LA_DIESIS = 58.27048f;
	  float SI_BEMOLLE = 58.27048f;
	  float SI = 61.7354f;
	  
	  // frequency hardcoded in english
	  
	  float B_SHARP = 32.7032f;
	  float C = 32.7032f;
	  float C_SHARP = 34.6478f;
	  float D_FLAT = 34.6478f;
	  float D = 36.7081f;
	  float D_SHARP = 38.8909f;
	  float E_FLAT = 38.8909f;
	  float E = 41.203453f;
	  float F_FLAT = 41.2034f;
	  float E_SHARP = 43.6535f;
	  float F = 43.6535f;
	  float F_SHARP = 46.2493f;
	  float G_FLAT = 46.2493f;
	  float G = 48.9994f;
	  float G_SHARP = 51.9131f;
	  float A_FLAT = 51.9131f;
	  float A = 55.0000f;
	  float A_SHARP = 58.27048f;
	  float B_FLAT = 58.27048f;
	  float B = 61.7354f;
	  
	  /*
	  T - T - s - T - T - T - s Ionio (come l'attuale scala maggiore)
	  T - s - T - T - T - s - T Dorico
	  s - T - T - T - s - T - T Frigio
	   T - T - T - s - T - T - s Lidio
	  T - T - s - T - T - s - T Misolidio
	  T - s - T - T - s - T - T Eolio (come l'attuale scala minore)
	   s - T - T - s - T - T - T Locrio
	   */
	  // modes hardcoded in english
	  
	  int[] MAJOR = { 2, 2, 1, 2, 2, 2, 1 };
	  int[] IONIAN = { 2, 2, 1, 2, 2, 2, 1 };
	  int[] DORIAN = { 2, 1, 2, 2, 2, 1, 2 };
	  int[] PHRYGIAN = {1, 2, 2, 2, 1, 2, 2 };
	  int[] LYDIAN =  {2, 2, 2, 1, 2, 2, 1 };
	  int[] MIXOLYDIAN = { 2, 2, 1, 2, 2, 1, 2};
	  int[] AEOLIAN = { 2, 1, 2, 2, 1, 2, 2 };
	  int[] NATURAL_MINOR = { 2, 1, 2, 2, 1, 2, 2 };
	  int[] LOCRIAN = { 1, 2, 2, 1, 2, 2, 2 };
	  
	  int[] CHROMATIC = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	  int[] WHOLE_TONE = { 2, 2, 2, 2, 2, 2 };
	  int[] OCTATONIC_TONE = { 2, 1, 2, 1, 2, 1, 2, 1 };
	  int[] OCTATONIC_HTONE = { 1, 2, 1, 2, 1, 2, 1, 2 };
	  
	  int[] PENTAPHONIC_MINOR = { 3, 2, 2, 3, 2 };
	  int[] PENTAPHONIC_MAJOR = { 2, 2, 3, 2, 3 };
	  
	  float [] SPECIAL = { 2.3f, 2.1f, 3.4f, 2.1f, 3 };
	  
	  /*
	  
	  int[] PENTAPHONIC_MINOR =
	  int[] PENTAPHONIC_MAJOR =
	   */
	  
	  }
