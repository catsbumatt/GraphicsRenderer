package Assignment3;

import java.awt.Color;

public class Polygon {

	private Vector3D vector1;
	private Vector3D vector2;
	private Vector3D vector3;

	// public Color reflectance;
	public float[] reflectance = new float[] { 0.95f, 0.9f, 0.85f };
	public Vector3D light;
	public boolean isHidden;

	private Color color;

	private float[][] edgeLists;

	public Polygon(Vector3D v1, Vector3D v2, Vector3D v3, int r, int g, int b,
			Vector3D lightDir) {
		setVector1(v1);
		setVector2(v2);
		setVector3(v3);
		color = new Color(r, g, b);
		// reflectance = new float[];
		light = lightDir;
	}

	public float[][] edgeLists() {
		float minY = Math.min(vector1.y, vector2.y);
		float maxY = Math.max(vector1.y, vector2.y);
		minY = Math.min(minY, vector3.y);
		maxY = Math.max(maxY, vector3.y);
		// float diff = maxY - minY;
		// truncation
		// edgeLists = new float[(int) diff][4];
		edgeLists = new float[Renderer.imageHeight][4];
		for (int i = 0; i < edgeLists.length; i++) {
			edgeLists[i][0] = Float.POSITIVE_INFINITY;
			edgeLists[i][1] = Float.POSITIVE_INFINITY;
			edgeLists[i][2] = Float.NEGATIVE_INFINITY;
			edgeLists[i][3] = Float.POSITIVE_INFINITY;

		}

		fillEdge(vector1, vector2);
		fillEdge(vector2, vector3);
		fillEdge(vector3, vector1);

		return edgeLists;
	}

	public void fillEdge(Vector3D v1, Vector3D v2) {
		// vector with lowest y
		Vector3D a;
		Vector3D b;
		if (v1.y < v2.y) {
			a = v1;
			b = v2;
		} else {
			a = v2;
			b = v1;
		}
		float mx = (b.x - a.x) / (b.y - a.y);
		float mz = (b.z - a.z) / (b.y - a.y);

		float x = a.x;
		float z = a.z;
		// truncating
		int i = (int) a.y;
		int maxi = (int) b.y;
		// rounding
		// int i = Math.round(a.y);
		// int maxi = Math.round(b.y);
		if (i < 0 || i >= Renderer.imageHeight || x < 0
				|| x >= Renderer.imageHeight || maxi < 0
				|| maxi >= Renderer.imageHeight) {
		}
		// else if (edgeLists[i][0] < 0 || edgeLists[i][0] >
		// Renderer.imageWidth){
		//
		// }
		else {
			while (i < maxi) {
				// left
				// System.out.println(edgeLists[i][0]);
				if (x < edgeLists[i][0]) {

					edgeLists[i][0] = x;
					edgeLists[i][1] = z;
				}
				// right
				if (x > edgeLists[i][2]) {
					edgeLists[i][2] = x;
					edgeLists[i][3] = z;
				}
				i++;
				x = x + mx;
				z = z + mz;
			}
			// edgeLists[maxi][0] = b.x;
			// edgeLists[maxi][1] = b.z;
			// edgeLists[maxi][2] = b.x;
			// edgeLists[maxi][3] = b.z;
			// edgeLists[Math.round(b.y)][0] = b.x;
			// edgeLists[Math.round(b.y)][1] = b.z;
			// edgeLists[Math.round(b.y)][2] = b.x;
			// edgeLists[Math.round(b.y)][3] = b.z;

		}
	}

	public Color getColor() {
		return color;
	}

	public void findHidden() {
		// Vector3D a = vector1.crossProduct(vector2);
		// Vector3D b = vector2.crossProduct(vector3);

		if ((vector2.x - vector1.x) * (vector3.y - vector2.y) > (vector2.y - vector1.y)
				* (vector3.x - vector2.x)) {
			isHidden = true;
		} else {
			isHidden = false;
		}
	}

