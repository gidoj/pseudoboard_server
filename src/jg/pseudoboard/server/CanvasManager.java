package jg.pseudoboard.server;

import java.util.HashMap;
import java.util.Map;

public class CanvasManager {
	
	private Map<String, ServerCanvas> openCanvas;
	
	public CanvasManager() {
		openCanvas = new HashMap<String, ServerCanvas>();
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
	
	public void updateCanvas(String canvasName, int[] change) {
		openCanvas.get(canvasName).updateCanvas(change);
		openCanvas.get(canvasName).broadcastUpdate();
	}

}
