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
import javax.swing.JComboBox;
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
	private static final int EQUALS = 0;
	private static final int GREATER_THAN = 1;
	private static final int LESS_THAN = 2;
	private static final int GREATER_THAN_OR_EQUAL = 3;
	private static final int LESS_THAN_OR_EQUAL = 4;
	private static final int NOT_EQUAL = 5;
	private final WraithScript script;
	private String input1 = "";
	private String input2 = "";
	private String output = "";
	private Variable outVar;
	private Object inRaw1;
	private Object inRaw2;
	private int compareType;
	public Compare(WraithScript script){
		this.script = script;
	}
	@Override
	public void save(BinaryFile bin){
		bin.addStringAllocated(input1);
		bin.addStringAllocated(input2);
		bin.addStringAllocated(output);
		bin.allocateBytes(1);
		bin.addByte((byte)compareType);
	}
	@Override
	public void load(BinaryFile bin, short version){
		input1 = bin.getString();
		input2 = bin.getString();
		output = bin.getString();
		compareType = bin.getByte();
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
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			private final VariableInput in1;
			private final VariableInput in2;
			private final VariableInput out;
			private final JComboBox compareType;
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
				JPanel panel = new JPanel();
				panel.setLayout(new InverseBorderLayout(5));
				in1 = new VariableInput(script.getLogic().getLocalVariables());
				in1.setValue(input1);
				panel.add(in1);
				in2 = new VariableInput(script.getLogic().getLocalVariables());
				in2.setValue(input2);
				panel.add(in2);
				compareType = new JComboBox(new String[]{
					"==", ">", "<", ">=", "=<", "!="
				});
				panel.add(compareType);
				add(panel);
			}
			@Override
			public void build(Object component){
				Compare c = (Compare)component;
				c.input1 = VariableInput.toStorageState(in1.getValue());
				c.input2 = VariableInput.toStorageState(in2.getValue());
				c.output = VariableInput.toStorageState(out.getValue());
				c.compareType = compareType.getSelectedIndex();
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
			switch(compareType){
				case EQUALS:{
					outVar.setValue(VariableInput.areValuesEqual(inRaw1, inRaw2));
					break;
				}
				case GREATER_THAN:{
					outVar.setValue(VariableInput.isGreaterThan(inRaw1, inRaw2));
					break;
				}
				case GREATER_THAN_OR_EQUAL:{
					outVar.setValue(VariableInput.isGreaterThan(inRaw1, inRaw2)||VariableInput.areValuesEqual(inRaw1, inRaw2));
					break;
				}
				case LESS_THAN:{
					outVar.setValue(!VariableInput.isGreaterThan(inRaw1, inRaw2));
					break;
				}
				case LESS_THAN_OR_EQUAL:{
					outVar.setValue(!VariableInput.isGreaterThan(inRaw1, inRaw2)||VariableInput.areValuesEqual(inRaw1, inRaw2));
					break;
				}
				case NOT_EQUAL:{
					outVar.setValue(!VariableInput.areValuesEqual(inRaw1, inRaw2));
					break;
				}
			}
		}
	}
	@Override
	public String getHtml(int in){
		String com;
		switch(compareType){
			case EQUALS:{
				com = " equal to ";
				break;
			}
			case GREATER_THAN:{
				com = " greater than ";
				break;
			}
			case GREATER_THAN_OR_EQUAL:{
				com = " greater than or equal to ";
				break;
			}
			case LESS_THAN:{
				com = " less than ";
				break;
			}
			case LESS_THAN_OR_EQUAL:{
				com = " less than or equal to ";
				break;
			}
			case NOT_EQUAL:{
				com = " not equal to ";
				break;
			}
			default:
				com = "  ";
		}
		return FunctionUtils.generateHtml("Is '"+input1+"'"+com+"'"+input2+"'? Set '"+output+"' to the result.", in);
	}
	@Override
	public void initalizeRuntime(){
		inRaw1 = VariableInput.fromStorageState(input1, script);
		inRaw2 = VariableInput.fromStorageState(input2, script);
		outVar = VariableInput.getVariable(output, script);
	}
}
