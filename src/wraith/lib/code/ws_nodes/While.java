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
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import wraith.lib.code.FunctionLineCaller;
import wraith.lib.code.FunctionUtils;
import wraith.lib.code.Indenter;
import wraith.lib.code.VariableInput;
import wraith.lib.code.WSNode;
import wraith.lib.code.WraithScript;
import wraith.lib.code.WraithScriptLogic;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class While implements WSNode, Indenter, FunctionLineCaller{
	private static final int ID = 11;
	private static final int EQUALS = 0;
	private static final int GREATER_THAN = 1;
	private static final int LESS_THAN = 2;
	private static final int GREATER_THAN_OR_EQUAL = 3;
	private static final int LESS_THAN_OR_EQUAL = 4;
	private static final int NOT_EQUAL = 5;
	private final WraithScript script;
	private String input1 = "";
	private String input2 = "";
	private Object inRaw1;
	private Object inRaw2;
	private int compareType;
	private int line;
	private int returnType;
	public While(WraithScript script){
		this.script = script;
	}
	@Override
	public void save(BinaryFile bin){
		bin.addStringAllocated(input1);
		bin.addStringAllocated(input2);
		bin.allocateBytes(1);
		bin.addByte((byte)compareType);
	}
	@Override
	public void load(BinaryFile bin, short version){
		input1 = bin.getString();
		input2 = bin.getString();
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
	@Override
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			private final VariableInput in1;
			private final VariableInput in2;
			private final JComboBox compareType;
			{
				setLayout(new VerticalFlowLayout(5, VerticalFlowLayout.FILL_SPACE));
				JLabel label2 = new JLabel("If");
				label2.setHorizontalAlignment(JLabel.CENTER);
				add(label2);
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(3, 1, 0, 0));
				in1 = new VariableInput(script.getLogic().getLocalVariables());
				in1.setValue(input1);
				panel.add(in1);
				compareType = new JComboBox(new String[]{
					"is equal to", "is greater than", "is less than", "is greater than or equal to", "is less than or equal to", "is not equal to"
				});
				panel.add(compareType);
				in2 = new VariableInput(script.getLogic().getLocalVariables());
				in2.setValue(input2);
				panel.add(in2);
				add(panel);
			}
			@Override
			public void build(Object component){
				While c = (While)component;
				c.input1 = VariableInput.toStorageState(in1.getValue());
				c.input2 = VariableInput.toStorageState(in2.getValue());
				c.compareType = compareType.getSelectedIndex();
			}
			@Override
			public JComponent getDefaultFocus(){
				return in1;
			}
		};
	}
	@Override
	public void run(){
		if(line==-1){
			return;
		}
		while(isTrue()){
			returnType = script.getLogic().run(line, this);
			if(returnType!=WraithScriptLogic.NORMAL_FUNCTION_END){
				break;
			}
		}
	}
	private boolean isTrue(){
		switch(compareType){
			case EQUALS:{
				return VariableInput.areValuesEqual(inRaw1, inRaw2);
			}
			case GREATER_THAN:{
				return VariableInput.isGreaterThan(inRaw1, inRaw2);
			}
			case GREATER_THAN_OR_EQUAL:{
				return VariableInput.isGreaterThan(inRaw1, inRaw2)||VariableInput.areValuesEqual(inRaw1, inRaw2);
			}
			case LESS_THAN:{
				return !VariableInput.isGreaterThan(inRaw1, inRaw2);
			}
			case LESS_THAN_OR_EQUAL:{
				return !VariableInput.isGreaterThan(inRaw1, inRaw2)||VariableInput.areValuesEqual(inRaw1, inRaw2);
			}
			case NOT_EQUAL:{
				return !VariableInput.areValuesEqual(inRaw1, inRaw2);
			}
			default:
				return false;
		}
	}
	@Override
	public String getHtml(int in){
		String com;
		switch(compareType){
			case EQUALS:{
				com = " is equal to ";
				break;
			}
			case GREATER_THAN:{
				com = " is greater than ";
				break;
			}
			case GREATER_THAN_OR_EQUAL:{
				com = " is greater than or equal to ";
				break;
			}
			case LESS_THAN:{
				com = " is less than ";
				break;
			}
			case LESS_THAN_OR_EQUAL:{
				com = " is less than or equal to ";
				break;
			}
			case NOT_EQUAL:{
				com = " is not equal to ";
				break;
			}
			default:
				com = " ";
		}
		return FunctionUtils.generateHtml("While '"+input1+"'"+com+"'"+input2+"':", in);
	}
	@Override
	public void initalizeRuntime(){
		int indent = script.getLogic().getIndent(this);
		inRaw1 = VariableInput.fromStorageState(input1, script);
		inRaw2 = VariableInput.fromStorageState(input2, script);
		int i = 0;
		for(WSNode node : script.getLogic().getNodes()){
			if(node==this){
				line = i+1;
				if(script.getLogic().getIndent(line)!=indent+1){
					// There is no code inside of this indent.
					line = -1;
				}
				return;
			}
			i++;
		}
		line = -1;
	}
	@Override
	public boolean shouldIndent(){
		return true;
	}
	@Override
	public boolean shouldTerminate(int endType){
		return endType!=WraithScriptLogic.NORMAL_FUNCTION_END;
	}
	@Override
	public int getReturnType(){
		return returnType;
	}
}
