package ebot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JComponent;
import javax.swing.JFrame;

import ebot.ebf.EBFGroup;
import ebot.ebf.EBFIO;
import ebot.ebf.EBFTree;
import ebot.text.TextComparer;

public class AnimatedBot extends EBot {
	private Color c;

	public AnimatedBot(EBFTree tree, TextComparer compare, Color c) {
		super(tree, compare);
		this.c = c;

		JFrame f = new JFrame();
		f.setSize(480, 360);

		f.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});

		f.add(new JComponent() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 3890995746851065260L;

			private BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
			private BufferedImage img2 = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);

			public Kernel makeKernel(float radius) {
				int r = (int)Math.ceil(radius);
				int rows = r*2+1;
				float[] matrix = new float[rows];
				float sigma = radius/3;
				float sigma22 = 2*sigma*sigma;
				float sigmaPi2 = (float) (2*Math.PI*sigma);
				float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
				float radius2 = radius*radius;
				float total = 0;
				int index = 0;
				for (int row = -r; row <= r; row++) {
					float distance = row*row;
					if (distance > radius2)
						matrix[index] = 0;
					else
						matrix[index] = (float)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
					total += matrix[index];
					index++;
				}
				for (int i = 0; i < rows; i++) {
					matrix[i] /= total;
					matrix[i] *= 2;
				}

				return new Kernel(rows, 1, matrix);
			}
			
			public Kernel makeKernel2(float radius) {
				int r = (int)Math.ceil(radius);
				int rows = r*2+1;
				float[] matrix = new float[rows];
				float sigma = radius/3;
				float sigma22 = 2*sigma*sigma;
				float sigmaPi2 = (float) (2*Math.PI*sigma);
				float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
				float radius2 = radius*radius;
				float total = 0;
				int index = 0;
				for (int row = -r; row <= r; row++) {
					float distance = row*row;
					if (distance > radius2)
						matrix[index] = 0;
					else
						matrix[index] = (float)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
					total += matrix[index];
					index++;
				}
				for (int i = 0; i < rows; i++) {
					matrix[i] /= total;
					matrix[i] *= 2;
				}

				return new Kernel(1, rows, matrix);
			}
			
			@Override
			public void paint(Graphics g) {
				if (img.getWidth() != getWidth() || img.getHeight() != getHeight()) {
					img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
					img2 = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
				}
				Graphics2D g2 = img.createGraphics();

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

				g2.setColor(Color.black);
				g2.fillRect(0, 0, getWidth(), getHeight());

				g2.translate(getWidth() / 2, getHeight() / 2);

				g2.setColor(AnimatedBot.this.c);
				g2.drawLine(0, 0, 0, 0);

				drawPoints(g2, tree.root, 1, 0, 0);

				float radius = 2.5f;
				
				BufferedImageOp blur = new ConvolveOp(makeKernel(radius), ConvolveOp.EDGE_NO_OP, g2.getRenderingHints());
				
				blur.filter(img, img2);
				
				blur = new ConvolveOp(makeKernel2(radius), ConvolveOp.EDGE_NO_OP, g2.getRenderingHints());
				
				blur.filter(img2, img);

				g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			}

			public void drawPoints(Graphics2D g2, EBFGroup g, int level, int ox, int oy) {
				for (EBFIO io : g.getIOs()) {
					float u = io.getLastUpdate() / 2 / 10 * level;
					if (u < 1) {
						u = (float) (2 - (2 / (Math.sqrt(u) + 1)));
					} else {
						u = 1;
					}

					double angle = (io.hashCode() / 100.0 + System.currentTimeMillis() / 1000.0 / u) % (Math.PI * 2);

					g2.rotate(angle);

					double cycles = 1f;
					double comp = 35 * (Math.sin(cycles * angle + System.currentTimeMillis() / 1000.0 % (Math.PI * 2))
							+ Math.sin(2 * (cycles * angle + System.currentTimeMillis() / 1000.0 % (Math.PI * 2)))
									* Math.sin(System.currentTimeMillis() / 1000.0 % (Math.PI * 2)))
							* u;
					int dist = (int) (getHeight() / 3 * u + comp);

					g2.drawLine(0, dist, 0, dist);

					g2.rotate(-angle);

					io.update(1f / 30);
				}

				for (EBFGroup sg : g.getSubGroups()) {
					float u = 0.2f * level;

					double angle = (g.hashCode() / 100.0 + System.currentTimeMillis() / 1000.0 / u) % (Math.PI * 2);

					g2.rotate(angle);

					double cycles = 1;
					double comp = 35 * (Math.sin(cycles * angle + System.currentTimeMillis() / 1000.0 % (Math.PI * 2))
							+ Math.sin(2 * (cycles * angle + System.currentTimeMillis() / 1000.0 % (Math.PI * 2)))
									* Math.sin(System.currentTimeMillis() / 1000.0 % (Math.PI * 2)))
							* u;
					int dist = (int) (getHeight() / 3 * u + comp);

					g2.drawLine(0, dist, 0, dist);

					g2.rotate(-angle);

					drawPoints(g2, sg, level + sg.getName().split("_").length,
							(int) (0 * Math.cos(angle) - dist * Math.sin(angle)),
							(int) (0 * Math.sin(angle) + dist * Math.cos(angle)));
				}
			}
		});

		f.setVisible(true);

		new Thread() {
			public void run() {
				while (true) {
					f.getComponent(0).repaint();
					try {
						Thread.sleep(1000 / 30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

}
