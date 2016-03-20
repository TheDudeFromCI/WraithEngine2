/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui.components;

import build.games.wraithaven.gui.Menu;
import build.games.wraithaven.gui.MenuComponent;
import build.games.wraithaven.gui.MenuComponentDialog;
import build.games.wraithaven.gui.MenuComponentHeirarchy;
import build.games.wraithaven.util.ImagePanel;
import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class ImageComponent implements MenuComponent{
	private static final int ID = 0;
	private final ArrayList<MenuComponentHeirarchy> children = new ArrayList(4);
	private final String uuid;
	private boolean collapsed;
	private boolean mousedOver;
	private MenuComponentHeirarchy parent;
	private String name = "Image Component";
	private BufferedImage image;
	boolean saveImage;
	private float x;
	private float y;
	public ImageComponent(String uuid){
		this.uuid = uuid;
		try{
			image = ImageIO.read(Algorithms.getAsset("No Image.png"));
		}catch(Exception exception){
			exception.printStackTrace();
			JOptionPane.showMessageDialog(null, "Default image failed to load!", "Error", JOptionPane.ERROR_MESSAGE);
			image = null;
		}
	}
	@Override
	public void load(Menu menu, BinaryFile bin, short version){
		switch(version){
			case 0:{
				name = bin.getString();
				if(bin.getBoolean()){
					try{
						image = ImageIO.read(Algorithms.getFile("Menus", menu.getUUID(), uuid+".png"));
					}catch(Exception exception){
						exception.printStackTrace();
						JOptionPane.showMessageDialog(null, "Image component image failed to load!", "Error", JOptionPane.ERROR_MESSAGE);
						image = null;
					}
				}else{
					try{
						image = ImageIO.read(Algorithms.getAsset("No Image.png"));
					}catch(Exception exception){
						exception.printStackTrace();
						JOptionPane.showMessageDialog(null, "Default image failed to load!", "Error", JOptionPane.ERROR_MESSAGE);
						image = null;
					}
				}
				break;
			}
			default:
				throw new RuntimeException();
		}
	}
	@Override
	public void save(Menu menu, BinaryFile bin){
		bin.addStringAllocated(name);
		bin.allocateBytes(1);
		bin.addBoolean(image!=null);
		if(saveImage){
			saveImage = false;
			try{
				ImageIO.write(image, "png", Algorithms.getFile("Menus", menu.getUUID(), uuid+".png"));
			}catch(Exception exception){
				exception.printStackTrace();
				JOptionPane.showMessageDialog(null, "Image component image failed to save!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	@Override
	public int getId(){
		return ID;
	}
	@Override
	public ArrayList<MenuComponentHeirarchy> getChildren(){
		return children;
	}
	@Override
	public void addChild(MenuComponentHeirarchy com){
		children.add(com);
	}
	@Override
	public boolean isCollapsed(){
		return collapsed;
	}
	@Override
	public void setCollapsed(boolean collapsed){
		this.collapsed = collapsed;
	}
	@Override
	public boolean isMousedOver(){
		return mousedOver;
	}
	@Override
	public void setMousedOver(boolean mousedOver){
		this.mousedOver = mousedOver;
	}
	@Override
	public MenuComponentHeirarchy getParent(){
		return parent;
	}
	@Override
	public void removeChild(MenuComponentHeirarchy com){
		children.remove(com);
	}
	@Override
	public void setParent(MenuComponentHeirarchy com){
		parent = com;
	}
	@Override
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			private final JTextField nameInput;
			private BufferedImage image;
			{
				// Builder
				setLayout(new VerticalFlowLayout(0, 5));
				{
					// Name
					nameInput = new JTextField();
					nameInput.setColumns(20);
					nameInput.setText(name);
					add(nameInput);
				}
				{
					// Picture Importer
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					JPanel panel2 = new JPanel();
					panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
					JButton button = new JButton("Select Image");
					panel2.add(button);
					panel.add(panel2, BorderLayout.EAST);
					image = ImageComponent.this.image;
					ImagePanel imagePanel = new ImagePanel(image, 256, 256);
					panel.add(imagePanel, BorderLayout.CENTER);
					add(panel);
					button.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							File file = Algorithms.userChooseImage("Select Image", "Select");
							try{
								image = ImageIO.read(file);
								imagePanel.setImage(image);
							}catch(Exception exception){
								exception.printStackTrace();
								JOptionPane.showMessageDialog(null, "Default image failed to load!", "Error", JOptionPane.ERROR_MESSAGE);
								image = null;
							}
						}
					});
				}
			}
			@Override
			public JComponent getDefaultFocus(){
				return nameInput;
			}
			@Override
			public void build(MenuComponent component){
				ImageComponent c = (ImageComponent)component;
				c.name = nameInput.getText();
				c.image = image;
				c.saveImage = true;
			}
		};
	}
	@Override
	public String getName(){
		return name;
	}
	@Override
	public String toString(){
		return name;
	}
	@Override
	public void move(MenuComponentHeirarchy com, int index){
		children.remove(com);
		children.add(index, com);
	}
	@Override
	public String getUUID(){
		return uuid;
	}
	@Override
	public float getX(){
		return x;
	}
	@Override
	public float getY(){
		return y;
	}
	@Override
	public void setPosition(float x, float y){
		this.x = x;
		this.y = y;
	}
}
