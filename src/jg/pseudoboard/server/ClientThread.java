package jg.pseudoboard.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import jg.pseudoboard.common.BoardElement;

public class ClientThread implements Runnable {
	
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Server server;
	
	private Thread run, handleServerMessage, handleCanvasUpdate;
	
	private volatile boolean running = false;
	
	private int clientID = -1;
	private String username = "";
	
	public ClientThread(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
		
		Logger.output("Connecting to new client...");
		Logger.output("Creating input/output object streams...");
		run = new Thread(this, "running");
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			Logger.output("Streams created.");
			run.start();
		} catch (IOException e) {
			Logger.output("Error creating input/output streams.");
			Logger.output(e);
			return;
		}
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			BoardElement elt;
			try {
				elt = (BoardElement) in.readObject();
			} catch (IOException e) {
				//check if error because client intentionally disconnected
				if (!running) {
					Logger.output("Client disconnected.");
					return;
				}
				Logger.output("Error reading stream: " + username + ", " + clientID);
				Logger.output(e);
				break;
			} catch (ClassNotFoundException e) {
				Logger.output(e);
				break;
			}
			
			switch (elt.getType()) {
			case MESSAGE_TO_SERVER:
				handleServerMessage(elt);
				break;
			case GRAPHIC:
			case MOVE:
			case ERASE:
				handleCanvasUpdate(elt);
				break;
			default:
				break;
			}
		}
		//no longer running - close socket
		try {
			socket.close();
			Logger.output("Client (ID: " + clientID + ") socket closed.");
		} catch (IOException e) {
			Logger.output(e);
		}
	}
	
	private void handleServerMessage(BoardElement elt) {
		handleServerMessage = new Thread("handleServerMessage") {
			public void run() {
				//TODO:
			}
		};
		handleServerMessage.start();
	}
	
	private void handleCanvasUpdate(BoardElement elt) {
		handleCanvasUpdate = new Thread("handleCanvasUpdate") {
			public void run() {
				//add stuff later
			}
		};
		handleCanvasUpdate.start();
	}
	
	private void sendData(BoardElement elt) {
		try {
			out.writeObject(elt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.output(e);
		}
	}
	
	public void setUserInfo(String username, int clientID) {
		this.clientID = clientID;
		this.username = username;
		Logger.output("Client info set: " + username + ", " + clientID);
	}
	
	public int getClientID() {
		return clientID;
	}
	
	public String getUsername() {
		return username;
	}

}
