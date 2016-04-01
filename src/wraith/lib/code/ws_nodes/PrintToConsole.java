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
import wraith.lib.code.FunctionUtils;
import wraith.lib.code.VariableInput;
import wraith.lib.code.WSNode;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class PrintToConsole implements WSNode{
	private static final int ID = 2;
	private String comment = "";
	@Override
	public void save(BinaryFile bin){
		bin.addStringAllocated(comment);
	}
	@Override
	public void load(BinaryFile bin, short version){
		comment = bin.getString();
	}
	@Override
	public int getId(){
		return ID;
	}
	public String getComment(){
		return comment;
	}
	public void setComment(String comment){
		this.comment = comment;
	}
	@Override
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			private final VariableInput input;
			{
				setLayout(new VerticalFlowLayout(5));
				input = new VariableInput();
				input.setValue(comment);
				add(input);
			}
			@Override
			public void build(Object component){
				PrintToConsole c = (PrintToConsole)component;
				Object val = input.getValue();
				if(val instanceof String){
					c.comment = "\""+val.toString()+"\"";
				}else if(val instanceof Number){
					c.comment = val.toString();
				}else{
					c.comment = "";
				}
			}
			@Override
			public JComponent getDefaultFocus(){
				return input;
			}
		};
	}
	@Override
	public void run(){
		System.out.println(comment);
	}
	@Override
	public String getHtml(int in){
		return FunctionUtils.generateHtml("Print To Console("+comment+")", in);
	}
}
