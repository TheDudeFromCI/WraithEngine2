/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui.components;

import build.games.wraithaven.gui.ComponentLayout;
import build.games.wraithaven.gui.Menu;
import build.games.wraithaven.gui.MenuComponent;
import build.games.wraithaven.gui.MenuComponentDialog;
import build.games.wraithaven.gui.MenuComponentHeirarchy;
import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import wraith.lib.gui.Anchor;
import wraith.lib.gui.MigObjectLocation;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class MigLayout implements ComponentLayout{
	private static final int ID = 0;
	private int[] cols = new int[1];
	private int[] rows = new int[1];
	private MigObjectLocation[] locs = new MigObjectLocation[0];
	@Override
	public void loadLayout(Menu menu, BinaryFile bin, short version){
		switch(version){
			case 0:
			case 1:{
				cols = new int[bin.getInt()];
				for(int i = 0; i<cols.length; i++){
					cols[i] = bin.getInt();
				}
				rows = new int[bin.getInt()];
				for(int i = 0; i<rows.length; i++){
					rows[i] = bin.getInt();
				}
				locs = new MigObjectLocation[bin.getInt()];
				for(int i = 0; i<locs.length; i++){
					locs[i] = new MigObjectLocation(0, 0, 0, 0);
					locs[i].x = bin.getInt();
					locs[i].y = bin.getInt();
					locs[i].w = bin.getInt();
					locs[i].h = bin.getInt();
				}
				break;
			}
			default:
				throw new RuntimeException();
		}
	}
	@Override
	public void saveLayout(Menu menu, BinaryFile bin){
		bin.allocateBytes(3*4+cols.length*4+rows.length*4+locs.length*4*4);
		bin.addInt(cols.length);
		for(int x : cols){
			bin.addInt(x);
		}
		bin.addInt(rows.length);
		for(int x : rows){
			bin.addInt(x);
		}
		bin.addInt(locs.length);
		for(MigObjectLocation l : locs){
			bin.addInt(l.x);
			bin.addInt(l.y);
			bin.addInt(l.w);
			bin.addInt(l.h);
		}
	}
	@Override
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			private final MigBuilder migBuilder;
			private int[] cols;
			private int[] rows;
			private MigObjectLocation[] locs;
			{
				// Builder
				setLayout(new VerticalFlowLayout(5));
				{
					// Preview
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					cols = MigLayout.this.cols;
					rows = MigLayout.this.rows;
					locs = MigLayout.this.locs;
					migBuilder = new MigBuilder(rows, cols, locs);
					panel.add(migBuilder, BorderLayout.CENTER);
					// Column and Row sizes.
					JPanel panel2 = new JPanel();
					panel2.setLayout(new GridLayout(0, 1, 0, 5));
					buildColEditor(panel2, "Cols", 0);
					buildColEditor(panel2, "Rows", 1);
					panel.add(panel2, BorderLayout.EAST);
					add(panel);
				}
				{
					// Child Locations
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					JTable table = new JTable();
					String[] tableColNames = new String[]{
						"#", "Col", "Row", "Width", "Height"
					};
					{
						// Table
						Object[][] data = buildLocTableData();
						updateTableModel(table, data, tableColNames, 2);
						JScrollPane scroll = new JScrollPane(table);
						scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
						scroll.setPreferredSize(new Dimension(400, 120));
						panel.add(scroll, BorderLayout.CENTER);
					}
					{
						// Buttons
						JPanel panel2 = new JPanel();
						panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
						JButton newCol = new JButton("Add");
						newCol.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								locs = Arrays.copyOf(locs, locs.length+1);
								locs[locs.length-1] = new MigObjectLocation(0, 0, 1, 1);
								migBuilder.setLocs(locs);
								Object[][] data = buildLocTableData();
								updateTableModel(table, data, tableColNames, 2);
							}
						});
						panel2.add(newCol);
						JButton delCol = new JButton("Del");
						delCol.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								int selectedIndex = table.getSelectedRow();
								if(selectedIndex==-1){
									return;
								}
								MigObjectLocation[] x = new MigObjectLocation[locs.length-1];
								{
									// Delete row
									int j = 0;
									for(int i = 0; i<locs.length; i++){
										if(i==selectedIndex){
											continue;
										}
										x[j++] = locs[i];
									}
								}
								locs = x;
								migBuilder.setLocs(locs);
								Object[][] data = buildLocTableData();
								updateTableModel(table, data, tableColNames, 2);
							}
						});
						panel2.add(delCol);
						panel.add(panel2, BorderLayout.SOUTH);
					}
					add(panel);
				}
			}
			private Object[][] buildLocTableData(){
				Object[][] data = new Object[locs.length][5];
				for(int i = 0; i<locs.length; i++){
					data[i][0] = i+1;
					data[i][1] = locs[i].x+1;
					data[i][2] = locs[i].y+1;
					data[i][3] = locs[i].w;
					data[i][4] = locs[i].h;
				}
				return data;
			}
			private void buildColEditor(JPanel panel2, String name, int colType){
				int[] x = colType==0?cols:rows;
				JPanel panel3 = new JPanel();
				panel3.setLayout(new BorderLayout());
				JLabel label = new JLabel(name);
				panel3.add(label, BorderLayout.NORTH);
				JTable table = new JTable();
				Object[][] tableData = buildTableData(x, colType);
				String[] tableColNames = new String[]{
					"#", "Size"
				};
				updateTableModel(table, tableData, tableColNames, colType);
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
						int[] x = colType==0?cols:rows;
						int[] x2 = Arrays.copyOf(x, x.length+1);
						x2[x.length] = 0;
						if(colType==0){
							cols = x2;
						}else{
							rows = x2;
						}
						migBuilder.setSizes(cols, rows);
						Object[][] tableData = buildTableData(x2, colType);
						String[] tableColNames = new String[]{
							"#", "Size"
						};
						updateTableModel(table, tableData, tableColNames, colType);
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
						int[] x = colType==0?cols:rows;
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
						if(colType==0){
							cols = x2;
						}else{
							rows = x2;
						}
						migBuilder.setSizes(cols, rows);
						Object[][] tableData = buildTableData(x2, colType);
						String[] tableColNames = new String[]{
							"#", "Size"
						};
						updateTableModel(table, tableData, tableColNames, colType);
					}
				});
				panel4.add(delCol);
				panel3.add(panel4, BorderLayout.SOUTH);
				panel2.add(panel3);
			}
			private void updateTableModel(JTable table, Object[][] data, String[] names, int colType){
				table.setModel(new DefaultTableModel(data, names){
					@Override
					public boolean isCellEditable(int row, int column){
						return column>0;
					}
				});
				table.getTableHeader().setReorderingAllowed(false);
				if(colType==0||colType==1){
					table.getColumnModel().getColumn(0).setResizable(false);
					table.getColumnModel().getColumn(0).setPreferredWidth(20);
				}
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
						switch(colType){
							case 0:{
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
								break;
							}
							case 1:{
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
								break;
							}
							case 2:{
								int col = e.getColumn();
								String s = (String)table.getModel().getValueAt(row, col);
								int v;
								try{
									v = Integer.valueOf(s);
									if(v<1){
										throw new NumberFormatException();
									}
								}catch(NumberFormatException exception){
									v = 1;
									table.getModel().setValueAt(v, row, col);
								}
								switch(col){
									case 1:
										locs[row].x = v-1;
										break;
									case 2:
										locs[row].y = v-1;
										break;
									case 3:
										locs[row].w = v;
										break;
									case 4:
										locs[row].h = v;
										break;
									default:
										break;
								}
								migBuilder.repaint();
								break;
							}
							default:
								break;
						}
					}
				});
			}
			private Object[][] buildTableData(int[] col, int colType){
				switch(colType){
					case 0:
					case 1:{
						Object[][] data = new Object[col.length][2];
						for(int i = 0; i<col.length; i++){
							data[i][0] = i+1;
							data[i][1] = col[i];
						}
						return data;
					}
					default:
						return null;
				}
			}
			@Override
			public JComponent getDefaultFocus(){
				return null;
			}
			@Override
			public void build(Object component){
				MigLayout c = (MigLayout)component;
				c.cols = cols;
				c.rows = rows;
				c.locs = locs;
			}
		};
	}
	@Override
	public void updateLayout(Anchor anchor, ArrayList<MenuComponentHeirarchy> children){
		int i = -1;
		for(MenuComponentHeirarchy h : children){
			i++;
			if(!(h instanceof MenuComponent)){
				continue;
			}
			Anchor a = ((MenuComponent)h).getAnchor();
			if(i>=locs.length){
				setChildPos(anchor, a, 0, 0, 0, 0);
				continue;
			}
			int[] x = new int[2];
			int[] y = new int[2];
			MigObjectLocation l = locs[i];
			getPos(cols, l.x, l.w, Math.round(anchor.getWidth()), x);
			getPos(rows, l.y, l.h, Math.round(anchor.getHeight()), y);
			setChildPos(anchor, a, x[0], y[0], x[1], y[1]);
		}
	}
	private void setChildPos(Anchor anchor, Anchor a, float x, float y, float w, float h){
		a.setChildPosition(0, 0);
		a.setParentPosition(x/anchor.getWidth(), y/anchor.getHeight());
		a.setSize(w, h);
	}
	private void getPos(int[] x, int col, int s, int size, int[] out){
		int extraSpaceCols = countExtraSpace(x, size);
		int w;
		out[0] = 0;
		out[1] = 0;
		for(int i = 0; i<x.length; i++){
			w = x[i]==0?extraSpaceCols:x[i];
			if(i<col){
				out[0] += w;
			}else{
				s--;
				out[1] += w;
				if(s==0){
					break;
				}
			}
		}
	}
	private int countExtraSpace(int[] x, int total){
		int y = 0;
		int a = 0;
		for(int z : x){
			if(z==0){
				a++;
				continue;
			}
			y += z;
		}
		return (total-y)/a;
	}
	@Override
	public int getId(){
		return ID;
	}
}
