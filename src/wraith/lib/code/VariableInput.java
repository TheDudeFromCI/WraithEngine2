/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * @author thedudefromci
 */
public class VariableInput extends JPanel{
	private final MouseAdapter mouseAdapter;
	private JComponent inputType;
	public VariableInput(){
		mouseAdapter = new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getButton()!=MouseEvent.BUTTON3){
					return;
				}
				JPopupMenu menu = new JPopupMenu();
				{
					// Build menu
					{
						// Nothing
						JMenuItem item = new JMenuItem("Change to Nothing");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								setAsNull();
							}
						});
						menu.add(item);
						if(inputType instanceof JLabel){
							item.setEnabled(false);
						}
					}
					{
						// Text
						JMenuItem item = new JMenuItem("Change to Text");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								setAsTextField();
							}
						});
						menu.add(item);
						if(inputType instanceof JTextField){
							item.setEnabled(false);
						}
					}
					{
						// Number
						JMenuItem item = new JMenuItem("Change to Number");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								setAsNumber();
							}
						});
						menu.add(item);
						if(inputType instanceof JSpinner){
							item.setEnabled(false);
						}
					}
				}
				menu.show(inputType, e.getX(), e.getY());
			}
		};
		setLayout(new BorderLayout());
		setAsNull();
	}
	private void setAsNull(){
		inputType = new JLabel("Nothing");
		updateInputType();
	}
	private void setAsTextField(){
		inputType = new JTextField();
		((JTextField)inputType).setColumns(20);
		updateInputType();
	}
	private void setAsNumber(){
		inputType = new JSpinner();
		((JSpinner)inputType).setModel(new SpinnerNumberModel(0, null, null, 1));
		((JSpinner)inputType).setPreferredSize(new Dimension(75, 20));
		updateInputType();
	}
	private void updateInputType(){
		inputType.addMouseListener(mouseAdapter);
		removeAll();
		add(inputType, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	public Object getValue(){
		if(inputType instanceof JTextField){
			return ((JTextField)inputType).getText();
		}
		if(inputType instanceof JSpinner){
			return ((JSpinner)inputType).getValue();
		}
		return null;
	}
	public void setValue(String value){
		if(value.startsWith("\"")){ // Is text?
			setAsTextField();
			((JTextField)inputType).setText(value.substring(1, value.length()-1));
			return;
		}
		try{ // Is number?
			int a = Integer.valueOf(value);
			setAsNumber();
			((JSpinner)inputType).setValue(a);
			return;
		}catch(Exception exception){
			try{
				float a = Float.valueOf(value);
				setAsNumber();
				((JSpinner)inputType).setValue(a);
				return;
			}catch(Exception exception1){}
		}
		// It's null, then.
		setAsNull();
	}
}
