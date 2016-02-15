/**
 * unPlarer 0.8.2
 *
 * @author Alvaro Lopez, Juan Baquero
 * 
 * http://code.google.com/p/unplayer/
 **/

package unplayer.videoplayer.util;

public class TimeUtil {

	public static String getTime(long lo1,long lo2)
	{
		String str1 = "";
		String str2 = "";
		
		if(lo2>=3600)
		{
			int ho1 = (int)(lo1/3600);
			int ho2 = (int)(lo2/3600);
			
			if(ho1<10) str1="0"+ho1+":";
			else str1=ho1+":";
			
			if(ho2<10) str2="0"+ho2+":";
			else str2=ho2+":";
			
			lo1 = (int)(lo1%3600);
			lo2 = (int)(lo2%3600);
		}
		
		int min1 = (int)(lo1/60);
		int min2 = (int)(lo2/60);
		int sec1 = (int)(lo1%60);
		int sec2 = (int)(lo2%60);
		
		if(min1<10) str1=str1+"0"+min1+":";
		else str1=str1+min1+":";
		
		if(min2<10) str2=str2+"0"+min2+":";
		else str2=str2+min2+":";
		
		if(sec1<10) str1=str1+"0"+sec1;
		else str1=str1+sec1;
		
		if(sec2<10) str2=str2+"0"+sec2;
		else str2=str2+sec2;
		
		return str1+" / "+str2;
		
	}
}
