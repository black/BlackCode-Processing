package ams.tool;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import processing.core.*;

public class SignerProperties {

	private final String TAB = "\t";
	private HashMap<String, String> map = null;

	public SignerProperties() {
		map = new HashMap<String, String>();
	}

	public void put(String k, String v) {
		v = v.trim();
		if (v.length() == 0)
			v = "?";
		map.put(k, v);
	}

	public String get(String k) {
		String v = map.get(k);
		if (v == null || v.equals("?"))
			v = "";
		return v;
	}

	public void loadFromTSV(File file) {
		String[] lines = PApplet.loadStrings(file);
		for (String line : lines) {
			int idx = line.indexOf(TAB);
			if (idx > 0) {
				String mKey = line.substring(0, idx);
				String mValue = line.substring(idx+1);
				map.put(mKey, mValue);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void saveAsTSV(File file) {
		String[] lines = new String[map.size()];
		int c = 0;
		Iterator iter = map.entrySet().iterator(); 
		while (iter.hasNext ()) {
			Map.Entry me = (Map.Entry)iter.next();
			lines[c] = me.getKey() + TAB + me.getValue();
			c++;
		}
		Arrays.sort(lines);
		PApplet.saveStrings(file, lines);
	}

}
