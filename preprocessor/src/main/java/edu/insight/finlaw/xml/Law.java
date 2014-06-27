package edu.insight.finlaw.xml;

import java.util.ArrayList;
import java.util.List;



public class Law {

	public List<Part> parts = new ArrayList<Part>();
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(parts.get(0));
		return buffer.toString().trim();
	}

}
