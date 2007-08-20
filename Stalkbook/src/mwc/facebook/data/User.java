package mwc.facebook.data;

public class User {
	private String userName;
	private Point home;
	
	public User(String username, Point home){
		this.userName = username;
		this.home = home;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public Point getHomePoint() {
		return home;
	}
}
