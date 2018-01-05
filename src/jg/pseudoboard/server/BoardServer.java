package jg.pseudoboard.server;

import java.io.IOException;

public class BoardServer {
	
	private static int DEFAULT_PORT = 21899;//CHANGE: 21898
	
	public BoardServer(int port) {
		try {
			new Server(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			new BoardServer(DEFAULT_PORT);
		} else {
			int p = Integer.parseInt(args[0]);
			new BoardServer(p);
		}
	}
	

}
