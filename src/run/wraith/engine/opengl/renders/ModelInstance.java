/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.renders;

import org.joml.Matrix4f;

/**
 * @author thedudefromci
 */
public class ModelInstance{
	private final Model model;
	private final Matrix4f position;
	public ModelInstance(Model model){
		this.model = model;
		position = new Matrix4f();
	}
	public Matrix4f getPosition(){
		return position;
	}
	public Model getModel(){
		return model;
	}
	public void render(){
		model.render();
	}
	public void update(double delta, double time){
		// This is here to be overriden by sub classes.
	}
}
