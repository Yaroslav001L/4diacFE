package main.java.application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

import main.java.instructions.Function;
import main.java.instructions.In;
import main.java.instructions.Wire;

public class ForteConnect  {
	
	Socket socket;
	BufferedReader in;
	BufferedWriter out;
	
	
	public void startConnection() {
		try {
		socket = new Socket("127.0.0.1", 61499);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		catch (IOException e) {
			System.out.println("Connection failed");
		}
		
	}
	
	public void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void send(String msg, String name) {
		String additional = "";
		additional = additional + (char)80 + (char)0 + (char)name.length() + name + (char)80 + (char)0 + (char)msg.length();
        try {
        	msg = additional + msg;
            out.write(msg);
            out.flush();
            }
        catch (IOException ignored) {}
	}
	
	public String recv() {
		String res = "";
		int answer = -1;
		
		try {
			in.read(new char[2], 0, 2);
			int k = in.read();
			for (int i = 0; i < k; i++) {
            answer = in.read();
            res += (char)answer;
			}
			return res;
        } catch (IOException ignored) {
        	return "ignored";
        }
	}
	public String transformFB(int id, Function func) {
		String msg = "<Request ID=\"" + id + "\" Action=\"CREATE\"><FB Name=\"" + func.name + "\" Type=\"" + func.type + "\" /></Request>";
		return msg;
	}
	public String transformConn(int id, Wire wire) {
		String msg = "<Request ID=\"" + id + "\" Action=\"CREATE\"><Connection Source=\"" + wire.source.getPath() + "\" Destination=\"" + wire.target.getPath() + "\" /></Request>";
		return msg;
	}
	public String transformParam(int id, In pin) {
		String msg = "<Request ID=\"" + id + "\" Action=\"WRITE\"><Connection Source=\"" + pin.getValue() + "\" Destination=\"" + pin.name + "\" /></Request>";
		return msg;
	}
	public String transformQuery(int id) {
		String msg = "<Request ID=\"" + id + "\" Action=\"QUERY\"><FB Name=\"*\" Type=\"*\" /></Request>";
		return msg;
	}
	public String transformEMB(int id) {
		String msg = "<Request ID=\"" + id + "\" Action=\"CREATE\"><FB Name=\"" + Main.proj.resName + "\" Type=\"" + Main.proj.resType + "\" /></Request>";
		return msg;
	}
	public String start(int id) {
		String msg = "<Request ID=\"" + id + "\" Action=\"START\"/>";
		return msg;
	}
	
	
}
