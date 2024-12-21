package main.java.application;

import java.util.ArrayList;

public class ProjParams { // все значения даны тут по умолчанию, чтобы не пришлось писать лишний софт, но при этом 4diac мог прочесть этот файл
	// устройства и их связи хранятся в другом месте.
	public String libpath = "";
	
	public String sysComment = "";
	public String sysName = "project_name";
	
	public String verVersion = "1.0";
	public String verAuthor = "User";
	public String verDate = "";
	
	public String appComment = "";
	public String appName = "project name";
	
	public String devName = "FORTE_PC";
	public String devType = "FORTE_PC";
	public String devComment = "";
	public String devX = "4000";
	public String devY = "4500";
	
	public String paramName = "MGR_ID";
	public String paramValue = "&quot;localhost:61499&quot;";
	public ArrayList<Attribute> attDev = new ArrayList<>();
	public String resName = "EMB_RES";
	public String resType = "EMB_RES";
	public String resComment = "";
	public String resX = "0.0";
	public String resY = "0.0";
	
	public String segName = "Ethernet";
	public String segType = "Ethernet";
	public String segComment = "";
	public String segX = "4500";
	public String segY = "6000";
	public ArrayList<Attribute> attSeg = new ArrayList<>();
	public String linkSegmentName = "Ethernet";
	public String linkCommResource = "FORTE_PC";
	public String linkComment = "";
	
	ProjParams(){
		attDev.add(new Attribute("Profile", "STRING", "HOLOBLOC", "device profile"));
		attDev.add(new Attribute("Color", "STRING", "255,255,255", "color"));
		attSeg.add(new Attribute("Color", "STRING", "255,255,255", "color"));
	}
	public void clear() {
		attDev.clear();
		attSeg.clear();
	}
	
	private void setValue() {
		
	}
}

