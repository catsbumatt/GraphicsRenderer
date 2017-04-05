package Assignment3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Renderer {

	public final static int imageWidth = 800;
	public final static int imageHeight = 800;

	private JFrame frame;
	private BufferedImage image;
	private JComponent drawing;


	private ReaderOfFiles rof;



	public int scale;

	private Renderer() {
		rof = new ReaderOfFiles();
		rof.loadImage();
		setupFrame();
		rof.findHiddenPolygons();
		rof.shade();
		// System.out.println(rof.amountOfHidden());
		// createImage();
		// rof.originalRotation();
		
		
		//this is to get the big boxes working, i don't know why
		rof.scaleVector();
		rof.translatePolygon();
		rof.translatePolygonBack();
		rof.translatePolygon(new Vector3D(imageWidth / 2,
				imageHeight / 2, 0));
		rof.translatePolygon();
		rof.translatePolygonBack();
		rof.translatePolygon(new Vector3D(imageWidth / 2,
				imageHeight / 2, 0));
		rof.translatePolygon();
		rof.translatePolygonBack();
		rof.translatePolygon(new Vector3D(imageWidth / 2,
				imageHeight / 2, 0));
		
//		rof.translatePolygon();
//		rof.translatePolygonBack();
		rof.translatePolygon(new Vector3D(imageWidth / 2,
				imageHeight / 2, 0));
		createZBuffer();
	}

	private void createZBuffer() {
		// rof.findBoundingBox();
		Color[][] zBufferC = new Color[imageWidth][imageHeight];
		float[][] zBufferD = new float[imageWidth][imageHeight];
		for (int i = 0; i < zBufferD.length; i++) {
			for (int j = 0; j < zBufferD[0].length; j++) {
				zBufferC[i][j] = new Color(0, 0, 0);
				zBufferD[i][j] = Float.POSITIVE_INFINITY;
			}
		}

		rof.findHiddenPolygons();
		ArrayList<Polygon> polygons = rof.getPolygons();
		for (Polygon p : polygons) {
			if (!p.isHidden) {

				float[][] edgeLists = p.edgeLists();
				// System.out.println(edgeLists.length);
				for (int y = 0; y < edgeLists.length; y++) {
					int x = (int) edgeLists[y][0];
					int z = (int) edgeLists[y][1];


					float mz = (edgeLists[y][3] - edgeLists[y][1])
							/ (edgeLists[y][2] - edgeLists[y][0]);

					while (x <= (int) edgeLists[y][2]) {

						if (x < 0 || x >= imageHeight) {
							// System.out.println("Dont do this man");
						} else {
							if (z < zBufferD[x][y]) {
								zBufferD[x][y] = z;
								zBufferC[x][y] = p.getColor();
								//zBufferC[x][y] = p.getShade();
							}
						}
						x++;
						z = z + (int) mz;

					}
				}
			}
		}
		// System.out.println(zBufferC[150][150]);
		convertBitmapToImage(zBufferC);
		drawing.repaint();
	}

	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	private void saveImage(String fname) {
		try {
			ImageIO.write(image, "png", new File(fname));
		} catch (IOException e) {
			System.out.println("Image saving failed: " + e);
		}
	}

	private void setupFrame() {
		frame = new JFrame("Renderer");
		frame.setSize(imageWidth + 10, imageHeight + 20);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		frame.add(panel, BorderLayout.NORTH);

		drawing = new JComponent() {
			protected void paintComponent(Graphics g) {
				g.drawImage(image, 0, 0, null);
			}
		};

		frame.add(drawing, BorderLayout.CENTER);
		addButton("Render", panel, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				createZBuffer();

				drawing.repaint();
			}
		});
		addButton("Save", panel, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveImage("savedImage.png");
			}
		});

		addButton("Quit", panel, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.exit(0);
			}
		});
//		addButton("Centre", panel, new ActionListener() {
//			public void actionPerformed(ActionEvent ev) {
////
//					rof.translatePolygon(new Vector3D(imageWidth / 2,
//							imageHeight / 2, 0));
//
//					createZBuffer();
//					centred = true;
////				}
//			}
//		});
//		addButton("scale", panel, new ActionListener() {
//			public void actionPerformed(ActionEvent ev) {
//				if (centred) {
//					System.out.println("already scaled");
//				} else {
//					rof.scaleVector();
//					createZBuffer();
//					scaled = true;
//				}
//			}
//		});
//		addButton("scaleup", panel, new ActionListener() {
//			public void actionPerformed(ActionEvent ev) {
//
//				rof.scaleVector(new Vector3D (1.1f, 1.1f, 1.1f));
//				createZBuffer();
//			}
//		});
//		addButton("scaledown", panel, new ActionListener() {
//			public void actionPerformed(ActionEvent ev) {
//
//				rof.scaleVector(new Vector3D (0.9f, 0.9f, 0.9f));
//				createZBuffer();
//			}
//		});

		InputMap iMap = drawing.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap aMap = drawing.getActionMap();
		iMap.put(KeyStroke.getKeyStroke("LEFT"), "shiftLeft");
		iMap.put(KeyStroke.getKeyStroke("RIGHT"), "shiftRight");
		iMap.put(KeyStroke.getKeyStroke("UP"), "shiftUp");
		iMap.put(KeyStroke.getKeyStroke("DOWN"), "shiftDown");
		aMap.put("shiftLeft", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				rof.translatePolygon();
				rof.rotatePolygonsLR(0.1f);
				rof.translatePolygonBack();
				rof.translatePolygon(new Vector3D(imageWidth / 2,
						imageHeight / 2, 0));
				createZBuffer();

			}
		});
		aMap.put("shiftRight", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				rof.translatePolygon();
				rof.rotatePolygonsLR(-0.1f);
				rof.translatePolygonBack();
				rof.translatePolygon(new Vector3D(imageWidth / 2,
						imageHeight / 2, 0));
				createZBuffer();
			}
		});
		aMap.put("shiftUp", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				rof.translatePolygon();
				rof.rotatePolygonsUD(0.1f);
				rof.translatePolygonBack();
				rof.translatePolygon(new Vector3D(imageWidth / 2,
						imageHeight / 2, 0));
				createZBuffer();
			}
		});
		aMap.put("shiftDown", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				rof.translatePolygon();
				rof.rotatePolygonsUD(-0.1f);
				rof.translatePolygonBack();
				rof.translatePolygon(new Vector3D(imageWidth / 2,
						imageHeight / 2, 0));
				createZBuffer();
			}
		});

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void addButton(String name, JComponent comp, ActionListener listener) {
		JButton button = new JButton(name);
		comp.add(button);
		button.addActionListener(listener);
	}

	public static void main(String[] args) {
		new Renderer();

	}

}
