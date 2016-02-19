/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.util.Algorithms;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class EntityImporter extends JFrame{
	private static boolean isEmptyImage(BufferedImage img){
		int[] rgb = new int[img.getWidth()*img.getHeight()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgb, 0, img.getWidth());
		for(int i : rgb){
			if(((i>>24)&0xFF)>0){
				return false;
			}
		}
		return true;
	}
	private final BufferedImage image;
	private final EntityImporterImagePainter painter;
	private final ChipsetList chipsetList;
	private boolean isBelow;
	public EntityImporter(File file, ChipsetList chipsetList) throws Exception{
		this.chipsetList = chipsetList;
		BufferedImage fileIn = ImageIO.read(file);
		if(fileIn.getWidth()%WraithEngine.projectBitSize!=0||fileIn.getHeight()%WraithEngine.projectBitSize!=0){
			fileIn = Algorithms.trimTransparency(fileIn);
		}
		image = fileIn;
		painter = new EntityImporterImagePainter(image);
		setLayout(new BorderLayout());
		JButton ok;
		{
			// Add confirm and cancel buttons.
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
			ok = new JButton("Ok");
			ok.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					build();
				}
			});
			ok.setEnabled(false);
			panel.add(ok);
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					dispose();
				}
			});
			panel.add(cancel);
			add(panel, BorderLayout.SOUTH);
		}
		{
			// Build components.
			JPanel main = new JPanel();
			main.setLayout(new BorderLayout());
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
			JCheckBox below = new JCheckBox("Is Below");
			below.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					isBelow = below.isSelected();
				}
			});
			panel.add(below);
			JButton confirmButton = new JButton("Confirm");
			confirmButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					ok.setEnabled(true);
					painter.setConfirmed();
					main.remove(panel);
					main.revalidate();
					pack();
					repaint();
				}
			});
			panel.add(confirmButton);
			main.add(panel, BorderLayout.SOUTH);
			main.add(painter, BorderLayout.CENTER);
			add(main, BorderLayout.CENTER);
		}
		setTitle("Entity Import");
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
	}
	public boolean isBelow(){
		return isBelow;
	}
	private void build(){
		ArrayList<Point> tiles = painter.getTiles();
		if(tiles.isEmpty()){
			JOptionPane.showMessageDialog(null, "You must select at least one base tile!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		int layers = painter.getLayers();
		tiles.sort(new Comparator<Point>(){
			@Override
			public int compare(Point a, Point b){
				if(a.y==b.y){
					return a.x==b.x?0:a.x>b.x?1:-1;
				}
				return a.y>b.y?-1:1;
			}
		});
		BufferedImage temp = new BufferedImage(painter.getIdealWidth(), painter.getIdealHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = temp.createGraphics();
		g.drawImage(painter.getImage(), painter.getImageX(), painter.getImageY(), null);
		g.setBackground(new Color(0, 0, 0, 0));
		TileCategory cat = chipsetList.getSelectedCategory();
		int s = WraithEngine.projectBitSize;
		int x, y, w, h;
		for(Point t : tiles){
			w = s;
			h = layers*s;
			x = t.x-s/2;
			y = t.y+s-h;
			if(y+h>=temp.getHeight()){
				h = (temp.getHeight()-1)-y;
			}
			if(y<0){
				h += y;
				y = 0;
			}
			BufferedImage col = temp.getSubimage(x, y, w, h);
			if(isEmptyImage(col)){
				continue;
			}
			int colHeight = (int)Math.ceil(h/(float)s);
			if(col.getHeight()!=colHeight*s){
				BufferedImage nCol = new BufferedImage(col.getWidth(), colHeight*s, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = nCol.createGraphics();
				g2.drawImage(col, 0, 0, null);
				g2.dispose();
				col = nCol;
			}
			cat.addEntityType(new EntityType(Algorithms.randomUUID(), colHeight, cat, true), col);
			g.clearRect(x, y, w, h);
		}
		g.dispose();
		chipsetList.getEntityList().repaint();
		dispose();
	}
}
