/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import build.games.wraithaven.core.window.BuilderTab;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import wraith.lib.code.ScriptEventType;

/**
 * @author thedudefromci
 */
public class SnipetPreviewer extends BuilderTab{
	private final Renderer renderer;
	private final SnipetList list;
	public SnipetPreviewer(){
		super("Script Editor");
		renderer = new Renderer();
		list = new SnipetList(renderer);
		addComponents();
	}
	private void addComponents(){
		setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 10));
		panel.add(renderer, BorderLayout.CENTER);
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		JTextField description = new JTextField();
		description.setEnabled(false);
		description.addCaretListener(new CaretListener(){
			@Override
			public void caretUpdate(CaretEvent e){
				Snipet script = list.getSelectedScript();
				if(script!=null){
					String des = description.getText();
					if(!des.equals(script.getDescription())){
						script.setDescription(des);
						script.save();
						list.save();
					}
				}
			}
		});
		panel1.add(description, BorderLayout.CENTER);
		JComboBox scriptEventType = new JComboBox(ScriptEventType.values());
		scriptEventType.setEnabled(false);
		scriptEventType.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				Snipet script = list.getSelectedScript();
				if(script!=null){
					if(scriptEventType.getSelectedItem()!=script.getEventType()){
						script.setEventType((ScriptEventType)scriptEventType.getSelectedItem());
						script.save();
					}
				}
			}
		});
		list.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				Snipet script = list.getSelectedScript();
				boolean enabled = script!=null;
				if(enabled!=description.isEnabled()){
					description.setEnabled(enabled);
					scriptEventType.setEnabled(enabled);
				}
				if(enabled){
					description.setText(script.getDescription());
					scriptEventType.setSelectedItem(script.getEventType());
				}else{
					description.setText("");
					scriptEventType.setSelectedIndex(0);
				}
			}
		});
		panel1.add(scriptEventType, BorderLayout.EAST);
		panel.add(panel1, BorderLayout.NORTH);
		JScrollPane scroll1 = new JScrollPane(list);
		JScrollPane scroll2 = new JScrollPane(panel);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, scroll1, scroll2);
		add(splitPane, BorderLayout.CENTER);
		splitPane.setResizeWeight(0.2);
	}
	@Override
	public void buildTabs(JMenuBar menuBar){
		// We don't have any menu bar commands yet.
		menuBar.add(new JMenu("Place Holder"));
	}
}
