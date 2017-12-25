package jg.pseudoboard.server;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
	
	public static void output(String s) {
		System.out.println(s);
	}
	
	public static void output(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		//above not used at the moment - will keep in case
		//want to log errors in the future
		
		e.printStackTrace();
	}

}
