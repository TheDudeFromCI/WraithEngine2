/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui.components;

import build.games.wraithaven.gui.DefaultLayout;
import build.games.wraithaven.gui.MenuComponent;
import build.games.wraithaven.gui.MenuComponentDialog;
import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * @author thedudefromci
 */
public class MigLayout extends DefaultLayout{
	private static final int ID = 2;
	private int[] cols = new int[1];
	private int[] rows = new int[1];
	public MigLayout(String uuid){
		super(uuid);
	}
	@Override
	public int getId(){
		return ID;
	}
	@Override
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			private final JTextField nameInput;
			private int[] cols;
			private int[] rows;
			private MigBuilder migBuilder;
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
					// Preview
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					cols = MigLayout.this.cols;
					rows = MigLayout.this.rows;
					migBuilder = new MigBuilder(rows, cols);
					panel.add(migBuilder, BorderLayout.CENTER);
					// Column and Row sizes.
					JPanel panel2 = new JPanel();
					panel2.setLayout(new GridLayout(0, 1, 0, 5));
					buildColEditor(panel2, "Cols", true);
					buildColEditor(panel2, "Rows", false);
					panel.add(panel2, BorderLayout.EAST);
					add(panel);
				}
			}
			private void buildColEditor(JPanel panel2, String name, boolean col){
				int[] x = col?cols:rows;
				JPanel panel3 = new JPanel();
				panel3.setLayout(new BorderLayout());
				JLabel label = new JLabel(name);
				panel3.add(label, BorderLayout.NORTH);
				JTable table = new JTable();
				Object[][] tableData = buildTableData(x);
				updateTableModel(table, tableData, col);
				JScrollPane scroll = new JScrollPane(table);
				scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				scroll.setPreferredSize(new Dimension(80, 120));
				panel3.add(scroll, BorderLayout.CENTER);
				JPanel panel4 = new JPanel();
				panel4.setLayout(new GridLayout(1, 2, 0, 5));
				JButton newCol = new JButton("Add");
				newCol.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						int[] x = col?cols:rows;
						int[] x2 = Arrays.copyOf(x, x.length+1);
						x2[x.length] = 0;
						if(col){
							cols = x2;
						}else{
							rows = x2;
						}
						migBuilder.setSizes(cols, rows);
						Object[][] tableData = buildTableData(x2);
						updateTableModel(table, tableData, col);
					}
				});
				panel4.add(newCol);
				JButton delCol = new JButton("Del");
				delCol.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						int selectedIndex = table.getSelectedRow();
						if(selectedIndex==-1){
							return;
						}
						int[] x = col?cols:rows;
						int[] x2 = new int[x.length-1];
						{
							// Delete row
							int j = 0;
							for(int i = 0; i<x.length; i++){
								if(i==selectedIndex){
									continue;
								}
								x2[j++] = x[i];
							}
						}
						if(col){
							cols = x2;
						}else{
							rows = x2;
						}
						migBuilder.setSizes(cols, rows);
						Object[][] tableData = buildTableData(x2);
						updateTableModel(table, tableData, col);
					}
				});
				panel4.add(delCol);
				panel3.add(panel4, BorderLayout.SOUTH);
				panel2.add(panel3);
			}
			private void updateTableModel(JTable table, Object[][] data, boolean col){
				String[] tableColNames = new String[]{
					"#", "Size"
				};
				table.setModel(new DefaultTableModel(data, tableColNames){
					@Override
					public boolean isCellEditable(int row, int column){
						return column==1;
					}
				});
				table.getTableHeader().setReorderingAllowed(false);
				table.getColumnModel().getColumn(0).setResizable(false);
				table.getColumnModel().getColumn(0).setPreferredWidth(20);
				table.setColumnSelectionAllowed(false);
				table.setRowSelectionAllowed(false);
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				table.setGridColor(Color.black);
				table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
						int column){
						JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						if(isSelected||hasFocus){
							label.setBackground(new Color(190, 190, 190));
						}else if(row%2==0){
							label.setBackground(new Color(245, 245, 245));
						}else{
							label.setBackground(new Color(225, 225, 225));
						}
						return label;
					}
				});
				table.getModel().addTableModelListener(new TableModelListener(){
					@Override
					public void tableChanged(TableModelEvent e){
						int row = e.getFirstRow();
						if(col){
							String s = (String)table.getModel().getValueAt(row, 1);
							try{
								cols[row] = Integer.valueOf(s);
								if(row<0){
									throw new NumberFormatException();
								}
							}catch(NumberFormatException exception){
								cols[row] = 0;
								table.getModel().setValueAt(0, row, 1);
							}
							migBuilder.repaint();
						}else{
							String s = (String)table.getModel().getValueAt(row, 1);
							try{
								rows[row] = Integer.valueOf(s);
								if(row<0){
									throw new NumberFormatException();
								}
							}catch(NumberFormatException exception){
								rows[row] = 0;
								table.getModel().setValueAt(0, row, 1);
							}
							migBuilder.repaint();
						}
					}
				});
			}
			private Object[][] buildTableData(int[] col){
				Object[][] data = new Object[col.length][2];
				for(int i = 0; i<col.length; i++){
					data[i][0] = i+1;
					data[i][1] = col[i];
				}
				return data;
			}
			@Override
			public JComponent getDefaultFocus(){
				return nameInput;
			}
			@Override
			public void build(MenuComponent component){
				MigLayout c = (MigLayout)component;
				c.name = nameInput.getText();
				c.cols = cols;
				c.rows = rows;
			}
		};
	}
	@Override
	public void updateLayout(){}
}
