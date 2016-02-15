package ams.tool;

import java.io.File;
import java.io.FileFilter;

public class FoldersOnlyFilter implements FileFilter {


	public FoldersOnlyFilter(){
	}

	@Override
	public boolean accept(File pathname) {
		// TODO Auto-generated method stub
		return pathname.isDirectory();
	}
}
