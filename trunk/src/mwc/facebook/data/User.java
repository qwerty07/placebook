package mwc.facebook.data;

public class User {
	private String user;
	private String name;
	private String pic;
	private Point home;
	
	public User(String user, String name, String pic, Point home){
		this.user = user;
		this.name = name;
		this.pic = pic;
		this.home = home;
	}
	
	public String getUser(){
		return user;
	}
	
	public String getName(){
		return name;
	}
	
	public String getPic() {
		return pic;
	}
	
	public Point getHomePoint() {
		return home;
	}
	
	public void setHomePoint(Point point) {
		this.home = point;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof User)) return false;
		User o = (User) other;
		return o.getUser().equals(user);
	}
	
	public int hashCode() {
		return user.hashCode();
	}
	
	public String toJSON() {
		StringBuffer sb = new StringBuffer();
		sb.append("{ ");
		sb.append("user: " + user + ", ");
		if (home != null) {
			sb.append("home: " + home.toJSON() + ", ");
		}
		if (pic != null) {
			sb.append("pic: \"" + Location.escapeString(pic) + "\", ");
		}
		sb.append("name: \"" + Location.escapeString(name) + "\", ");
		sb.append("}");
		return sb.toString();
	}
}
