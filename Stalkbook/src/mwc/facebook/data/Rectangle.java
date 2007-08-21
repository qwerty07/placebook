package mwc.facebook.data;

import org.postgresql.geometric.PGpoint;


public class Rectangle {
	private Point topLeft, bottomRight;

	public Rectangle(Point topLeft, Point bottomRight) {
		super();
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}
	
	public boolean contains(Point p) {
		return (p.x >= topLeft.x && p.x < bottomRight.x && p.y >= topLeft.y && p.y < bottomRight.y);
	}

	public PGpoint getTopLeft() {
		return topLeft;
	}

	public PGpoint getBottomRight() {
		return bottomRight;
	}
}
