package ie.gmit.sw.os;

import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Requester{
	Socket requestSocket;
	ObjectOutputStream out;
 	ObjectInputStream in;
 	String message="";
 	String ipaddress;
 	Scanner stdin;
	Requester(){}
	void run()
	{
		stdin = new Scanner(System.in);
		try{
			//1. creating a socket to connect to the server
			//System.out.println("Please Enter your IP Address");
			//ipaddress = stdin.next();
			ipaddress = "40.117.90.232";
			requestSocket = new Socket(ipaddress, EchoServer.PORT);
			System.out.println("Connected to "+ipaddress+" in port " + EchoServer.PORT);
			//2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			//3: Communicating with the server
			do{
				try
				{
					message = (String)in.readObject();
					System.out.println("Requester: " + message);
					message = stdin.nextLine();
					if(message.contains("put ")){
						String origin = message.split(" ")[1];
						sendMessage(message);
						byte[] obj = CmdExecutor.sendFile(origin);
						if(obj == null){
							System.out.println("Invalid path or not allowed area\nEnter \"OK\" to try again or \"exit\" to finish");
							message = stdin.nextLine();
						}
						out.writeObject(obj);
						out.flush();
					}
					else if(message.contains("get ")){
						sendMessage(message);

						String filename = (String)in.readObject();
						byte[] content = (byte[])in.readObject();
						if(content == null){
							System.out.println("Invalid path or not allowed area\nEnter \"OK\" to try again or \"exit\" to finish");
							message = stdin.nextLine();
						}
							
						CmdExecutor.saveFileClient(content, filename);

					}
					else
					{
						sendMessage(message);
					}
					
						
				}
				catch(ClassNotFoundException classNot)
				{
					System.err.println("data received in unknown format");
				}
			}while(!message.toLowerCase().equals("exit"));
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			//System.out.println("client>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		Requester client = new Requester();
		client.run();
	}
}