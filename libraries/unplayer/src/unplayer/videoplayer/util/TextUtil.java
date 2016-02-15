/**
 * unPlarer 0.8.2
 *
 * @author Alvaro Lopez, Juan Baquero
 * 
 * http://code.google.com/p/unplayer/
 **/

package unplayer.videoplayer.util;

import processing.core.PFont;

public class TextUtil {

	public static int getwidth(PFont font,float size, String str)
	{
		float total =0;

		for(int i=0;i<str.length();i++)
		{
			total = total+(font.width(str.charAt(i))*size);
			
			if(i!=str.length()-1)
				total = total + (font.kern(str.charAt(i), str.charAt(i+1))*size);
			
		}
		return (int)total;
	}

	public static String getFile(String filename) {
		
		int i;
		String file = "";
		for(i=filename.length()-1;i>=0;i--)
		{
			if(filename.charAt(i)=='/' || filename.charAt(i)=='\\')
			{
				file=filename.substring(i+1);
				break;
			}
		}
		
		if(i==-1)
			file=filename;
		
		return file;
	}
}
