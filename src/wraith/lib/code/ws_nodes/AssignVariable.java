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
import javax.swing.JComponent;
import javax.swing.JLabel;
import wraith.lib.code.FunctionUtils;
import wraith.lib.code.Variable;
import wraith.lib.code.VariableInput;
import wraith.lib.code.WSNode;
import wraith.lib.code.WraithScript;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class AssignVariable implements WSNode{
	private static final int ID = 6;
	private final WraithScript script;
	private String input = "";
	private String output = "";
	private Variable outVar;
	private Object inRaw;
	public AssignVariable(WraithScript script){
		this.script = script;
	}
	@Override
	public void save(BinaryFile bin){
		bin.addStringAllocated(input);
		bin.addStringAllocated(output);
	}
	@Override
	public void load(BinaryFile bin, short version){
		input = bin.getString();
		output = bin.getString();
	}
	@Override
	public int getId(){
		return ID;
	}
	public String getInput(){
		return input;
	}
	public void setInput(String input){
		this.input = input;
	}
	public String getOuput(){
		return output;
	}
	public void setOutput(String output){
		this.output = output;
	}
	@Override
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			private final VariableInput in;
			private final VariableInput out;
			{
				setLayout(new VerticalFlowLayout(5, VerticalFlowLayout.FILL_SPACE));
				JLabel label1 = new JLabel("Set");
				label1.setHorizontalAlignment(JLabel.CENTER);
				add(label1);
				out = new VariableInput(script.getLogic().getLocalVariables());
				out.setValue(output);
				add(out);
				JLabel label2 = new JLabel("To");
				label2.setHorizontalAlignment(JLabel.CENTER);
				add(label2);
				in = new VariableInput(script.getLogic().getLocalVariables());
				in.setValue(input);
				add(in);
			}
			@Override
			public void build(Object component){
				AssignVariable c = (AssignVariable)component;
				c.input = VariableInput.toStorageState(in.getValue());
				c.output = VariableInput.toStorageState(out.getValue());
			}
			@Override
			public JComponent getDefaultFocus(){
				return out;
			}
		};
	}
	@Override
	public void run(){
		if(outVar!=null){
			if(inRaw instanceof Variable){
				outVar.setValue(((Variable)inRaw).getValue());
			}else{
				outVar.setValue(inRaw);
			}
		}
	}
	@Override
	public String getHtml(int in){
		return FunctionUtils.generateHtml("Set "+output+", To "+input, in);
	}
	@Override
	public void initalizeRuntime(){
		inRaw = VariableInput.fromStorageState(input, script);
		outVar = VariableInput.getVariable(output, script);
	}
}
