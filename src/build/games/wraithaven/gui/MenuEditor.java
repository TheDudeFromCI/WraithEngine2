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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * @author thedudefromci
 */
public class MenuEditor extends JPanel{
	private static final int BORDER_SPACING = 20;
	private final int[] selectedImageRegion = new int[5];
	private Menu menu;
	private ComponentDrag componentDrag;
	private MenuComponentList componentList;
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
			}
			@Override
			public void mousePressed(MouseEvent event){
				if(menu==null){
					return;
				}
				int x = event.getX();
				int y = event.getY();
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
		repaint();
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		int width = getWidth();
		int height = getHeight();
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, width, height);
		selectedImageRegion[0] = 0; // Turn off selection region.
		if(menu!=null){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(Color.white);
			g.drawRect(BORDER_SPACING, BORDER_SPACING, width-BORDER_SPACING*2, height-BORDER_SPACING*2);
			drawHeirachry(g, menu, 0, 0, width, height);
			if(selectedImageRegion[0]==1){
				// If we have a selected component.
				g.setColor(Color.black);
				g.setStroke(new BasicStroke(1));
				g.drawRect(selectedImageRegion[1], selectedImageRegion[2], selectedImageRegion[3], selectedImageRegion[4]);
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
				selectedImageRegion[0] = 1;
				selectedImageRegion[1] = Math.round(x);
				selectedImageRegion[2] = Math.round(y);
				selectedImageRegion[3] = Math.round(width);
				selectedImageRegion[4] = Math.round(height);
			}
		}
		for(MenuComponentHeirarchy com : h.getChildren()){
			drawHeirachry(g, com, x, y, width, height);
		}
	}
}
