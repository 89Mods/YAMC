package theGhastModding.meshingTest.maths;

public class Vector3i {
	
	public int x,y,z;
	
	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int x() {
		return this.x;
	}
	
	public int y() {
		return this.y;
	}
	
	public int z() {
		return this.z;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Vector3i)) return false;
		Vector3i other = (Vector3i)o;
		if(other.x == this.x && other.y == this.y && other.z == this.z) return true;
		return false;
	}
	
	public Vector3i clone() {
		return new Vector3i(this.x, this.y, this.z);
	}
	
	public String toString() {
		return String.format("[\"%d\"],[\"%d\"],[\"%d\"]", this.x, this.y, this.z);
	}
	
	public int hashCode() {
		return x * 1073741824 + y * 536870912 + z * 31;
	}
	
}