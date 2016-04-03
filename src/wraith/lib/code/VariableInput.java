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
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
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
	public static String toStorageState(Object val){
		if(val instanceof String){
			if(((String)val).startsWith("@")){ // Is Variable
				return val.toString();
			}else{
				return "\""+val.toString()+"\"";
			}
		}else if(val instanceof Number){
			return val.toString();
		}else if(val instanceof Boolean){
			return (boolean)val?"True":"False";
		}else{
			// Null, or no value.
			return "Nothing";
		}
	}
	public static Variable getVariable(String line, WraithScript script){
		if(line.startsWith("@")){
			line = line.substring(1);
			for(LocalVariable var : script.getLogic().getLocalVariables()){
				if(var.getName().equals(line)){
					return var;
				}
			}
		}
		return null;
	}
	public static Object fromStorageState(String line, WraithScript script){
		if(line.startsWith("@")){
			return getVariable(line, script);
		}
		if(line.startsWith("\"")){ // Text
			return line.substring(1, line.length()-1);
		}
		if(line.equals("True")){
			return true;
		}
		if(line.equals("False")){
			return false;
		}
		try{
			return Integer.valueOf(line);
		}catch(Exception exception){
			try{
				return Float.valueOf(line);
			}catch(Exception exception1){}
		}
		return null;
	}
	private static final int INPUT_NULL = 0;
	private static final int INPUT_TEXT = 1;
	private static final int INPUT_NUMBER = 2;
	private static final int INPUT_LOCAL_VAR = 3;
	private static final int INPUT_BOOLEAN = 4;
	private final MouseAdapter mouseAdapter;
	private final Object[] localVariables;
	private JComponent inputType;
	private int inputMode;
	public VariableInput(ArrayList<LocalVariable> localVariables){
		this.localVariables = localVariables.toArray();
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
						if(inputMode==INPUT_NULL){
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
						if(inputMode==INPUT_TEXT){
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
						if(inputMode==INPUT_NUMBER){
							item.setEnabled(false);
						}
					}
					{
						// Local Variable
						JMenuItem item = new JMenuItem("Change to Local Variable");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								setAsLocalVariable();
							}
						});
						menu.add(item);
						if(inputMode==INPUT_LOCAL_VAR){
							item.setEnabled(false);
						}
					}
					{
						// Boolean
						JMenuItem item = new JMenuItem("Change to Boolean");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								setAsBoolean();
							}
						});
						menu.add(item);
						if(inputMode==INPUT_BOOLEAN){
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
		inputMode = INPUT_NULL;
		inputType = new JLabel("Nothing");
		updateInputType();
	}
	private void setAsTextField(){
		inputMode = INPUT_TEXT;
		inputType = new JTextField();
		updateInputType();
	}
	private void setAsNumber(){
		inputMode = INPUT_NUMBER;
		inputType = new JSpinner();
		((JSpinner)inputType).setModel(new SpinnerNumberModel(0, null, null, 1));
		updateInputType();
		((JSpinner.DefaultEditor)((JSpinner)inputType).getEditor()).getTextField().addMouseListener(mouseAdapter);
		((JSpinner)inputType).getComponent(0).addMouseListener(mouseAdapter);
		((JSpinner)inputType).getComponent(1).addMouseListener(mouseAdapter);
	}
	private void setAsLocalVariable(){
		inputMode = INPUT_LOCAL_VAR;
		inputType = new JComboBox();
		((JComboBox)inputType).setModel(new DefaultComboBoxModel(localVariables));
		updateInputType();
	}
	private void setAsBoolean(){
		inputMode = INPUT_BOOLEAN;
		inputType = new TrueFalseComponent();
		updateInputType();
	}
	private void updateInputType(){
		inputType.addMouseListener(mouseAdapter);
		inputType.setPreferredSize(new Dimension(240, 20));
		removeAll();
		add(inputType, BorderLayout.CENTER);
		revalidate();
		repaint();
		inputType.requestFocusInWindow();
	}
	public Object getValue(){
		if(inputMode==INPUT_LOCAL_VAR){
			return "@"+((JComboBox)inputType).getSelectedItem();
		}
		if(inputMode==INPUT_NUMBER){
			return ((JSpinner)inputType).getValue();
		}
		if(inputMode==INPUT_TEXT){
			return ((JTextField)inputType).getText();
		}
		if(inputMode==INPUT_BOOLEAN){
			return ((TrueFalseComponent)inputType).getState();
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
		if(value.startsWith("@")){ // Is Local Variable?
			setAsLocalVariable();
			value = value.substring(1);
			int i = 0;
			for(Object var : localVariables){
				if(((Variable)var).getName().equals(value)){
					((JComboBox)inputType).setSelectedIndex(i);
					return;
				}
				i++;
			}
		}
		// It's null, then.
		setAsNull();
	}
}
