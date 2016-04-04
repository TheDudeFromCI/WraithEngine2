/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import build.games.wraithaven.gui.MenuComponent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
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
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import wraith.lib.util.Algorithms;
import wraith.lib.util.InverseBorderLayout;

/**
 * @author thedudefromci
 */
public class AttachScriptsDialog extends JPanel{
	private final JList leftList;
	private final JList rightList;
	private final ArrayList<Snipet> allSnipets = new ArrayList(1);
	private final ArrayList<Snipet> usedSnipets = new ArrayList(1);
	public AttachScriptsDialog(MenuComponent component){
		setLayout(new InverseBorderLayout());
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
				panel2.setLayout(new GridLayout(4, 1, 5, 0));
				ImageIcon[] images = new ImageIcon[12];
				try{
					BufferedImage image = ImageIO.read(Algorithms.getAsset("BlueArrow.png"));
					for(int i = 0; i<images.length; i++){
						images[i] =
							new ImageIcon(image.getSubimage(image.getWidth()/images.length*i, 0, image.getWidth()/images.length, image.getHeight())
								.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
					}
				}catch(Exception exception){
					exception.printStackTrace();
					JOptionPane.showMessageDialog(null, "There has been an error loading some of the assets for this window.", "Error",
						JOptionPane.ERROR_MESSAGE);
				}
				JButton moveUp = new JButton(images[3]);
				moveUp.setPressedIcon(images[4]);
				moveUp.setRolloverIcon(images[5]);
				moveUp.setOpaque(false);
				moveUp.setBorderPainted(false);
				moveUp.setContentAreaFilled(false);
				moveUp.setFocusPainted(false);
				panel2.add(moveUp);
				JButton moveRight = new JButton(images[0]);
				moveRight.setPressedIcon(images[1]);
				moveRight.setRolloverIcon(images[2]);
				moveRight.setOpaque(false);
				moveRight.setBorderPainted(false);
				moveRight.setContentAreaFilled(false);
				moveRight.setFocusPainted(false);
				panel2.add(moveRight);
				JButton moveLeft = new JButton(images[6]);
				moveLeft.setPressedIcon(images[7]);
				moveLeft.setRolloverIcon(images[8]);
				moveLeft.setOpaque(false);
				moveLeft.setBorderPainted(false);
				moveLeft.setContentAreaFilled(false);
				moveLeft.setFocusPainted(false);
				panel2.add(moveLeft);
				JButton moveDown = new JButton(images[9]);
				moveDown.setPressedIcon(images[10]);
				moveDown.setRolloverIcon(images[11]);
				moveDown.setOpaque(false);
				moveDown.setBorderPainted(false);
				moveDown.setContentAreaFilled(false);
				moveDown.setFocusPainted(false);
				panel2.add(moveDown);
				panel.add(panel2);
				add(panel);
				moveUp.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						int[] sel = rightList.getSelectedIndices();
						if(sel.length==0){
							return;
						}
						for(int i : sel){
							if(i==0){
								break;
							}
							usedSnipets.add(i-1, usedSnipets.remove(i));
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
				moveLeft.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						int[] sel = rightList.getSelectedIndices();
						if(sel.length<=0){
							return;
						}
						for(int i = sel.length-1; i>=0; i--){
							allSnipets.add(usedSnipets.remove(sel[i]));
						}
						updateLists();
					}
				});
				moveDown.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						int[] sel = rightList.getSelectedIndices();
						if(sel.length==0){
							return;
						}
						for(int i = sel.length-1; i>=0; i--){
							if(sel[i]>=usedSnipets.size()-1){
								break;
							}
							usedSnipets.add(sel[i]+1, usedSnipets.remove(sel[i]));
						}
						updateLists();
					}
				});
			}
			{
				// Bottom
				JTextArea des = new JTextArea();
				des.setEditable(false);
				des.setLineWrap(true);
				des.setWrapStyleWord(true);
				JScrollPane scroll = new JScrollPane(des);
				scroll.setPreferredSize(new Dimension(10, 50));
				scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				add(scroll);
				rightList.addListSelectionListener(new ListSelectionListener(){
					@Override
					public void valueChanged(ListSelectionEvent e){
						int[] sel = rightList.getSelectedIndices();
						if(sel.length!=1){
							des.setText("Description:\n");
							return;
						}
						des.setText("Description:\n"+usedSnipets.get(sel[0]).getDescription());
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
