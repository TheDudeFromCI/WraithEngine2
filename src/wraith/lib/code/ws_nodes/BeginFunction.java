/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code.ws_nodes;

import build.games.wraithaven.gui.MenuComponentDialog;
import wraith.lib.code.Indenter;
import wraith.lib.code.WSNode;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class BeginFunction implements WSNode, Indenter{
	private static final int ID = 4;
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
	public String toString(){
		return "Function()";
	}
}
