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
	
	public boolean equals(Object other) {
		if (!(other instanceof User)) return false;
		User o = (User) other;
		return o.getUserName().equals(userName) && o.getHomePoint().equals(home);
	}
}
