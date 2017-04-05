package Assignment3;

public class BoundingBox {

	public float minX;
	public float maxX;
	public float minY;
	public float maxY;
	public float minZ;
	public float maxZ;


	public BoundingBox(float minx, float maxx, float miny, float maxy, float minz, float maxz){
		minX = minx;
		maxX = maxx;
		minY = miny;
		maxY = maxy;
		minZ = minz;
		maxZ = maxz;
	}

	/**
	 * returns a new vector half the size of the bounding box also with
	 * a z value of 0
	 * @return
	 */
	public Vector3D centre(){
		return new Vector3D((minX + maxX)/2, (minY + maxY)/2, (minZ + maxZ)/2);
	}

	public String toString(){
		return ("minX: " + minX + " maxX: " + maxX + " minY: " + minY + " maxY: " + maxY + " minZ: " + minZ + " maxZ: " + maxZ);
	}
}
