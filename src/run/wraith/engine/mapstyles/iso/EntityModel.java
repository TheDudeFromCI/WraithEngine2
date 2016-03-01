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
import run.wraith.engine.opengl.utils.PrimitiveGenerator.PrimitiveFlags;
import run.wraith.engine.opengl.utils.VertexBuildData;

/**
 * @author thedudefromci
 */
public class EntityModel extends Model{
	private static VAO generateVAO(Entity entity){
		// TODO Optimize algorithm to cut out transparency.
		PrimitiveFlags flags = new PrimitiveFlags(true, true);
		VertexBuildData data = PrimitiveGenerator.generateSquare(0.5f, 0.5f*entity.getHeight(), flags);
		VAO vao = PrimitiveGenerator.convertToVAO(data, flags);
		return vao;
	}
	private final Entity entity;
	private final Texture texture;
	public EntityModel(Entity entity){
		super(generateVAO(entity));
		this.entity = entity;
		texture = new Texture(entity.getImage(), false);
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
