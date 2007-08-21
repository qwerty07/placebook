package mwc.facebook.data;

import org.postgresql.geometric.PGpoint;
import org.postgresql.util.MD5Digest;

public class Point extends PGpoint{
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
		return x == o.x && y == o.y;
	}
}
