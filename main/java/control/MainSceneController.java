package main.java.control;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.Painter;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import main.java.application.Attribute;
import main.java.application.ForteConnect;
import main.java.application.FunctionInList;
import main.java.application.Main;
import main.java.application.StateConnect;
import main.java.application.WireInList;
import main.java.instructions.*;

import main.java.paint.PainterP;

public class MainSceneController {
	@FXML
	private ScrollPane Map;
	@FXML
	public Label state;
	@FXML
	private TextField pathToNewFile;
	@FXML
	private TextField xNewElem;
	@FXML
	private TextField yNewElem;
	@FXML
	private VBox functions;
	@FXML
	private VBox wires;
	@FXML
	private ScrollPane scrPan_f;
	@FXML
	private ScrollPane scrPan_w;
	
	public static StateConnect stateConn;
	public static PainterP painter;
	private static int newFuncId = 0; 
	
	public void new_proj() { // TODO тут тоже сначала папку надо выбрать
		new_proj(true);
	}
	public void new_proj(boolean createMainFunc) {
		if (!askSave()) return;
		Main.main.init(createMainFunc);
	}
	
	public void open_proj() {
		if (!askSave()) return;
		
//		FileChooser file_chooser = setUpFileChooser();
//		
//		
//		File file = file_chooser.showOpenDialog(Main.main.getPrimaryStage()); TODO НЕ ЗАБУДЬ УБРАТЬ!!!!
		File file = new File("C:\\Users\\Kai\\4diacIDE-workspace\\test\\test_app.sys");
		 
        if (file != null) {
        	setUpDir(file);
        	//Main.projName = file.getName();
            load(Main.saveDirectory);
        }
		
	}
	
	public boolean askSave() { // возращает false, если выбрана отмена, в остальных случаях true
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(Main.NAME);
		alert.setHeaderText("Do you want to save changes? " + Main.saveDirectory);
		alert.setContentText("Choose your option.");

		ButtonType yes = new ButtonType("Save");
		ButtonType no = new ButtonType("Don't save");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(yes, no, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == yes){
			saveAs_proj();
		    return true;
		} else if (result.get() == no) {
		    return true;
		} else {
		    return false;
		}
	}

	public void save_proj() {
		save(Main.saveDirectory);
	}
	
	public void saveAs_proj() {
		FileChooser file_chooser = setUpProjFileChooser();
		
		File file = file_chooser.showSaveDialog(Main.main.getPrimaryStage());
		 
        if (file != null) {
        	Main.saveDirectory = file.getAbsolutePath();
        	//ProjParams.projName = file.getName();
            save_proj();
        }
	}
	
	public void run() {
		ForteConnect fc = new ForteConnect();
		fc.startConnection();
		int id = 1;
		String secondPar = Main.proj.resName;
		
		String msg = fc.transformQuery(id++);
		
		System.out.println(msg);
		fc.send(msg, "");
		System.out.println(fc.recv());
		
		msg = fc.transformEMB(id++);
		
		System.out.println(msg);
		fc.send(msg, "");
		System.out.println(fc.recv());
		
		for (int i = 0; i < Function.getFuncList().size(); i++) {
			if (Function.getFuncList().get(i).name.equals(MainFunction.START)) continue;
		msg = fc.transformFB(id++, Function.getFuncList().get(i));
		
		System.out.println(msg);
		fc.send(msg, secondPar);
		System.out.println(fc.recv());
		
		for (int j = 0; j < Function.getFuncList().get(i).InOutList.length; j++) {
			InOut io = Function.getFuncList().get(i).InOutList[i];
			if (io instanceof Out) continue;
			if (((In)io).value == null) continue;
			
			msg = fc.transformParam(id++, (In)io);
			
			System.out.println(msg);
			fc.send(msg, secondPar);
			System.out.println(fc.recv());
			}
		}
		
		for (int i = 0; i < Wire.wires().size(); i++) {
			msg = fc.transformConn(id++, Wire.wires().get(i));
			
			System.out.println(msg);
			fc.send(msg, secondPar);
			System.out.println(fc.recv());
			}
		id--;
		msg = fc.start(id++);
		
		System.out.println(msg);
		fc.send(msg, secondPar);
		System.out.println(fc.recv());
		
		fc.closeConnection();
	}
	
	public void makeFboot() {
		
	}
	
	public void quit() {
		if (!askSave()) return;
		Main.main.exit();
	}
	
