/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code.ws_nodes;

import build.games.wraithaven.gui.MenuComponentDialog;
import wraith.lib.code.FunctionLineCaller;
import wraith.lib.code.FunctionUtils;
import wraith.lib.code.Indenter;
import wraith.lib.code.Unindenter;
import wraith.lib.code.WSNode;
import wraith.lib.code.WraithScript;
import wraith.lib.code.WraithScriptLogic;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class Else implements WSNode, Indenter, Unindenter, FunctionLineCaller{
	private static final int ID = 10;
	private final WraithScript script;
	private IfStatement parentStatement;
	private int line;
	private int returnType;
	public Else(WraithScript script){
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
	public void run(){
		if(!parentStatement.didRun()){
			returnType = script.getLogic().run(line, this);
		}
	}
	@Override
	public void initalizeRuntime(){
		parentStatement = getParent();
		int i = 0;
		for(WSNode node : script.getLogic().getNodes()){
			if(node==this){
				line = i+1;
				return;
			}
			i++;
		}
		line = -1;
	}
	@Override
	public String getHtml(int in){
		if(getParent()==null){
			return FunctionUtils.generateHtml("Else", "gray", in);
		}
		return FunctionUtils.generateHtml("Else", in);
	}
	private IfStatement getParent(){
		int i = 0;
		for(WSNode node : script.getLogic().getNodes()){
			if(node==this){
				i--;
				if(i<0){
					return null;
				}
				break;
			}
			if(node instanceof Unindenter&&((Unindenter)node).shouldUnindent()){
				i--;
				if(i<0){
					i = 0; // No negative indents.
				}
			}
			if(node instanceof Indenter&&((Indenter)node).shouldIndent()){
				i++;
			}
		}
		int goalIndent = i;
		i = 0;
		IfStatement parent = null;
		for(WSNode node : script.getLogic().getNodes()){
			if(node==this){
				i--;
				break;
			}
			if(node instanceof Unindenter&&((Unindenter)node).shouldUnindent()){
				i--;
				if(i<goalIndent){
					// All parents before this point were in another branch. (Lol, another castle.)
					parent = null;
				}
				if(i<0){
					i = 0; // No negative indents.
				}
			}
			if(node instanceof IfStatement&&i==goalIndent){ // Hey we found a parent!
				parent = (IfStatement)node;
			}
			if(node!=this&&node instanceof Else&&i==goalIndent){ // Wait, nevermind. Already claimed.
				parent = null;
			}
			if(node instanceof Indenter&&((Indenter)node).shouldIndent()){
				i++;
			}
		}
		return parent;
	}
	@Override
	public boolean shouldIndent(){
		return true;
	}
	@Override
	public boolean shouldUnindent(){
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
