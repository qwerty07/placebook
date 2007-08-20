package mwc.facebook.data;

public class Point {
	private float x, y;

	public Point(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public boolean equals(Object o){
		if (o instanceof Point) {
			return ((Point)o).x == x && ((Point)o).y == y;
		}
		return false;
	}
	
	public int hashCode() {
		return new Float(x).byteValue();
	}
}