	public void chooseFB() {		
		FileChooser file_chooser = setUpLibFileChooser();
		if (!Main.proj.libpath.equals("")) file_chooser.setInitialDirectory(new File(Main.proj.libpath));
		
		
		File file = file_chooser.showOpenDialog(Main.main.getPrimaryStage()); 
		if (file != null) { 
        pathToNewFile.setText(file.getAbsolutePath());
		}
	}
	
	
	
	public static void unzip(InputStream is, Path targetDir) throws IOException {
	    targetDir = targetDir.toAbsolutePath();
	    try (ZipInputStream zipIn = new ZipInputStream(is)) {
	        for (ZipEntry ze; (ze = zipIn.getNextEntry()) != null; ) {
	            Path resolvedPath = targetDir.resolve(ze.getName()).normalize();
	            if (!resolvedPath.startsWith(targetDir)) {
	                // see: https://snyk.io/research/zip-slip-vulnerability
	                throw new RuntimeException("Entry with an illegal path: " 
	                        + ze.getName());
	            }
	            if (ze.isDirectory()) {
	                Files.createDirectories(resolvedPath);
	            } else {
	                Files.createDirectories(resolvedPath.getParent());
	                Files.copy(zipIn, resolvedPath);
	            }
	        }
	    }
	}
	
