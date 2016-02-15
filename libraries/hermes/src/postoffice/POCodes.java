package hermes.postoffice;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Constants used for Post Office.
 * Contains constants representing keys and mouse buttons.
 */
public class POCodes {

	///////////////////
	/* OSC constants */
	///////////////////
	
	//Default ports for OSC communication
	static class Osc {
		public static final int DEFAULT_PORT_IN = 7000;
		public static final int DEFAULT_PORT_OUT = 7070;
	}
	
	
	/////////////////////
	/* Mouse constants */
	/////////////////////
	
	/**
	 *  Indicated Button sending mouse message
	 */
	public enum Button {
		NO,
		LEFT,
		MIDDLE,
		RIGHT
	}
	
	/**
	 * Indicates button event creating mouse message
	 * PRESSED, RELEASED, DRAGGED correspond to buttons
	 * MOVED corresponds to Button.NO
	 */
	public enum Click {
		PRESSED,
		RELEASED,
		DRAGGED,
		MOVED
	}
	
	
	////////////////////////
	/* Virtual key codes. */
	////////////////////////
	public static class Key {
	    public static final int ENTER = KeyEvent.VK_ENTER;
	    public static final int BACK_SPACE = KeyEvent.VK_BACK_SPACE;
	    public static final int TAB = KeyEvent.VK_TAB;
	    public static final int CANCEL = KeyEvent.VK_CANCEL;
	    public static final int CLEAR = KeyEvent.VK_CLEAR;
	    public static final int SHIFT = KeyEvent.VK_SHIFT;
	    public static final int CONTROL = KeyEvent.VK_CONTROL;
	    public static final int ALT = KeyEvent.VK_ALT;
	    public static final int PAUSE = KeyEvent.VK_PAUSE;
	    public static final int CAPS_LOCK = KeyEvent.VK_CAPS_LOCK;
	    public static final int ESCAPE = KeyEvent.VK_ESCAPE;
	    public static final int SPACE = KeyEvent.VK_SPACE;
	    public static final int PAGE_UP = KeyEvent.VK_PAGE_UP;
	    public static final int PAGE_DOWN = KeyEvent.VK_PAGE_DOWN;
	    public static final int END = KeyEvent.VK_END;
	    public static final int HOME = KeyEvent.VK_HOME;
	
	    /**
	     * Constant for the non-numpad <b>left</b> arrow key.
	     */
	    public static final int LEFT = KeyEvent.VK_LEFT;
	
	    /**
	     * Constant for the non-numpad <b>up</b> arrow key.
	     */
	    public static final int UP = KeyEvent.VK_UP;
	
	    /**
	     * Constant for the non-numpad <b>right</b> arrow key.
	     */
	    public static final int RIGHT = KeyEvent.VK_RIGHT;
	
	    /**
	     * Constant for the non-numpad <b>down</b> arrow key.
	     */
	    public static final int DOWN = KeyEvent.VK_DOWN;
	
	    /**
	     * Constant for the comma key, ","
	     */
	    public static final int COMMA = KeyEvent.VK_COMMA;
	
	    /**
	     * Constant for the minus key, "-"
	     * @since 1.2
	     */
	    public static final int MINUS = KeyEvent.VK_MINUS;
	
	    /**
	     * Constant for the period key, "."
	     */
	    public static final int PERIOD = KeyEvent.VK_PERIOD;
	
	    /**
	     * Constant for the forward slash key, "/"
	     */
	    public static final int SLASH = KeyEvent.VK_SLASH;
	
	    /** VK_0 thru VK_9 are the same as ASCII '0' thru '9' (0x30 - 0x39) */
	    public static final int VK_0 = KeyEvent.VK_0;
	    public static final int VK_1 = KeyEvent.VK_1;
	    public static final int VK_2 = KeyEvent.VK_2;
	    public static final int VK_3 = KeyEvent.VK_3;
	    public static final int VK_4 = KeyEvent.VK_4;
	    public static final int VK_5 = KeyEvent.VK_5;
	    public static final int VK_6 = KeyEvent.VK_6;
	    public static final int VK_7 = KeyEvent.VK_7;
	    public static final int VK_8 = KeyEvent.VK_8;
	    public static final int VK_9 = KeyEvent.VK_9;
	
