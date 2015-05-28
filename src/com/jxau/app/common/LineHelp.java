package com.jxau.app.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jxau.app.bean.CurrentLineAndDireBean;
import com.jxau.app.bean.CurrentLineBean;
import com.jxau.app.bean.LineBean;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

public class LineHelp {
	private final String packageName = "com.zero.jxauapp";
	private final String lineName = "/data/data/" + packageName
			+ "/lineName.xml";

	public List<String> getCurrentLine(String stationName) {
		return readNameInXml(lineName, stationName);
	}

	public List<CurrentLineBean> getRealNameAndFirstbusAndLastbusOfTime(
			LineBean lineBean) {
		
		String currentLine = lineBean.getLine();
		boolean dire = lineBean.isStatus();
		String fileName = "/data/data/" + packageName + "/" + currentLine
				+ ".xml";
		
		List<CurrentLineBean> list = new ArrayList<CurrentLineBean>();
		if (!XMLReadDataInLineForGetTime(fileName, list)) {
			XMLWriteLineDataAppendForTime(fileName, list, currentLine, dire);
		}
		return list;
	}

	public List<CurrentLineAndDireBean> getFirstAndLastStation(LineBean lineBean) {
		
		List<CurrentLineAndDireBean> list = new ArrayList<CurrentLineAndDireBean>();
		if(!XMLReadStation(lineBean, list))
			XMLStationAdd(lineBean, list);
		return list;
	}

	private boolean XMLReadStation(LineBean lineBean,
			List<CurrentLineAndDireBean> list) {
		
		String fileName = "/data/data/" + packageName + "/"
				+ lineBean.getLine() + ".xml";
		
		InputStream is = readXML(fileName);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nlRoot = doc.getElementsByTagName("body");
			Element eleRoot = (Element) nlRoot.item(0);

			NodeList stationList=null;
			stationList	= eleRoot.getElementsByTagName("station");
			if(stationList==null) return false;
			
			Element firstStation = (Element) stationList.item(0);
			String first = firstStation.getAttribute("name");
			Element lastStation = (Element) stationList.item(stationList.getLength()-1);
			String last = lastStation.getAttribute("name");
			
			Element testDirection = (Element) stationList.item(0);
			String dire = lineBean.isStatus()+"";
			CurrentLineAndDireBean bean = new CurrentLineAndDireBean();
			if(testDirection.getAttribute("direction").equals(dire)){
				bean.setFirstStation(first);
				bean.setLastStation(last);
			}else{
				bean.setFirstStation(last);
				bean.setLastStation(first);
			}			
			list.add(bean);
			return true;
			
		} catch (ParserConfigurationException e) { // factory.newDocumentBuilder
			e.printStackTrace();
		} catch (SAXException e) { // builder.parse
			e.printStackTrace();
		} catch (IOException e) { // builder.parse
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}		
	
