/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import wraith.lib.gui.Anchor;

/**
 * @author thedudefromci
 */
public class MenuComponentLocationPanel extends JPanel{
	private class ComponentInfo extends JPanel{
		private final ArrayList<ChangeListener> listeners = new ArrayList(1);
		private final int typeId;
		private final JSpinner spinner;
		private ComponentInfo(int typeId){
			this.typeId = typeId;
			spinner = new JSpinner();
			SpinnerModel model = new SpinnerModel(){
				private Number value;
				private String visual;
				{
					if(isPercent()){
						setValue(0.0f);
					}else{
						setValue(0);
					}
				}
				@Override
				public Object getValue(){
					return visual;
				}
				@Override
				public void setValue(Object val){
					if(val instanceof Number){
						if(isPercent()){
							value = (float)val;
							visual = String.format("%.1f", value.floatValue()*100)+"%";
						}else{
							value = (int)val;
							visual = value.toString();
						}
					}else{
						visual = (String)val;
						if(isPercent()){
							value = Float.valueOf(visual.replace("%", ""))/100;
						}else{
							value = Integer.valueOf(visual);
						}
						updateCompPos(value);
					}
					for(ChangeListener listener : listeners){
						listener.stateChanged(new ChangeEvent(this));
					}
				}
				@Override
				public Object getNextValue(){
					if(isPercent()){
						return String.format("%.0f", value.floatValue()*100+1)+"%";
					}else{
						return String.valueOf(value.intValue()+1)+"";
					}
				}
				@Override
				public Object getPreviousValue(){
					if(isPercent()){
						return String.format("%.0f", value.floatValue()*100-1)+"%";
					}else{
						return String.valueOf(value.intValue()-1);
					}
				}
				@Override
				public void addChangeListener(ChangeListener l){
					listeners.add(l);
				}
				@Override
				public void removeChangeListener(ChangeListener l){
					listeners.remove(l);
				}
			};
			spinner.setModel(model);
			JLabel label = new JLabel("");
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setFont(new Font("Courier", Font.PLAIN, 10));
			switch(typeId){
				case 0:
					label.setText("X");
					break;
				case 1:
					label.setText("Y");
					break;
				case 2:
					label.setText("X");
					break;
				case 3:
					label.setText("Y");
					break;
				case 4:
					label.setText("W");
					break;
				case 5:
					label.setText("H");
					break;
			}
			setLayout(new BorderLayout(5, 0));
			add(label, BorderLayout.WEST);
			add(spinner, BorderLayout.CENTER);
		}
		private void updateCompPos(Number value){
			if(selectedComponent==null){
				return;
			}
			Anchor a = selectedComponent.getAnchor();
			switch(typeId){
				case 0:
					a.setParentPosition(value.floatValue(), a.getParentY());
					break;
				case 1:
					a.setParentPosition(a.getParentX(), value.floatValue());
					break;
				case 2:
					a.setChildPosition(value.floatValue(), a.getChildY());
					break;
				case 3:
					a.setChildPosition(a.getChildX(), value.floatValue());
					break;
				case 4:
					a.setSize(value.intValue(), a.getHeight());
					break;
				case 5:
					a.setSize(a.getWidth(), value.intValue());
					break;
			}
			menuEditor.updateAllLayouts();
		}
		private void updateValue(){
			if(selectedComponent==null){
				if(isPercent()){
					spinner.setValue(0.0f);
				}else{
					spinner.setValue(0);
				}
				return;
			}
			switch(typeId){
				case 0:
					spinner.setValue(selectedComponent.getAnchor().getParentX());
					break;
				case 1:
					spinner.setValue(selectedComponent.getAnchor().getParentY());
					break;
				case 2:
					spinner.setValue(selectedComponent.getAnchor().getChildX());
					break;
				case 3:
					spinner.setValue(selectedComponent.getAnchor().getChildY());
					break;
				case 4:
					spinner.setValue((int)selectedComponent.getAnchor().getWidth());
					break;
				case 5:
					spinner.setValue((int)selectedComponent.getAnchor().getHeight());
					break;
			}
			for(ChangeListener listener : listeners){
				listener.stateChanged(new ChangeEvent(this));
			}
		}
		private boolean isPercent(){
			return typeId<4;
		}
	}
	private final ComponentInfo[] infoBits = new ComponentInfo[6];
	private MenuComponent selectedComponent;
	private MenuEditor menuEditor;
	public MenuComponentLocationPanel(){
		infoBits[0] = new ComponentInfo(0);
		infoBits[1] = new ComponentInfo(1);
		infoBits[2] = new ComponentInfo(2);
		infoBits[3] = new ComponentInfo(3);
		infoBits[4] = new ComponentInfo(4);
		infoBits[5] = new ComponentInfo(5);
		setLayout(new BorderLayout(10, 0));
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(0, 1, 5, 5));
		panel1.add(new JLabel("Location:"));
		panel1.add(new JLabel("Anchor:"));
		panel1.add(new JLabel("Size:"));
		add(panel1, BorderLayout.WEST);
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(0, 2, 5, 5));
		add(panel2, BorderLayout.CENTER);
		panel2.add(infoBits[0]);
		panel2.add(infoBits[1]);
		panel2.add(infoBits[2]);
		panel2.add(infoBits[3]);
		panel2.add(infoBits[4]);
		panel2.add(infoBits[5]);
	}
	public void updateComponent(){
		for(ComponentInfo info : infoBits){
			info.updateValue();
		}
	}
	public void setComponent(MenuComponent component){
		selectedComponent = component;
		updateComponent();
	}
	public void setMenuEditor(MenuEditor menuEditor){
		this.menuEditor = menuEditor;
	}
}
