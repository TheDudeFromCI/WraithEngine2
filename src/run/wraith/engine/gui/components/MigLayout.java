/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.gui.components;

import java.util.ArrayList;
import run.wraith.engine.gui.Layout;
import run.wraith.engine.gui.MenuComponent;
import wraith.lib.gui.Anchor;
import wraith.lib.gui.MigObjectLocation;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class MigLayout implements Layout{
	private int[] cols;
	private int[] rows;
	private MigObjectLocation[] locs;
	@Override
	public void loadLayout(BinaryFile bin, short version){
		switch(version){
			case 1:{
				cols = new int[bin.getInt()];
				for(int i = 0; i<cols.length; i++){
					cols[i] = bin.getInt();
				}
				rows = new int[bin.getInt()];
				for(int i = 0; i<rows.length; i++){
					rows[i] = bin.getInt();
				}
				locs = new MigObjectLocation[bin.getInt()];
				for(int i = 0; i<locs.length; i++){
					locs[i] = new MigObjectLocation(0, 0, 0, 0);
					locs[i].x = bin.getInt();
					locs[i].y = bin.getInt();
					locs[i].w = bin.getInt();
					locs[i].h = bin.getInt();
				}
				break;
			}
			default:
				throw new RuntimeException("Unknown file version! '"+version+"'");
		}
	}
	@Override
	public void updateLayout(Anchor anchor, ArrayList<MenuComponent> children){
		int i = -1;
		for(MenuComponent com : children){
			i++;
			Anchor a = com.getAnchor();
			if(i>=locs.length){
				setChildPos(anchor, a, 0, 0, 0, 0);
				continue;
			}
			int[] x = new int[2];
			int[] y = new int[2];
			MigObjectLocation l = locs[i];
			getPos(cols, l.x, l.w, Math.round(anchor.getWidth()), x);
			getPos(rows, l.y, l.h, Math.round(anchor.getHeight()), y);
			setChildPos(anchor, a, x[0], y[0], x[1], y[1]);
		}
	}
	private void setChildPos(Anchor anchor, Anchor a, float x, float y, float w, float h){
		a.setChildPosition(0, 0);
		a.setParentPosition(x/anchor.getWidth(), y/anchor.getHeight());
		a.setSize(w, h);
	}
	private void getPos(int[] x, int col, int s, int size, int[] out){
		int extraSpaceCols = countExtraSpace(x, size);
		int w;
		out[0] = 0;
		out[1] = 0;
		for(int i = 0; i<x.length; i++){
			w = x[i]==0?extraSpaceCols:x[i];
			if(i<col){
				out[0] += w;
			}else{
				s--;
				out[1] += w;
				if(s==0){
					break;
				}
			}
		}
	}
	private int countExtraSpace(int[] x, int total){
		int y = 0;
		int a = 0;
		for(int z : x){
			if(z==0){
				a++;
				continue;
			}
			y += z;
		}
		return (total-y)/a;
	}
}
