/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class ChipsetImporter{
	public static final int TILE_SIZE = 96;
	private final JLabel tilePreview;
	private BufferedImage left;
	private BufferedImage right;
	private BufferedImage top;
	private BufferedImage finalImage;
	public ChipsetImporter(ChipsetList chipsetList, File file){
		JFrame frame = new JFrame();
		frame.setTitle("Import Tile");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		{
			frame.setLayout(new BorderLayout());
			{
				JPanel panel = new JPanel();
				panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
				JButton okButton = new JButton("Ok");
				okButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						frame.dispose();
						Tile tile = new Tile(Algorithms.randomUUID(), finalImage);
						chipsetList.addTile(tile);
					}
				});
				panel.add(okButton);
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						frame.dispose();
					}
				});
				panel.add(cancelButton);
				frame.add(panel, BorderLayout.SOUTH);
			}
			{
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				tilePreview = new JLabel();
				{
					// Load inital tile textures.
					try{
						top = ImageIO.read(file);
						left = top;
						right = top;
					}catch(Exception exception){
						exception.printStackTrace();
					}
				}
				updatePreview();
				panel.add(tilePreview, BorderLayout.CENTER);
				frame.add(panel, BorderLayout.WEST);
			}
			{
				JPanel panel = new JPanel();
				panel.setLayout(new VerticalFlowLayout(0, 5));
				JButton chooseNewTopButton = new JButton("Load New Top");
				JButton chooseNewLeftButton = new JButton("Choose New Left");
				JButton chooseNewRightButton = new JButton("Choose New Right");
				chooseNewTopButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						File file = Algorithms.userChooseImage("Upload Texture", "Open");
						if(file==null){
							return;
						}
						try{
							top = ImageIO.read(file);
							updatePreview();
						}catch(Exception exception){
							exception.printStackTrace();
						}
					}
				});
				chooseNewLeftButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						File file = Algorithms.userChooseImage("Upload Texture", "Open");
						if(file==null){
							return;
						}
						try{
							left = ImageIO.read(file);
							updatePreview();
						}catch(Exception exception){
							exception.printStackTrace();
						}
					}
				});
				chooseNewRightButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						File file = Algorithms.userChooseImage("Upload Texture", "Open");
						if(file==null){
							return;
						}
						try{
							right = ImageIO.read(file);
							updatePreview();
						}catch(Exception exception){
							exception.printStackTrace();
						}
					}
				});
				panel.add(chooseNewTopButton);
				panel.add(chooseNewLeftButton);
				panel.add(chooseNewRightButton);
				frame.add(panel, BorderLayout.CENTER);
			}
		}
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	private void updatePreview(){
		finalImage = generateCube(left, right, top);
		tilePreview.setIcon(new ImageIcon(finalImage));
	}
	private static BufferedImage generateCube(BufferedImage left, BufferedImage right, BufferedImage top){
		BufferedImage topPanel = rotate(top);
		BufferedImage leftPanel = skew(left, 0, 0.5);
		BufferedImage rightPanel = skew(right, 0, -0.5);
		darken(leftPanel, 0.9f);
		darken(rightPanel, 0.75f);
		return shrink(blur(combine(leftPanel, rightPanel, topPanel)));
	}
	private static BufferedImage shrink(BufferedImage image){
		BufferedImage buf = image;
		int s = image.getWidth();
		do{
			if(s>TILE_SIZE){
				s /= 2;
				if(s<TILE_SIZE){
					s = TILE_SIZE;
				}
			}else{
				s *= 2;
				if(s>TILE_SIZE){
					s = TILE_SIZE;
				}
			}
			BufferedImage out = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = out.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(buf, 0, 0, s, s, null);
			g.dispose();
			buf = out;
		}while(s!=TILE_SIZE); // This loop enhances quality, while resizing!
		return buf;
	}
	private static BufferedImage combine(BufferedImage left, BufferedImage right, BufferedImage top){
		double size = top.getWidth()/2.0/left.getWidth();
		int h1 = top.getHeight()/2+left.getHeight();
		BufferedImage out = new BufferedImage(top.getWidth(), (int)(top.getHeight()/2.0+left.getHeight()*size), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = out.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(top, 0, 0, null);
		g.setTransform(AffineTransform.getScaleInstance(size, size));
		g.drawImage(left, 0, (int)((h1-left.getHeight())/size), null);
		g.drawImage(right, left.getWidth(), (int)((h1-right.getHeight())/size), null);
		g.dispose();
		return out;
	}
	private static BufferedImage blur(BufferedImage image){
		final int blurAmount = 5;
		float[] elements = new float[blurAmount*blurAmount];
		float seg = 1f/elements.length;
		for(int i = 0; i<elements.length; i++){
			elements[i] = seg;
		}
		Kernel kernel = new Kernel(blurAmount, blurAmount, elements);
		ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		return cop.filter(image, null);
	}
	private static BufferedImage skew(BufferedImage input, double angle, double angle2){
		double x = (angle<0)?-angle*input.getHeight():0;
		double y = (angle2<0)?-angle2*input.getWidth():0;
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		at.shear(angle, angle2);
		AffineTransformOp op =
			new AffineTransformOp(at, new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
		return op.filter(input, null);
	}
	private static BufferedImage rotate(BufferedImage top){
		double angle = Math.toRadians(45);
		double sin = Math.abs(Math.sin(angle));
		double cos = Math.abs(Math.cos(angle));
		int w = top.getWidth();
		int h = top.getHeight();
		int newWidth = (int)Math.floor(w*cos+h*sin);
		int newHeight = (int)Math.floor(h*cos+w*sin);
		AffineTransform at = AffineTransform.getTranslateInstance((newWidth-w)/2.0, (newHeight-h)/2.0);
		at.concatenate(AffineTransform.getRotateInstance(angle, w/2.0, h/2.0));
		at.preConcatenate(AffineTransform.getScaleInstance(1, 0.5));
		AffineTransformOp op =
			new AffineTransformOp(at, new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
		return op.filter(top, null);
	}
	private static void darken(BufferedImage image, float percent){
		int[] rgb = new int[image.getWidth()*image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());
		float r, g, b, a;
		Color color;
		for(int i = 0; i<rgb.length; i++){
			color = new Color(rgb[i], true);
			r = color.getRed()/255f*percent;
			g = color.getGreen()/255f*percent;
			b = color.getBlue()/255f*percent;
			a = color.getAlpha()/255f;
			rgb[i] = new Color(r, g, b, a).getRGB();
		}
		image.setRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());
	}
}
