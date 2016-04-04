/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code.ws_nodes;

import build.games.wraithaven.gui.MenuComponentDialog;
import wraith.lib.code.FunctionUtils;
import wraith.lib.code.Indenter;
import wraith.lib.code.Unindenter;
import wraith.lib.code.WSNode;
import wraith.lib.code.WraithScript;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class End implements WSNode, Unindenter{
	private static final int ID = 5;
	private final WraithScript script;
	public End(WraithScript script){
		this.script = script;
	}
	@Override
	public void save(BinaryFile bin){}
	@Override
	public void load(BinaryFile bin, short version){}
	@Override
	public int getId(){
		return ID;
	}
	@Override
	public MenuComponentDialog getCreationDialog(){
		return null;
	}
	@Override
	public void run(){}
	@Override
	public String getHtml(int in){
		boolean dead = false;
		int i = 0;
		for(WSNode node : script.getLogic().getNodes()){
			if(node instanceof Unindenter){
				i--;
				if(node==this){
					dead = i<0;
					break;
				}
				if(i<0){
					i = 0; // No negative indents.
				}
			}
			if(node instanceof Indenter){
				i++;
			}
		}
		if(dead){
			return FunctionUtils.generateHtml("End", "gray", 0);
		}
		return FunctionUtils.generateHtml("End", in);
	}
	@Override
	public void initalizeRuntime(){}
	@Override
	public boolean shouldUnindent(){
		return true;
	}
}
