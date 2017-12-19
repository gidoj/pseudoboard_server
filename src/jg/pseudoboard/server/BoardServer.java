package jg.pseudoboard.server;

public class BoardServer {
	
	private static int DEFAULT_PORT = 9365;
	
	public BoardServer(int port) {
		new Server(port);
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
