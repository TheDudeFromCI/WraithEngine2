/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.InputAdapter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class EntityList extends JPanel{
	private static Polygon generateCursor(){
		int[] x = new int[4];
		int[] y = new int[4];
		x[0] = 0;
		y[0] = 0;
		x[1] = PREVIEW_ICON_SIZE;
		y[1] = 0;
		x[2] = PREVIEW_ICON_SIZE;
		y[2] = PREVIEW_ICON_SIZE;
		x[3] = 0;
		y[3] = PREVIEW_ICON_SIZE;
		return new Polygon(x, y, 4);
	}
	private static final int PREVIEW_WIDTH = 4;
	private static final int PREVIEW_ICON_SIZE = 64;
	private final Polygon cursor;
	private final MapEditorTab mapStyle;
	private final ArrayList<EntityInterface> entities = new ArrayList(16);
	public EntityList(MapEditorTab mapStyle){
		this.mapStyle = mapStyle;
		cursor = generateCursor();
		updatePrefferedSize();
		InputAdapter ia = new InputAdapter(){
			@Override
			public void mouseClicked(MouseEvent event){
				int x = event.getX()/PREVIEW_ICON_SIZE;
				int y = event.getY()/PREVIEW_ICON_SIZE;
				int index = y*PREVIEW_WIDTH+x-1;
				CursorSelection cursorSelection = mapStyle.getChipsetList().getPainter().getCursorSelection();
				if(index<0||index>=entities.size()){
					cursorSelection.setSelectedEntity(null, -1);
					repaint();
					return;
				}
				cursorSelection.setSelectedEntity(entities.get(index), index);
				repaint();
			}
		};
		addMouseListener(ia);
	}
	public void updateEntities(){
		TileCategory cat = mapStyle.getChipsetList().getSelectedCategory();
		entities.clear();
		if(cat!=null){
			for(EntityType e : cat.getEntities()){
				if(e.isComplex()){
					continue;
				}
				entities.add(e);
			}
			for(ComplexEntity e : cat.getComplexEntityList().getAllEntities()){
				entities.add(e);
			}
		}
		mapStyle.getChipsetList().getPainter().getCursorSelection().setSelectedEntity(null, -1);
		updatePrefferedSize();
		repaint();
	}
	private void updatePrefferedSize(){
		setPreferredSize(new Dimension(PREVIEW_WIDTH*PREVIEW_ICON_SIZE, Math.max((int)Math.ceil((entities.size()+1)/(double)PREVIEW_WIDTH), 150)));
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.lightGray);
		int width = getWidth();
		int height = getHeight();
		g.fillRect(0, 0, width, height);
		int x = PREVIEW_ICON_SIZE;
		int y = 0;
		int[] imageSize = new int[4];
		MapImageStorage imageStorage = mapStyle.getMapEditor().getImageStorage();
		BufferedImage image;
		for(EntityInterface e : entities){
			image = imageStorage.getImage(e);
			getImageSize(image, imageSize);
			g.drawImage(image, x+imageSize[0], y+imageSize[1], imageSize[2], imageSize[3], null);
			x += PREVIEW_ICON_SIZE;
			if(x==PREVIEW_ICON_SIZE*PREVIEW_WIDTH){
				x = 0;
				y += PREVIEW_ICON_SIZE;
			}
		}
		CursorSelection cursorSelection = mapStyle.getChipsetList().getPainter().getCursorSelection();
		int index = cursorSelection.isEntityActive()?cursorSelection.getSelectedEntityIndex()+1:0;
		g.setStroke(new BasicStroke(3));
		g.translate(index%PREVIEW_WIDTH*PREVIEW_ICON_SIZE, index/PREVIEW_WIDTH*PREVIEW_ICON_SIZE);
		g.setColor(Color.white);
		g.drawPolygon(cursor);
		g.dispose();
	}
	private void getImageSize(BufferedImage image, int[] out){
		out[2] = image.getWidth();
		out[3] = image.getHeight();
		if(image.getWidth()>PREVIEW_ICON_SIZE){
			out[2] = PREVIEW_ICON_SIZE;
			out[3] = (out[2]*image.getHeight())/image.getWidth();
		}
		if(out[3]>PREVIEW_ICON_SIZE){
			out[3] = PREVIEW_ICON_SIZE;
			out[2] = (out[3]*image.getWidth())/image.getHeight();
		}
		out[0] = (PREVIEW_ICON_SIZE-out[2])/2;
		out[1] = (PREVIEW_ICON_SIZE-out[3])/2;
	}
}
