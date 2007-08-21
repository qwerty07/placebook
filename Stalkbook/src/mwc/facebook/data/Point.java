package mwc.facebook.data;

import org.postgresql.geometric.PGpoint;

public class Point extends PGpoint{

	private static final long serialVersionUID = 1L;

	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public int hashCode() {
		return new Double(Math.log(2) * x + Math.log(3) * y).hashCode();
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Point)) return false;
		Point o = (Point) other;
		return Math.abs(x - o.x) < (x / 100000000) && Math.abs(y - o.y) < (y / 100000000);
	}
}
