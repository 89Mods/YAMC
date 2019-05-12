package theGhastModding.meshingTest.phys;

public class AABB {
	
	public float x0,y0,z0;
	public float x1,y1,z1;
	
	public AABB(float x0, float y0, float z0, float x1, float y1, float z1) {
		super();
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
	}
	
	public AABB moveClone(float xm, float ym, float zm) {
		return new AABB(x0 + xm, y0 + ym, z0 + zm, x1 + xm, y1 + ym, z1 + zm);
	}
	
	public boolean isColliding(AABB other) {
		if(other == null) return false;
		//return !((x0 <= other.x1 && x1 >= other.x0) && (y0 <= other.y1 && y1 >= other.y0) && (z0 <= other.z1 && z1 >= other.z0));
	    if ((other.x1 <= this.x0) || (other.x0 >= this.x1)) return false;
	    if ((other.y1 <= this.y0) || (other.y0 >= this.y1)) return false;
	    return (other.z1 > this.z0) && (other.z0 < this.z1);
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof AABB)) return false;
		AABB other = (AABB)o;
		if(other == this) return true;
		if(other.x0 == this.x0 && other.y0 == this.y0 && other.z0 == this.z0 && other.x1 == this.x1 && other.y1 == this.y1 && other.z1 == this.z1) return true;
		return false;
	}
	
	public AABB clone() {
		return new AABB(this.x0, this.y0, this.z0, this.x1, this.y1, this.z1);
	}
	
}