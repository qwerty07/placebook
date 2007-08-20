package mwc.facebook.data;

public class User {
	private String userName;
	
	public User(String username){
		this.userName = username;
	}
	
	public String getUserName(){
		return userName;
	}
}
