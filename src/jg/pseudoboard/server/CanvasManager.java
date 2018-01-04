package jg.pseudoboard.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CanvasManager {
	
	private Map<String, ServerCanvas> openCanvas;
	
	public CanvasManager() {
		openCanvas = new HashMap<String, ServerCanvas>();
	}
	
	public List<ServerCanvas> getOpenCanvases() {
		List<ServerCanvas> canvasList = new ArrayList<ServerCanvas>();
		for (String canvasName : openCanvas.keySet()) {
			canvasList.add(openCanvas.get(canvasName));
		}
		return canvasList;
	}
	
	public void addCanvas(String canvasName, ServerCanvas canvas) {
		openCanvas.put(canvasName, canvas);
	}
	
	public void addUserToCanvas(String canvasName, ClientThread user) {
		openCanvas.get(canvasName).addUser(user);
	}
	
	public void removeUserFromCanvas(String canvasName, ClientThread user) {
		openCanvas.get(canvasName).removeUser(user);
		if (openCanvas.get(canvasName).numUsers() == 0) {
			openCanvas.remove(canvasName);
		}
	}
	
	public void shareCanvas(String canvasName, String shareWith) {
		openCanvas.get(canvasName).shareCanvas(shareWith);
	}
	
	public void updateCanvas(String canvasName, int[] change) {
		openCanvas.get(canvasName).updateCanvas(change);
		openCanvas.get(canvasName).broadcastUpdate();
	}
	
	public void saveCanvas(String canvasName, String username) {
		openCanvas.get(canvasName).saveCanvas(username);
	}
	
	public boolean canvasIsOpen(String canvasName) {
		return openCanvas.containsKey(canvasName);
	}
	
	public String getCanvasString(String canvasName) {
		return openCanvas.get(canvasName).getCanvasString();
	}
	
	public String openCanvas(String canvasName) {
		ServerCanvas sc = new ServerCanvas(canvasName);
		if (!openCanvas.containsKey(canvasName)) openCanvas.put(canvasName, sc);
		return sc.openCanvas();
	}
	
	public String getCanvasList(String username) {
		String canvasList = PathManager.getPath(PathManager.FILE.USER_INFO, username, "");
		StringBuilder canvasString = new StringBuilder();
		try {
			Scanner scanner = new Scanner(new File(canvasList));
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				canvasString.append(nextLine + ";");
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			Logger.output(e);
		}
		if (canvasString.length() > 0) canvasString.deleteCharAt(canvasString.length()-1);
		return canvasString.toString();
	}

}
