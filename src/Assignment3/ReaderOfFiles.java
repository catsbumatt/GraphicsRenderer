package Assignment3;

import java.awt.Color;
import java.awt.Graphics;
import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;

public class ReaderOfFiles {

	public Vector3D light;
	// public Color reflectance;

	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();

	BoundingBox b;

	// public Polygon test;
	public Color ambient = new Color(150, 150, 150);
	public Color directed = Color.white;

	public ReaderOfFiles() {

	}

	public void loadImage() {
		File file = getFile();
		if (file == null) {
			System.out.println("no file selected");
			return;
		}
		BufferedReader data;
		try {
			data = new BufferedReader(new FileReader(file));
			// data.readLine();
			String firstLine = data.readLine();
			// System.out.println("Light: " +firstLine);
			String vals[] = firstLine.split(" ");
			// int count = 2;
			light = new Vector3D(Float.parseFloat(vals[0]),
					Float.parseFloat(vals[1]), Float.parseFloat(vals[2]));

			while (true) {
				String line = data.readLine();

				if (line == null) {
					break;
				}
				// System.out.println("Line " + " = " + line);
				String values[] = line.split(" ");
				Vector3D one = new Vector3D(Float.parseFloat(values[0]),
						Float.parseFloat(values[1]),
						Float.parseFloat(values[2]));
				Vector3D two = new Vector3D(Float.parseFloat(values[3]),
						Float.parseFloat(values[4]),
						Float.parseFloat(values[5]));
				Vector3D three = new Vector3D(Float.parseFloat(values[6]),
						Float.parseFloat(values[7]),
						Float.parseFloat(values[8]));
				int r = Integer.parseInt(values[9]);
				int g = Integer.parseInt(values[10]);
				int b = Integer.parseInt(values[11]);

				polygons.add(new Polygon(one, two, three, r, g, b, light));
				// count++;

			}

		} catch (IOException e) {
			System.out.println("Failed to open monkey.txt: " + e);
		}
		b = findBoundingBox();
	}