	    /**
	     * Constant for the semicolon key, ";"
	     */
	    public static final int SEMICOLON = KeyEvent.VK_SEMICOLON;
	
	    /**
	     * Constant for the equals key, "="
	     */
	    public static final int EQUALS = KeyEvent.VK_EQUALS;
	
	    /** VK_A thru VK_Z are the same as ASCII 'A' thru 'Z' (0x41 - 0x5A) */
	    public static final int A = KeyEvent.VK_A;
	    public static final int B = KeyEvent.VK_B;
	    public static final int C = KeyEvent.VK_C;
	    public static final int D = KeyEvent.VK_D;
	    public static final int E = KeyEvent.VK_E;
	    public static final int F = KeyEvent.VK_F;
	    public static final int G = KeyEvent.VK_G;
	    public static final int H = KeyEvent.VK_H;
	    public static final int I = KeyEvent.VK_I;
	    public static final int J = KeyEvent.VK_J;
	    public static final int K = KeyEvent.VK_K;
	    public static final int L = KeyEvent.VK_L;
	    public static final int M = KeyEvent.VK_M;
	    public static final int N = KeyEvent.VK_N;
	    public static final int O = KeyEvent.VK_O;
	    public static final int P = KeyEvent.VK_P;
	    public static final int Q = KeyEvent.VK_Q;
	    public static final int R = KeyEvent.VK_R;
	    public static final int S = KeyEvent.VK_S;
	    public static final int T = KeyEvent.VK_T;
	    public static final int U = KeyEvent.VK_U;
	    public static final int V = KeyEvent.VK_V;
	    public static final int W = KeyEvent.VK_W;
	    public static final int X = KeyEvent.VK_X;
	    public static final int Y = KeyEvent.VK_Y;
	    public static final int Z = KeyEvent.VK_Z;
	
	    /**
	     * Constant for the open bracket key, "["
	     */
	    public static final int OPEN_BRACKET = KeyEvent.VK_OPEN_BRACKET;
	
	    /**
	     * Constant for the back slash key, "\"
	     */
	    public static final int BACK_SLASH = KeyEvent.VK_BACK_SLASH;
	
	    /**
	     * Constant for the close bracket key, "]"
	     */
	    public static final int CLOSE_BRACKET = KeyEvent.VK_CLOSE_BRACKET;
	
	    public static final int NUMPAD0 = KeyEvent.VK_NUMPAD0;
	    public static final int NUMPAD1 = KeyEvent.VK_NUMPAD1;
	    public static final int NUMPAD2 = KeyEvent.VK_NUMPAD2;
	    public static final int NUMPAD3 = KeyEvent.VK_NUMPAD3;
	    public static final int NUMPAD4 = KeyEvent.VK_NUMPAD4;
	    public static final int NUMPAD5 = KeyEvent.VK_NUMPAD5;
	    public static final int NUMPAD6 = KeyEvent.VK_NUMPAD6;
	    public static final int NUMPAD7 = KeyEvent.VK_NUMPAD7;
	    public static final int NUMPAD8 = KeyEvent.VK_NUMPAD8;
	    public static final int NUMPAD9 = KeyEvent.VK_NUMPAD9;
	    public static final int MULTIPLY = KeyEvent.VK_MULTIPLY;
	    public static final int ADD = KeyEvent.VK_ADD;
	
	    /** 
	     * This constant is obsolete, and is included only for backwards
	     * compatibility.
	     */
	    public static final int SEPARATER = KeyEvent.VK_SEPARATER;
	
	    /** 
	     * Constant for the Numpad Separator key. 
	     */
	    public static final int SEPARATOR = KeyEvent.VK_SEPARATOR;
	
	    public static final int SUBTRACT = KeyEvent.VK_SUBTRACT;
	    public static final int DECIMAL = KeyEvent.VK_DECIMAL;
	    public static final int DIVIDE = KeyEvent.VK_DIVIDE;
	    public static final int DELETE = KeyEvent.VK_DELETE; /* ASCII DEL */
	    public static final int NUM_LOCK = KeyEvent.VK_NUM_LOCK;
	    public static final int SCROLL_LOCK = KeyEvent.VK_SCROLL_LOCK;
	
