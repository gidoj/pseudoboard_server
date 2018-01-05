package jg.pseudoboard.server;

public class PathManager {
	
	//Add string to include path to server files for testing. SERVER_PATH will be actual path to files
	//once everything is moved to server.
	
	private static final String SERVER_PATH = "/home/joseph/Documents/pseudoboard/";
	private static final String MAC_PATH = "/Users/josephgido/Documents/Tech_Stuff/Computer_Programming/pseudoboard/";
	
	public enum FILE {
		USER_LIST, USER_INFO, CANVAS
	}
	
	public static String getPath(FILE f, String username, String canvasName) {
		String path = "";
		String end = "";
		switch (f) {
		case USER_LIST:
			end = "user_list.txt";
			break;
		case USER_INFO:
			end = "users/user_" + username + ".txt";
			break;
		case CANVAS:
			end = "canvas/" + canvasName + ".txt";
			break;
		default:
			break;
		}
		path = MAC_PATH + end;
		return path;
	}
	
}
