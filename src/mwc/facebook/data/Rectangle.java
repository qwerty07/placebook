package mwc.facebook.data;

import org.postgresql.geometric.PGpoint;


public class Rectangle {
	private Point topLeft, bottomRight;

	public Rectangle(Point pt1, Point pt2) {
		super();
		double minX = Math.min(pt1.x, pt2.x);
		double maxX = Math.max(pt1.x, pt2.x);
		double minY = Math.min(pt1.y, pt2.y);
		double maxY = Math.max(pt1.y, pt2.y);
		this.topLeft = new Point(minX, minY);
		this.bottomRight = new Point(maxX, maxY);
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