	    /** Constant for the F1 function key. */
	    public static final int F1 = KeyEvent.VK_F1;
	
	    /** Constant for the F2 function key. */
	    public static final int F2 = KeyEvent.VK_F2;
	
	    /** Constant for the F3 function key. */
	    public static final int F3 = KeyEvent.VK_F3;
	
	    /** Constant for the F4 function key. */
	    public static final int F4 = KeyEvent.VK_F4;
	
	    /** Constant for the F5 function key. */
	    public static final int F5 = KeyEvent.VK_F5;
	
	    /** Constant for the F6 function key. */
	    public static final int F6 = KeyEvent.VK_F6;
	
	    /** Constant for the F7 function key. */
	    public static final int F7 = KeyEvent.VK_F7;
	
	    /** Constant for the F8 function key. */
	    public static final int F8 = KeyEvent.VK_F8;
	
	    /** Constant for the F9 function key. */
	    public static final int F9 = KeyEvent.VK_F9;
	
	    /** Constant for the F10 function key. */
	    public static final int F10 = KeyEvent.VK_F10;
	
	    /** Constant for the F11 function key. */
	    public static final int F11 = KeyEvent.VK_F11;
	
	    /** Constant for the F12 function key. */
	    public static final int F12 = KeyEvent.VK_F12;
	
	    /**
	     * Constant for the F13 function key.
	     * @since 1.2
	     */
	    /* F13 - F24 are used on IBM 3270 keyboard; use random range for constants. */
	    public static final int F13 = KeyEvent.VK_F13;
	 
	    /**
	     * Constant for the F14 function key.
	     * @since 1.2
	     */
	    public static final int F14 = KeyEvent.VK_F14;
	 
	    /**
	     * Constant for the F15 function key.
	     * @since 1.2
	     */
	    public static final int F15 = KeyEvent.VK_F15;
	 
	    /**
	     * Constant for the F16 function key.
	     * @since 1.2
	     */
	    public static final int F16 = KeyEvent.VK_F16;
	 
	    /**
	     * Constant for the F17 function key.
	     * @since 1.2
	     */
	    public static final int F17 = KeyEvent.VK_F17;
	 
	    /**
	     * Constant for the F18 function key.
	     * @since 1.2
	     */
	    public static final int F18 = KeyEvent.VK_F18;
	 
	    /**
	     * Constant for the F19 function key.
	     * @since 1.2
	     */
	    public static final int F19 = KeyEvent.VK_F19;
	 
	    /**
	     * Constant for the F20 function key.
	     * @since 1.2
	     */
	    public static final int F20 = KeyEvent.VK_F20;
	 
	    /**
	     * Constant for the F21 function key.
	     * @since 1.2
	     */
	    public static final int F21 = KeyEvent.VK_F21;
	 
	    /**
	     * Constant for the F22 function key.
	     * @since 1.2
	     */
	    public static final int F22 = KeyEvent.VK_F22;
	 
	    /**
	     * Constant for the F23 function key.
	     * @since 1.2
	     */
	    public static final int F23 = KeyEvent.VK_F23;
	 
	    /**
	     * Constant for the F24 function key.
	     * @since 1.2
	     */
	    public static final int F24 = KeyEvent.VK_F24;
	 
	    public static final int PRINTSCREEN = KeyEvent.VK_PRINTSCREEN;
	    public static final int INSERT = KeyEvent.VK_INSERT;
	    public static final int HELP = KeyEvent.VK_HELP;
	    public static final int META = KeyEvent.VK_META;
	
	    public static final int BACK_QUOTE = KeyEvent.VK_BACK_QUOTE;
	    public static final int QUOTE = KeyEvent.VK_QUOTE;
	
	    /**
	     * Constant for the numeric keypad <b>up</b> arrow key.
	     */
	    public static final int KP_UP = KeyEvent.VK_KP_UP;
	
	    /**
	     * Constant for the numeric keypad <b>down</b> arrow key.
	     */
	    public static final int KP_DOWN = KeyEvent.VK_KP_DOWN;
	
	    /**
	     * Constant for the numeric keypad <b>left</b> arrow key.
	     */
	    public static final int KP_LEFT = KeyEvent.VK_KP_LEFT;
	