	private void save(String filepath) { // сохраняем
		DocumentBuilderFactory dbf = null;
	    DocumentBuilder        db  = null;
	    Document               doc = null;
	    try {
	    	dbf = DocumentBuilderFactory.newInstance();
	        db  = dbf.newDocumentBuilder();
	        doc = db.newDocument();
	        
	        // Тэги
	        Element e_root = doc.createElement("System");
	        Element e_vers = doc.createElement("VersionInfo");
	        Element e_app = doc.createElement("Application");
	        Element e_suNet = doc.createElement("SubAppNetwork");
	        Element e_suNet_ec = doc.createElement("EventConnections");
	        Element e_suNet_dc = doc.createElement("DataConnections");
	        Element e_dev = doc.createElement("Device");
	        Element dev_param = doc.createElement("Parameter");
	        Element e_res = doc.createElement("Resource");
	        Element e_fbNet = doc.createElement("FBNetwork");
	        Element e_fbNet_ec = doc.createElement("EventConnections");
	        Element e_fbNet_dc = doc.createElement("DataConnections");
	        Element e_seg = doc.createElement("Segment");
	        Element e_link = doc.createElement("Link");
	        
	        ArrayList<Element> suNet_fb = new ArrayList<>(); // сюда только не переносится стартовый узел
	        ArrayList<ArrayList<Element>> suNet_fb_param = new ArrayList<>();
	        ArrayList<Element> suNet_ec = new ArrayList<>();
	        ArrayList<Element> suNet_dc = new ArrayList<>();
	        
	        ArrayList<Element> dev_att = new ArrayList<>();

	        ArrayList<Element> fbNet_fb = new ArrayList<>(); 
	        ArrayList<ArrayList<Element>> fbNet_fb_param = new ArrayList<>();
	        ArrayList<Element> fbNet_ec = new ArrayList<>();
	        ArrayList<Element> fbNet_dc = new ArrayList<>();
	        
	        ArrayList<Element> maps = new ArrayList<>();
	        
	        ArrayList<Element> seg_att = new ArrayList<>();
	        
	        final String name = "Name";
	        final String type = "Type";
	        final String comment = "Comment";
	        final String x = "x";
	        final String y = "y";
	        final String value = "Value";
	        final String version = "Version";
	        final String author = "Author";
	        final String date = "Date";
	        final String segmentName = "SegmentName";
	        final String commResource = "CommResource";
	        final String source = "Source";
	        final String destination = "Destination";
	        final String from = "From";
	        final String to = "To";
	        
	        e_root.setAttribute(name, Main.proj.sysName);
	        e_root.setAttribute(comment, Main.proj.sysComment);
	        
	        e_vers.setAttribute(version, Main.proj.verVersion);
	        e_vers.setAttribute(author, Main.proj.verAuthor);
	        e_vers.setAttribute(date, Main.proj.verDate);
	        
	        e_app.setAttribute(name, Main.proj.appName);
	        e_app.setAttribute(comment, Main.proj.appComment);
	        
	        e_dev.setAttribute(name, Main.proj.devName);
	        e_dev.setAttribute(type, Main.proj.devType);
	        e_dev.setAttribute(comment, Main.proj.devComment);
	        e_dev.setAttribute(x, Main.proj.devX);
	        e_dev.setAttribute(y, Main.proj.devY);
	        
	        dev_param.setAttribute(name, Main.proj.paramName);
	        dev_param.setAttribute(value, Main.proj.paramValue);
	        
	        e_res.setAttribute(name, Main.proj.resName);
	        e_res.setAttribute(type, Main.proj.resType);
	        e_res.setAttribute(comment, Main.proj.resComment);
	        e_res.setAttribute(x, Main.proj.resX);
	        e_res.setAttribute(y, Main.proj.resY);
	        
	        e_seg.setAttribute(name, Main.proj.segName);
	        e_seg.setAttribute(type, Main.proj.segType);
	        e_seg.setAttribute(comment, Main.proj.segComment);
	        e_seg.setAttribute(x, Main.proj.segX);
	        e_seg.setAttribute(y, Main.proj.segY);
	        
	        e_link.setAttribute(segmentName, Main.proj.linkSegmentName);
	        e_link.setAttribute(commResource, Main.proj.linkCommResource);
	        e_link.setAttribute(comment, Main.proj.linkComment);
	        
	        for (int i = 0; i < Function.getFuncList().size(); i++) {
	        	Element fb = doc.createElement("FB");
	        	Function func = Function.getFuncFromList(i);
	        	if (func.name.equals(MainFunction.getMainFunc().name)) {
	        		continue;
	        	}
	        	fb.setAttribute(name, func.name);
	        	fb.setAttribute(type, func.type);
	        	fb.setAttribute(comment, func.comment);
	        	fb.setAttribute(x, ((Double)func.x).toString());
	        	fb.setAttribute(y, ((Double)func.y).toString());
	        	suNet_fb.add(fb);
	        	ArrayList<Element> fb_params = new ArrayList<>();
		        for (int j = 0; j < func.InOutList.length; j++) {
		        	InOut pin = func.InOutList[j];
		        	Element param;
		        	if (pin instanceof In && ((In)pin).getValue() != null) {
			        	param = doc.createElement("Parameter");
			        	param.setAttribute(name, func.InOutList[j].name);
			        	param.setAttribute(value, ((In)pin).getValue());
		        	}
		        	else {
		        		param = null;
		        	}
		        	fb_params.add(param);
		        }
		        suNet_fb_param.add(fb_params);
	        }
	        
	        for (int i = 0; i < Function.getFuncList().size(); i++) { // повтор, можно клонировать было, но клонирование надо ещё прописывать
	        	Element fb = doc.createElement("FB");
	        	Function func = Function.getFuncFromList(i);
	        	fb.setAttribute(name, func.name);
	        	fb.setAttribute(type, func.type);
	        	fb.setAttribute(comment, func.comment);
	        	fb.setAttribute(x, ((Double)func.x).toString());
	        	fb.setAttribute(y, ((Double)func.y).toString());
	        	fbNet_fb.add(fb);
	        	ArrayList<Element> fb_params = new ArrayList<>();
		        for (int j = 0; j < func.InOutList.length; j++) {
		        	InOut pin = func.InOutList[j];
		        	Element param;
		        	if (pin instanceof In && ((In)pin).getValue() != null) {
			        	param = doc.createElement("Parameter");
			        	param.setAttribute(name, func.InOutList[j].name);
			        	param.setAttribute(value, ((In)pin).getValue());
		        	}
		        	else {
		        		param = null;
		        	}
		        	fb_params.add(param);
		        }
		        fbNet_fb_param.add(fb_params);
	        }
	        
	        
	        for (int i = 0; i < Wire.wires().size(); i++) { // dx игнорируем
	        	Wire wire = Wire.wires().get(i);
	        	Element conn = doc.createElement("Connection");
	        	conn.setAttribute(source, wire.source.getPath());
	        	conn.setAttribute(destination, wire.target.getPath());
	        	if (wire.source.types.length > 1 || wire.source.types[0] != InOut.TYPES.EVENT) suNet_dc.add(conn);
	        	else if (wire.source.func instanceof MainFunction) fbNet_ec.add(conn);
	        	else if (!wire.source.func.name.equals(MainFunction.getMainFunc().name)) suNet_ec.add(conn);
	        }
	        for (int i = 0; i < Wire.wires().size(); i++) { //повтор
	        	Wire wire = Wire.wires().get(i);
	        	Element conn = doc.createElement("Connection");
	        	conn.setAttribute(source, wire.source.getPath());
	        	conn.setAttribute(destination, wire.target.getPath());
	        	if (wire.source.types.length > 1 || wire.source.types[0] != InOut.TYPES.EVENT) fbNet_dc.add(conn);
	        	else fbNet_ec.add(conn);
	        }
	        
	        
	        for (int i = 0; i < Main.proj.attDev.size(); i++) {
	        	Attribute att = Main.proj.attDev.get(i);
	        	Element e_att = doc.createElement("Attribute");
	        	e_att.setAttribute(name, att.name);
	        	e_att.setAttribute(type, att.type);
	        	e_att.setAttribute(value, att.value);
	        	e_att.setAttribute(comment, att.comment);
	        	dev_att.add(e_att);
	        }
	        
	        
	        for (int i = 0; i < Function.getFuncList().size(); i++) {
	        	if (Function.getFuncFromList(i).name.equals(MainFunction.getMainFunc().name)) continue;
	        	Element e_map = doc.createElement("Mapping");
	        	e_map.setAttribute(from, Main.proj.sysName + "." + Function.getFuncFromList(i).name);
	        	e_map.setAttribute(to, Main.proj.devName + "." + Main.proj.resName + "." + Function.getFuncFromList(i).name);
	        	maps.add(e_map);
	        }
	        
	        
	        for (int i = 0; i < Main.proj.attSeg.size(); i++) {
	        	Attribute att = Main.proj.attSeg.get(i);
	        	Element e_att = doc.createElement("Attribute");
	        	e_att.setAttribute(name, att.name);
	        	e_att.setAttribute(type, att.type);
	        	e_att.setAttribute(value, att.value);
	        	e_att.setAttribute(comment, att.comment);
	        	seg_att.add(e_att);
	        }
	        
	        
	        
	        
	        e_root.appendChild(e_vers);
	        e_vers.appendChild(e_app);
	        e_app.appendChild(e_suNet);
	        for (int i = 0; i < suNet_fb.size(); i++) { 
	        	e_suNet.appendChild(suNet_fb.get(i));
	        	if (suNet_fb_param.get(i) == null) continue;
	        	for (int j = 0; j < suNet_fb_param.get(i).size(); j++) {
	        		if (suNet_fb_param.get(i).get(j) == null) continue;
	        		else suNet_fb.get(i).appendChild(suNet_fb_param.get(i).get(j));
	        	}
	        }
	        for (Element e : suNet_ec) e_suNet_ec.appendChild(e);
	        e_suNet.appendChild(e_suNet_ec);
	        for (Element e : suNet_dc) e_suNet_dc.appendChild(e);
	        e_suNet.appendChild(e_suNet_dc);
	        e_root.appendChild(e_dev);
	        e_dev.appendChild(dev_param);
	        for (Element e : dev_att) e_dev.appendChild(e);
	        e_dev.appendChild(e_res);
	        e_res.appendChild(e_fbNet);
	        for (int i = 0; i < fbNet_fb.size(); i++) { 
	        	e_fbNet.appendChild(fbNet_fb.get(i));
	        	if (fbNet_fb_param.get(i) == null) continue;
	        	for (int j = 0; j < fbNet_fb_param.get(i).size(); j++) {
	        		if (fbNet_fb_param.get(i).get(j) == null) continue;
	        		else fbNet_fb.get(i).appendChild(fbNet_fb_param.get(i).get(j));
	        	}
	        }
	        for (Element e : fbNet_ec) e_fbNet_ec.appendChild(e);
	        e_fbNet.appendChild(e_fbNet_ec);
	        for (Element e : fbNet_dc) e_fbNet_dc.appendChild(e);
	        e_fbNet.appendChild(e_fbNet_dc);
	        for (Element e : maps) e_root.appendChild(e);
	        for (Element e : seg_att) e_seg.appendChild(e);
	        e_root.appendChild(e_seg);
	        e_root.appendChild(e_link);
	        doc.appendChild(e_root);
	        
	    } catch (ParserConfigurationException e) {
	        e.printStackTrace();
	    } finally {
	        // Сохраняем Document в XML-файл
	        if (doc != null)
	            writeDocument(doc, filepath);
	    }
	    }
		
		
	private void writeDocument(Document document, String path) 
        throws TransformerFactoryConfigurationError
	{
		String xmlPath = path.replace("sys", "xml");
		
		Transformer      trf = null;
		DOMSource        src = null;
		FileOutputStream fos = null;
		try {
			trf = TransformerFactory.newInstance().newTransformer();
			src = new DOMSource(document);
			fos = new FileOutputStream(xmlPath);

			StreamResult result = new StreamResult(fos);
			trf.transform(src, result);
		} catch (TransformerException e) {
			e.printStackTrace(System.out);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		
		try  {
			StringBuilder line = new StringBuilder();
			FileReader r = new FileReader(xmlPath);
			FileWriter w = new FileWriter(path);
			int depth = -2;
			int i;
			while((i=r.read())!= -1){
				if (i == '>') {
					line.append((char)i);
					line.append('\n');
					if (line.toString().indexOf('<')+1 == line.toString().indexOf('/')) depth--;
					for (int d = 0; d < depth; d++) line.insert(0, '\t');
					w.write(line.toString());
					if (line.toString().indexOf('>')-1 == line.toString().indexOf('/')) depth--;
					if (line.toString().indexOf('<')+1 == line.toString().indexOf('/')) depth--; // повтор
					line = new StringBuilder();
				}
				else if (i == '<') { 
					depth++;
					line.append((char)i);
					}
				else line.append((char)i);
			}
			r.close();
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	private void load(String filepath) {
		DocumentBuilderFactory dbf;
		DocumentBuilder        db ;
		Document               doc;

		dbf = DocumentBuilderFactory.newInstance();
		try {
			db  = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		try {
			doc = db.parse(new File(filepath));
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return;
		}
		// сначала просто считываем данные с файла
		String[] tags = {"System", "VersionInfo", "Application", "Device", "Parameter", "Resource", "Segment", "Link", "Attribute",};
		String newV;
		NodeList nodeList = doc.getElementsByTagName(tags[0]);
		newV = readAttribute(nodeList, "Comment");
		if (thereIs(newV)) Main.proj.sysComment = newV;
		newV = readAttribute(nodeList, "Name");
		if (thereIs(newV)) Main.proj.sysName = newV;
		
		nodeList = doc.getElementsByTagName(tags[1]);
		newV = readAttribute(nodeList, "Comment");
		if (thereIs(newV)) Main.proj.verVersion = newV;
		newV = readAttribute(nodeList, "Author");
		if (thereIs(newV)) Main.proj.verAuthor = newV;
		newV = readAttribute(nodeList, "Date");
		if (thereIs(newV)) Main.proj.verDate = newV;
		
		nodeList = doc.getElementsByTagName(tags[2]);
		newV = readAttribute(nodeList, "Comment");
		if (thereIs(newV)) Main.proj.appComment = newV;
		newV = readAttribute(nodeList, "Name");
		if (thereIs(newV)) Main.proj.appName = newV;
		
		nodeList = doc.getElementsByTagName(tags[3]);
		newV = readAttribute(nodeList, "Comment");
		if (thereIs(newV)) Main.proj.devComment = newV;
		newV = readAttribute(nodeList, "Name");
		if (thereIs(newV)) Main.proj.devName = newV;
		newV = readAttribute(nodeList, "Type");
		if (thereIs(newV)) Main.proj.devType = newV;
		newV = readAttribute(nodeList, "x");
		if (thereIs(newV)) Main.proj.devX = newV;
		newV = readAttribute(nodeList, "y");
		if (thereIs(newV)) Main.proj.devY = newV;

		nodeList = doc.getElementsByTagName(tags[3]);
		if (nodeList != null) {nodeList = nodeList.item(0).getChildNodes();
		
			int i = 0; 
			for (; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getNodeName().equals("Parameter")) break;
			}
			newV = readAttribute(nodeList, "Value");
			if (thereIs(newV)) Main.proj.paramValue = newV;
			newV = readAttribute(nodeList, "Name");
			if (thereIs(newV)) Main.proj.paramName = newV;
		}
		
		nodeList = doc.getElementsByTagName(tags[5]);
		newV = readAttribute(nodeList, "Comment");
		if (thereIs(newV)) Main.proj.resComment = newV;
		newV = readAttribute(nodeList, "Name");
		if (thereIs(newV)) Main.proj.resName = newV;
		newV = readAttribute(nodeList, "Type");
		if (thereIs(newV)) Main.proj.resType = newV;
		newV = readAttribute(nodeList, "x");
		if (thereIs(newV)) Main.proj.resX = newV;
		newV = readAttribute(nodeList, "y");
		if (thereIs(newV)) Main.proj.resY = newV;
		
		nodeList = doc.getElementsByTagName(tags[6]);
		newV = readAttribute(nodeList, "Comment");
		if (thereIs(newV)) Main.proj.segComment = newV;
		newV = readAttribute(nodeList, "Name");
		if (thereIs(newV)) Main.proj.segName = newV;
		newV = readAttribute(nodeList, "Type");
		if (thereIs(newV)) Main.proj.segType = newV;
		newV = readAttribute(nodeList, "x");
		if (thereIs(newV)) Main.proj.segX = newV;
		newV = readAttribute(nodeList, "y");
		if (thereIs(newV)) Main.proj.segY = newV;
		
		nodeList = doc.getElementsByTagName(tags[7]);
		newV = readAttribute(nodeList, "Comment");
		if (thereIs(newV)) Main.proj.linkComment = newV;
		newV = readAttribute(nodeList, "CommResource");
		if (thereIs(newV)) Main.proj.linkCommResource = newV;
		newV = readAttribute(nodeList, "SegmentName");
		if (thereIs(newV)) Main.proj.linkSegmentName = newV;
		
		nodeList = doc.getElementsByTagName(tags[8]);
		if (nodeList != null) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getParentNode().getNodeName().equals("Device")) Main.proj.attDev.clear();
			if (nodeList.item(i).getParentNode().getNodeName().equals("Segment")) Main.proj.attSeg.clear();
		}
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(0).getParentNode().getNodeName().equals("Segment")) continue;
			String comment = "", name = "", type = "", value = "";
			newV = readAttribute(nodeList, "Comment", i);
			if (thereIs(newV)) comment = newV;
			newV = readAttribute(nodeList, "Name", i);
			if (thereIs(newV)) name = newV;
			newV = readAttribute(nodeList, "Value", i);
			if (thereIs(newV)) value = newV;
			newV = readAttribute(nodeList, "Type", i);
			if (thereIs(newV)) type = newV;
			if (nodeList.item(i).getParentNode().getNodeName().equals("Device"))
			Main.proj.attDev.add(new Attribute(name, 
					type, value, comment));
			else Main.proj.attSeg.add(new Attribute(name, 
					type, value, comment));
		}
		}
		int fileBroken = 0;
		nodeList = doc.getElementsByTagName("FBNetwork").item(0).getChildNodes();
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				String nodeType = nodeList.item(i).getNodeName();
				if (nodeType.equals("FB")) {
					fileBroken = addFB(nodeList, i);

				}
				else if (nodeType.equals("EventConnections") || nodeType.equals("DataConnections")) { 
					NodeList connList = nodeList.item(i).getChildNodes(); // если вносишь исправление сюда, не забудь сделать это ниже
					for (int j = 0; j < connList.getLength(); j++) {
						fileBroken = addConn(connList, j);
					}
				}
			}
		}
		nodeList = doc.getElementsByTagName("SubAppNetwork").item(0).getChildNodes(); // сюда же подключаем ещё не мапнутые элементы, тем самым автоматически добавляя их туда
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				String nodeType = nodeList.item(i).getNodeName();
				if (nodeType.equals("FB")) {
					fileBroken = addFB(nodeList, i);
				}
				else if (nodeType.equals("EventConnections") || nodeType.equals("DataConnections")) { 
					NodeList connList = nodeList.item(i).getChildNodes();
					for (int j = 0; j < connList.getLength(); j++) {
						fileBroken = addConn(connList, j); // если вносишь исправление сюда, не забудь сделать это выше
					}
				}
			}
			
			NodeList params = doc.getElementsByTagName("Parameter");

			if (params != null) { // 
				for (int i = 0; i < params.getLength(); i++) {
					Node param = params.item(i);
					Node parNode = param.getParentNode();
					if (parNode.getNodeName().equals("FB")) {
						String name = readAttribute(parNode, "Name");
						Function func = Function.searchFunc(name);
						String namePin = readAttribute(param, "Name");
						String valuePin = readAttribute(param, "Value");
						((In)Function.searchPin(func, namePin)).setValue(valuePin);
					}
				}
			}
			if (fileBroken > 0) {
			}
			
		}
		repaintMap();
	}
	public void addFBfromlib() { // кнопка
		double x = 0, y = 0;
		if (!xNewElem.getText().equals("")) {
			try {
			x = Double.parseDouble(xNewElem.getText());
			}
			catch (Exception e) {
				xNewElem.setText("");
				x = 0;
			}
		}
		
		if (!yNewElem.getText().equals("")) {
			try {
			y = Double.parseDouble(yNewElem.getText());
			}
			catch (Exception e) {
				yNewElem.setText("");
				y = 0;
			}
		}
		
		File file = new File(pathToNewFile.getText());
		if (!file.getName().contains(".fbt") || !file.exists()) pathToNewFile.setText("Something wrong");
		else addFB(file.getName().replace(".fbt", "")+((Integer)newFuncId++).toString(), file.getName().replace(".fbt", ""), "", x, y);
		
	}
	private int addFB(NodeList nodeList, int i) {
		String funcType = readAttribute(nodeList, "Type", i);
		
		double funcX, funcY;
		String funcComment, funcName; // funcType создано выше
		funcX = Double.parseDouble(readAttribute(nodeList, "x", i));
		funcY = Double.parseDouble(readAttribute(nodeList, "y", i));
		funcComment = readAttribute(nodeList, "Comment", i);
		funcName = readAttribute(nodeList, "Name", i);
		return addFB(funcName, funcType, funcComment, funcX, funcY);
		
		
	}
	public int addFB(String funcName, String funcType, String funcComment, double funcX, double funcY) {
		File[] matchingFiles;
		
		matchingFiles = searchFiles(Main.proj.libpath, funcType);
		if (matchingFiles == null) {
			return 1; 
		}
		
		DocumentBuilderFactory dbf2;
		DocumentBuilder        db2 ;
		Document               doc2;

		dbf2 = DocumentBuilderFactory.newInstance();
		try {
			db2  = dbf2.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return 1;
		}
		try {
			
			String xml = readAllBytes(matchingFiles[0].getAbsolutePath());
			xml = removeDOCTYPE(xml);
			InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
			doc2 = db2.parse(stream);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return 1;
		}
		String[] pinTypes = {"Event", "VarDeclaration"};
		ArrayList<InOut> pinFunc = new ArrayList<>(); 
		
		for (String pt : pinTypes) {
			NodeList pinList = doc2.getElementsByTagName(pt);
			for (int j = 0; j < pinList.getLength(); j++) {
				InOut pin; 
				String pinComment = "";
				String pinName = "";
				String pinType = "";
				String newV = readAttribute(pinList, "Comment", j);
				if (thereIs(newV)) pinComment = newV;
				newV = readAttribute(pinList, "Name", j);
				if (thereIs(newV)) pinName = newV;
				newV = readAttribute(pinList, "Type", j);
				if (thereIs(newV)) pinType = newV;
				if (pt.contains("Event")) {
					pinType = "EVENT";
				}
				if (pinList.item(j).getParentNode().getNodeName().contains("In")) {
					pin = new In(pinComment, pinName, pinType);
				}
				else {
					pin = new Out(pinComment, pinName, pinType);
				}
				if (pinName.equals("")) return 3;
				pinFunc.add(pin);
			}
		}
		// при создании сразу добавляется в лист, не нужно отдельно добавлять
		InOut[] pinFuncArr = new InOut[pinFunc.size()];
		for (int j = 0; j < pinFunc.size(); j++) {
			pinFuncArr[j] = pinFunc.get(j);
		}
		Function func = Function.create(pinFuncArr, funcX, funcY, funcComment, funcName, funcType);
		if (painter.checkBorders()) updateLinkMap();
		if (func != null) {
			addFuncToList(func);
			updateInterface();
		}
		return 0;
	}
	public int addMainFB(){
		return addFB(MainFunction.START, MainFunction.TYPE, "", MainFunction.X, MainFunction.Y);
	}
	public void addFuncToList(Function func) {
		functions.getChildren().add(new FunctionInList(func));
	}
	
	private int addConn(NodeList nodeList, int i) {
		String source, destination;
		String str = readAttribute(nodeList, "Source", i);
		if (thereIs(str)) source = str;
		else return 2;
		str = readAttribute(nodeList, "Destination", i);
		if (thereIs(str)) destination = str;
		else return 2;
		Out out = (Out) Function.searchPin(source);
		In in = (In) Function.searchPin(destination);
		return addConn(out, in);
	}
	
	public int addConn(Out out, In in) {
		Wire wire = Wire.connect(out, in);
		if (wire != null) {
			addWireToList(wire);
			updateWires();
		}
		return 0;
	}
	
	
	public void addWireToList(Wire wire) {
		wires.getChildren().add(new WireInList(wire));
	}
	
	private File[] searchFiles(String filepath, String funcType) {
		File f = new File(filepath);
		File[] matchingFiles = null;
		matchingFiles = f.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith(funcType) && name.endsWith("fbt");
		    }
		});
		String[] dirs = f.list();
		if (dirs != null) {
			for (int i = 0; i < dirs.length; i++) {
				if (matchingFiles != null && matchingFiles.length > 0) break;
				if (new File(filepath+"\\"+dirs[i]).isDirectory()) matchingFiles = searchFiles(filepath+"\\"+dirs[i], funcType);
			}
		}
		return matchingFiles;
	}
	public final String SIGN_NO_ATTRIBUTE = "wyjjk307479#35mo7";
	public boolean thereIs(String newV) {
		return !newV.equals(SIGN_NO_ATTRIBUTE);
	}
	private String readAttribute(NodeList nodeList, String nameAtt) {
		return readAttribute(nodeList, nameAtt, 0);	
	}
	private String readAttribute(NodeList nodeList, String nameAtt, int idAtt) { // последний элемент нужен, если есть несколько тэгов с одинаковым названием
				if (nodeList == null) return SIGN_NO_ATTRIBUTE;
				Node node = nodeList.item(idAtt);
				return readAttribute(node, nameAtt);
				    
	}
	private String readAttribute(Node node, String nameAtt) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
	        NamedNodeMap attributes = node.getAttributes();
	        Node valueAttrib;
	        valueAttrib = attributes.getNamedItem(nameAtt);
	        if (valueAttrib != null) {
	        	return valueAttrib.getNodeValue();
	        }
	    }
	   return SIGN_NO_ATTRIBUTE;
}
	
	private FileChooser setUpProjFileChooser() {
		FileChooser file_chooser = new FileChooser();
		//file_chooser.setInitialFileName(Main.projName);
		file_chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("System files (*.sys)", "*.sys"));
		file_chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
		return file_chooser;
	}
	private FileChooser setUpLibFileChooser() {
		FileChooser file_chooser = new FileChooser();
		//file_chooser.setInitialFileName(Main.projName);
		file_chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fboot files (*.fbt)", "*.fbt"));
		file_chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
		if (!Main.proj.libpath.equals("") && new File(Main.proj.libpath).exists()) file_chooser.setInitialDirectory(new File(Main.proj.libpath));
		return file_chooser;
	}
	private void setUpDir(File f) {
		Main.saveDirectory = f.getAbsolutePath();
    	File file = f.getParentFile();
    	file = new File(file.getAbsolutePath()+"/Type Library");
    	if (file.exists()) Main.proj.libpath = file.getAbsolutePath();
    	else {
    		String path = Main.class.getResource("/Type_Library.zip").toString();
    		path = path.replaceFirst("file:/", "");
    		try {
				FileInputStream fis = new FileInputStream(path);
				unzip((InputStream)fis, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
	}
	private String removeDOCTYPE(String xml) {
		int id = xml.indexOf("!DOCTYPE"), idl, idr; // надо удалить доктайпы, ибо мешают читать xml. ищем доктайп, ищем индекс левых угловых и правых угловых скобок и удаляем всю подстроку.
		while (id != -1) {
			for (idl = id; idl > 0; idl--) {
				if (xml.charAt(idl) == '<') {
					break;
				}
			}
			for (idr = id; idr < xml.length(); idr++) {
				if (xml.charAt(idr) == '>') {
					break;
				}
			}
			String contentToReplace = xml.substring(idl, idr + 2); // +2, потому что надо убрать ">\n"
			xml = xml.replace(contentToReplace, "");
			id = xml.indexOf("!DOCTYPE");
		}
		
		return xml;
	}
	
	private String readAllBytes(String filename){
		try {
		    Path filePath = Paths.get(filename);
		    // читаем все байты из файла
		    byte [] fileBytes = Files.readAllBytes(filePath);
		    // конвертируем байты в строку
		    String fileContent = new String(fileBytes);
		    return fileContent;
		} catch (IOException e) {
		    e.printStackTrace();
		    return "";
		}
	}
	public void linkMap(PainterP p) {
		painter = p;
		Map.setContent(p.canvas);
	}public void updateLinkMap() {
		Map.setContent(painter.canvas);
	}
	public void repaintMap() {
		painter.repaint();
		updateLinkMap();
	}
	public void updateInterface() {
		updateFunc();
		updateWires();
	}
	public void updateFunc() {
		for (int i = 0; i < functions.getChildren().size(); i++) 
			if (functions.getChildren().get(i) instanceof FunctionInList) if (!((FunctionInList)functions.getChildren().get(i)).checkSource())
				functions.getChildren().remove(i);
				//		for (int i = 0; i < Function.getFuncList().size(); i++) functions.getChildren().add(new FunctionInList(Function.getFuncList().get(i)));
	}
	public void updateWires() {
		for (int i = 0; i < wires.getChildren().size();i++)
		wires.getChildren().remove(i);
		for (int i = 0; i < Wire.wires().size(); i++) wires.getChildren().add(new WireInList(Wire.wires().get(i)));
	}
	
}
