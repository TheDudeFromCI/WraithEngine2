/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.renders;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * @author thedudefromci
 */
public class VAO{
	public static class VaoArray{
		private final int size;
		private final boolean normalized;
		public VaoArray(int size, boolean normalized){
			this.size = size;
			this.normalized = normalized;
		}
	}
	private final int vaoId;
	private final int vboId;
	private final int vboiId;
	private final int indexCount;
	private final int arrayCount;
	public VAO(float[] vertices, short[] indices, VaoArray[] arrays){
		indexCount = indices.length;
		arrayCount = arrays.length;
		FloatBuffer vertexData = BufferUtils.createFloatBuffer(vertices.length);
		vertexData.put(vertices);
		vertexData.flip();
		ShortBuffer indexData = BufferUtils.createShortBuffer(indices.length);
		indexData.put(indices);
		indexData.flip();
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
		int stride = 0;
		for(VaoArray a : arrays){
			stride += a.size*4;
		}
		int offset = 0;
		for(int i = 0; i<arrays.length; i++){
			GL20.glVertexAttribPointer(i, arrays[i].size, GL11.GL_FLOAT, arrays[i].normalized, stride, offset);
			offset += arrays[i].size*4;
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		vboiId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexData, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	public void dispose(){
		GL30.glBindVertexArray(vaoId);
		for(int i = 0; i<arrayCount; i++){
			GL20.glDisableVertexAttribArray(i);
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboiId);
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}
	public void render(){
		GL30.glBindVertexArray(vaoId);
		for(int i = 0; i<arrayCount; i++){
			GL20.glEnableVertexAttribArray(i);
		}
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_SHORT, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		for(int i = 0; i<arrayCount; i++){
			GL20.glDisableVertexAttribArray(i);
		}
		GL30.glBindVertexArray(0);
	}
}
