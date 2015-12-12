package ie.gmit.sw.os;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Authentication {
	private User currentUser;
	private HashMap<String, User> usermap;
	
	public Authentication(User user) {
		super();
		this.currentUser = new User(user.getName(), user.getPassword(), user.getDirectory());
	}
	
	public boolean initHash(){
		
		usermap = new HashMap<String, User>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(EchoServer.ROOT + CmdExecutor.SEP +
				"userslist.txt"))) {
		    String line = br.readLine();

		    while(line != null){
		    	User u = new User();
		    	u.setName(line.split(" ")[0]);
		    	u.setPassword(line.split(" ")[1]);
		    	if(line.split(" ").length > 2)
		    		u.setDirectory(line.split(" ")[2]);
		    	else{
		    		this.mkDir();
		    		u.setDirectory("users" + CmdExecutor.SEP + u.getName());
		    	}
			    
		    	usermap.put(u.getName(), u);
			    line = br.readLine();
		    }	
		    
		    
		    
		    return true;
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean initSession(){
		
		User u = usermap.get(currentUser.getName()); 
		if(u == null)
			return false;
	    if(u.getPassword().equals(this.currentUser.getPassword())){
	    	currentUser = u;
	    	currentUser.setCurrentDir(currentUser.getDirectory());
	    	return true;
	    }
	    
	    
	    return false;
	        
	}

	public boolean mkDir(){
		File theDir = new File(EchoServer.ROOT + CmdExecutor.SEP + "users" + CmdExecutor.SEP + this.currentUser.getName());
	    boolean result = false;

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    //System.out.println("creating directory: " + directoryName);
		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		        System.out.println("DIR created");  
		    }
		}
		return result;
	}
	
	public boolean updateFile(){

		String outputString = "";
		
		for(String s : usermap.keySet()){
			User u = usermap.get(s);
			outputString += u.getName() + " " + u.getPassword() + " " + u.getDirectory() + "\n";
		}
		
		
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter(new FileWriter(EchoServer.ROOT + CmdExecutor.SEP +
					"userslist.txt"));
		    writer.write(outputString);
	
		}
		catch ( IOException e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		        return true;
		    }
		    catch ( IOException e)
		    {
		    }
		}
		return false;
	}
	
	public User getUser() {
		return currentUser;
	}

	public void setUser(User user) {
		this.currentUser = user;
	}
	
}
