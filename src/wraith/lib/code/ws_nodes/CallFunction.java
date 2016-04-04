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
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import wraith.lib.code.FunctionUtils;
import wraith.lib.code.WSNode;
import wraith.lib.code.WraithScript;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class CallFunction implements WSNode{
	private static final int ID = 8;
	private final WraithScript script;
	private String function;
	private int line;
	public CallFunction(WraithScript script){
		this.script = script;
	}
	@Override
	public void save(BinaryFile bin){
		bin.allocateBytes(1);
		bin.addBoolean(function!=null);
		bin.addStringAllocated(function);
	}
	@Override
	public void load(BinaryFile bin, short version){
		if(bin.getBoolean()){
			function = bin.getString();
		}
	}
	@Override
	public int getId(){
		return ID;
	}
	@Override
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			private final JComboBox functionList;
			{
				setLayout(new VerticalFlowLayout(5, VerticalFlowLayout.FILL_SPACE));
				{
					// Name
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					JLabel label = new JLabel("Function: ");
					panel.add(label, BorderLayout.WEST);
					functionList = new JComboBox();
					{
						// Get function list.
						ArrayList<BeginFunction> functions = new ArrayList(4);
						for(WSNode node : script.getLogic().getNodes()){
							if(node instanceof BeginFunction){
								functions.add((BeginFunction)node);
							}
						}
						functionList.setModel(new DefaultComboBoxModel(functions.toArray()));
					}
					panel.add(functionList, BorderLayout.CENTER);
					add(panel);
				}
			}
			@Override
			public void build(Object component){
				CallFunction call = (CallFunction)component;
				call.function = functionList.getSelectedItem()==null?null:((BeginFunction)functionList.getSelectedItem()).getUUID();
			}
			@Override
			public JComponent getDefaultFocus(){
				return functionList;
			}
		};
	}
	@Override
	public void run(){
		if(line!=-1){
			script.getLogic().run(line);
		}
	}
	@Override
	public String getHtml(int in){
		String functionName = "";
		for(WSNode node : script.getLogic().getNodes()){
			if(node instanceof BeginFunction){
				if(((BeginFunction)node).getUUID().equals(function)){
					functionName = ((BeginFunction)node).toString();
					break;
				}
			}
		}
		return FunctionUtils.generateHtml("Call Function(\""+functionName+"\")", in);
	}
	@Override
	public void initalizeRuntime(){
		int i = 0;
		for(WSNode node : script.getLogic().getNodes()){
			if(node instanceof BeginFunction){
				if(((BeginFunction)node).getUUID().equals(function)){
					line = i+1;
					return;
				}
			}
			i++;
		}
		line = -1;
	}
}
