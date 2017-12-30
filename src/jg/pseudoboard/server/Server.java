package jg.pseudoboard.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jg.pseudoboard.common.BoardElement;

public class Server implements Runnable {
	
	private int port;
	private ServerSocket serverSocket;
	
	private volatile boolean running = false;
	private Thread run, receive, broadcast;
	
	private UserManager userManager;
	private List<ClientThread> clientThreads;
	
	private Server s;
	
	public Server(int port) throws IOException {
		s = this;//keep track of server for client threads
		
		//Start up message
		Logger.output("--------------------------------");
		Logger.output("-------PseudoBoard Server-------");
		Logger.output("--------------------------------");
		Logger.output("Starting on port: " + port);
		
		
		//initialize variables
		userManager = new UserManager();
		clientThreads = new ArrayList<ClientThread>();
		this.port = port;
		serverSocket = new ServerSocket(port);
		
		//start server thread
		run = new Thread(this, "Server");
		run.start();
	}

	@Override
	public void run() {
		running = true;
		
		Logger.output("Server started on port: " + port);
		
		receive();//start listening for messages from clients
		
		Scanner scanner = new Scanner(System.in);
		while (running) {
			//handle command line control
			//--add more control in future
			String text = scanner.nextLine();
			if (text.equals("/quit")) shutDown();
		}
		scanner.close();
	}
	
	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					try {
						//listen for messages from clients
						Socket socket = serverSocket.accept();
						ClientThread clientThread = new ClientThread(socket, s, userManager);
						clientThreads.add(clientThread);
					} catch (IOException e) {
						//if not running, then no error - exception because socket closed
						if (!running) Logger.output("Socket closed for shutdown.");
						else Logger.output(e);
					}
				}
			}
		};
		receive.start();
	}
	
	public void updateCanvas() {
		//Add stuff later
	}
	
	public void broadcast(BoardElement elt) {
		broadcast = new Thread("broadcast") {
			public void run() {
				//add stuff here later
			}
		};
		broadcast.start();
	}
	
	public void disconnectClient(ClientThread ct) {
		clientThreads.remove(ct);
	}
	
	public void shutDown() {
		//add stuff here later
	}
	
	

}