	private void XMLStationAdd(LineBean lineBean,
			List<CurrentLineAndDireBean> list) {

		Map<Integer, String> map = new HashMap<Integer, String>();
		
		map = InitLine.getAllStation(lineBean);
		
		String fileName = "/data/data/" + packageName + "/"
				+ lineBean.getLine() + ".xml";

		InputStream is = readXML(fileName);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nlRoot = doc.getElementsByTagName("body");
			Element eleRoot = (Element) nlRoot.item(0);

			for (int i = 1; i <= map.size(); i++) {
				Element e = doc.createElement("station");
				e.setAttribute("direction", lineBean.isStatus()+"");
				e.setAttribute("id", i + "");
				e.setAttribute("name", map.get(i));
				eleRoot.appendChild(e);
			}
			
			CurrentLineAndDireBean bean = new CurrentLineAndDireBean();
			bean.setFirstStation(map.get(1));
			bean.setLastStation(map.get(map.size()));
			list.add(bean);
			
			Properties properties = new Properties();
			properties.setProperty(OutputKeys.INDENT, "yes");
			properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
			properties.setProperty(OutputKeys.VERSION, "1.0");
			properties.setProperty(OutputKeys.ENCODING, "utf-8");
			properties.setProperty(OutputKeys.METHOD, "xml");
			properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperties(properties);
			DOMSource domSource = new DOMSource(doc.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);

			savedXML(fileName, output.toString());

		} catch (ParserConfigurationException e) { // factory.newDocumentBuilder
			e.printStackTrace();
		} catch (SAXException e) { // builder.parse
			e.printStackTrace();
		} catch (IOException e) { // builder.parse
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean XMLReadDataInLineForGetTime(String fileName,
			List<CurrentLineBean> list) {
		InputStream is = readXML(fileName);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();

			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			NodeList nlRoot = null;
			if (month >= 4 && month < 12) {
				nlRoot = doc.getElementsByTagName("Summer");
			} else {
				nlRoot = doc.getElementsByTagName("Winter");
			}
			if (nlRoot == null) {
				return false;
			} else {
				Element eleRoot = (Element) nlRoot.item(0);
				String firstTime = eleRoot.getAttribute("firstTime");
				String lastTime = eleRoot.getAttribute("lastTime");
				CurrentLineBean a = new CurrentLineBean();
				a.setFirstTime(firstTime);
				a.setLastTime(lastTime);

				NodeList realName = doc.getElementsByTagName("RealName");
				Element eleName = (Element) realName.item(0);
				String strName = eleName.getAttribute("name");
				a.setRealName(strName);

				list.add(a);
				return true;
			}
		} catch (ParserConfigurationException e) { // factory.newDocumentBuilder
			e.printStackTrace();
		} catch (SAXException e) { // builder.parse
			e.printStackTrace();
		} catch (IOException e) { // builder.parse
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void XMLWriteLineDataAppendForTime(String fileName,
			List<CurrentLineBean> list, String currentLine, boolean dire) {

		InputStream is = readXML(fileName);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nlRoot = doc.getElementsByTagName("body");
			Element eleRoot = (Element) nlRoot.item(0);

			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			String time = "";
			if (month >= 4 && month < 12) {
				time = "Summer";
			} else
				time = "Winter";
			Element timeNode = doc.createElement(time);
			List<String> bustime = LineDeal.getFirsttimeAndLasttime(
					currentLine, dire);
			timeNode.setAttribute("firstTime", bustime.get(0));
			timeNode.setAttribute("lastTime", bustime.get(1));
			eleRoot.appendChild(timeNode);

			CurrentLineBean a = new CurrentLineBean();
			a.setFirstTime(bustime.get(0));
			a.setLastTime(bustime.get(1));

			NodeList nameList = doc.getElementsByTagName("RealName");
			Element nameNode = (Element) nameList.item(0);
			String realNameNode = nameNode.getAttribute("name");
			a.setRealName(realNameNode);

			list.add(a);

			Properties properties = new Properties();
			properties.setProperty(OutputKeys.INDENT, "yes");
			properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
			properties.setProperty(OutputKeys.VERSION, "1.0");
			properties.setProperty(OutputKeys.ENCODING, "utf-8");
			properties.setProperty(OutputKeys.METHOD, "xml");
			properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperties(properties);

			DOMSource domSource = new DOMSource(doc.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);

			savedXML(fileName, output.toString());

		} catch (ParserConfigurationException e) { // factory.newDocumentBuilder
			e.printStackTrace();
		} catch (SAXException e) { // builder.parse
			e.printStackTrace();
		} catch (IOException e) { // builder.parse
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String> readNameInXml(String fileName, String stationName) {
		File file = new File(fileName);
		List<String> list = new ArrayList<String>();
		if (!file.exists()) {
			try {
				file.createNewFile();
				XMLWrite(fileName, stationName, list);
			} catch (IOException e) {
				System.out.println("problem in create file(LineHelp)");
				e.printStackTrace();
			}
		} else {
			if (!XMLRead(fileName, stationName, list)) {
				XMLWriteAppend(fileName, stationName, list);
			}
		}
		return list;
	}

	private void XMLWriteAppend(String fileName, String stationName,
			List<String> list) {
		InputStream is = readXML(fileName);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			doc.getDocumentElement().normalize();
			NodeList nlRoot = doc.getElementsByTagName("body");
			Element eleRoot = (Element) nlRoot.item(0);

			Element newNode = doc.createElement("stationName");
			newNode.setAttribute("name", stationName);

			if (LineDeal.getCurrentLineCount(stationName) == 2) {
				Element eleId = doc.createElement("currentLine");
				Node nodeId = doc.createTextNode("1" + stationName);
				eleId.appendChild(nodeId);
				newNode.appendChild(eleId);
				XMLWriteLineData(stationName + "短班", "1" + stationName);

				Element eleName = doc.createElement("currentLine");
				Node nodeName = doc.createTextNode("2" + stationName);
				eleName.appendChild(nodeName);
				newNode.appendChild(eleName);
				XMLWriteLineData(stationName + "长班", "2" + stationName);

				list.add("1" + stationName);
				list.add("2" + stationName);
			} else {
				Element eleId = doc.createElement("currentLine");
				Node nodeId = doc.createTextNode(stationName);
				eleId.appendChild(nodeId);
				newNode.appendChild(eleId);
				XMLWriteLineData(stationName, stationName);
				list.add(stationName);
			}
			eleRoot.appendChild(newNode);

			Properties properties = new Properties();
			properties.setProperty(OutputKeys.INDENT, "yes");
			properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
			properties.setProperty(OutputKeys.VERSION, "1.0");
			properties.setProperty(OutputKeys.ENCODING, "utf-8");
			properties.setProperty(OutputKeys.METHOD, "xml");
			properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperties(properties);

			DOMSource domSource = new DOMSource(doc.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);

			savedXML(fileName, output.toString());

		} catch (ParserConfigurationException e) { // factory.newDocumentBuilder
			e.printStackTrace();
		} catch (SAXException e) { // builder.parse
			e.printStackTrace();
		} catch (IOException e) { // builder.parse
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean XMLRead(String fileName, String stationName,
			List<String> list) {

		InputStream is = readXML(fileName);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			doc.getDocumentElement().normalize();
			NodeList nlRoot = doc.getElementsByTagName("body");
			Element eleRoot = (Element) nlRoot.item(0);

			NodeList nlPerson = eleRoot.getElementsByTagName("stationName");
			int personsLen = nlPerson.getLength();
			int i;
			for (i = 0; i < personsLen; i++) {
				Element elePerson = (Element) nlPerson.item(i);
				String name = elePerson.getAttribute("name");
				if (name.equals(stationName)) {
					NodeList nlId = elePerson
							.getElementsByTagName("currentLine");
					for (int j = 0; j < nlId.getLength(); j++) {
						Element eleId = (Element) nlId.item(j);
						String id = eleId.getChildNodes().item(0)
								.getNodeValue();
						list.add(id);
					}
					break;
				}
			}
			if (i == personsLen)
				return false;
			return true;

		} catch (ParserConfigurationException e) { // factory.newDocumentBuilder
			e.printStackTrace();
		} catch (SAXException e) { // builder.parse
			e.printStackTrace();
		} catch (IOException e) { // builder.parse
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void XMLWrite(String fileName, String stationName, List<String> list) {
		String xmlWriter = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element eleRoot = doc.createElement("body");
			eleRoot.setAttribute("author", "zou");
			eleRoot.setAttribute("date", "2014-01-02");
			doc.appendChild(eleRoot);

			Element elePerson = doc.createElement("stationName");
			elePerson.setAttribute("name", stationName);
			eleRoot.appendChild(elePerson);

			if (LineDeal.getCurrentLineCount(stationName) == 2) {
				Element eleId = doc.createElement("currentLine");
				Node nodeId = doc.createTextNode("1" + stationName);
				eleId.appendChild(nodeId);
				elePerson.appendChild(eleId);
				XMLWriteLineData(stationName + "短班", "1" + stationName);

				Element eleName = doc.createElement("currentLine");
				Node nodeName = doc.createTextNode("2" + stationName);
				eleName.appendChild(nodeName);
				elePerson.appendChild(eleName);
				XMLWriteLineData(stationName + "长班", "2" + stationName);

				list.add("1" + stationName);
				list.add("2" + stationName);
			} else {
				Element eleId = doc.createElement("currentLine");
				Node nodeId = doc.createTextNode(stationName);
				eleId.appendChild(nodeId);
				elePerson.appendChild(eleId);
				XMLWriteLineData(stationName, stationName);

				list.add(stationName);
			}

			Properties properties = new Properties();
			properties.setProperty(OutputKeys.INDENT, "yes");
			properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
			properties.setProperty(OutputKeys.VERSION, "1.0");
			properties.setProperty(OutputKeys.ENCODING, "utf-8");
			properties.setProperty(OutputKeys.METHOD, "xml");
			properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperties(properties);

			DOMSource domSource = new DOMSource(doc.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);

			xmlWriter = output.toString();
			savedXML(fileName, xmlWriter.toString());

		} catch (ParserConfigurationException e) { // factory.newDocumentBuilder
			e.printStackTrace();
		} catch (DOMException e) { // doc.createElement
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) { // TransformerFactory.newInstance
			e.printStackTrace();
		} catch (TransformerConfigurationException e) { // transformerFactory.newTransformer
			e.printStackTrace();
		} catch (TransformerException e) { // transformer.transform
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void XMLWriteLineData(String realName, String currentLine) {
		String fileName = "/data/data/" + packageName + "/" + currentLine
				+ ".xml";
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("LineHelp->XMLWriteLineData");
				e.printStackTrace();
			}
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element eleRoot = doc.createElement("body");
			eleRoot.setAttribute("author", "zou");
			eleRoot.setAttribute("date", "2014-01-02");
			doc.appendChild(eleRoot);

			Element elePerson = doc.createElement("CurrentLine");
			elePerson.setAttribute("name", currentLine);
			eleRoot.appendChild(elePerson);

			Element eleId = doc.createElement("RealName");

			eleId.setAttribute("name", realName);
			eleRoot.appendChild(eleId);

			Properties properties = new Properties();
			properties.setProperty(OutputKeys.INDENT, "yes");
			properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
			properties.setProperty(OutputKeys.VERSION, "1.0");
			properties.setProperty(OutputKeys.ENCODING, "utf-8");
			properties.setProperty(OutputKeys.METHOD, "xml");
			properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperties(properties);

			DOMSource domSource = new DOMSource(doc.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);

			savedXML(fileName, output.toString());

		} catch (ParserConfigurationException e) { // factory.newDocumentBuilder
			e.printStackTrace();
		} catch (DOMException e) { // doc.createElement
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) { // TransformerFactory.newInstance
			e.printStackTrace();
		} catch (TransformerConfigurationException e) { // transformerFactory.newTransformer
			e.printStackTrace();
		} catch (TransformerException e) { // transformer.transform
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void savedXML(String fileName, String xml) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileName);
			byte[] buffer = xml.getBytes();
			fos.write(buffer);
			fos.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private InputStream readXML(String fileName) {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			System.out.println("file out found");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fin;
	}
}

class LineDeal {
	// 2表示有长短班的区别，1反之
	public static int getCurrentLineCount(String stationName) {
		String url = "http://mybus.jx139.com/LineQuery?line=" + stationName
				+ "&";
		int count = 0;
		String htmlInfo = HtmlDeal.GetContentFromUrl(url);
		String mainInfo = HtmlDeal.getDivContentByJsoup_Line(htmlInfo);
		String longBus = stationName + "路长";
		if (mainInfo.indexOf(longBus) == -1) {
			count = 1;
		} else
			count = 2;
		return count;
	}

	public static List<String> getFirsttimeAndLasttime(String currentLine,
			boolean dire) {
		List<String> list = new ArrayList<String>();
		int realDire;
		if (dire) {
			realDire = 2;
		} else
			realDire = 1;
		String url = "http://mybus.jx139.com/LineDetailQuery?lineId="
				+ currentLine + "&direction=" + realDire + "&version=3";
		String htmlInfo = HtmlDeal.GetContentFromUrl(url);
		String mainInfo = HtmlDeal.getDivContentByJsoup_LineDeal(htmlInfo);

		char ch[] = mainInfo.toCharArray();
		String firstTime = "";
		String lastTime = "";
		for (int i = 0, count = 0; i < ch.length; i++) {
			if (ch[i] == '首' && ch[i + 1] == '班') {
				for (int j = i + 2; j < ch.length; j++) {
					if (ch[j] == '末' && ch[j + 1] == '班')
						break;
					if ((ch[j] >= '0' && ch[j] <= '9') || ch[j] == ':') {
						firstTime += ch[j];
					}
				}
				count++;
			} else {
				if (ch[i] == '末' && ch[i + 1] == '班') {
					for (int k = i + 3; k < ch.length; k++) {
						if (HtmlDeal.isChinese(ch[k]))
							break;
						if ((ch[k] >= '0' && ch[k] <= '9') || ch[k] == ':') {
							lastTime += ch[k];
						}
					}

					count++;
				}
			}
			if (count == 2)
				break;
		}
		list.add(firstTime);
		list.add(lastTime);
		return list;
	}

}
