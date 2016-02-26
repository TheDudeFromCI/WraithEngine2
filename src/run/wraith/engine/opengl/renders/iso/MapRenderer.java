/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.renders.iso;

import org.lwjgl.opengl.GL11;
import run.wraith.engine.mapstyles.iso.Map;
import run.wraith.engine.opengl.loop.RenderLoop;

/**
 * @author thedudefromci
 */
public class MapRenderer implements RenderLoop{
	private Map map;
	public void setMap(Map map){
		this.map = map;
	}
	public void initalize(){
		GL11.glClearColor(0, 0, 0, 1);
	}
	@Override
	public void render(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		if(map==null){
			return;
		}
	}
	@Override
	public void update(double delta, double time){}
}
