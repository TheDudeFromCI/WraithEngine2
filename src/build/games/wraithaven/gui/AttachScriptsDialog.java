/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import build.games.wraithaven.code.Snipet;
import build.games.wraithaven.code.SnipetList;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class AttachScriptsDialog extends JPanel{
	private final JList leftList;
	private final JList rightList;
	private final ArrayList<Snipet> allSnipets = new ArrayList(1);
	private final ArrayList<Snipet> usedSnipets = new ArrayList(1);
	public AttachScriptsDialog(MenuComponent component){
		setLayout(new LayoutManager(){
			@Override
			public void addLayoutComponent(String name, Component comp){}
			@Override
			public void removeLayoutComponent(Component comp){}
			@Override
			public Dimension preferredLayoutSize(Container parent){
				int compCount = parent.getComponentCount();
				Component left = compCount>=1?parent.getComponent(0):null;
				Component right = compCount>=2?parent.getComponent(1):null;
				Component center = compCount>=3?parent.getComponent(2):null;
				int width = 0;
				int height = 0;
				Dimension pref;
				if(left!=null){
					pref = left.getPreferredSize();
					width += pref.width;
					height = Math.max(height, pref.height);
				}
				if(center!=null){
					pref = center.getPreferredSize();
					width += pref.width;
					height = Math.max(height, pref.height);
				}
				if(right!=null){
					pref = right.getPreferredSize();
					width += pref.width;
					height = Math.max(height, pref.height);
				}
				return new Dimension(width, height);
			}
			@Override
			public Dimension minimumLayoutSize(Container parent){
				return new Dimension(0, 0);
			}
			@Override
			public void layoutContainer(Container parent){
				System.out.println("Updated layout.");
				Dimension p = preferredLayoutSize(parent);
				int width = (int)p.getWidth();
				int height = (int)p.getHeight();
				int extra = width;
				int compCount = parent.getComponentCount();
				Component left = compCount>=1?parent.getComponent(0):null;
				Component right = compCount>=2?parent.getComponent(1):null;
				Component center = compCount>=3?parent.getComponent(2):null;
				if(center!=null&&center.isVisible()){
					Dimension pref = center.getPreferredSize();
					center.setBounds((width-(int)pref.getWidth())/2, 0, (int)pref.getWidth(), height);
					System.out.println(center.getBounds().toString());
					extra -= (int)pref.getWidth();
				}
				extra /= 2;
				if(left!=null&&left.isVisible()){
					left.setBounds(0, 0, extra, height);
					System.out.println(left.getBounds().toString());
				}
				if(right!=null&&right.isVisible()){
					right.setBounds(width-extra, 0, extra, height);
					System.out.println(right.getBounds().toString());
				}
			}
		});
		SnipetList.load(allSnipets);
		{
			// Find used scripts.
			for(String uuid : component.getScripts()){
				Snipet script = null;
				for(Snipet snip : allSnipets){
					if(snip.getUuid().equals(uuid)){
						script = snip;
						break;
					}
				}
				if(script==null){ // This should never really be called...
					continue;
				}
				allSnipets.remove(script);
				usedSnipets.add(script);
			}
		}
		{
			// Build
			{
				// Left side
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				JLabel label = new JLabel("Unused");
				panel.add(label, BorderLayout.NORTH);
				leftList = new JList();
				leftList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				JScrollPane scroll = new JScrollPane(leftList);
				panel.add(scroll, BorderLayout.CENTER);
				add(panel);
			}
			{
				// Right side.
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				JLabel label = new JLabel("Used");
				panel.add(label, BorderLayout.NORTH);
				rightList = new JList();
				rightList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				JScrollPane scroll = new JScrollPane(rightList);
				panel.add(scroll, BorderLayout.CENTER);
				add(panel);
			}
			{
				// Center
				JPanel panel = new JPanel();
				panel.setLayout(new GridBagLayout());
				JPanel panel2 = new JPanel();
				panel2.setLayout(new GridLayout(2, 1, 5, 0));
				BufferedImage arrow1 = null;
				BufferedImage arrow2 = null;
				try{
					arrow1 = ImageIO.read(Algorithms.getAsset("BlueArrow1.png"));
					arrow2 = ImageIO.read(Algorithms.getAsset("BlueArrow2.png"));
				}catch(Exception exception){
					exception.printStackTrace();
					JOptionPane.showMessageDialog(null, "There has been an error loading some of the assets for this window.", "Error",
						JOptionPane.ERROR_MESSAGE);
				}
				JButton moveRight = new JButton(new ImageIcon(arrow1));
				panel2.add(moveRight);
				JButton moveLeft = new JButton(new ImageIcon(arrow2));
				panel2.add(moveLeft);
				panel.add(panel2);
				add(panel);
				moveLeft.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						int[] sel = rightList.getSelectedIndices();
						if(sel.length==0){
							return;
						}
						for(int i = sel.length-1; i>=0; i--){
							allSnipets.add(usedSnipets.remove(sel[i]));
						}
						updateLists();
					}
				});
				moveRight.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						int[] sel = leftList.getSelectedIndices();
						if(sel.length==0){
							return;
						}
						for(int i = sel.length-1; i>=0; i--){
							usedSnipets.add(allSnipets.remove(sel[i]));
						}
						updateLists();
					}
				});
			}
		}
		updateLists();
	}
	private void updateLists(){
		leftList.setModel(new DefaultComboBoxModel(allSnipets.toArray()));
		rightList.setModel(new DefaultComboBoxModel(usedSnipets.toArray()));
		revalidate();
		repaint();
	}
	public JComponent getFocus(){
		return leftList;
	}
	public void compile(MenuComponent component){
		ArrayList<String> scripts = component.getScripts();
		scripts.clear();
		for(Snipet snip : usedSnipets){
			scripts.add(snip.getUuid());
		}
	}
}
