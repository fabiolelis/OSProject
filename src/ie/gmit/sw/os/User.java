package ie.gmit.sw.os;

public class User {
	private String name;
	private String password;
	private String directory;
	private String currentDir;
  
	
	
	public User() {
		super();
	}
	public User(String name, String password, String directory) {
		super();
		this.name = name;
		this.password = password;
		this.directory = directory;
		this.currentDir = directory;
	}
	
	
	public boolean isAtRoot()
	{
		return this.currentDir.equals(this.directory);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public String toString(){
		return "USER {name: " + this.name + ", password: " + this.password + ", root: " + this.directory + "}";
	}
	public String getCurrentDir() {
		return currentDir;
	}
	public void setCurrentDir(String currentDir) {
		this.currentDir = currentDir;
	}
	  
  
}
