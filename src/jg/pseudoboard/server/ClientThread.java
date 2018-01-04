package jg.pseudoboard.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import jg.pseudoboard.common.BoardElement;
import jg.pseudoboard.common.MessageElement;
import jg.pseudoboard.common.MessageTypeConverter;
import jg.pseudoboard.common.MessageTypeConverter.MessageType;

public class ClientThread implements Runnable {
	
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Server server;
	private ClientThread self;
	
	
	private Thread run, handleServerMessage, handleCanvasUpdate;
	
	private volatile boolean running = false;
	
	private int clientID = -1;
	private String username = "";
	
	private String canvasOpen = "";
	
	public ClientThread(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
		self = this;
		
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
					break;
				}
				Logger.output("Error reading stream: " + username);
				Logger.output("Forcing client disconnect: " + username);
				disconnect();
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
	}
	
	private void handleServerMessage(BoardElement elt) {
		handleServerMessage = new Thread("handleServerMessage") {
			public void run() {
				switch (MessageTypeConverter.getMessageType(elt.getID())) {
				case DISCONNECT:
					disconnect();
					break;
				case LOGIN_EXISTING_USER:
					String[] retUser = ((String) elt.getData()).split(";");
					int retID = Integer.parseInt(retUser[1]);
					String retName = retUser[0];
					Logger.output("Trying to load existing user(" + retName + ").");
					if (server.userManager.checkExistingUser(retID, retName)) {
						sendData(new MessageElement("", MessageType.LOGIN_SUCCESS));
						setUserInfo(retName, retID);
					} else {
						sendData(new MessageElement("", MessageType.LOGIN_FAIL));
					}
					break;
				case LOGIN_NEW_USER:
					String[] newUser = ((String) elt.getData()).split(";");
					int newID = Integer.parseInt(newUser[1]);
					String newName = newUser[0];
					Logger.output("Trying to setup new user (" + newName + ").");
					if (server.userManager.uniqueNewUser(newID, newName)) {
						sendData(new MessageElement("", MessageType.LOGIN_SUCCESS));
						setUserInfo(newName, newID);
						server.userManager.addUser(newID, newName);
					} else {
						sendData(new MessageElement("", MessageType.LOGIN_FAIL));
					}
					break;
				case NEW_CANVAS:
					if (!canvasOpen.equals("")) server.canvasManager.removeUserFromCanvas(canvasOpen, self);
					String canvasInfo[] = ((String) elt.getData()).split(";");
					String canvasName = "canvas_" + username + "-" + canvasInfo[0];
					int canvasWidth = Integer.parseInt(canvasInfo[1]);
					int canvasHeight = Integer.parseInt(canvasInfo[2]);
					int canvasBG = Integer.parseInt(canvasInfo[3]);
					ServerCanvas sc = new ServerCanvas(canvasName);
					Boolean success = sc.createNewCanvas(canvasName, canvasWidth, canvasHeight, canvasBG);
					if (!success) sendData(new MessageElement((String) elt.getData(), MessageType.NEW_CANVAS_FAIL));
					else {
						server.canvasManager.addCanvas(canvasName, sc);
						server.canvasManager.addUserToCanvas(canvasName, self);
						setOpenCanvas(canvasName);
						sendData(new MessageElement((String) elt.getData(), MessageType.NEW_CANVAS));
					}
					break;
				case OPEN_CANVAS:
					if (!canvasOpen.equals("")) server.canvasManager.removeUserFromCanvas(canvasOpen, self);
					String canvasRequest = (String) elt.getData();
					String canvasString = "";
					if (server.canvasManager.canvasIsOpen(canvasRequest)) {
						canvasString = server.canvasManager.getCanvasString(canvasRequest);
					} else {
						canvasString = server.canvasManager.openCanvas(canvasRequest);
					}
					server.canvasManager.addUserToCanvas(canvasRequest, self);
					setOpenCanvas(canvasRequest);
					sendData(new MessageElement(canvasString, MessageType.OPEN_CANVAS));
					break;
				case SAVE_CANVAS:
					if (canvasOpen.equals("")) return;
					server.canvasManager.saveCanvas(canvasOpen, username);
					break;
				case USER_LIST:
					String userList = server.userManager.getShareString(username);
					sendData(new MessageElement(userList, MessageType.USER_LIST));
					break;
				case CANVAS_LIST:
					String canvasList = server.canvasManager.getCanvasList(username);
					sendData(new MessageElement(canvasList, MessageType.CANVAS_LIST));
					break;
				case SHARE_CANVAS:
					String shareWith = (String) elt.getData();
					server.canvasManager.shareCanvas(canvasOpen, shareWith);
					break;
				default:
					break;
				}
			}
		};
		handleServerMessage.start();
	}
	
	private void handleCanvasUpdate(BoardElement elt) {
		handleCanvasUpdate = new Thread("handleCanvasUpdate") {
			public void run() {
				server.canvasManager.updateCanvas(canvasOpen, (int[]) elt.getData());
			}
		};
		handleCanvasUpdate.start();
	}
	
	public void sendData(BoardElement elt) {
		try {
			out.writeObject(elt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.output(e);
		}
	}
	
	public void disconnect() {
		Logger.output("User disconnecting (" + username + ")");
		if (!canvasOpen.equals("")) server.canvasManager.removeUserFromCanvas(canvasOpen, self);
		running = false;
		try {
			socket.close();
			Logger.output("Client (" + username + ") socket closed.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.output(e);
		}
		server.disconnectClient(this);
	}
	
	public void setOpenCanvas(String canvasName) {
		canvasOpen = canvasName;
	}
	
	public String getOpenCanvas() {
		return canvasOpen;
	}
	
	public void setUserInfo(String username, int clientID) {
		this.clientID = clientID;
		this.username = username;
		Logger.output("Client info set: " + username);
	}
	
	public int getClientID() {
		return clientID;
	}
	
	public String getUsername() {
		return username;
	}

}
