/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.mapstyles.iso;

import run.wraith.engine.opengl.renders.Model;
import run.wraith.engine.opengl.renders.ModelInstance;
import run.wraith.engine.opengl.utils.RenderIndex;

/**
 * @author thedudefromci
 */
public class EntityModelInstance extends ModelInstance implements RenderIndex{
	private double renderIndex;
	public EntityModelInstance(Model model){
		super(model);
	}
	@Override
	public void setRenderIndex(double index){
		renderIndex = index;
	}
	@Override
	public double getRenderIndex(){
		return renderIndex;
	}
}