	    /**
	     * Constant for the numeric keypad <b>right</b> arrow key.
	     */
	    public static final int KP_RIGHT = KeyEvent.VK_KP_RIGHT;
	    
	    /* For European keyboards */
	    /** @since 1.2 */
	    public static final int DEAD_GRAVE = KeyEvent.VK_DEAD_GRAVE;
	    /** @since 1.2 */
	    public static final int DEAD_ACUTE = KeyEvent.VK_DEAD_ACUTE;
	    /** @since 1.2 */
	    public static final int DEAD_CIRCUMFLEX = KeyEvent.VK_DEAD_CIRCUMFLEX;
	    /** @since 1.2 */
	    public static final int DEAD_TILDE = KeyEvent.VK_DEAD_TILDE;
	    /** @since 1.2 */
	    public static final int DEAD_MACRON = KeyEvent.VK_DEAD_MACRON;
	    /** @since 1.2 */
	    public static final int DEAD_BREVE = KeyEvent.VK_DEAD_BREVE;
	    /** @since 1.2 */
	    public static final int DEAD_ABOVEDOT = KeyEvent.VK_DEAD_ABOVEDOT;
	    /** @since 1.2 */
	    public static final int DEAD_DIAERESIS = KeyEvent.VK_DEAD_DIAERESIS;
	    /** @since 1.2 */
	    public static final int DEAD_ABOVERING = KeyEvent.VK_DEAD_ABOVERING;
	    /** @since 1.2 */
	    public static final int DEAD_DOUBLEACUTE = KeyEvent.VK_DEAD_DOUBLEACUTE;
	    /** @since 1.2 */
	    public static final int DEAD_CARON = KeyEvent.VK_DEAD_CARON;
	    /** @since 1.2 */
	    public static final int DEAD_CEDILLA = KeyEvent.VK_DEAD_CEDILLA;
	    /** @since 1.2 */
	    public static final int DEAD_OGONEK = KeyEvent.VK_DEAD_OGONEK;
	    /** @since 1.2 */
	    public static final int DEAD_IOTA = KeyEvent.VK_DEAD_IOTA;
	    /** @since 1.2 */
	    public static final int DEAD_VOICED_SOUND = KeyEvent.VK_DEAD_VOICED_SOUND;
	    /** @since 1.2 */
	    public static final int DEAD_SEMIVOICED_SOUND = KeyEvent.VK_DEAD_SEMIVOICED_SOUND;
	
	    /** @since 1.2 */
	    public static final int AMPERSAND = KeyEvent.VK_AMPERSAND;
	    /** @since 1.2 */
	    public static final int ASTERISK = KeyEvent.VK_ASTERISK;
	    /** @since 1.2 */
	    public static final int QUOTEDBL = KeyEvent.VK_QUOTEDBL;
	    /** @since 1.2 */
	    public static final int LESS = KeyEvent.VK_LESS;
	
	    /** @since 1.2 */
	    public static final int GREATER = KeyEvent.VK_GREATER;
	    /** @since 1.2 */
	    public static final int BRACELEFT = KeyEvent.VK_BRACELEFT;
	    /** @since 1.2 */
	    public static final int BRACERIGHT = KeyEvent.VK_BRACERIGHT;
	
	    /**
	     * Constant for the "@" key.
	     * @since 1.2
	     */
	    public static final int AT = KeyEvent.VK_AT;
	 
	    /**
	     * Constant for the ":" key.
	     * @since 1.2
	     */
	    public static final int COLON = KeyEvent.VK_COLON;
	 
	    /**
	     * Constant for the "^" key.
	     * @since 1.2
	     */
	    public static final int CIRCUMFLEX = KeyEvent.VK_CIRCUMFLEX;
	 
	    /**
	     * Constant for the "$" key.
	     * @since 1.2
	     */
	    public static final int DOLLAR = KeyEvent.VK_DOLLAR;
	 
	    /**
	     * Constant for the Euro currency sign key.
	     * @since 1.2
	     */
	    public static final int EURO_SIGN = KeyEvent.VK_EURO_SIGN;
	 
