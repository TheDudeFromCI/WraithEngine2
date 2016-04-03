/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code.ws_nodes;

import build.games.wraithaven.code.NodeLineLogic;
import build.games.wraithaven.gui.MenuComponentDialog;
import build.games.wraithaven.util.VerticalFlowLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import wraith.lib.code.FunctionUtils;
import wraith.lib.code.LocalVariable;
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
	private String input = "";
	private String output = "";
	private Variable outVar;
	private Variable inVar;
	private Object inRaw;
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
	public void setOutputUuid(String output){
		this.output = output;
	}
	@Override
	public MenuComponentDialog getCreationDialog(NodeLineLogic logic){
		return new MenuComponentDialog(){
			private final VariableInput in;
			private final VariableInput out;
			{
				setLayout(new VerticalFlowLayout(5, VerticalFlowLayout.FILL_SPACE));
				JLabel label1 = new JLabel("Set");
				label1.setHorizontalAlignment(JLabel.CENTER);
				add(label1);
				out = new VariableInput(logic.getLocalVariables());
				out.setValue(output);
				add(out);
				JLabel label2 = new JLabel("To");
				label2.setHorizontalAlignment(JLabel.CENTER);
				add(label2);
				in = new VariableInput(logic.getLocalVariables());
				in.setValue(input);
				add(in);
			}
			@Override
			public void build(Object component){
				AssignVariable c = (AssignVariable)component;
				{
					// Input
					Object val = in.getValue();
					if(val instanceof String){
						if(((String)val).startsWith("@")){ // Is Variable
							c.input = val.toString();
						}else{
							c.input = "\""+val.toString()+"\"";
						}
					}else if(val instanceof Number){
						c.input = val.toString();
					}else{
						c.input = "";
					}
				}
				{
					// Output
					Object val = out.getValue();
					if(val instanceof String){
						if(((String)val).startsWith("@")){ // Is Variable
							c.output = val.toString();
						}else{
							c.output = "\""+val.toString()+"\"";
						}
					}else if(val instanceof Number){
						c.output = val.toString();
					}else{
						c.output = "";
					}
				}
			}
			@Override
			public JComponent getDefaultFocus(){
				return in;
			}
		};
	}
	@Override
	public void run(){
		if(outVar!=null){
			if(inVar!=null){
				outVar.setValue(inVar.getValue());
			}else{
				outVar.setValue(inRaw);
			}
		}
	}
	@Override
	public String getHtml(int in){
		return FunctionUtils.generateHtml("Set "+output+" = "+input, in);
	}
	@Override
	public void initalizeRuntime(WraithScript wraithScript){
		if(input.startsWith("@")){ // Is variable?
			input = input.substring(1);
			for(LocalVariable var : wraithScript.getLogic().getLocalVariables()){
				if(var.getName().equals(input)){
					inVar = var;
					break;
				}
			}
		}else // Assign raw input.
		{
			if(input.startsWith("\"")){ // Text
				inRaw = input;
			}else{ // Number
				try{
					inRaw = Integer.valueOf(input);
				}catch(Exception exception){
					try{
						inRaw = Float.valueOf(input);
					}catch(Exception exception1){}
				}
			}
		}
		if(output.startsWith("@")){ // Is variable?
			output = output.substring(1);
			for(LocalVariable var : wraithScript.getLogic().getLocalVariables()){
				if(var.getName().equals(output)){
					outVar = var;
					break;
				}
			}
		}
	}
}
