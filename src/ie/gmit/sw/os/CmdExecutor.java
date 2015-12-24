package ie.gmit.sw.os;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CmdExecutor {
	
	public static String SEP = "\\";
	
	public static boolean mkDir(Authentication auth, String folderName){
		
	    String newDir = CmdExecutor.smartDirectory(folderName, auth);
	    
	    
	    if(!newDir.contains(auth.getUser().getDirectory()))
	    	return false;	
	    
		File theDir = new File(EchoServer.ROOT + SEP + auth.getUser().getCurrentDir() + SEP + folderName);


	    boolean result = false;

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        

		}
		return result;
		
	}
	
	public static boolean moveDir(Authentication auth, String folderName){
	    boolean result = false;
	    
	    String newDir = CmdExecutor.smartDirectory(folderName, auth);
	    if(!newDir.contains(auth.getUser().getDirectory()))
	    	return false;	
	    
		File theDir = new File(newDir);
		if (theDir.exists()) {
			auth.getUser().setCurrentDir(newDir);
			result = true;
		}
	    
	    
		return result;

	}
	
	public static String smartDirectory(String dir, Authentication auth){
		String newDir = "";
		String[] splitted = dir.split(SEP+SEP);
		
		if(splitted[0].equals("."))
			newDir = auth.getUser().getDirectory();
		else 
			newDir = auth.getUser().getCurrentDir();
		
		for(int i = 0; i< splitted.length; i++){
			
			if(!splitted[i].contains("."))
				newDir += (SEP + splitted[i]);
			else if(splitted[i].equals("..") && !auth.getUser().isAtRoot()){
				newDir = newDir.substring(0, newDir.lastIndexOf(SEP));
			}
			else if(splitted[i].equals("..") && auth.getUser().isAtRoot()){
				newDir = "ERROR";
			}
			
				
		}
		
		return newDir;
	}
	
	public static String listFiles(Authentication auth){
		
		String newDir = smartDirectory("", auth);
		if(newDir.equals("ERROR"))
			return "Invalid path or not allowed area\n";
		
		File folder = new File(newDir);
		File[] listOfFiles = folder.listFiles();
		String list = "";
		
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	        list += ("File " + listOfFiles[i].getName() + "\n");
	      } else if (listOfFiles[i].isDirectory()) {
	        list += ("Directory " + listOfFiles[i].getName() + "\n");
	      }
	    }
	    return list;
	}
	
	public static byte[] sendFile(String origin){
		
		File f = new File(origin);
		byte[] content = null;
		try {
			content = Files.readAllBytes(f.toPath());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
		
	}
	public static boolean saveFile(byte[] content, Authentication auth, String name){
		//name = "teste.txt";
		File f = new File(auth.getUser().getCurrentDir() + SEP + name);
		try {
			Files.write(f.toPath(), content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;

	}
	
	public static boolean saveFileClient(byte[] content, String name){
		//name = "teste.txt";
		File f = new File(name);
		try {
			Files.write(f.toPath(), content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;

	}

}