	    /**
	     * Constant for the "!" key.
	     * @since 1.2
	     */
	    public static final int EXCLAMATION_MARK = KeyEvent.VK_EXCLAMATION_MARK;
	 
	    /**
	     * Constant for the inverted exclamation mark key.
	     * @since 1.2
	     */
	    public static final int INVERTED_EXCLAMATION_MARK = KeyEvent.VK_INVERTED_EXCLAMATION_MARK;
	 
	    /**
	     * Constant for the "(" key.
	     * @since 1.2
	     */
	    public static final int LEFT_PARENTHESIS = KeyEvent.VK_LEFT_PARENTHESIS;
	 
	    /**
	     * Constant for the "#" key.
	     * @since 1.2
	     */
	    public static final int NUMBER_SIGN = KeyEvent.VK_NUMBER_SIGN;
	 
	    /**
	     * Constant for the "+" key.
	     * @since 1.2
	     */
	    public static final int PLUS = KeyEvent.VK_PLUS;
	 
	    /**
	     * Constant for the ")" key.
	     * @since 1.2
	     */
	    public static final int RIGHT_PARENTHESIS = KeyEvent.VK_RIGHT_PARENTHESIS;
	 
	    /**
	     * Constant for the "_" key.
	     */
	    public static final int UNDERSCORE = KeyEvent.VK_UNDERSCORE;
	 
	    /**
	     * Constant for the Microsoft Windows "Windows" key.
	     * It is used for both the left and right version of the key.  
	     */
	    public static final int WINDOWS = KeyEvent.VK_WINDOWS;
	 
	    /**
	     * Constant for the Microsoft Windows Context Menu key.
	     * @since 1.5
	     */
	    public static final int CONTEXT_MENU = KeyEvent.VK_CONTEXT_MENU;
	 
	    /* for input method support on Asian Keyboards */
	
	    /* not clear what this means - listed in Microsoft Windows API */
	    public static final int FINAL = KeyEvent.VK_FINAL;
	    
	    /** Constant for the Convert function key. */
	    /* Japanese PC 106 keyboard, Japanese Solaris keyboard: henkan */
	    public static final int CONVERT = KeyEvent.VK_CONVERT;
	
	    /** Constant for the Don't Convert function key. */
	    /* Japanese PC 106 keyboard: muhenkan */
	    public static final int NONCONVERT = KeyEvent.VK_NONCONVERT;
	    
	    /** Constant for the Accept or Commit function key. */
	    /* Japanese Solaris keyboard: kakutei */
	    public static final int ACCEPT = KeyEvent.VK_ACCEPT;
	
	    /* not clear what this means - listed in Microsoft Windows API */
	    public static final int MODECHANGE = KeyEvent.VK_MODECHANGE;
	
	    /* replaced by VK_KANA_LOCK for Microsoft Windows and Solaris; 
	       might still be used on other platforms */
	    public static final int KANA = KeyEvent.VK_KANA;
	
	    /* replaced by VK_INPUT_METHOD_ON_OFF for Microsoft Windows and Solaris; 
	       might still be used for other platforms */
	    public static final int KANJI = KeyEvent.VK_KANJI;
	
	    /**
	     * Constant for the Alphanumeric function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: eisuu */
	    public static final int ALPHANUMERIC = KeyEvent.VK_ALPHANUMERIC;
	 
	    /**
	     * Constant for the Katakana function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: katakana */
	    public static final int KATAKANA = KeyEvent.VK_KATAKANA;
	 
	    /**
	     * Constant for the Hiragana function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: hiragana */
	    public static final int HIRAGANA = KeyEvent.VK_HIRAGANA;
	 
	    /**
	     * Constant for the Full-Width Characters function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: zenkaku */
	    public static final int FULL_WIDTH = KeyEvent.VK_FULL_WIDTH;
	 
	    /**
	     * Constant for the Half-Width Characters function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: hankaku */
	    public static final int HALF_WIDTH = KeyEvent.VK_HALF_WIDTH;
	 
	    /**
	     * Constant for the Roman Characters function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: roumaji */
	    public static final int ROMAN_CHARACTERS = KeyEvent.VK_ROMAN_CHARACTERS;
	 
