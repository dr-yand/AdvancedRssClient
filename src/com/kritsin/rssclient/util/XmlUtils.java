package com.kritsin.rssclient.util;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlUtils {
	public static Document loadXMLFromString(String xml) throws Exception{
		DocumentBuilderFactory tempFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder tempBuilder;

		tempBuilder = tempFactory.newDocumentBuilder();

		InputSource is = new InputSource(new StringReader(xml));
		Document tempDoc = tempBuilder.parse(is);

		tempDoc.getDocumentElement().normalize();

		return tempDoc;
	}
	
	public static  Node getChannel(Element element){
		Node result=null;
		
		NodeList nd =  element.getChildNodes();
		for(int i=0;i<nd.getLength();i++){
			Node n = nd.item(i);
			if(n.getNodeName().equals("channel")){
				result = n;
			}
		}
		
		return result;
	}
	
	public static  ArrayList<Element> getItems(Element element){
		ArrayList<Element> result = new ArrayList<Element>();
		
		NodeList nd = element.getElementsByTagName("item");
		for(int i=0;i<nd.getLength();i++){
			Node n = nd.item(i);
			result.add((Element)n);
		}
		
		return result;
	}
	
	public static  String getItemProperty(Element element, String property){
		String result="";
		
		NodeList nl = element.getElementsByTagName(property);
		if(nl.getLength()>0){
			result = nl.item(0).getTextContent();
		}
		
		return result;
	}
}
