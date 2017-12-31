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
	
	Map<String, Integer> userMap;
	
	public UserManager() {
		//load users (ids, names) into map
		userMap = new HashMap<String, Integer>();
		loadUsers();
	}
	
	public boolean uniqueNewUser(int id, String username) {
		if (userMap.containsKey(username)) return false;
		return true;
	}
	
	public boolean checkExistingUser(int id, String username) {
		if (userMap.containsKey(username) && userMap.get(username) == id) return true;
		return false;
	}
	
	public void addUser(int id, String username) {
		//update map
		userMap.put(username, id);
		
		//update user list file
		String userString = username + ";" + id;
		try {
			FileWriter fw = new FileWriter(new File(PathManager.getPath(PathManager.FILE.USER_LIST, "", "")), true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(userString);
			pw.close();
		} catch (IOException e) {
			Logger.output("Error adding new user (" + username + ", " + id + ").");
			Logger.output(e);
		}
		
		//generate new file for user's canvas and other relevant information
		File f = new File(PathManager.getPath(PathManager.FILE.USER_INFO, username, ""));
		try {
			f.createNewFile();
		} catch (IOException e) {
			Logger.output("Error creating new user file (" + username + ", " + id + ").");
			Logger.output(e);
		}
	}
	
	private void loadUsers() {
		List<String> userList = getFileLines(PathManager.FILE.USER_LIST, "", "");
		String[] user = null;
		for (int i = 0; i < userList.size(); i++) {
			user = userList.get(i).split(";");
			String name = user[0];
			Integer id = Integer.parseInt(user[1]);
			userMap.put(name, id);
		}
	}
	
	private List<String> getFileLines(PathManager.FILE f, String username, String canvasName) {
		List<String> lines = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(new File(PathManager.getPath(f, username, canvasName)));
			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			Logger.output(e);
		}
		return lines;
	}

}
