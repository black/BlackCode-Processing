package mother.library;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import foetus.FoetusParameter;

public class OSC_Namespace_Serializer {
	/*
	public static void Serialize_Synths(SynthContainer scIn, String filePath) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try	{			
			for(int i = 0; i<scIn.Synths().size(); i++) {
				// get an instance of builder
				DocumentBuilder db = dbf.newDocumentBuilder();

				// create an instance of DOM
				Document dom = db.newDocument();
				
				ChildWrapper wrapper;

				wrapper = scIn.Synths().get(i);
			
				System.out.println("Started making synth file.. ");
				createDOMSynthTree(dom, wrapper);
				String pathAndName = filePath + "//" + wrapper.GetTypeName() + ".node"; 
				printToFile(dom, pathAndName);
				System.out.println("Generated SYNTH file successfully.");
			}
		} 
		catch (ParserConfigurationException pce) {
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
		}
	}
	*/
	
	public static void Serialize_Synth(ChildWrapper wrapper, String filePath) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try	{
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// create an instance of DOM
			Document dom = db.newDocument();
			
			System.out.println("Started making synth file: " + wrapper.GetTypeName());
			createDOMSynthTree(dom, wrapper);
			String pathAndName = filePath + "//" + wrapper.GetTypeName() + ".node"; 
			printToFile(dom, pathAndName);
			System.out.println("Generated SYNTH file successfully: " + wrapper.GetTypeName());
		} 
		catch (ParserConfigurationException pce) {
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
		}
	}

	/*
	private static void createDOMSynthsTree(Document dom, SynthContainer scIn) {
		// create the root element
		Element rootEle = dom.createElement("OSC-Namespace");
		rootEle.setAttribute("Version", "1");
		dom.appendChild(rootEle);

		Element entityEle;
		for(int i = 0; i<scIn.Synths().size(); i++) {
			entityEle = create_Synth_Element(scIn.Synths().get(i), dom);
			rootEle.appendChild(entityEle);
		}				
	}
	 */
	private static void createDOMSynthTree(Document dom, ChildWrapper wrapper) {
		// create the root element
		Element rootEle = dom.createElement("OSC-Namespace");
		rootEle.setAttribute("Version", "1");
		dom.appendChild(rootEle);

		// Synth:
		Element entityEle = create_Synth_Element(wrapper, dom);
		rootEle.appendChild(entityEle);		
	}

	private static Element create_Synth_Element(ChildWrapper wrapper, Document dom ) {
		Element toReturn = dom.createElement("Node");
		
		toReturn.setAttribute("ID", wrapper.GetName());	// ID
		toReturn.setAttribute("AP", "");
		toReturn.setAttribute("Direction", "Out");
			    
		Hashtable<String,String>   messages = wrapper.getFoetusField().getSupportedMessages();
		ArrayList<FoetusParameter> fps 		= wrapper.getFoetusField().getParameters();
		
		Enumeration<String> e =  messages.elements();
		
		ArrayList<String> listAP = new ArrayList<String>();
		ArrayList<String> listTT = new ArrayList<String>();
		for (Enumeration<String> ek = messages.keys(); ek.hasMoreElements();) {
			listAP.add(ek.nextElement());
			listTT.add(e.nextElement());
		}
		
		for(int i=0; i<listAP.size(); i++) {
			String addressPattern = listAP.get(i);
			addressPattern = addressPattern.substring(1);
			String tt = listTT.get(i);
			
			String defaultValue = "";
			
			for(int j=0; j<fps.size(); j++) {
				String address = fps.get(j).getAddress(); 
				if(address.compareTo("/" + addressPattern) == 0 ) {
					defaultValue = String.valueOf(fps.get(j).getValue());
				}
			}
						
			Element valEle = createValueEntryElement(addressPattern, tt, defaultValue, dom);
	    	
	    	toReturn.appendChild(valEle);
		}
		
		Element alpha = createValueEntryElement("Set_Alpha", "f", "1", dom);
    	toReturn.appendChild(alpha);
    	
    	Element blendMode = createValueEntryElement("Set_BlendMode", "i", "1", dom);
    	toReturn.appendChild(blendMode);
		
		return toReturn;
	}
	
	private static Element createValueEntryElement(String addressPattern, String tt, String defaultValue, Document dom) {
		Element toReturn   = dom.createElement("Node");
		Element ttselement = dom.createElement("TTS");
		Element ttelement  = dom.createElement("TT");
		
		toReturn.appendChild(ttselement);
		ttselement.appendChild(ttelement);
		
		toReturn.setAttribute("ID", addressPattern);
		toReturn.setAttribute("AP", addressPattern);
		toReturn.setAttribute("Direction", "Out");
				
		ttselement.setAttribute("ID", tt + "_TTS_ID");
		
		ttelement.setAttribute("ID", tt + "_ID");
		ttelement.setAttribute("Tag", tt);
		
		ttelement.setAttribute("Default", defaultValue);
				
		return toReturn;
	}
	
	private static void printToFile(Document dom, String fp) {
		try {
			// print
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			// to generate output to console use this serializer
			// XMLSerializer serializer = new XMLSerializer(System.out, format);

			// to generate a file output use fileoutputstream instead of system.out
			
			FileOutputStream fos = new FileOutputStream(new File(fp));
			
			XMLSerializer serializer = new XMLSerializer(fos, format);

			serializer.serialize(dom);
			
			fos.close();
		} 
		catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}
