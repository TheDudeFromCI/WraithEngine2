/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import build.games.wraithaven.util.InputAdapter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 * @author thedudefromci
 */
public class MenuEditor extends JPanel{
	private static final int BORDER_SPACING = 50;
	private static final int END_BSPACING = 50;
	private static final int WINDOW_DRAG_ICON_R = 4;
	private final Object[] selectedImageRegion = new Object[5];
	private Menu menu;
	private ComponentDrag componentDrag;
	private MenuComponentList componentList;
	private WindowDrag windowDrag;
	public MenuEditor(){
		InputAdapter ia = new InputAdapter(){
			@Override
			public void mouseReleased(MouseEvent event){
				if(menu==null){
					return;
				}
				if(componentDrag!=null){
					menu.save();
					componentDrag = null;
					repaint();
				}
				if(windowDrag!=null){
					windowDrag = null;
					repaint();
				}
			}
			@Override
			public void mousePressed(MouseEvent event){
				if(menu==null){
					return;
				}
				int x = event.getX();
				int y = event.getY();
				int width = getWidth();
				int height = getHeight();
				if(Math.pow(x-(width-END_BSPACING), 2)+Math.pow(y-(height-END_BSPACING), 2)<WINDOW_DRAG_ICON_R*WINDOW_DRAG_ICON_R){
					windowDrag = new WindowDrag(x, y, END_BSPACING);
					return;
				}
				if(event.isShiftDown()){
					// Update selected.
					componentList.setSelectedComponent(null);
					updateSelectedComponent(menu, x, y, 0, 0, getWidth(), getHeight());
				}
				MenuComponentHeirarchy h = componentList.getSelectedComponent();
				if(h!=null&&h.getParent()!=null){
					// Make sure a have a component selected, other than root.
					componentDrag = new ComponentDrag((MenuComponent)h, x, y);
				}
				repaint();
			}
			@Override
			public void mouseDragged(MouseEvent event){
				if(menu==null){
					return;
				}
				if(componentDrag!=null){
					componentDrag.updatePosition(event.getX(), event.getY(), getWidth(), getHeight());
					repaint();
				}
				if(windowDrag!=null){
					windowDrag.update(event.getX(), event.getY());
					repaint();
				}
			}
			private void updateSelectedComponent(MenuComponentHeirarchy root, int mouseX, int mouseY, float x, float y, float width, float height){
				if(root instanceof MenuComponent){
					Anchor a = ((MenuComponent)root).getAnchor();
					x = x+width*a.getParentX()-a.getWidth()*a.getChildX();
					y = y+height*a.getParentY()-a.getHeight()*a.getChildY();
					width = a.getWidth();
					height = a.getHeight();
					if(mouseX>=x&&mouseX<x+width&&mouseY>=y&&mouseY<y+height){
						componentList.setSelectedComponent(root);
					}
				}
				for(MenuComponentHeirarchy c : root.getChildren()){
					updateSelectedComponent(c, mouseX, mouseY, x, y, width, height);
				}
			}
		};
		addMouseListener(ia);
		addMouseMotionListener(ia);
	}
	public void setMenuComponentList(MenuComponentList componentList){
		this.componentList = componentList;
	}
	public Menu getMenu(){
		return menu;
	}
	public void loadMenu(Menu menu){
		// No need to load the menu here, as it would always be loaded first by the menu component list.
		this.menu = menu;
		// And also remove references to temp variables.
		componentDrag = null;
		windowDrag = null;
		repaint();
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		int width = getWidth();
		int height = getHeight();
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, width, height);
		if(menu!=null){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(Color.white);
			int bSpaceEndX = windowDrag==null?END_BSPACING:windowDrag.getSpacingX();
			int bSpaceEndY = windowDrag==null?END_BSPACING:windowDrag.getSpacingY();
			g.drawRect(BORDER_SPACING, BORDER_SPACING, width-BORDER_SPACING-bSpaceEndX, height-BORDER_SPACING-bSpaceEndY);
			g.setColor(new Color(230, 230, 230));
			g.fillOval(width-bSpaceEndX-WINDOW_DRAG_ICON_R, height-bSpaceEndY-WINDOW_DRAG_ICON_R, WINDOW_DRAG_ICON_R*2, WINDOW_DRAG_ICON_R*2);
			drawHeirachry(g, menu, BORDER_SPACING, BORDER_SPACING, width-BORDER_SPACING-bSpaceEndX, height-BORDER_SPACING-bSpaceEndY);
			if(selectedImageRegion[0]!=null){
				// If we have a selected component.
				g.setColor(Color.black);
				g.setStroke(new BasicStroke(2));
				g.drawRect(Math.round((float)selectedImageRegion[1]), Math.round((float)selectedImageRegion[2]),
					Math.round((float)selectedImageRegion[3]), Math.round((float)selectedImageRegion[4]));
				g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{
					9
				}, 0));
				Anchor an = ((MenuComponent)selectedImageRegion[0]).getAnchor();
				int anchorX = Math.round((float)selectedImageRegion[1]+(float)selectedImageRegion[3]*an.getChildX());
				int anchorY = Math.round((float)selectedImageRegion[2]+(float)selectedImageRegion[4]*an.getChildY());
				g.drawLine(BORDER_SPACING, anchorY, anchorX, anchorY);
				g.drawLine(anchorX, BORDER_SPACING, anchorX, anchorY);
				String percentX = String.format("%.1f", an.getParentX()*100)+"%";
				String percentY = String.format("%.1f", an.getParentY()*100)+"%";
				FontMetrics fm = g.getFontMetrics();
				Rectangle2D recX = fm.getStringBounds(percentX, g);
				Rectangle2D recY = fm.getStringBounds(percentY, g);
				g.drawString(percentX, anchorX-(float)recX.getWidth()/2, (BORDER_SPACING-(float)recX.getHeight())/2+fm.getAscent());
				g.drawString(percentY, (BORDER_SPACING-(float)recY.getWidth())/2, anchorY-(float)recX.getHeight()/2+fm.getAscent());
				// This also disposes an unnessicary use of memory, and possible leak.
				selectedImageRegion[0] = null; // Turn off selection region.
			}
		}
		g.dispose();
	}
	private void drawHeirachry(Graphics2D g, MenuComponentHeirarchy h, float x, float y, float width, float height){
		if(h instanceof MenuComponent){
			Anchor a = ((MenuComponent)h).getAnchor();
			x = x+width*a.getParentX()-a.getWidth()*a.getChildX();
			y = y+height*a.getParentY()-a.getHeight()*a.getChildY();
			width = a.getWidth();
			height = a.getHeight();
			((MenuComponent)h).draw(g, x, y, width, height);
			if(componentList.getSelectedComponent()==h){
				// Turn on selection region.
				selectedImageRegion[0] = h;
				selectedImageRegion[1] = x;
				selectedImageRegion[2] = y;
				selectedImageRegion[3] = width;
				selectedImageRegion[4] = height;
			}
		}
		for(MenuComponentHeirarchy com : h.getChildren()){
			drawHeirachry(g, com, x, y, width, height);
		}
	}
}
