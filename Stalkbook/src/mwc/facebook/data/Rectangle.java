package mwc.facebook.data;


public class Rectangle {
	private Point topLeft, bottomRight;

	public Rectangle(Point topLeft, Point bottomRight) {
		super();
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}
	
	public boolean contains(Point p) {
		return (p.getX() >= topLeft.getX() && p.getX() < bottomRight.getX() && p.getY() >= topLeft.getY() && p.getY() < bottomRight.getY());
	}
}
