package jg.pseudoboard.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserManager {
	
	Map<Integer, String> userMap;
	
	public UserManager() {
		//load users (ids, names) into map
		userMap = new HashMap<Integer, String>();
		loadUsers();
	}
	
	public boolean uniqueNewUser(int id, String username) {
		if (userMap.containsValue(username)) return false;
		return true;
	}
	
	public boolean checkExistingUser(int id, String username) {
		if (userMap.containsKey(id) && userMap.containsValue(username)) return true;
		return false;
	}
	
	public void addUser(int id, String username) {
		//update map
		userMap.put(id, username);
		
		//update user list file
		String userString = id + ";" + username;
		try {
			FileWriter fw = new FileWriter(new File(PathManager.getPath(PathManager.FILE.USER_LIST, -1, -1)));
			PrintWriter pw = new PrintWriter(fw);
			pw.println(userString);
			pw.close();
		} catch (IOException e) {
			Logger.output("Error adding new user (" + username + ", " + id + ").");
			Logger.output(e);
		}
	}
	
	private void loadUsers() {
		List<String> userList = getFileLines(PathManager.FILE.USER_LIST, -1, -1);
		String[] user = null;
		for (int i = 0; i < userList.size(); i++) {
			user = userList.get(i).split(";");
			Integer id = Integer.parseInt(user[0]);
			String name = user[1];
			userMap.put(id, name);
		}
	}
	
	private List<String> getFileLines(PathManager.FILE f, int userID, int canvasID) {
		List<String> lines = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(new File(PathManager.getPath(f, userID, canvasID)));
			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			Logger.output(e);
		}
		return null;
	}

}
