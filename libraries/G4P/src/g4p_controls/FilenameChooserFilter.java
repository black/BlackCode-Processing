/*
  Part of the G4P library for Processing 
  	http://www.lagers.org.uk/g4p/index.html
	http://sourceforge.net/projects/g4p/files/?source=navbar

  Copyright (c) 2012 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package g4p_controls;

import java.io.File;
import java.io.FilenameFilter;

import processing.core.PApplet;

class FilenameChooserFilter implements FilenameFilter {

	private final String[] ftypes;

	public FilenameChooserFilter(String types){
		ftypes = PApplet.split(types.toLowerCase(), ',');
		for(String e : ftypes)
			e = e.trim();
	}

//    public boolean accept(File f) {
//        if (f != null) {
//            if (f.isDirectory()) {
//                return true;
//            }
//            // NOTE: we tested implementations using Maps, binary search
//            // on a sorted list and this implementation. All implementations
//            // provided roughly the same speed, most likely because of
//            // overhead associated with java.io.File. Therefor we've stuck
//            // with the simple lightweight approach.
//            String fileName = f.getName();
//            int i = fileName.lastIndexOf('.');
//            if (i > 0 && i < fileName.length() - 1) {
//                String desiredExtension = fileName.substring(i+1).
//                        toLowerCase(Locale.ENGLISH);
//                for (String extension : lowerCaseExtensions) {
//                    if (desiredExtension.equals(extension)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
    
	public boolean accept(File dir, String name) {
		String fext = null;
		int i = name.lastIndexOf('.');
		if (i > 0 &&  i < name.length() - 1)
			fext = name.substring(i+1).toLowerCase();
		if(fext != null){
			for(String e : ftypes)
				if(fext.equals(e))
					return true;
		}
		return false;
	}

}
