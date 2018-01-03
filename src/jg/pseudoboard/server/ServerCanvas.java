package jg.pseudoboard.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jg.pseudoboard.common.CanvasElement;

public class ServerCanvas {
	
	List<ClientThread> users;
	String canvasName;
	//List<int[]> canvasStack; TODO: implement this later
	int[] canvasStack;
	
	int width;
	int height;
	int size;
	
	int bg;
	
	public ServerCanvas(String canvasName) {
		this.canvasName = canvasName;
		users = new ArrayList<ClientThread>();
	}
	
	public boolean createNewCanvas(String username, int width, int height, int bg) {
		this.width = width;
		this.height = height;
		size = width*height;
		
		this.bg = bg;
		
		String filename = PathManager.getPath(PathManager.FILE.CANVAS, username, canvasName);
		File f = new File(filename);
		if (!f.exists()) {
			canvasStack = new int[size];
			for (int i = 0; i < size; i++) canvasStack[i] = -1;
			return true;
		}
		
		return false;
	}
	
	public String openCanvas() {
		String canvasFilepath = PathManager.getPath(PathManager.FILE.CANVAS, "", canvasName);
		StringBuilder canvasString = new StringBuilder();
		try {
			Scanner scanner = new Scanner(new File(canvasFilepath));
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				canvasString.append(nextLine);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			Logger.output(e);
		}
		canvasString.deleteCharAt(canvasString.length()-1);
		
		String raw = canvasString.toString();
		String[] rawArray = raw.split(";");
		width = Integer.parseInt(rawArray[1]);
		height = Integer.parseInt(rawArray[2]);
		size = width*height;
		bg = Integer.parseInt(rawArray[3]);
		
		String[] imageStrings = rawArray[4].split(",");
		canvasStack = new int[size];
		for (int i = 0; i < size; i++) {
			canvasStack[i] = Integer.parseInt(imageStrings[i]);
		}
		
		return raw;
	}
	
	public void saveCanvas(String username) {
		File f = new File(PathManager.getPath(PathManager.FILE.CANVAS, "", canvasName));
		String name = canvasName;
		String info = name + ";" + width + ";" + height + ";" + bg + ";";
		//write string to canvas file
		try {
			f.createNewFile();//create file if doesn't exist
			FileWriter canvasFW = new FileWriter(f, true);
			PrintWriter canvasPW = new PrintWriter(canvasFW);
			canvasPW.print(info);
			
			//write canvas array to file
			for (int i = 0; i < size; i++) {
				canvasPW.print(Integer.toString(canvasStack[i]) + ",");
			}
			canvasPW.close();
			
			//write canvas name to user file
			File userFile = new File(PathManager.getPath(PathManager.FILE.USER_INFO, username, ""));
			FileWriter userFW = new FileWriter(userFile, true);
			PrintWriter userPW = new PrintWriter(userFW);
			userPW.println(name);
			userPW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void updateCanvas(int[] canvas) {
		//TODO: implement later - add to top of stack
		//if stack size is > 5, collapse bottom two into one
		//stack just one layer for now
		for (int i = 0; i < size; i++) {
			canvasStack[i] = canvas[i];
		}
	}
	
	public void broadcastUpdate() {
		for (int i = 0; i < users.size(); i++) {
			users.get(i).sendData(new CanvasElement(canvasStack));
		}
	}
	
	public void addUser(ClientThread user) {
		users.add(user);
		Logger.output(user.getUsername() + " added to canvas: " + canvasName + " (" + numUsers() + " users).");
	}
	
	public void removeUser(ClientThread user) {
		users.remove(user);
		Logger.output(user.getUsername() + " removed from canvas: " + canvasName + " (" + numUsers() + " users).");
	}
	
	public int numUsers() {
		return users.size();
	}
	
	public String getCanvasString() {
		String info = canvasName + ";" + width + ";" + height + ";" + bg + ";";
		StringBuilder all = new StringBuilder();
		all.append(info);
		for (int i = 0; i < size; i++) {
			all.append(Integer.toString(canvasStack[i]) + ",");
		}
		all.deleteCharAt(all.length()-1);
		return all.toString();
	}

}
