/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code;

import java.util.ArrayList;
import wraith.lib.code.ws_nodes.AssignVariable;
import wraith.lib.code.ws_nodes.BeginFunction;
import wraith.lib.code.ws_nodes.BlankLine;
import wraith.lib.code.ws_nodes.CommentLine;
import wraith.lib.code.ws_nodes.End;
import wraith.lib.code.ws_nodes.PrintToConsole;
import wraith.lib.code.ws_nodes.Return;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class WraithScriptLogic{
	private final ArrayList<WSNode> nodes = new ArrayList(32);
	private final ArrayList<LocalVariable> localVariables = new ArrayList(4);
	public void load(BinaryFile bin, short version){
		switch(version){
			case 0:{
				int count = bin.getInt();
				nodes.ensureCapacity(count);
				for(int i = 0; i<count; i++){
					WSNode node = getNodeInstance(bin.getInt());
					nodes.add(node);
					node.load(bin, version);
				}
				int localVarCount = bin.getInt();
				localVariables.ensureCapacity(localVarCount);
				for(int i = 0; i<localVarCount; i++){
					LocalVariable var = new LocalVariable(bin.getString());
					var.setName(bin.getString());
					localVariables.add(var);
				}
				break;
			}
			default:
				throw new RuntimeException("Unknown file version! '"+version+"'");
		}
	}
	private WSNode getNodeInstance(int id){
		switch(id){
			case 0:
				return new CommentLine();
			case 1:
				return new BlankLine();
			case 2:
				return new PrintToConsole();
			case 3:
				return new Return();
			case 4:
				return new BeginFunction();
			case 5:
				return new End();
			case 6:
				return new AssignVariable();
			default:
				throw new RuntimeException("Unknown node id! '"+id+"'");
		}
	}
	public void save(BinaryFile bin){
		bin.allocateBytes(8+nodes.size()*4);
		bin.addInt(nodes.size());
		for(WSNode node : nodes){
			bin.addInt(node.getId());
			node.save(bin);
		}
		bin.addInt(localVariables.size());
		for(LocalVariable var : localVariables){
			bin.addStringAllocated(var.getUUID());
			bin.addStringAllocated(var.getName());
		}
	}
	public ArrayList<WSNode> getNodes(){
		return nodes;
	}
	public void run(){
		for(WSNode node : nodes){
			node.run();
		}
	}
	public ArrayList<LocalVariable> getLocalVariables(){
		return localVariables;
	}
}
