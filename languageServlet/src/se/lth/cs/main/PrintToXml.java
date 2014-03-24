package se.lth.cs.main;

import java.io.File;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.lth.cs.semantics.*;


public class PrintToXml {
	
	private static Element createElement(PredArgs p, Document doc){
		// whatever
		return null;
	}
	
	public static String printToXml(List<PredArgs> stms){
		 try {
			 
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("Commands");
				doc.appendChild(rootElement);
		 
				// staff elements
				for(PredArgs p: stms){
				Element s = createElement(p, doc);
				rootElement.appendChild(s);
		 
				}
				
				// Output to console for testing
				DOMSource source = new DOMSource(doc);
				 StreamResult result = new StreamResult(System.out);
try{
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
				transformer.transform(source, result);
}catch(Exception e){}			
				
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } /*catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }*/
			
	return "";
	}

}
