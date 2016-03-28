/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import build.games.wraithaven.util.InputAdapter;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import wraith.lib.gui.Anchor;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class MenuEditor extends JPanel{
	private static final int BORDER_SPACING = 10;
	private static final int END_BSPACING = 10;
	private static final int WINDOW_DRAG_ICON_R = 6;
	private static final int COMP_DRAG_ICON_R = 5;
	private static final int RESIZE_ICON_SIZE = 16;
	private static final int RESIZE_ICON_DISTANCE = 25;
	private final Object[] selectedImageRegion = new Object[5];
	private final BufferedImage autoResizeIcon;
	private final Area resizeIconClip;
	private final MenuComponentLocationPanel componentInfo;
	private final Cursor[] cursors;
	private Menu menu;
	private ComponentDrag componentDrag;
	private MenuComponentList componentList;
	private WindowDrag windowDrag;
	private CompResizeDrag compResizeDrag;
	private boolean overResizeIcon;
	private BufferedImage tempImage;
	public MenuEditor(MenuComponentLocationPanel componentInfo){
		this.componentInfo = componentInfo;
		BufferedImage autoResizeIconTemp;
		try{
			autoResizeIconTemp = ImageIO.read(Algorithms.getAsset("Auto Resize.png"));
		}catch(Exception exception){
			exception.printStackTrace();
			autoResizeIconTemp = null;
		}
		cursors = new Cursor[]{
			new Cursor(Cursor.DEFAULT_CURSOR), new Cursor(Cursor.NW_RESIZE_CURSOR), new Cursor(Cursor.NE_RESIZE_CURSOR),
			new Cursor(Cursor.SW_RESIZE_CURSOR), new Cursor(Cursor.SE_RESIZE_CURSOR), new Cursor(Cursor.CROSSHAIR_CURSOR),
			new Cursor(Cursor.MOVE_CURSOR)
		};
		autoResizeIcon = autoResizeIconTemp;
		resizeIconClip = Algorithms.createClip(autoResizeIcon, RESIZE_ICON_SIZE, RESIZE_ICON_SIZE);
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
					setCursor(cursors[0]);
					repaint();
				}
				if(compResizeDrag!=null){
					menu.save();
					compResizeDrag = null;
					repaint();
				}
			}
			@Override
			public void mousePressed(MouseEvent event){
				if(menu==null){
					return;
				}
				if(overResizeIcon){
					MenuComponentHeirarchy h = ((MenuComponent)selectedImageRegion[0]).getParent();
					float height, width;
					if(h instanceof MenuComponent){
						width = ((MenuComponent)h).getAnchor().getWidth();
						height = ((MenuComponent)h).getAnchor().getHeight();
					}else{
						width = getWidth()-BORDER_SPACING-END_BSPACING;
						height = getHeight()-BORDER_SPACING-END_BSPACING;
					}
					((AutoResizableComponent)selectedImageRegion[0]).resize(width, height);
					menu.save();
					overResizeIcon = false; // The icon has likely moved, so unselect it.
					repaint();
					return;
				}
				int x = event.getX();
				int y = event.getY();
				int width = getWidth();
				int height = getHeight();
				if(Math.pow(x-(width-END_BSPACING), 2)+Math.pow(y-(height-END_BSPACING), 2)<WINDOW_DRAG_ICON_R*WINDOW_DRAG_ICON_R){
					windowDrag = new WindowDrag(x, y, END_BSPACING);
					setCursor(cursors[6]);
					return;
				}
				if(event.isShiftDown()){
					// Update selected.
					componentList.setSelectedComponent(null);
					updateSelectedComponent(menu, x, y, BORDER_SPACING, BORDER_SPACING, width-BORDER_SPACING-END_BSPACING,
						height-BORDER_SPACING-END_BSPACING);
				}
				MenuComponentHeirarchy h = componentList.getSelectedComponent();
				if(h!=null&&h instanceof MenuComponent){
					// Make sure a have a component selected, other than root.
					// First check for resize-drag.
					{
						if(selectedImageRegion[0]!=null){
							// Yep, we are building off of this, again.
							float a = (float)selectedImageRegion[1];
							float b = (float)selectedImageRegion[2];
							float c = (float)selectedImageRegion[3];
							float d = (float)selectedImageRegion[4];
							Anchor an = ((MenuComponent)selectedImageRegion[0]).getAnchor();
							int corner;
							if(Math.pow(x-(a+c*an.getChildX()), 2)+Math.pow(y-(b+d*an.getChildY()), 2)<COMP_DRAG_ICON_R*COMP_DRAG_ICON_R){ // Center
								corner = -1;
							}else if(Math.pow(x-a, 2)+Math.pow(y-b, 2)<COMP_DRAG_ICON_R*COMP_DRAG_ICON_R){ // Top Left
								corner = 0;
							}else if(Math.pow(x-(a+c), 2)+Math.pow(y-b, 2)<COMP_DRAG_ICON_R*COMP_DRAG_ICON_R){ // Top Right
								corner = 1;
							}else if(Math.pow(x-a, 2)+Math.pow(y-(b+d), 2)<COMP_DRAG_ICON_R*COMP_DRAG_ICON_R){ // Bottom Left
								corner = 2;
							}else if(Math.pow(x-(a+c), 2)+Math.pow(y-(b+d), 2)<COMP_DRAG_ICON_R*COMP_DRAG_ICON_R){ // Bottom Right
								corner = 3;
							}else{
								corner = -2;
							}
							if(corner>-2){
								MenuComponentHeirarchy par = h.getParent();
								if(par!=null&&par instanceof MenuComponent){
									a = ((MenuComponent)par).getAnchor().getWidth();
									b = ((MenuComponent)par).getAnchor().getHeight();
								}else{
									a = getWidth()-BORDER_SPACING-END_BSPACING;
									b = getHeight()-BORDER_SPACING-END_BSPACING;
								}
								compResizeDrag = new CompResizeDrag(MenuEditor.this, (MenuComponent)h, x, y, corner, a, b);
								repaint();
								return;
							}
						}
					}
					// Default to move-drag.
					componentDrag = new ComponentDrag(MenuEditor.this, (MenuComponent)h, x, y);
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
				if(compResizeDrag!=null){
					compResizeDrag.update(event.getX(), event.getY(), event.isControlDown());
					repaint();
				}
			}
			@Override
			public void mouseMoved(MouseEvent event){
				if(menu==null){
					setCursor(cursors[0]);
					overResizeIcon = false;
					return;
				}
				int x = event.getX();
				int y = event.getY();
				if(selectedImageRegion[0]!=null){
					float a = (float)selectedImageRegion[1];
					float b = (float)selectedImageRegion[2];
					float c = (float)selectedImageRegion[3];
					float d = (float)selectedImageRegion[4];
					Anchor an = ((MenuComponent)selectedImageRegion[0]).getAnchor();
					if(Math.pow(x-(a+c*an.getChildX()), 2)+Math.pow(y-(b+d*an.getChildY()), 2)<COMP_DRAG_ICON_R*COMP_DRAG_ICON_R){ // Center
						setCursor(cursors[5]);
					}else if(Math.pow(x-a, 2)+Math.pow(y-b, 2)<COMP_DRAG_ICON_R*COMP_DRAG_ICON_R){ // Top Left
						setCursor(cursors[1]);
					}else if(Math.pow(x-(a+c), 2)+Math.pow(y-b, 2)<COMP_DRAG_ICON_R*COMP_DRAG_ICON_R){ // Top Right
						setCursor(cursors[2]);
					}else if(Math.pow(x-a, 2)+Math.pow(y-(b+d), 2)<COMP_DRAG_ICON_R*COMP_DRAG_ICON_R){ // Bottom Left
						setCursor(cursors[3]);
					}else if(Math.pow(x-(a+c), 2)+Math.pow(y-(b+d), 2)<COMP_DRAG_ICON_R*COMP_DRAG_ICON_R){ // Bottom Right
						setCursor(cursors[4]);
					}else{
						setCursor(cursors[0]);
					}
				}
				if(selectedImageRegion[0]==null||!(selectedImageRegion[0] instanceof AutoResizableComponent)){
					overResizeIcon = false;
					return;
				}
				float a = (float)selectedImageRegion[1]+(float)selectedImageRegion[3]+RESIZE_ICON_DISTANCE;
				float b = (float)selectedImageRegion[2]+(float)selectedImageRegion[4]+RESIZE_ICON_DISTANCE;
				boolean isOver = x>=a&&x<a+RESIZE_ICON_SIZE&&y>=b&&y<b+RESIZE_ICON_SIZE;
				if(isOver!=overResizeIcon){
					overResizeIcon = isOver;
					repaint();
				}
			}
			@Override
			public void mouseExited(MouseEvent event){
				overResizeIcon = false;
				repaint();
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
		compResizeDrag = null;
		repaint();
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		int width = getWidth();
		int height = getHeight();
		if(tempImage==null){
			g.setColor(Color.lightGray);
			g.fillRect(0, 0, width, height);
		}else{
			g.drawImage(tempImage, 0, 0, width, height, null);
		}
		if(menu!=null){
			// Rendering hints are set after filling the background color to optimize rendering time.
			// Also, no need to set rendering hints if there's nothing to render.
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(Color.white);
			int bSpaceEndX = windowDrag==null?END_BSPACING:windowDrag.getSpacingX();
			int bSpaceEndY = windowDrag==null?END_BSPACING:windowDrag.getSpacingY();
			g.drawRect(BORDER_SPACING, BORDER_SPACING, width-BORDER_SPACING-bSpaceEndX, height-BORDER_SPACING-bSpaceEndY);
			g.setColor(Color.gray);
			g.fillOval(width-bSpaceEndX-WINDOW_DRAG_ICON_R, height-bSpaceEndY-WINDOW_DRAG_ICON_R, WINDOW_DRAG_ICON_R*2, WINDOW_DRAG_ICON_R*2);
			selectedImageRegion[0] = null;
			ArrayList<MenuComponentDrawPosition> drawOrder = new ArrayList(64);
			drawHeirachry(g, menu, BORDER_SPACING, BORDER_SPACING, width-BORDER_SPACING-bSpaceEndX, height-BORDER_SPACING-bSpaceEndY, drawOrder, 0);
			drawOrder.sort(null);
			for(MenuComponentDrawPosition draw : drawOrder){
				draw.draw(g);
			}
			if(selectedImageRegion[0]!=null){
				// If we have a selected component.
				drawSelectionRegion(g);
			}
			{
				// Drag window size.
				String ratio = (width-BORDER_SPACING-bSpaceEndX)+"x"+(height-BORDER_SPACING-bSpaceEndY);
				float x = width-bSpaceEndX;
				float y = height-bSpaceEndY;
				FontMetrics fm = g.getFontMetrics();
				g.setColor(Color.black);
				g.drawString(ratio, x-fm.getHeight()/2f-fm.stringWidth(ratio), y-fm.getHeight()/2f);
			}
		}
		g.dispose();
	}
	private void drawSelectionRegion(Graphics2D g1){
		Graphics2D g = (Graphics2D)g1.create();
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(2));
		float x = (float)selectedImageRegion[1];
		float y = (float)selectedImageRegion[2];
		float w = (float)selectedImageRegion[3];
		float h = (float)selectedImageRegion[4];
		Anchor an = ((MenuComponent)selectedImageRegion[0]).getAnchor();
		g.drawRect(Math.round(x), Math.round(y), Math.round(w), Math.round(h));
		{
			// Draw anchor helper lines.
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{
				9
			}, 0));
			int anchorX = Math.round(x+w*an.getChildX());
			int anchorY = Math.round(y+h*an.getChildY());
			g2.drawLine(BORDER_SPACING, anchorY, anchorX, anchorY);
			g2.drawLine(anchorX, BORDER_SPACING, anchorX, anchorY);
			g2.dispose();
		}
		FontMetrics fm = g.getFontMetrics();
		{
			// Draw component position and scale.
			String size = Math.round((float)selectedImageRegion[3])+"x"+Math.round((float)selectedImageRegion[4]);
			String pos = "("+Math.round((float)selectedImageRegion[1]-BORDER_SPACING)+", "+Math.round((float)selectedImageRegion[2]-BORDER_SPACING)+")";
			g.drawString(size, (float)selectedImageRegion[1]+(float)selectedImageRegion[3]+fm.getHeight()/2f,
				(float)selectedImageRegion[2]+(float)selectedImageRegion[4]-fm.getHeight()/2f);
			g.drawString(pos, (float)selectedImageRegion[1]-fm.getHeight()/2f, (float)selectedImageRegion[2]-fm.getHeight()/2f);
		}
		if(selectedImageRegion[0] instanceof AutoResizableComponent){
			Graphics2D g2 =
				(Graphics2D)g.create(Math.round(x+w+RESIZE_ICON_DISTANCE), Math.round(y+h+RESIZE_ICON_DISTANCE), RESIZE_ICON_SIZE, RESIZE_ICON_SIZE);
			g2.setColor(Color.white);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			g2.fillRect(0, 0, RESIZE_ICON_SIZE, RESIZE_ICON_SIZE);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			g2.drawImage(autoResizeIcon, 0, 0, RESIZE_ICON_SIZE, RESIZE_ICON_SIZE, this);
			if(overResizeIcon){
				g2.setClip(resizeIconClip);
				g2.fillRect(0, 0, RESIZE_ICON_SIZE, RESIZE_ICON_SIZE);
			}
			g2.dispose();
		}
		g.dispose();
	}
	private void drawHeirachry(Graphics2D g, MenuComponentHeirarchy h, float x, float y, float width, float height,
		ArrayList<MenuComponentDrawPosition> drawOrder, int depth){
		if(h instanceof MenuComponent){
			Anchor a = ((MenuComponent)h).getAnchor();
			x = x+width*a.getParentX()-a.getWidth()*a.getChildX();
			y = y+height*a.getParentY()-a.getHeight()*a.getChildY();
			width = a.getWidth();
			height = a.getHeight();
			drawOrder.add(new MenuComponentDrawPosition((MenuComponent)h, depth, x, y, width, height));
			if(componentList.getSelectedComponent()==h){
				// Turn on selection region.
				selectedImageRegion[0] = h;
				selectedImageRegion[1] = x;
				selectedImageRegion[2] = y;
				selectedImageRegion[3] = width;
				selectedImageRegion[4] = height;
			}
		}
		depth++;
		for(MenuComponentHeirarchy com : h.getChildren()){
			drawHeirachry(g, com, x, y, width, height, drawOrder, depth);
		}
	}
	public void setTempBackground(BufferedImage tempImage){
		this.tempImage = tempImage;
		repaint();
	}
	public void updateAllLayouts(){
		if(menu==null){
			return;
		}
		// It's best to update layouts with parent node first, incase the size needs to be changed.
		updateAllLayouts(menu);
		componentInfo.updateComponent();
		menu.save();
		repaint();
	}
	private void updateAllLayouts(MenuComponentHeirarchy h){
		if(h instanceof MenuComponent){
			MenuComponent comp = (MenuComponent)h;
			ComponentLayout layout = comp.getLayout();
			if(layout!=null){
				layout.updateLayout(comp.getAnchor(), comp.getChildren());
			}
		}
		for(MenuComponentHeirarchy com : h.getChildren()){
			updateAllLayouts(com);
		}
	}
}