	public File getFile() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null); // Where frame is the parent
													// component

		File file = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			// Now you have your file to do whatever you want to do
		} else {
			// User did not choose a valid file
		}
		return file;
	}

	public void findHiddenPolygons() {
		for (Polygon p : polygons) {
			p.findHidden();
		}
	}

	public int amountOfHidden() {
		int count = 0;
		for (Polygon p : polygons) {
			if (p.isHidden) {
				count++;
			}
		}
		return count;
	}

	public ArrayList<Polygon> getPolygons() {
		return polygons;
	}

	/**
	 * rotate the polygons left or right given an angle
	 * 
	 * @param angle
	 *            a floating number
	 */
	public void rotatePolygonsLR(float angle) {
		// BoundingBox b = findBoundingBox();
		// Vector3D translateVector = b.centre();
		// Transform translate = Transform.newTranslation(translateVector);
		// translatePolygon(translateVector);
		// translatePolygon();
		Transform t = Transform.newYRotation(angle);
		for (Polygon p : polygons) {
			p.setVector1(t.multiply(p.getVector1()));
			p.setVector2(t.multiply(p.getVector2()));
			p.setVector3(t.multiply(p.getVector3()));
			p.setLight(t.multiply(p.getLight()));
		}
		// origin = t.multiply(origin);
		// translatePolygonBack();
	}

	/**
	 * rotate the polygons up or down given an angle
	 * 
	 * @param angle
	 *            a floating number
	 */
	public void rotatePolygonsUD(float angle) {

		Transform t = Transform.newXRotation(angle);
		for (Polygon p : polygons) {
			p.setVector1(t.multiply(p.getVector1()));
			p.setVector2(t.multiply(p.getVector2()));
			p.setVector3(t.multiply(p.getVector3()));
			p.setLight(t.multiply(p.getLight()));
		}

	}

	public void rotateZAxis(float angle) {
		Transform t = Transform.newZRotation(angle);
		for (Polygon p : polygons) {
			p.setVector1(t.multiply(p.getVector1()));
			p.setVector2(t.multiply(p.getVector2()));
			p.setVector3(t.multiply(p.getVector3()));
		}
	}

	/**
	 * translate the polygon so the centre of the polygon is at the origin
	 */
	public void translatePolygon() {
		// BoundingBox b = findBoundingBox();
		// checkBounds();
		scaleVector();
		Vector3D translateVector = b.centre();
		// System.out.println(b.toString());
		// System.out.println("Centre: " + b.centre());//
		// System.out.println(translateVector.toString());
		Transform t = Transform.newTranslation(-translateVector.x,
				-translateVector.y, -translateVector.z);
		for (Polygon p : polygons) {
			p.setVector1(t.multiply(p.getVector1()));
			p.setVector2(t.multiply(p.getVector2()));
			p.setVector3(t.multiply(p.getVector3()));
		}
	}

	/**
	 * translates the polygon that is centred at the origin back to its original
	 */
	public void translatePolygonBack() {
		// BoundingBox b = findBoundingBox();
		Vector3D translateVector = b.centre();
		// System.out.println(b.toString());
		// System.out.println("Centre: " + b.centre());
		// System.out.println(translateVector.toString());
		Transform t = Transform.newTranslation(translateVector.x,
				translateVector.y, translateVector.z);
		for (Polygon p : polygons) {
			p.setVector1(t.multiply(p.getVector1()));
			p.setVector2(t.multiply(p.getVector2()));
			p.setVector3(t.multiply(p.getVector3()));
		}
	}

	public void translatePolygon(Vector3D v) {
		// Vector3D v = new Vector3D(length,length,length);
		Vector3D centre = b.centre();

		Vector3D translateVector = new Vector3D(v.x - centre.x, v.y - centre.y,
				0);
		Transform t = Transform.newTranslation(translateVector);
		for (Polygon p : polygons) {
			p.setVector1(t.multiply(p.getVector1()));
			p.setVector2(t.multiply(p.getVector2()));
			p.setVector3(t.multiply(p.getVector3()));
		}
		b = findBoundingBox();
	}

	public void scaleVector() {
		// Transform t = Transform.newScale(new
		// Vector3D(Renderer.imageHeight/b.maxX - 0.1f,
		// Renderer.imageWidth/b.maxY - 0.1f ,Renderer.imageHeight/b.maxX -
		// 0.1f));
		forcedTranslatePolygon();
		float min = Math.min(Renderer.imageHeight / b.maxX, Renderer.imageWidth
				/ b.maxY);
		Transform t = Transform.newScale(new Vector3D(min - 0.1f, min - 0.1f,
				min - 0.1f));

		for (Polygon p : polygons) {
			p.setVector1(t.multiply(p.getVector1()));
			p.setVector2(t.multiply(p.getVector2()));
			p.setVector3(t.multiply(p.getVector3()));
		}
		translatePolygonBack();
		b = findBoundingBox();
		// return v;

	}

	public void scaleVector(Vector3D v) {
		forcedTranslatePolygon();
		Transform t = Transform.newScale(v);
		for (Polygon p : polygons) {
			p.setVector1(t.multiply(p.getVector1()));
			p.setVector2(t.multiply(p.getVector2()));
			p.setVector3(t.multiply(p.getVector3()));
		}
		translatePolygonBack();

	}

	public void forcedTranslatePolygon() {
		Vector3D translateVector = b.centre();

		Transform t = Transform.newTranslation(-translateVector.x,
				-translateVector.y, -translateVector.z);
		for (Polygon p : polygons) {
			p.setVector1(t.multiply(p.getVector1()));
			p.setVector2(t.multiply(p.getVector2()));
			p.setVector3(t.multiply(p.getVector3()));
		}
	}

	public void checkBounds() {
		for (Polygon p : polygons) {
			while (p.getVector1().x < 0
					|| p.getVector1().x > Renderer.imageHeight
					|| p.getVector1().y < 0
					|| p.getVector1().y > Renderer.imageHeight) {
				scaleVector(new Vector3D(0.9f, 0.9f, 0.9f));

			}
		}
	}

	public void shade() {
		for (Polygon p : polygons) {
			if (!p.isHidden) {
				p.getshade(ambient, directed, light);
			}
		}
	}

	public BoundingBox findBoundingBox() {
		float minx = Float.MAX_VALUE;
		float maxx = Float.MIN_VALUE;
		float miny = Float.MAX_VALUE;
		float maxy = Float.MIN_VALUE;
		float minz = Float.MAX_VALUE;
		float maxz = Float.MIN_NORMAL;
		for (Polygon p : polygons) {
			if (p.getVector1().x < minx) {
				minx = p.getVector1().x;
			}
			if (p.getVector1().y < miny) {
				miny = p.getVector1().y;
			}
			if (p.getVector1().z < minz) {
				minz = p.getVector1().z;
			}
			if (p.getVector2().x < minx) {
				minx = p.getVector2().x;
			}
			if (p.getVector2().y < miny) {
				miny = p.getVector2().y;
			}
			if (p.getVector2().z < minz) {
				minz = p.getVector2().z;
			}
			if (p.getVector3().x < minx) {
				minx = p.getVector3().x;
			}
			if (p.getVector3().y < miny) {
				miny = p.getVector3().y;
			}
			if (p.getVector3().z < minz) {
				minz = p.getVector3().z;
			}
			if (p.getVector1().x > maxx) {
				maxx = p.getVector1().x;
			}
			if (p.getVector1().y > maxy) {
				maxy = p.getVector1().y;
			}
			if (p.getVector1().z > maxz) {
				maxz = p.getVector1().z;
			}
			if (p.getVector2().x > maxx) {
				maxx = p.getVector2().x;
			}
			if (p.getVector2().y > maxy) {
				maxy = p.getVector2().y;
			}
			if (p.getVector2().z > maxz) {
				maxz = p.getVector2().z;
			}
			if (p.getVector3().x > maxx) {
				maxx = p.getVector3().x;
			}
			if (p.getVector3().y > maxy) {
				maxy = p.getVector3().y;
			}
			if (p.getVector3().z > maxz) {
				maxz = p.getVector3().z;
			}
		}
		BoundingBox b = new BoundingBox(minx, maxx, miny, maxy, minz, maxz);

		return b;
	}

}
