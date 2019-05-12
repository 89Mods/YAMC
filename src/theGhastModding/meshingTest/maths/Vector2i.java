package theGhastModding.meshingTest.maths;

public class Vector2i {
	
	public int x,y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int x() {
		return this.x;
	}
	
	public int y() {
		return this.y;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Vector2i)) return false;
		Vector2i other = (Vector2i)o;
		if(other.x == this.x && other.y == this.y) return true;
		return false;
	}
	
	public Vector2i clone() {
		return new Vector2i(this.x, this.y);
	}
	
	public String toString() {
		return String.format("[\"%d\"],[\"%d\"]", this.x, this.y);
	}
	
	public int hashCode() {
		return x * 1073741824 + y;
	}
	
}