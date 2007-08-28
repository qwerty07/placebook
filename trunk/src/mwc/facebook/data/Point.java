package mwc.facebook.data;

import java.math.BigDecimal;
import java.math.MathContext;

import mwc.facebook.JSONable;
import org.postgresql.geometric.PGpoint;

public class Point extends PGpoint implements JSONable{

	@Override
	public String getValue()
	{
        return "(" + this.x + "," + this.y + ")";
	}

	private static final long serialVersionUID = 1L;
	
	public final double x;
	public final double y;

	public Point(double x, double y) {
		super();
		this.x = BigDecimal.valueOf(x).round(new MathContext(15)).doubleValue();
		this.y = BigDecimal.valueOf(y).round(new MathContext(15)).doubleValue();
	}
	
	public int hashCode() {
		return new Double(Math.log(2) * x + Math.log(3) * y).hashCode();
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Point)) return false;
		Point o = (Point) other;
		return  this.x == o.x && this.y == y;  //Math.abs(x - o.x) < (Math.abs(x) / 100000000) && Math.abs(y - o.y) < (Math.abs(y) / 100000000);
	}

	public String toJSON() {
		return "{x:" + this.x + ", y:" + this.y +"}";
	}

	public double distanceTo(Point o) {
		return Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2));
	}
}
