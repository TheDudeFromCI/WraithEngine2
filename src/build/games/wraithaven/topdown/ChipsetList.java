/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.BinaryFile;
import build.games.wraithaven.util.InputAdapter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ChipsetList extends JPanel{
	private static final int TITLE_BAR_HEIGHT = 30;
	private static final Color TITLE_BAR_COLOR = new Color(220, 220, 220);
	private static final Color TITLE_BAR_COLOR_2 = new Color(235, 235, 235);
	private static final Color TITLE_BAR_COLOR_3 = new Color(210, 210, 210);
	private static final Font TITLE_BAR_FONT = new Font("Tahoma", Font.BOLD|Font.ITALIC, 20);
	private final ArrayList<ChipsetListComponent> chipsets = new ArrayList(8);
	private final ChipsetTileSelection selection = new ChipsetTileSelection();
	private Polygon selectionBox;
	private int selectionBoxWidth;
	private int selectionBoxHeight;
	public ChipsetList(){
		setMinimumSize(new Dimension(Chipset.TILE_OUT_SIZE*Chipset.PREVIEW_TILES_WIDTH, 300));
		updateSize();
		load();
		InputAdapter ml = new InputAdapter(){
			private int dragXStart;
			private int dragYStart;
			private void checkForTileSelection(int x, int y){
				int height = 0;
				for(ChipsetListComponent c : chipsets){
					height += TITLE_BAR_HEIGHT;
					if(c.isExpanded()){
						if(y>=height&&y<height+c.getImage().getHeight()){
							int selX = x/Chipset.TILE_OUT_SIZE;
							int selY = (y-height)/Chipset.TILE_OUT_SIZE;
							selection.select(c.getChipset(), new int[]{
								selY*Chipset.PREVIEW_TILES_WIDTH+selX
							}, selX, selY, 1, 1);
							repaint();
							return;
						}
						height += c.getImage().getHeight();
					}
				}
				selection.reset();
			}
			private ChipsetListComponent getComponentTitleAt(int y){
				int height = 0;
				for(ChipsetListComponent c : chipsets){
					if(y>=height&&y<height+TITLE_BAR_HEIGHT){
						return c;
					}
					height += TITLE_BAR_HEIGHT;
					if(c.isExpanded()){
						height += c.getImage().getHeight();
					}
				}
				return null;
			}
			@Override
			public void mouseClicked(MouseEvent e){
				ChipsetListComponent c = getComponentTitleAt(e.getY());
				if(c==null){
					checkForTileSelection(e.getX(), e.getY());
					return;
				}
				c.setExpanded(!c.isExpanded());
				if(selection.getChipset()==c.getChipset()){
					selection.reset();
				}
				updateSize();
				repaint();
			}
			@Override
			public void mouseDragged(MouseEvent e){
				if(getComponentTitleAt(dragYStart)==null){
					int height = 0;
					for(ChipsetListComponent c : chipsets){
						height += TITLE_BAR_HEIGHT;
						if(c.isExpanded()){
							if(dragYStart>=height&&dragYStart<height+c.getImage().getHeight()){
								int selX = dragXStart/Chipset.TILE_OUT_SIZE;
								int selY = (dragYStart-height)/Chipset.TILE_OUT_SIZE;
								int endX = e.getX();
								int endY = e.getY();
								if(endY>=height+c.getImage().getHeight()){
									endY = height+c.getImage().getHeight()-1;
								}
								if(endY<height){
									endY = height;
								}
								if(endX<0){
									endX = 0;
								}
								if(endX>=getWidth()){
									endX = getWidth()-1;
								}
								int selX2 = endX/Chipset.TILE_OUT_SIZE;
								int selY2 = (endY-height)/Chipset.TILE_OUT_SIZE;
								int x = Math.min(selX, selX2);
								int y = Math.min(selY, selY2);
								int w = Math.max(selX, selX2)-x+1;
								int h = Math.max(selY, selY2)-y+1;
								int[] indices = new int[w*h];
								int a, b;
								for(a = 0; a<w; a++){
									for(b = 0; b<h; b++){
										indices[b*w+a] = (b+y)*Chipset.PREVIEW_TILES_WIDTH+(a+x);
									}
								}
								selection.select(c.getChipset(), indices, x, y, w, h);
								repaint();
								return;
							}
							height += c.getImage().getHeight();
						}
					}
					selection.reset();
				}
			}
			@Override
			public void mousePressed(MouseEvent e){
				dragXStart = e.getX();
				dragYStart = e.getY();
			}
		};
		addMouseListener(ml);
		addMouseMotionListener(ml);
	}
	private void generateSelectionBox(int width, int height){
		if(selectionBoxWidth==width&&selectionBoxHeight==height){
			return;
		}
		selectionBoxWidth = width;
		selectionBoxHeight = height;
		int[] x = new int[4];
		int[] y = new int[4];
		x[0] = 0;
		y[0] = 0;
		x[1] = width;
		y[1] = 0;
		x[2] = width;
		y[2] = height;
		x[3] = 0;
		y[3] = height;
		selectionBox = new Polygon(x, y, 4);
	}
	public void addChipset(Chipset chipset){
		chipsets.add(new ChipsetListComponent(chipset));
		updateSize();
		repaint();
		save();
	}
	public Chipset getChipset(String uuid){
		for(ChipsetListComponent chipset : chipsets){
			if(chipset.getChipset().getUUID().equals(uuid)){
				return chipset.getChipset();
			}
		}
		return null;
	}
	public ArrayList<ChipsetListComponent> getChipsets(){
		return chipsets;
	}
	public ChipsetTileSelection getSelectedTile(){
		return selection;
	}
	private void load(){
		File file = Algorithms.getFile("Chipsets", "List.dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(true);
		int listSize = bin.getInt();
		for(int i = 0; i<listSize; i++){
			chipsets.add(new ChipsetListComponent(new Chipset(bin)));
		}
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		int width = getWidth();
		int height = getHeight();
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
		g.setFont(TITLE_BAR_FONT);
		FontMetrics fm = g.getFontMetrics();
		int verticalOffset = 0;
		int selectionVerticalOffset = 0;
		for(ChipsetListComponent chip : chipsets){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setPaint(new GradientPaint(0, verticalOffset, TITLE_BAR_COLOR, 0, TITLE_BAR_HEIGHT/2+verticalOffset, TITLE_BAR_COLOR_2, true));
			g.fillRect(0, verticalOffset, width, TITLE_BAR_HEIGHT);
			g.setColor(TITLE_BAR_COLOR_3);
			g.drawLine(0, verticalOffset, width, verticalOffset);
			g.drawLine(0, verticalOffset+TITLE_BAR_HEIGHT-1, width, verticalOffset+TITLE_BAR_HEIGHT-1);
			g.setColor(new Color(0, 0, 0));
			g.drawString(chip.getName(), (width-fm.stringWidth(chip.getName()))/2, (TITLE_BAR_HEIGHT-fm.getHeight())/2+fm.getAscent()+verticalOffset);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			verticalOffset += TITLE_BAR_HEIGHT;
			if(chip.getChipset()==selection.getChipset()){
				selectionVerticalOffset = verticalOffset;
			}
			if(chip.isExpanded()){
				g.drawImage(chip.getImage(), 0, verticalOffset, null);
				verticalOffset += chip.getImage().getHeight();
			}
		}
		if(selection.isActive()){
			generateSelectionBox(selection.getWidth()*Chipset.TILE_OUT_SIZE, selection.getHeight()*Chipset.TILE_OUT_SIZE);
			g.setStroke(new BasicStroke(3));
			float offset = (float)Math.sin(System.currentTimeMillis()/100.0)*3f;
			g.setPaint(new GradientPaint(offset, offset, Color.white, offset+5, offset+5, Color.black, true));
			g.translate(selection.getSelectionX()*Chipset.TILE_OUT_SIZE, selection.getSelectionY()*Chipset.TILE_OUT_SIZE+selectionVerticalOffset);
			g.drawPolygon(selectionBox);
			repaint();
		}
		g.dispose();
	}
	private void save(){
		BinaryFile bin = new BinaryFile(4);
		bin.addInt(chipsets.size());
		for(ChipsetListComponent chip : chipsets){
			chip.getChipset().save(bin);
		}
		bin.compress(true);
		bin.compile(Algorithms.getFile("Chipsets", "List.dat"));
	}
	private void updateSize(){
		int height = 0;
		for(ChipsetListComponent c : chipsets){
			height += TITLE_BAR_HEIGHT;
			if(c.isExpanded()){
				height += c.getImage().getHeight();
			}
		}
		setPreferredSize(new Dimension(Chipset.TILE_OUT_SIZE*Chipset.PREVIEW_TILES_WIDTH, Math.max(height, 300)));
		revalidate();
		repaint();
	}
}
