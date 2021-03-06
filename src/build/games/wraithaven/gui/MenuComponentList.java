/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import build.games.wraithaven.code.AttachScriptsDialog;
import build.games.wraithaven.gui.components.EmptyComponent;
import build.games.wraithaven.gui.components.ImageComponent;
import build.games.wraithaven.gui.components.MigLayout;
import build.games.wraithaven.util.InputAdapter;
import build.games.wraithaven.util.InputDialog;
import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
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
	private final BufferedImage arrow5;
	private final BufferedImage arrow6;
	private final MenuEditor menuEditor;
	private final MenuComponentLocationPanel componentInfo;
	private Menu menu;
	private MenuComponentHeirarchy selectedComponent;
	private TreeDrag treeDrag;
	private MenuComponentHeirarchy mousedOver;
	public MenuComponentList(MenuEditor menuEditor, MenuComponentLocationPanel componentInfo){
		this.menuEditor = menuEditor;
		this.componentInfo = componentInfo;
		arrow1 = attemptLoadImage("Arrow1.png");
		arrow2 = attemptLoadImage("Arrow2.png");
		arrow3 = attemptLoadImage("Arrow3.png");
		arrow4 = attemptLoadImage("Arrow4.png");
		arrow5 = attemptLoadImage("Arrow5.png");
		arrow6 = attemptLoadImage("Arrow6.png");
		setMinimumSize(new Dimension(100, 200));
		InputAdapter ia = new InputAdapter(){
			@Override
			public void mousePressed(MouseEvent event){
				if(menu==null){
					return;
				}
				int button = event.getButton();
				if(button==MouseEvent.BUTTON1){
					int x = event.getX();
					int y = event.getY();
					int r = checkForToggleCollapse(x, y, 0, 0, menu, true);
					if(r!=-1){
						setSelectedComponent(null);
					}
					if(selectedComponent!=null&&selectedComponent.getParent()!=null){
						// Make sure we have a parent, otherwise we couldn't exactly drag stuff.
						treeDrag = new TreeDrag(selectedComponent, y/TEXT_HEIGHT);
						repaint();
					}
				}else if(button==MouseEvent.BUTTON3){
					int x = event.getX();
					int y = event.getY();
					// first check to see if we are selecting anything.
					int r = checkForToggleCollapse(x, y, 0, 0, menu, false);
					if(r!=-1){
						// Nah, just clicking void. Go ahead and return.
						setSelectedComponent(null);
						return;
					}
					JPopupMenu menu = new JPopupMenu();
					{
						// Build menu
						{
							// New Component
							JMenu menu2 = new JMenu("New");
							{
								// Components
								addComponentToList(menu2, "Empty", EmptyComponent.class);
								addComponentToList(menu2, "Image", ImageComponent.class);
							}
							menu.add(menu2);
						}
						{
							// Edit
							if(selectedComponent instanceof MenuComponent){
								// Is not root.
								JMenuItem item = new JMenuItem("Edit");
								item.addActionListener(new ActionListener(){
									@Override
									public void actionPerformed(ActionEvent e){
										attemptEditComponet((MenuComponent)selectedComponent);
									}
								});
								menu.add(item);
							}
						}
						{
							// Edit Scrippts
							if(selectedComponent instanceof MenuComponent){
								// Is not root.
								JMenuItem item = new JMenuItem("Edit Scripts");
								item.addActionListener(new ActionListener(){
									@Override
									public void actionPerformed(ActionEvent e){
										AttachScriptsDialog scriptsDialog = new AttachScriptsDialog((MenuComponent)selectedComponent);
										InputDialog dialog = new InputDialog();
										dialog.setTitle("Edit Component Scripts");
										dialog.setOkButton(true);
										dialog.setCancelButton(true);
										dialog.setData(scriptsDialog);
										dialog.setDefaultFocus(scriptsDialog.getFocus());
										dialog.show();
										if(dialog.getResponse()!=InputDialog.OK){
											return;
										}
										scriptsDialog.compile((MenuComponent)selectedComponent);
										MenuComponentList.this.menu.save();
									}
								});
								menu.add(item);
							}
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
									MenuComponentHeirarchy parent = selectedComponent.getParent();
									parent.removeChild((MenuComponent)selectedComponent);
									if(parent.getChildren().isEmpty()){
										// Just in case it is for some reason...
										parent.setCollapsed(false);
									}
									setSelectedComponent(null);
									MenuComponentList.this.menu.save();
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
			private int checkForToggleCollapse(int x, int y, int h, int w, MenuComponentHeirarchy com, boolean full){
				if(full&&x>=w&&x<w+TEXT_INDENT&&y>=h&&y<h+TEXT_HEIGHT){
					if(com.getChildren().isEmpty()){
						com.setCollapsed(false);
					}else{
						com.setCollapsed(!com.isCollapsed());
					}
					repaint();
					return -1;
				}
				if(y>=h&&y<h+TEXT_HEIGHT){
					setSelectedComponent(com);
					repaint();
					return -1;
				}
				h += TEXT_HEIGHT;
				if(!com.isCollapsed()){
					w += TEXT_INDENT;
					for(MenuComponentHeirarchy c : com.getChildren()){
						h = checkForToggleCollapse(x, y, h, w, c, full);
						if(h==-1){
							return -1;
						}
					}
				}
				return h;
			}
			private int checkForMouseOver(int x, int y, int h, int w, MenuComponentHeirarchy com){
				if(x>=w&&x<w+TEXT_INDENT&&y>=h&&y<h+TEXT_HEIGHT){
					com.setMousedOver(true);
					mousedOver = com;
					repaint();
					return -1;
				}
				h += TEXT_HEIGHT;
				if(!com.isCollapsed()){
					w += TEXT_INDENT;
					for(MenuComponentHeirarchy c : com.getChildren()){
						h = checkForMouseOver(x, y, h, w, c);
						if(h==-1){
							return -1;
						}
					}
				}
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
			@Override
			public void mouseDragged(MouseEvent event){
				if(treeDrag==null){
					return;
				}
				int x = event.getX();
				int y = event.getY();
				int ideal = y/TEXT_HEIGHT;
				ideal = Math.min(ideal, getMaxComponentIndex(menu, -1));
				treeDrag.setCurrentLocation(ideal, x<=getWidth()/2);
				repaint();
			}
			@Override
			public void mouseReleased(MouseEvent event){
				if(treeDrag!=null){
					if(treeDrag.isOriginalLocation()){
						treeDrag = null;
						repaint();
						return;
					}
					MenuComponentHeirarchy comp;
					{
						// Get drop target.
						MenuComponentHeirarchy[] h = new MenuComponentHeirarchy[1];
						getByIndex(menu, treeDrag.getCurrentLocation(), 0, h);
						comp = h[0];
					}
					if(isChildOf(treeDrag.getObject(), comp)){
						// Cancel the event.
						treeDrag = null;
						repaint();
						return;
					}
					if(treeDrag.isSibiling()){
						if(comp.getParent()==null){
							// Can't have a sibiling, without parents.
							treeDrag = null;
							repaint();
							return;
						}
						MenuComponentHeirarchy tar = treeDrag.getObject();
						if(tar.getParent()==comp.getParent()){
							int index = tar.getParent().getChildren().indexOf(comp);
							tar.getParent().move(tar, index);
						}else{
							tar.getParent().removeChild(tar);
							comp.getParent().addChild(tar);
							tar.setParent(comp.getParent());
							comp.getParent().move(tar, tar.getParent().getChildren().indexOf(comp));
						}
						menu.save();
					}else{
						MenuComponentHeirarchy tar = treeDrag.getObject();
						tar.getParent().removeChild(tar);
						comp.addChild(tar);
						tar.setParent(comp);
						comp.move(tar, 0);
						menu.save();
					}
					treeDrag = null;
					repaint();
				}
			}
		};
		addMouseListener(ia);
		addMouseMotionListener(ia);
		calculatePrefferedSize();
	}
	public Menu getMenu(){
		return menu;
	}
	public void setMenu(Menu menu){
		if(this.menu==menu){
			return;
		}
		setSelectedComponent(null);
		treeDrag = null;
		mousedOver = null;
		if(this.menu!=null){
			this.menu.save();
			this.menu.dispose();
		}
		this.menu = menu;
		if(this.menu!=null){
			menu.load();
		}
		calculatePrefferedSize();
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
		// Just to make sure everything matches up over there.
		menuEditor.repaint();
		calculatePrefferedSize();
	}
	private void calculatePrefferedSize(){
		int[] out = new int[2];
		if(menu!=null){
			findPrefferedSize(menu, out, 0);
		}
		out[0] = Math.max(out[0], 10);
		out[1] = Math.max(out[1], 10);
		Dimension pre = getPreferredSize();
		if(pre.width==out[0]&&pre.height==out[1]){
			return;
		}
		setPreferredSize(new Dimension(out[0], out[1]));
	}
	private void findPrefferedSize(MenuComponentHeirarchy h, int[] out, int indent){
		FontMetrics fm = getFontMetrics(getFont());
		out[0] = Math.max(out[0], fm.stringWidth(h.toString())+indent);
		out[1] += TEXT_HEIGHT;
		indent += TEXT_INDENT;
		if(!h.isCollapsed()){
			for(MenuComponentHeirarchy h2 : h.getChildren()){
				findPrefferedSize(h2, out, indent);
			}
		}
	}
	private int drawComponentHeirarchy(Graphics2D g, MenuComponentHeirarchy com, int x, int y, FontMetrics fm){
		// Draw
		if(com==selectedComponent){
			g.setColor(SELECTED_COLOR);
			g.fillRect(0, y, getWidth(), TEXT_HEIGHT);
			g.setColor(Color.black);
		}
		int textPosition = (TEXT_HEIGHT-fm.getHeight())/2+fm.getAscent()+y;
		g.drawString(com.toString(), ARROW_SIZE+x, textPosition);
		if(treeDrag!=null&&treeDrag.isActive()&&y/TEXT_HEIGHT==treeDrag.getCurrentLocation()){
			g.setColor(Color.gray);
			Stroke s = g.getStroke();
			g.setStroke(new BasicStroke(2));
			int xStart = ARROW_SIZE+x;
			if(!treeDrag.isSibiling()){
				xStart += TEXT_INDENT;
			}
			g.drawLine(xStart, textPosition+3, xStart+fm.stringWidth(com.toString()), textPosition+3);
			g.setStroke(s);
			g.setColor(Color.black);
		}
		BufferedImage arrowIcon;
		if(com.getChildren().isEmpty()){
			if(com.isMousedOver()){
				arrowIcon = arrow6;
			}else{
				arrowIcon = arrow5;
			}
		}else if(com.isCollapsed()){
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
				y = drawComponentHeirarchy(g, c, x, y, fm);
			}
		}
		return y;
	}
	private void attemptCreateComponet(MenuComponentHeirarchy parent, MenuComponent child){
		InputDialog dialog = new InputDialog();
		MenuComponentDialog builder = child.getCreationDialog();
		JPanel dialogComponent = new JPanel();
		dialogComponent.setLayout(new VerticalFlowLayout(5));
		dialogComponent.add(builder);
		JComboBox comboBox;
		{
			// Layout List
			comboBox = new JComboBox(getLayoutList());
			dialogComponent.add(comboBox);
		}
		dialog.setData(dialogComponent);
		dialog.setOkButton(true);
		dialog.setCancelButton(true);
		dialog.setDefaultFocus(builder.getDefaultFocus());
		dialog.setTitle(child.getName());
		dialog.show();
		if(dialog.getResponse()!=InputDialog.OK){
			return;
		}
		builder.build(child);
		parent.addChild(child);
		child.setParent(parent);
		placeLayout(child, (String)comboBox.getSelectedItem());
		menuEditor.updateAllLayouts();
		menu.save();
		repaint();
	}
	private String[] getLayoutList(){
		return new String[]{
			"No Layout", "Mig Layout"
		};
	}
	private int getSelectedComponent(ComponentLayout layout){
		if(layout==null){
			return 0;
		}
		if(layout instanceof MigLayout){
			return 1;
		}
		throw new RuntimeException("Unknown layout '"+layout.toString()+"'");
	}
	private void placeLayout(MenuComponent component, String response){
		switch(response){
			case "No Layout":
				if(component.getLayout()!=null){
					component.setLayout(null);
				}
				break;
			case "Mig Layout":
				if(!(component.getLayout() instanceof MigLayout)){
					component.setLayout(new MigLayout());
				}
				break;
			default:
				// ???
				System.out.println("Failed to load layout: '"+response+"'");
				// Do nothing I guess...
				break;
		}
	}
	private void attemptEditComponet(MenuComponent com){
		InputDialog dialog = new InputDialog();
		MenuComponentDialog builder = com.getCreationDialog();
		MenuComponentDialog builder2 = null;
		JPanel dialogContent = new JPanel();
		dialogContent.setLayout(new BorderLayout(5, 5));
		dialogContent.add(builder, BorderLayout.CENTER);
		if(com.getLayout()!=null){
			builder2 = com.getLayout().getCreationDialog();
			dialogContent.add(builder2, BorderLayout.EAST);
		}
		JComboBox comboBox;
		{
			// Layout List
			comboBox = new JComboBox(getLayoutList());
			comboBox.setSelectedIndex(getSelectedComponent(com.getLayout()));
			dialogContent.add(comboBox, BorderLayout.SOUTH);
		}
		dialog.setData(dialogContent);
		dialog.setOkButton(true);
		dialog.setCancelButton(true);
		dialog.setDefaultFocus(builder.getDefaultFocus());
		dialog.setTitle(com.getName());
		dialog.show();
		if(dialog.getResponse()!=InputDialog.OK){
			return;
		}
		builder.build(com);
		if(builder2!=null){
			builder2.build(com.getLayout());
		}
		placeLayout(com, (String)comboBox.getSelectedItem());
		menuEditor.updateAllLayouts();
		menu.save();
		repaint();
	}
	private int getMaxComponentIndex(MenuComponentHeirarchy parent, int i){
		i++;
		if(!parent.isCollapsed()){
			for(MenuComponentHeirarchy com : parent.getChildren()){
				i = getMaxComponentIndex(com, i);
			}
		}
		return i;
	}
	private int getByIndex(MenuComponentHeirarchy root, int index, int pos, MenuComponentHeirarchy[] out){
		if(index==pos){
			out[0] = root;
			return -1;
		}
		pos++;
		if(!root.isCollapsed()){
			for(MenuComponentHeirarchy c : root.getChildren()){
				pos = getByIndex(c, index, pos, out);
				if(pos==-1){
					return -1;
				}
			}
		}
		return pos;
	}
	private boolean isChildOf(MenuComponentHeirarchy parent, MenuComponentHeirarchy child){
		if(parent==child){
			return true;
		}
		for(MenuComponentHeirarchy c : parent.getChildren()){
			if(isChildOf(c, child)){
				return true;
			}
		}
		return false;
	}
	public MenuComponentHeirarchy getSelectedComponent(){
		return selectedComponent;
	}
	public void addComponentToList(JMenu menu, String simpleName, Class<? extends MenuComponent> component){
		JMenuItem item = new JMenuItem(simpleName);
		item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				try{
					MenuComponent com = component.getDeclaredConstructor(String.class).newInstance(Algorithms.randomUUID());
					attemptCreateComponet(selectedComponent, com);
				}catch(NoSuchMethodException|SecurityException|InstantiationException|IllegalAccessException|IllegalArgumentException
					|InvocationTargetException ex){
					// I'm sure this will never get called. But whatever. :P
					ex.printStackTrace();
				}
			}
		});
		menu.add(item);
	}
	public void setSelectedComponent(MenuComponentHeirarchy selectedComponent){
		this.selectedComponent = selectedComponent;
		if(selectedComponent instanceof MenuComponent){
			componentInfo.setComponent((MenuComponent)selectedComponent);
		}
		repaint();
	}
}
