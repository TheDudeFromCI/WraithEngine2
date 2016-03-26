/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
					}
					for(ChangeListener listener : listeners){
						listener.stateChanged(new ChangeEvent(this));
					}
				}
				@Override
				public Object getNextValue(){
					if(isPercent()){
						return String.format("%.1f", value.floatValue()*100+1)+"%";
					}else{
						return String.valueOf(value.intValue()+1)+"";
					}
				}
				@Override
				public Object getPreviousValue(){
					if(isPercent()){
						return String.format("%.1f", value.floatValue()*100-1)+"%";
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
			switch(typeId){
				case 0:
					label.setText("PX");
					break;
				case 1:
					label.setText("PY");
					break;
				case 2:
					label.setText("CX");
					break;
				case 3:
					label.setText("CY");
					break;
				case 4:
					label.setText("W");
					break;
				case 5:
					label.setText("H");
					break;
			}
			setLayout(new BorderLayout());
			add(label, BorderLayout.NORTH);
			add(spinner, BorderLayout.CENTER);
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
					spinner.setValue(selectedComponent.getAnchor().getParentY());
					break;
				case 1:
					spinner.setValue(selectedComponent.getAnchor().getParentX());
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
	public MenuComponentLocationPanel(){
		int i = 0;
		infoBits[i] = new ComponentInfo(i++);
		infoBits[i] = new ComponentInfo(i++);
		infoBits[i] = new ComponentInfo(i++);
		infoBits[i] = new ComponentInfo(i++);
		infoBits[i] = new ComponentInfo(i++);
		infoBits[i] = new ComponentInfo(i++);
		setLayout(new GridLayout(0, 4, 5, 5));
		i = 0;
		add(infoBits[i++]);
		add(infoBits[i++]);
		add(infoBits[i++]);
		add(infoBits[i++]);
		add(infoBits[i++]);
		add(infoBits[i++]);
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
}
