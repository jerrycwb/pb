package com.jerry.protocol.pb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utility {
	public static String getProjectPath(){
		String rootpath = System.getProperty("user.dir");
		String codePath = new Utility().getClass().getProtectionDomain()
				.getCodeSource().getLocation().getPath();
		if(codePath.endsWith("jar")){
			File jarFile = new File(codePath);
			rootpath = jarFile.getParentFile().getAbsolutePath();
		}
		return rootpath;
	}
	
	public static String runLocalCmd(String cmd){
	    String line = null;  
	    StringBuilder sb = new StringBuilder();  
	    Runtime runtime = Runtime.getRuntime();  
	    try {  
		    Process process = null;
		    if(isLinux()){
		    	process = runtime.exec(new String[]{"sh", "-c", cmd});  
		    }else{
		    	process = runtime.exec(cmd);
		    }
		    BufferedReader  bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));	      
	        while ((line = bufferedReader.readLine()) != null) {  
	            sb.append(line + "\n"); 
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } 
	    return sb.toString();
	}
	
	public static Boolean isLinux(){
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("linux") > -1;
	}
}
