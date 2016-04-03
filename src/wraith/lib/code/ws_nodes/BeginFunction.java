/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code.ws_nodes;

import build.games.wraithaven.gui.MenuComponentDialog;
import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import wraith.lib.code.FunctionUtils;
import wraith.lib.code.Indenter;
import wraith.lib.code.WSNode;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class BeginFunction implements WSNode, Indenter{
	private static final int ID = 4;
	private String name;
	private String uuid;
	@Override
	public void save(BinaryFile bin){
		bin.addStringAllocated(uuid);
		bin.addStringAllocated(name);
	}
	@Override
	public void load(BinaryFile bin, short version){
		uuid = bin.getString();
		name = bin.getString();
	}
	@Override
	public int getId(){
		return ID;
	}
	@Override
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			private final JTextField name;
			{
				setLayout(new VerticalFlowLayout(5, VerticalFlowLayout.FILL_SPACE));
				{
					// Name
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					JLabel label = new JLabel("Name: ");
					panel.add(label, BorderLayout.WEST);
					name = new JTextField();
					panel.add(name, BorderLayout.CENTER);
					add(panel);
				}
			}
			@Override
			public void build(Object component){
				BeginFunction func = (BeginFunction)component;
				if(func.uuid==null){ // New Function
					func.uuid = Algorithms.randomUUID();
				}
				func.name = name.getText();
			}
			@Override
			public JComponent getDefaultFocus(){
				return name;
			}
		};
	}
	@Override
	public void run(){}
	@Override
	public String getHtml(int in){
		return FunctionUtils.generateHtml("Function \""+name+"\"()", in);
	}
	@Override
	public void initalizeRuntime(){}
	public String getUUID(){
		return uuid;
	}
	public String getName(){
		return name;
	}
	@Override
	public String toString(){
		return name==null?uuid:name;
	}
}
