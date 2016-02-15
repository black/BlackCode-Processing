package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Utility {
	
	public static ArrayList<String> getFileNames( String directory ){
		File dir = new File(directory);
		ArrayList<String> files = new ArrayList<String>();
		String[] children = dir.list();
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
		    for (int i=0; i<children.length; i++) {
		        // Get filename of file or directory
		        String filename = children[i];
		        if( filename.startsWith(".") == false ){
		        	files.add(directory+filename);
		        }
		    }
		}
		return files;
	}
	
	public static ArrayList<String> mergeList( ArrayList<String> a, ArrayList<String> b ){
		ArrayList<String> merge = new ArrayList<String>();
		for( int i=0; i<a.size(); i++ )
			merge.add(a.get(i));
		for( int i=0; i<b.size(); i++ )
			merge.add(b.get(i));
		return merge;
	}
	
	public static String leggiFile (String nomeFile) throws IOException {
		File f = new File( nomeFile );
		if( f.exists() ){
			InputStream is = null;
			InputStreamReader isr = null;
			StringBuffer sb = new StringBuffer();
			char[] buf = new char[1024];
			int len;
			is = new FileInputStream (nomeFile);
			isr = new InputStreamReader (is);
			while ((len = isr.read (buf)) > 0)
				sb.append (buf, 0, len);
			if (isr!=null) {
				isr.close();
			}
			return sb.toString();
		}else
			return "";
	} 
	
}