	public void getshade(Color ambient, Color specular, Vector3D direction) {
		Vector3D norm = polynorm();
		float angle = (norm.cosTheta(direction));
		float r;
		float g;
		float b;
		if (angle < 0) {
			angle = Math.abs(angle);
			r = ((float) this.color.getRed())
					/ 255
					* (((float) ambient.getRed()) / 255 + ((float) specular
							.getRed()) / 255 * angle);
			g = ((float) this.color.getGreen())
					/ 255
					* (((float) ambient.getGreen()) / 255 + ((float) specular
							.getGreen()) / 255 * angle);
			b = ((float) this.color.getBlue())
					/ 255
					* (((float) ambient.getBlue()) / 255 + ((float) specular
							.getBlue()) / 255 * angle);

		} else {
			r = ((float) this.color.getRed()) / 255
					* (((float) ambient.getRed()) / 255);
			g = ((float) this.color.getGreen()) / 255
					* (((float) ambient.getGreen() / 255));
			b = ((float) this.color.getBlue()) / 255
					* (((float) ambient.getBlue()) / 255);
		}

		if (r > 1) {
			r = 1;
		}
		if (r < 0) {
			r = 0;
		}
		if (g > 1) {
			g = 1;
		}
		if (g < 0) {
			g = 0;
		}
		if (b > 1) {
			b = 1;
		}
		if (b < 0) {
			b = 0;
		}

		color = new Color(r, g, b);
		// return new Color (r,g,b);

	}

	public Vector3D polynorm() {
		Vector3D b = vector3.minus(vector2);
		Vector3D a = vector2.minus(vector1);
		Vector3D norm = b.crossProduct(a);
		return norm;
	}

	// public Color getShade() {
		// Vector3D lightDir = light.unitVector();
		// // v1-v2*v2-v3
		// Vector3D surfaceNormal = vector2.crossProduct(vector1);
		// surfaceNormal = surfaceNormal.unitVector();
		// // System.out.println("light: " + lightDir.toString());
		// // System.out.println("surfnorm: " + surfaceNormal.toString());
		// float costh = lightDir.cosTheta(surfaceNormal);
		// if (costh < 0) {
		// costh = 0f;
		// }
		// // Color ambient = new Color(0.2f, 0.4f, 0.5f);
		// // Color intensity = new Color(0.5f, 0.25f, 0.25f);
		// float[] ambient = new float[] { 0.2f, 0.4f, 0.5f };
		// float[] specular = new float[] { 0.5f, 0.5f, 0.5f };
		// // float[] specular = new float[]{light.x, light.y, light.z};
		// // float[] ambient = new float[]{0.1f, 0.1f, 0.1f};
		// // float[] intensity = new float[]{0.8f, 0.8f, 0.8f};
		// float red = (ambient[0] + specular[0] * costh) * reflectance[0];
		// float green = (ambient[1] + specular[1] * costh) * reflectance[1];
		// float blue = (ambient[2] + specular[2] * costh) * reflectance[2];
		// // System.out.println("r: " + red + "  g: " + green + "  b:  " + blue +
		// // " costh: " + costh);
		// // float red = (ambient.getRed() + intensity.getRed() *costh) * light.x;
		// // float green = (ambient.getGreen() + intensity.getGreen() * costh) *
		// // light.y;
		// // float blue = (ambient.getRed() + intensity.getRed() * costh) *
		// // light.z;
		//
		// int r = Math.round(red * color.getRed());
		// int g = Math.round(green * color.getGreen());
		// int b = Math.round(blue * color.getBlue());
		//
		// // float r = red * color.getRed();
		// // float g = green * color.getGreen();
		// // float b = blue * color.getBlue();
		//
		// Color col = new Color(r, g, b);
		//
		// // System.out.println(col.getRed() + "  " + col.getGreen() + "  " +
		// // col.getBlue());
		// return col;
		// }

	public Vector3D getVector1() {
		return vector1;
	}

	public void setVector1(Vector3D vector1) {
		this.vector1 = vector1;
	}

	public Vector3D getVector2() {
		return vector2;
	}

	public void setVector2(Vector3D vector2) {
		this.vector2 = vector2;
	}

	public Vector3D getVector3() {
		return vector3;
	}

	public void setVector3(Vector3D vector3) {
		this.vector3 = vector3;
	}

	public Vector3D getLight() {
		return light;
	}

	public void setLight(Vector3D light) {
		this.light = light;
	}

	public float[] getReflectance() {
		return reflectance;
	}

	public void setReflectance(float[] l) {
		reflectance = l;
	}

}
