/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import build.games.wraithaven.util.InputAdapter;
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
	private Menu menu;
	private ComponentDrag componentDrag;
	private MenuComponentList componentList;
	public MenuEditor(){
		InputAdapter ia = new InputAdapter(){
			@Override
			public void mouseReleased(MouseEvent event){
				if(componentDrag!=null){
					menu.save();
					componentDrag = null;
					repaint();
				}
			}
			@Override
			public void mousePressed(MouseEvent event){
				int x = event.getX();
				int y = event.getY();
				updateSelectedComponent(x, y);
				MenuComponentHeirarchy h = componentList.getSelectedComponent();
				if(h!=null&&h.getParent()!=null){
					// Make sure a have a component selected, other than root.
					componentDrag = new ComponentDrag((MenuComponent)h, x, y);
				}
				repaint();
			}
			@Override
			public void mouseDragged(MouseEvent event){
				if(componentDrag!=null){
					componentDrag.updatePosition(event.getX(), event.getY(), getWidth(), getHeight());
					repaint();
				}
			}
			private void updateSelectedComponent(int x, int y){
				// TODO
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
		this.menu = menu;
		// No need to load the menu here, as it would always be loaded first by the menu component list.
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
			g.drawRect(BORDER_SPACING, BORDER_SPACING, width-BORDER_SPACING*2, height-BORDER_SPACING*2);
			drawHeirachry(g, menu, 0, 0, width, height);
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
		}
		for(MenuComponentHeirarchy com : h.getChildren()){
			drawHeirachry(g, com, x, y, width, height);
		}
	}
}
