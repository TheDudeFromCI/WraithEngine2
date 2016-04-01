/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import build.games.wraithaven.gui.MenuComponentDialog;
import build.games.wraithaven.util.InputDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
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
import wraith.lib.code.Indenter;
import wraith.lib.code.WSNode;
import wraith.lib.code.WraithScriptLogic;
import wraith.lib.code.ws_nodes.BeginFunction;
import wraith.lib.code.ws_nodes.BlankLine;
import wraith.lib.code.ws_nodes.CommentLine;
import wraith.lib.code.ws_nodes.End;
import wraith.lib.code.ws_nodes.PrintToConsole;
import wraith.lib.code.ws_nodes.Return;

/**
 * @author thedudefromci
 */
public class NodeLineLogic extends JList{
	private static final int INDENT_SIZE = 3;
	private final WraithScriptLogic logic;
	private final Snipet script;
	private int[] indents;
	public NodeLineLogic(Snipet script, WraithScriptLogic logic){
		this.script = script;
		this.logic = logic;
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setCellRenderer(new DefaultListCellRenderer(){
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
				JLabel label = new JLabel();
				String indent;
				boolean enabled;
				if(indents[index]>=0){
					StringBuilder sb = new StringBuilder(indents[index]);
					for(int i = 0; i<indents[index]; i++){
						sb.append(' ');
					}
					indent = sb.toString();
					enabled = true;
				}else{
					indent = "";
					enabled = false;
				}
				if(value instanceof String){
					label.setText("<html><pre><font face=\"Courier\" size=\"3\" color=red>"+indent+"[]</font></pre></html>");
				}else if(value instanceof CommentLine){
					label.setText("<html><pre><font face=\"Courier\" size=\"3\" color=green>"+indent+value.toString()+"</font></pre></html>");
				}else if(value instanceof BlankLine){
					label.setText("<html><pre><font face=\"Courier\" size=\"3\"> </font></pre></html>");
				}else{
					label.setText("<html><pre><font face=\"Courier\" size=\"3\" color="+(enabled?"black":"gray")+">"+indent+value.toString()
						+"</font></pre></html>");
				}
				if(isSelected){
					label.setBackground(new Color(0, 230, 230));
				}
				label.setOpaque(true);
				label.setPreferredSize(new Dimension(label.getPreferredSize().width, 15));
				return label;
			}
		});
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getButton()!=MouseEvent.BUTTON3){
					return;
				}
				int[] sel1 = getSelectedIndices();
				if(sel1.length==0){
					int index = locationToIndex(new Point(e.getX(), e.getY()));
					sel1 = new int[]{
						index
					};
					setSelectedIndices(sel1);
				}
				final int[] sel = sel1;
				JPopupMenu menu = new JPopupMenu();
				{
					// Build menu
					{
						// New
						JMenu menu2 = new JMenu("New");
						attemptAddNode(menu2, "Syntax/Comment Line", CommentLine.class, sel[0]);
						attemptAddNode(menu2, "Syntax/Blank Line", BlankLine.class, sel[0]);
						attemptAddNode(menu2, "Syntax/Return", Return.class, sel[0]);
						attemptAddNode(menu2, "Syntax/Begin Function", BeginFunction.class, sel[0]);
						attemptAddNode(menu2, "Syntax/End Function", End.class, sel[0]);
						attemptAddNode(menu2, "Debug/Print to Console", PrintToConsole.class, sel[0]);
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
								setSelectedIndex(sel[0]);
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
		String[] nameParts = name.split("/");
		JMenu m = menu;
		full:for(int i = 0; i<nameParts.length-1; i++){
			for(Component comp : m.getMenuComponents()){
				if(comp instanceof JMenu&&((JMenu)comp).getText().equals(nameParts[i])){
					m = (JMenu)comp;
					continue full;
				}
			}
			// If we're here, then the sub menu we need doesn't exist.
			JMenu m2 = new JMenu(nameParts[i]);
			m.add(m2);
			m = m2;
		}
		JMenuItem item = new JMenuItem(nameParts[nameParts.length-1]);
		item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				try{
					WSNode com = node.getDeclaredConstructor().newInstance();
					MenuComponentDialog builder = com.getCreationDialog();
					if(builder!=null){
						InputDialog dialog = new InputDialog();
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
					}
					logic.getNodes().add(insertIndex, com);
					updateModel();
					script.save();
					setSelectedIndex(insertIndex);
				}catch(NoSuchMethodException|SecurityException|InstantiationException|IllegalAccessException|IllegalArgumentException
					|InvocationTargetException ex){
					// I'm sure this will never get called. But whatever. :P
					ex.printStackTrace();
				}
			}
		});
		m.add(item);
	}
	private void updateModel(){
		ArrayList<WSNode> nodes = logic.getNodes();
		Object[] lines = new Object[nodes.size()+1];
		indents = new int[lines.length];
		int i = 0;
		int a = 0;
		for(WSNode node : nodes){
			if(node instanceof End){
				i -= INDENT_SIZE;
			}
			indents[a++] = i;
			if(node instanceof Indenter){
				i += INDENT_SIZE;
			}
		}
		nodes.toArray(lines);
		lines[nodes.size()] = "";
		setModel(new DefaultComboBoxModel(lines));
	}
}