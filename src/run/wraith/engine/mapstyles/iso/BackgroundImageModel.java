/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.mapstyles.iso;

import run.wraith.engine.opengl.renders.Model;
import run.wraith.engine.opengl.renders.Texture;
import run.wraith.engine.opengl.renders.VAO;
import run.wraith.engine.opengl.utils.PrimitiveGenerator;
import run.wraith.engine.opengl.utils.VertexBuildData;

/**
 * @author thedudefromci
 */
public class BackgroundImageModel extends Model{
	private static VAO generateVAO(BackgroundImage image){
		PrimitiveGenerator.PrimitiveFlags flags = new PrimitiveGenerator.PrimitiveFlags(true, true);
		VertexBuildData data = PrimitiveGenerator.generateSquare(800, 600, flags);
		VAO vao = PrimitiveGenerator.convertToVAO(data, flags);
		return vao;
	}
	private final Texture texture;
	public BackgroundImageModel(BackgroundImage image){
		super(generateVAO(image));
		texture = new Texture(image.getImage(), true);
	}
	@Override
	public void render(){
		texture.bind();
		super.render();
		Texture.unbind();
	}
	@Override
	public void dispose(){
		super.dispose();
		texture.dispose();
	}
}
