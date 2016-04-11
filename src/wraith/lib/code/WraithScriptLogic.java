/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code;

import java.util.ArrayList;
import wraith.lib.code.ws_nodes.*;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class WraithScriptLogic implements FunctionLineCaller{
	public static final int NORMAL_FUNCTION_END = 0;
	public static final int RETURN_FUNCTION_END = 1;
	public static final int BREAK_FUNCTION_END = 2;
	public static final int CONTINUE_FUNCTION_END = 3;
	private final ArrayList<WSNode> nodes = new ArrayList(32);
	private final ArrayList<Variable> localVariables = new ArrayList(4);
	private final WraithScript script;
	private int[] indents;
	public WraithScriptLogic(WraithScript script){
		this.script = script;
	}
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
					Variable var = new Variable(bin.getString());
					var.setName(bin.getString());
					localVariables.add(var);
				}
				break;
			}
			default:
				throw new RuntimeException("Unknown file version! '"+version+"'");
		}
		// Load indents.
		// As indents are only used by the runner, we know the node list won't be edited.
		// Therefore, the indents will not change. So we can load these here.
		indents = new int[nodes.size()];
		int i, a;
		i = 0;
		for(a = 0; a<nodes.size(); a++){
			if(nodes.get(a) instanceof Unindenter&&((Unindenter)nodes.get(a)).shouldUnindent()){
				i--;
				if(i<0){
					i = 0; // No negative indents.
				}
			}
			indents[a] = i;
			if(nodes.get(a) instanceof Indenter&&((Indenter)nodes.get(a)).shouldIndent()){
				i++;
			}
		}
	}
	private WSNode getNodeInstance(int id){
		switch(id){
			case 0:
				return new CommentLine();
			case 1:
				return new BlankLine();
			case 2:
				return new PrintToConsole(script);
			case 3:
				return new Return();
			case 4:
				return new BeginFunction();
			case 5:
				return new End(script);
			case 6:
				return new AssignVariable(script);
			case 7:
				return new Compare(script);
			case 8:
				return new CallFunction(script);
			case 9:
				return new IfStatement(script);
			case 10:
				return new Else(script);
			case 11:
				return new While(script);
			case 12:
				return new Break();
			case 13:
				return new Continue();
			case 14:
				return new DoWhile(script);
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
		for(Variable var : localVariables){
			bin.addStringAllocated(var.getUUID());
			bin.addStringAllocated(var.getName());
		}
	}
	public ArrayList<WSNode> getNodes(){
		return nodes;
	}
	public void run(){
		run(0, this);
	}
	/**
	 * Run script at a specific line. Only runs it's current indention.
	 *
	 * @param line
	 *            - The line to run the script from.
	 * @param caller
	 *            - The WSNode that is calling this line to run.
	 * @return The conditions of the return, or function break. 0 = Normal function end. 1 = Return. 2 = Break.
	 */
	public int run(int line, FunctionLineCaller caller){
		int indent = indents[line];
		WSNode n;
		for(; line<nodes.size(); line++){
			if(indents[line]==indent){
				n = nodes.get(line);
				n.run();
				if(n instanceof FunctionLineCaller){
					int returnType = ((FunctionLineCaller)n).getReturnType();
					if(((FunctionLineCaller)n).shouldTerminate(returnType)){
						return returnType;
					}
				}
				if(n instanceof Return){
					return RETURN_FUNCTION_END;
				}
				if(n instanceof Break){
					return BREAK_FUNCTION_END;
				}
				if(n instanceof Continue){
					return CONTINUE_FUNCTION_END;
				}
			}else if(indents[line]<indent){
				return NORMAL_FUNCTION_END;
			}
		}
		return NORMAL_FUNCTION_END;
	}
	public ArrayList<Variable> getLocalVariables(){
		return localVariables;
	}
	public WraithScript getWraithScript(){
		return script;
	}
	@Override
	public boolean shouldTerminate(int endType){
		return false;
	}
	@Override
	public int getReturnType(){
		return RETURN_FUNCTION_END;
	}
	public int getIndent(WSNode node){
		return getIndent(nodes.indexOf(node));
	}
	public int getIndent(int line){
		return indents[line];
	}
}
