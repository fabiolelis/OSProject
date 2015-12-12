package ie.gmit.sw.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class EchoServer {

  public static int PORT = 2004;
  public static String ROOT = "/Users/fabiolelis/Documents/workspace/OSXmasProject";
  

  public static void main(String[] args) throws Exception {
    ServerSocket m_ServerSocket = new ServerSocket(PORT,10);
    int id = 0;
    while (true) {
      Socket clientSocket = m_ServerSocket.accept();
      ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
      cliThread.start();
    }
  }
}

class ClientServiceThread extends Thread {
  Socket clientSocket;
  String message;
  int clientID = -1;
  boolean running = true;
  ObjectOutputStream out;
  ObjectInputStream in;

  ClientServiceThread(Socket s, int i) {
    clientSocket = s;
    clientID = i;
  }

  void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client> " + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
  public void run() {
    System.out.println("Accepted Client : ID - " + clientID + " : Address - "
        + clientSocket.getInetAddress().getHostName());
    try 
    {
    	out = new ObjectOutputStream(clientSocket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(clientSocket.getInputStream());
		System.out.println("Accepted Client : ID - " + clientID + " : Address - "
		        + clientSocket.getInetAddress().getHostName());
		Authentication auth = null;
		
		//sendMessage("Connection successful");
		do{
			try
			{
				
				//System.out.println("server>"+clientID+"  "+ message);
				//if (message.equals("bye"))
				//sendMessage("server got the following: "+message);
				//message = (String)in.readObject();
				while(auth == null){
					String name, password;
					sendMessage("Please insert your user name: ");
					name = (String)in.readObject();
					sendMessage("Password: ");
					password = (String)in.readObject();
					auth = new Authentication(new User(name, password, ""));
					auth.initHash();
					auth.updateFile();
					if(auth.initSession()){
						sendMessage(auth.getUser().getDirectory() + "Enter \"OK\" to go on or \"Exit\" to finish");
						message = (String)in.readObject();

					}
					else{
						sendMessage("User not found " + auth.getUser().toString() + "\nEnter \"login\" to try again or \"exit\" to finish" );
						message = (String)in.readObject();

						auth = null;
					}
				}
				sendMessage("Dir: " + auth.getUser().getCurrentDir()  + " - Enter a command: ");
				message = (String)in.readObject();
				
				if(message.contains("mkdir ")){
					String folderName = message.split(" ")[1];
					boolean op = CmdExecutor.mkDir(auth, folderName);
					if(!op){
						sendMessage("Invalid path or not allowed area\nEnter \"OK\" to try again or \"exit\" to finish" );
						message = (String)in.readObject();
					}
					
						
				}
				else if(message.contains("cd ")){
					String folderName = message.split(" ")[1];
					boolean op = CmdExecutor.moveDir(auth, folderName);
					if(!op){
						sendMessage("Invalid path or not allowed area\nEnter \"OK\" to try again or \"exit\" to finish" );
						message = (String)in.readObject();
					}
				}
				else if(message.contains("ls ")){
					
					String listFiles = CmdExecutor.listFiles(auth);
					sendMessage("Files: \n" + listFiles + "\nEnter \"OK\" to more commands or \"exit\" to finish" ); 
					message = (String)in.readObject();

				}
				else if(message.contains("put ")){
					String filename = null;
					if(message.contains(CmdExecutor.SEP))
						filename = message.substring(message.lastIndexOf(CmdExecutor.SEP) +1, message.length());
					else
						filename = message.substring(message.indexOf(" ") +1, message.length());
					
					byte[] content = (byte[])in.readObject();
					CmdExecutor.saveFile(content, auth, filename);

				}
				else if(message.contains("get ")){
					
					String filename = null;
					if(message.contains(CmdExecutor.SEP))
						filename = message.substring(message.lastIndexOf(CmdExecutor.SEP) +1, message.length());
					else
						filename = message.substring(message.indexOf(" ") +1, message.length());
					
					byte[] content = CmdExecutor.sendFile(filename);
				
					sendMessage(filename);
					out.writeObject(content);
					out.flush();

				}
				
				
				
			}
			catch(ClassNotFoundException classnot){
				System.err.println("Data received in unknown format");
			}
			
    	}while(!message.toLowerCase().equals("exit"));
      
		System.out.println("Ending Client : ID - " + clientID + " : Address - "
		        + clientSocket.getInetAddress().getHostName());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
