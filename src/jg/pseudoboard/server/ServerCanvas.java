package jg.pseudoboard.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jg.pseudoboard.common.GraphicElement;

public class ServerCanvas {
	
	private List<ClientThread> users;
	private String canvasName;
	//List<int[]> canvasStack; TODO: implement this later
	private int[] canvasStack;
	
	private List<int[]> updateStack;
	
	private int width;
	private int height;
	private int size;

	private int bg;
	
	//values relating to graphic update
	private int minX, minY, maxX, maxY;
	private int[] graphicArray;
	
	public ServerCanvas(String canvasName) {
		this.canvasName = canvasName;
		users = new ArrayList<ClientThread>();
		updateStack = new ArrayList<int[]>();
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
			for (int i = 0; i < size; i++) canvasStack[i] = 0;
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
			boolean filecreated = f.createNewFile();//create file if doesn't exist
			FileWriter canvasFW = new FileWriter(f, false);
			PrintWriter canvasPW = new PrintWriter(canvasFW);
			canvasPW.print(info);
			
			//write canvas array to file
			for (int i = 0; i < size; i++) {
				canvasPW.print(Integer.toString(canvasStack[i]) + ",");
			}
			canvasPW.close();
			
			//write canvas name to user file only if canvas didn't already exist
			if (filecreated) {
				File userFile = new File(PathManager.getPath(PathManager.FILE.USER_INFO, username, ""));
				FileWriter userFW = new FileWriter(userFile, true);
				PrintWriter userPW = new PrintWriter(userFW);
				userPW.println(name);
				userPW.close();				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void shareCanvas(String shareWith) {
		String userFile = PathManager.getPath(PathManager.FILE.USER_INFO, shareWith, "");
		List<String> ownedCanvases = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(new File(userFile));
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				ownedCanvases.add(nextLine);
			}
			scanner.close();
			
			if (ownedCanvases.contains(canvasName)) return;
			
			FileWriter fw = new FileWriter(userFile, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(canvasName);
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateCanvas(int[] sectionUpdate) {
		//TODO: implement later - add to top of stack
		//if stack size is > 5, collapse bottom two into one
		//stack just one layer for now
		if (sectionUpdate != null) updateStack.add(sectionUpdate);
		int[] canvas = updateStack.remove(0);
		minX = canvas[0];
		minY = canvas[1];
		maxX = canvas[2];
		maxY = canvas[3];
		int graphicWidth = maxX - minX + 1;
		int graphicHeight = maxY - minY + 1;
		int graphicSize = graphicWidth * graphicHeight;
		graphicArray = new int[graphicSize];
		for (int y = 0; y < graphicHeight; y++) {
			for (int x = 0; x < graphicWidth; x++) {
				int updateVal = canvas[y*graphicWidth + x + 4];
				graphicArray[y*graphicWidth + x] = updateVal;
				if (updateVal == 0) continue;
				canvasStack[(y+minY)*width + (x+minX)] = updateVal;
			}
		}
		
		if (updateStack.size() > 0) updateCanvas(null); 
	}
	
	public void broadcastUpdate() {
		for (int i = 0; i < users.size(); i++) {
			users.get(i).sendData(new GraphicElement(minX, minY, maxX, maxY, graphicArray));
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
	
	public String getName() {
		return canvasName;
	}
	
	public List<String> getUsers() {
		List<String> usernames = new ArrayList<String>();
		for (int i = 0; i < numUsers(); i++) {
			usernames.add(users.get(i).getUsername());
		}
		return usernames;
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
