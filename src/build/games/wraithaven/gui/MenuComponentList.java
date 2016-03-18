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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class MenuComponentList extends JPanel{
	private static BufferedImage attemptLoadImage(String name){
		try{
			return ImageIO.read(Algorithms.getAsset(name));
		}catch(Exception exception){
			exception.printStackTrace();
			JOptionPane.showMessageDialog(null, "There has been an arrow loading some of the visual elements in this window.", "Error",
				JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	private static final Font FONT = new Font("Tahoma", Font.PLAIN, 10);
	private static final int TEXT_HEIGHT = 15;
	private static final int TEXT_INDENT = 10;
	private static final int ARROW_SIZE = 12;
	private static final Color SELECTED_COLOR = new Color(113, 184, 201);
	private final BufferedImage arrow1;
	private final BufferedImage arrow2;
	private final BufferedImage arrow3;
	private final BufferedImage arrow4;
	private Menu menu;
	private MenuComponentHeirarchy selectedComponent;
	private MenuList menuList;
	public MenuComponentList(){
		arrow1 = attemptLoadImage("Arrow1.png");
		arrow2 = attemptLoadImage("Arrow2.png");
		arrow3 = attemptLoadImage("Arrow3.png");
		arrow4 = attemptLoadImage("Arrow4.png");
		setMinimumSize(new Dimension(100, 200));
		InputAdapter ia = new InputAdapter(){
			private MenuComponentHeirarchy mousedOver;
			@Override
			public void mousePressed(MouseEvent event){
				if(menu==null){
					return;
				}
				int button = event.getButton();
				if(button==MouseEvent.BUTTON1){
					int x = event.getX();
					int y = event.getY();
					int r = checkForToggleCollapse(x, y, 0, 0, menu);
					if(r!=-1){
						selectedComponent = null;
					}
				}else if(button==MouseEvent.BUTTON3){
					int x = event.getX();
					int y = event.getY();
					if(selectedComponent==null){
						return;
					}
					JPopupMenu menu = new JPopupMenu();
					{
						// Build menu
						{
							// New Component
							JMenu menu2 = new JMenu("New");
							// TODO
							menu.add(menu2);
						}
						{
							// Delete
							JMenuItem item = new JMenuItem("Delete");
							item.addActionListener(new ActionListener(){
								@Override
								public void actionPerformed(ActionEvent e){
									int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this component?",
										"Confirm Delete", JOptionPane.YES_NO_OPTION);
									if(response!=JOptionPane.YES_OPTION){
										return;
									}
									selectedComponent.getParent().removeChild((MenuComponent)selectedComponent);
									selectedComponent = null;
									menuList.save();
									repaint();
								}
							});
							menu.add(item);
							item.setEnabled(selectedComponent!=menu);
						}
					}
					menu.show(MenuComponentList.this, x, y);
				}
			}
			private int checkForToggleCollapse(int x, int y, int h, int w, MenuComponentHeirarchy com){
				if(x>=w&&x<w+TEXT_INDENT&&y>=h&&y<h+TEXT_HEIGHT){
					com.setCollapsed(!com.isCollapsed());
					repaint();
					return -1;
				}
				if(y>=h&&y<h+TEXT_HEIGHT){
					selectedComponent = com;
					repaint();
					return -1;
				}
				if(!com.isCollapsed()){
					w += TEXT_INDENT;
					int r;
					for(MenuComponentHeirarchy c : com.getChildren()){
						r = checkForToggleCollapse(x, y, h, w, c);
						if(r==-1){
							return -1;
						}
						h += r;
					}
				}
				h += TEXT_HEIGHT;
				return h;
			}
			private int checkForMouseOver(int x, int y, int h, int w, MenuComponentHeirarchy com){
				int a = (TEXT_INDENT-ARROW_SIZE)/2+h;
				int b = (TEXT_HEIGHT-ARROW_SIZE)/2+w;
				if(x>=a&&x<a+ARROW_SIZE&&y>=b&&y<b+ARROW_SIZE){
					com.setMousedOver(true);
					mousedOver = com;
					repaint();
					return -1;
				}
				if(!com.isCollapsed()){
					w += TEXT_INDENT;
					int r;
					for(MenuComponentHeirarchy c : com.getChildren()){
						r = checkForToggleCollapse(x, y, h, w, c);
						if(r==-1){
							return -1;
						}
						h += r;
					}
				}
				h += TEXT_HEIGHT;
				return h;
			}
			@Override
			public void mouseMoved(MouseEvent event){
				if(menu==null){
					return;
				}
				if(mousedOver!=null){
					mousedOver.setMousedOver(false);
					mousedOver = null;
				}
				int x = event.getX();
				int y = event.getY();
				int r = checkForMouseOver(x, y, 0, 0, menu);
				if(r!=-1){
					// No object is moused over.
					repaint();
				}
			}
		};
		addMouseListener(ia);
		addMouseMotionListener(ia);
	}
	public void setMenuList(MenuList menuList){
		this.menuList = menuList;
	}
	public Menu getMenu(){
		return menu;
	}
	public void setMenu(Menu menu){
		this.menu = menu;
		selectedComponent = null;
		repaint();
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		g.setColor(Color.lightGray);
		int width = getWidth();
		int height = getHeight();
		g.fillRect(0, 0, width, height);
		if(menu!=null){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setFont(FONT);
			g.setColor(Color.black);
			FontMetrics fm = g.getFontMetrics();
			drawComponentHeirarchy(g, menu, 0, 0, fm);
		}
		g.dispose();
	}
	private int drawComponentHeirarchy(Graphics2D g, MenuComponentHeirarchy com, int x, int y, FontMetrics fm){
		// Draw
		if(com==selectedComponent){
			g.setColor(SELECTED_COLOR);
			g.fillRect(0, y, getWidth(), TEXT_HEIGHT);
			g.setColor(Color.black);
		}
		g.drawString(com.toString(), ARROW_SIZE+x, (TEXT_HEIGHT-fm.getHeight())/2+fm.getAscent()+y);
		BufferedImage arrowIcon;
		if(com.isCollapsed()){
			if(com.isMousedOver()){
				arrowIcon = arrow2;
			}else{
				arrowIcon = arrow1;
			}
		}else if(com.isMousedOver()){
			arrowIcon = arrow4;
		}else{
			arrowIcon = arrow3;
		}
		g.drawImage(arrowIcon, (TEXT_INDENT-ARROW_SIZE)/2+x, (TEXT_HEIGHT-ARROW_SIZE)/2+y, ARROW_SIZE, ARROW_SIZE, null);
		// Draw children.
		y += TEXT_HEIGHT;
		if(!com.isCollapsed()){
			x += TEXT_INDENT;
			for(MenuComponentHeirarchy c : com.getChildren()){
				y += drawComponentHeirarchy(g, c, x, y, fm);
			}
		}
		return y;
	}
}
