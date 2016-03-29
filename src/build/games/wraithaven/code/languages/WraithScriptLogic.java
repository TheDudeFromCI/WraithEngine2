/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code.languages;

import java.util.ArrayList;
import wraith.lib.code.WSNode;
import wraith.lib.code.ws_nodes.CommentLine;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class WraithScriptLogic{
	private final ArrayList<WSNode> nodes = new ArrayList(32);
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
			default:
				throw new RuntimeException("Unknown node id! '"+id+"'");
		}
	}
	public void save(BinaryFile bin){
		bin.allocateBytes(4+nodes.size()*4);
		bin.addInt(nodes.size());
		for(WSNode node : nodes){
			bin.addInt(node.getId());
			node.save(bin);
		}
	}
	public ArrayList<WSNode> getNodes(){
		return nodes;
	}
}