	    /**
	     * Constant for the All Candidates function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard - VK_CONVERT + ALT: zenkouho */
	    public static final int ALL_CANDIDATES = KeyEvent.VK_ALL_CANDIDATES;
	 
	    /**
	     * Constant for the Previous Candidate function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard - VK_CONVERT + SHIFT: maekouho */
	    public static final int PREVIOUS_CANDIDATE = KeyEvent.VK_PREVIOUS_CANDIDATE;
	 
	    /**
	     * Constant for the Code Input function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard - VK_ALPHANUMERIC + ALT: kanji bangou */
	    public static final int CODE_INPUT = KeyEvent.VK_CODE_INPUT;
	 
	    /**
	     * Constant for the Japanese-Katakana function key.
	     * This key switches to a Japanese input method and selects its Katakana input mode.
	     * @since 1.2
	     */
	    /* Japanese Macintosh keyboard - VK_JAPANESE_HIRAGANA + SHIFT */
	    public static final int JAPANESE_KATAKANA = KeyEvent.VK_JAPANESE_KATAKANA;
	 
	    /**
	     * Constant for the Japanese-Hiragana function key.
	     * This key switches to a Japanese input method and selects its Hiragana input mode.
	     * @since 1.2
	     */
	    /* Japanese Macintosh keyboard */
	    public static final int JAPANESE_HIRAGANA = KeyEvent.VK_JAPANESE_HIRAGANA;
	 
	    /**
	     * Constant for the Japanese-Roman function key.
	     * This key switches to a Japanese input method and selects its Roman-Direct input mode.
	     * @since 1.2
	     */
	    /* Japanese Macintosh keyboard */
	    public static final int JAPANESE_ROMAN = KeyEvent.VK_JAPANESE_ROMAN;
	
	    /**
	     * Constant for the locking Kana function key.
	     * This key locks the keyboard into a Kana layout.
	     * @since 1.3
	     */
	    /* Japanese PC 106 keyboard with special Windows driver - eisuu + Control; Japanese Solaris keyboard: kana */
	    public static final int KANA_LOCK = KeyEvent.VK_KANA_LOCK;
	
	    /**
	     * Constant for the input method on/off key.
	     * @since 1.3
	     */
	    /* Japanese PC 106 keyboard: kanji. Japanese Solaris keyboard: nihongo */
	    public static final int INPUT_METHOD_ON_OFF = KeyEvent.VK_INPUT_METHOD_ON_OFF;
	
	    /* for Sun keyboards */
	    /** @since 1.2 */
	    public static final int CUT = KeyEvent.VK_CUT;
	    /** @since 1.2 */
	    public static final int COPY = KeyEvent.VK_COPY;
	    /** @since 1.2 */
	    public static final int PASTE = KeyEvent.VK_PASTE;
	    /** @since 1.2 */
	    public static final int UNDO = KeyEvent.VK_UNDO;
	    /** @since 1.2 */
	    public static final int AGAIN = KeyEvent.VK_AGAIN;
	    /** @since 1.2 */
	    public static final int FIND = KeyEvent.VK_FIND;
	    /** @since 1.2 */
	    public static final int PROPS = KeyEvent.VK_PROPS;
	    /** @since 1.2 */
	    public static final int STOP = KeyEvent.VK_STOP;
	    
	    /**
	     * Constant for the Compose function key.
	     * @since 1.2
	     */
	    public static final int COMPOSE = KeyEvent.VK_COMPOSE;
	 
	    /**
	     * Constant for the AltGraph function key.
	     * @since 1.2
	     */
	    public static final int ALT_GRAPH = KeyEvent.VK_ALT_GRAPH;
	
	    /**
	     * Constant for the Begin key.
	     * @since 1.5
	     */
	    public static final int BEGIN = KeyEvent.VK_BEGIN;
	
	    /**
	     * This value is used to indicate that the keyCode is unknown.
	     * KEY_TYPED events do not have a keyCode value; this value 
	     * is used instead.  
	     */
	    public static final int UNDEFINED = KeyEvent.VK_UNDEFINED;
	
	    /**
	     * KEY_PRESSED and KEY_RELEASED events which do not map to a
	     * valid Unicode character use this for the keyChar value.
	     */
	    public static final char CHAR_UNDEFINED   = 0xFFFF;
	}
}
