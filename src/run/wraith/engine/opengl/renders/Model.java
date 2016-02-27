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
public class Model{
	private final Matrix4f positionMatrix;
	private final VAO vao;
	public Model(VAO vao){
		positionMatrix = new Matrix4f();
		this.vao = vao;
	}
	public void dispose(){
		vao.dispose();
	}
	public Matrix4f getMatrix(){
		return positionMatrix;
	}
	public void render(){
		vao.render();
	}
}
