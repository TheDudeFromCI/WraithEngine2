/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code.languages;

import build.games.wraithaven.code.Snipet;
import build.games.wraithaven.gui.MenuComponentDialog;
import build.games.wraithaven.util.InputDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import wraith.lib.code.WSNode;
import wraith.lib.code.ws_nodes.CommentLine;

/**
 * @author thedudefromci
 */
public class NodeLineLogic extends JList{
	private final WraithScriptLogic logic;
	private final Snipet script;
	public NodeLineLogic(Snipet script, WraithScriptLogic logic){
		this.script = script;
		this.logic = logic;
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setCellRenderer(new DefaultListCellRenderer(){
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
				JLabel label = new JLabel();
				if(value instanceof String){
					label.setText("<html><font color=red>[]</font></html>");
				}else if(value instanceof CommentLine){
					label.setText("<html><font color=green>"+value.toString()+"</font></html>");
				}else{
					label.setText("<html><font color=red>[] </font><font color=black>"+value.toString()+"</font></html>");
				}
				label.setOpaque(true);
				if(isSelected){
					label.setBackground(Color.blue);
				}
				return label;
			}
		});
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getButton()!=MouseEvent.BUTTON3){
					return;
				}
				int[] sel = getSelectedIndices();
				if(sel.length==0){
					return;
				}
				JPopupMenu menu = new JPopupMenu();
				{
					// Build menu
					{
						// New
						JMenu menu2 = new JMenu("New");
						attemptAddNode(menu2, "Comment Line", CommentLine.class, sel[0]);
						menu.add(menu2);
					}
					{
						// Edit
						JMenuItem item = new JMenuItem("Edit");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								attemptEditNode(logic.getNodes().get(sel[0]));
							}
						});
						menu.add(item);
					}
					{
						// Delete
						JMenuItem item = new JMenuItem("Delete");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete these lines?", "Confirm Delete",
									JOptionPane.WARNING_MESSAGE);
								if(response!=JOptionPane.YES_OPTION){
									return;
								}
								ArrayList<WSNode> nodes = logic.getNodes();
								for(int i = sel.length-1; i>=0; i--){
									nodes.remove(sel[i]);
								}
								updateModel();
								script.save();
							}
						});
						menu.add(item);
					}
				}
				menu.show(NodeLineLogic.this, e.getX(), e.getY());
			}
		});
		updateModel();
	}
	private void attemptEditNode(WSNode node){
		InputDialog dialog = new InputDialog();
		MenuComponentDialog builder = node.getCreationDialog();
		dialog.setData(builder);
		dialog.setOkButton(true);
		dialog.setCancelButton(true);
		dialog.setDefaultFocus(builder.getDefaultFocus());
		dialog.setTitle("Edit Line");
		dialog.show();
		if(dialog.getResponse()!=InputDialog.OK){
			return;
		}
		builder.build(node);
		script.save();
		repaint();
	}
	private void attemptAddNode(JMenu menu, String name, Class<? extends WSNode> node, int insertIndex){
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				try{
					WSNode com = node.getDeclaredConstructor().newInstance();
					InputDialog dialog = new InputDialog();
					MenuComponentDialog builder = com.getCreationDialog();
					dialog.setData(builder);
					dialog.setOkButton(true);
					dialog.setCancelButton(true);
					dialog.setDefaultFocus(builder.getDefaultFocus());
					dialog.setTitle(name);
					dialog.show();
					if(dialog.getResponse()!=InputDialog.OK){
						return;
					}
					builder.build(com);
					logic.getNodes().add(insertIndex, com);
					updateModel();
					script.save();
				}catch(NoSuchMethodException|SecurityException|InstantiationException|IllegalAccessException|IllegalArgumentException
					|InvocationTargetException ex){
					// I'm sure this will never get called. But whatever. :P
					ex.printStackTrace();
				}
			}
		});
		menu.add(item);
	}
	private void updateModel(){
		ArrayList<WSNode> nodes = logic.getNodes();
		Object[] lines = new Object[nodes.size()+1];
		nodes.toArray(lines);
		lines[nodes.size()] = "";
		setModel(new DefaultComboBoxModel(lines));
	}
}
