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
import javax.swing.JPanel;
import wraith.lib.code.FunctionUtils;
import wraith.lib.code.Variable;
import wraith.lib.code.VariableInput;
import wraith.lib.code.WSNode;
import wraith.lib.code.WraithScript;
import wraith.lib.util.BinaryFile;
import wraith.lib.util.InverseBorderLayout;

/**
 * @author thedudefromci
 */
public class Compare implements WSNode{
	private static final int ID = 7;
	private String input1 = "";
	private String input2 = "";
	private String output = "";
	private Variable outVar;
	private Object inRaw1;
	private Object inRaw2;
	@Override
	public void save(BinaryFile bin){
		bin.addStringAllocated(input1);
		bin.addStringAllocated(input2);
		bin.addStringAllocated(output);
	}
	@Override
	public void load(BinaryFile bin, short version){
		input1 = bin.getString();
		input2 = bin.getString();
		output = bin.getString();
	}
	@Override
	public int getId(){
		return ID;
	}
	public String getInput1(){
		return input1;
	}
	public String getInput2(){
		return input2;
	}
	public void setInput1(String input){
		this.input1 = input;
	}
	public void setInput2(String input){
		this.input2 = input;
	}
	public String getOuput(){
		return output;
	}
	public void setOutput(String output){
		this.output = output;
	}
	@Override
	public MenuComponentDialog getCreationDialog(NodeLineLogic logic){
		return new MenuComponentDialog(){
			private final VariableInput in1;
			private final VariableInput in2;
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
				JPanel panel = new JPanel();
				panel.setLayout(new InverseBorderLayout(4));
				in1 = new VariableInput(logic.getLocalVariables());
				in1.setValue(input1);
				panel.add(in1);
				in2 = new VariableInput(logic.getLocalVariables());
				in2.setValue(input2);
				panel.add(in2);
				JLabel label3 = new JLabel("==");
				panel.add(label3);
				add(panel);
			}
			@Override
			public void build(Object component){
				Compare c = (Compare)component;
				c.input1 = VariableInput.toStorageState(in1.getValue());
				c.input2 = VariableInput.toStorageState(in2.getValue());
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
			if(inRaw1==null||inRaw2==null){
				outVar.setValue(inRaw1==inRaw2);
			}else{
				Object i1, i2;
				if(inRaw1 instanceof Variable){
					i1 = ((Variable)inRaw1).getValue();
				}else{
					i1 = inRaw1;
				}
				if(inRaw2 instanceof Variable){
					i2 = ((Variable)inRaw2).getValue();
				}else{
					i2 = inRaw2;
				}
				outVar.setValue(i1.equals(i2));
			}
		}
	}
	@Override
	public String getHtml(int in){
		return FunctionUtils.generateHtml("Set "+output+" = "+input1+" equals "+input2, in);
	}
	@Override
	public void initalizeRuntime(WraithScript wraithScript){
		inRaw1 = VariableInput.fromStorageState(input1, wraithScript);
		inRaw2 = VariableInput.fromStorageState(input2, wraithScript);
		outVar = VariableInput.getVariable(output, wraithScript);
	}
}
