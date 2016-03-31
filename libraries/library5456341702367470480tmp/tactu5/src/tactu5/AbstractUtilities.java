package tactu5;

import java.lang.reflect.Array;
/*
 * TACTU5 by Alessandro Capozzo  
 * www.abstract-codex.net
 */

public class AbstractUtilities {
	
	

 public	AbstractUtilities () 
	
	
	{
		
	}

	static Object AbstractExpand(Object a, int pos)
	      {
		
	     Class cl = a.getClass();
		
	    if (!cl.isArray()) return null;
	    
	    
	    int lengtha = Array.getLength(a);
	    int newLength = lengtha+1;
	    Class componentType = a.getClass().getComponentType();
	    Object newArray = Array.newInstance(componentType, newLength);

	    if(pos+1>lengtha)  {

	     System.arraycopy(a, 0, newArray, 0, lengtha);

	    } 
	    else {

	      System.arraycopy(a, 0, newArray, 0, pos);
	      System.arraycopy(a, pos, newArray, pos+1, lengtha-pos);

	    }
	    return newArray;
	}

}
