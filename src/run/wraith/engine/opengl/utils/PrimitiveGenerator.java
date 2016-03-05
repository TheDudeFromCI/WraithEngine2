/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.utils;

import java.util.Arrays;
import run.wraith.engine.opengl.renders.VAO;
import run.wraith.engine.opengl.renders.VAO.VaoArray;

/**
 * @author thedudefromci
 */
public class PrimitiveGenerator{
	public static class PrimitiveFlags{
		private final boolean position;
		private final boolean texture;
		private final int size;
		public PrimitiveFlags(boolean position, boolean texture){
			this.position = position;
			this.texture = texture;
			int s = 0;
			if(position){
				s += 3;
			}
			if(texture){
				s += 2;
			}
			size = s;
		}
	}
	private static class Builder{
		private final PrimitiveFlags flags;
		private float[] verts = new float[0];
		private short[] indes = new short[0];
		private Builder(PrimitiveFlags flags){
			this.flags = flags;
		}
		private void add(float x, float y, float z, float s, float t){
			addIndex(addVertex(x, y, z, s, t));
		}
		private void addIndex(int index){
			indes = Arrays.copyOf(indes, indes.length+1);
			indes[indes.length-1] = (short)index;
		}
		private int addVertex(float x, float y, float z, float s, float t){
			int offset;
			for(int i = 0; i<verts.length; i += flags.size){
				offset = 0;
				if(flags.position){
					if(verts[i+offset++]!=x){
						continue;
					}
					if(verts[i+offset++]!=y){
						continue;
					}
					if(verts[i+offset++]!=z){
						continue;
					}
				}
				if(flags.texture){
					if(verts[i+offset++]!=s){
						continue;
					}
					if(verts[i+offset++]!=t){
						continue;
					}
				}
				return i/flags.size;
			}
			int pos = verts.length;
			verts = Arrays.copyOf(verts, verts.length+flags.size);
			if(flags.position){
				verts[pos++] = x;
				verts[pos++] = y;
				verts[pos++] = z;
			}
			if(flags.texture){
				verts[pos++] = s;
				verts[pos++] = t;
			}
			return pos/flags.size-1;
		}
	}
	public static VertexBuildData generateBox(float x, float y, float z, PrimitiveFlags flags){
		Builder builder = new Builder(flags);
		// Face 0
		builder.add(x, y, z, 0, 0);
		builder.add(x, -y, z, 0, 1);
		builder.add(x, -y, -z, 1, 1);
		builder.add(x, y, z, 0, 0);
		builder.add(x, -y, -z, 1, 1);
		builder.add(x, y, -z, 1, 0);
		// Face 1
		builder.add(-x, y, -z, 0, 0);
		builder.add(-x, -y, -z, 0, 1);
		builder.add(-x, -y, z, 1, 1);
		builder.add(-x, y, -z, 0, 0);
		builder.add(-x, -y, z, 1, 1);
		builder.add(-x, y, z, 1, 0);
		// Face 2
		builder.add(-x, y, -z, 0, 0);
		builder.add(-x, y, z, 0, 1);
		builder.add(x, y, z, 1, 1);
		builder.add(-x, y, -z, 0, 0);
		builder.add(x, y, z, 1, 1);
		builder.add(x, y, -z, 1, 0);
		// Face 3
		builder.add(-x, -y, z, 0, 0);
		builder.add(-x, -y, -z, 0, 1);
		builder.add(x, -y, -z, 1, 1);
		builder.add(-x, -y, z, 0, 0);
		builder.add(x, -y, -z, 1, 1);
		builder.add(x, -y, z, 1, 0);
		// Face 4
		builder.add(-x, y, z, 0, 0);
		builder.add(-x, -y, z, 0, 1);
		builder.add(x, -y, z, 1, 1);
		builder.add(-x, y, z, 0, 0);
		builder.add(x, -y, z, 1, 1);
		builder.add(x, y, z, 1, 0);
		// Face 5
		builder.add(x, y, -z, 0, 0);
		builder.add(x, -y, -z, 0, 1);
		builder.add(-x, -y, -z, 1, 1);
		builder.add(x, y, -z, 0, 0);
		builder.add(-x, -y, -z, 1, 1);
		builder.add(-x, y, -z, 1, 0);
		// Compile
		return new VertexBuildData(builder.verts, builder.indes);
	}
	public static VertexBuildData generateSquare(float x, float y, PrimitiveFlags flags){
		Builder builder = new Builder(flags);
		builder.add(0, 0, 0, 0, 1);
		builder.add(0, y, 0, 0, 0);
		builder.add(x, y, 0, 1, 0);
		builder.add(0, 0, 0, 0, 1);
		builder.add(x, y, 0, 1, 0);
		builder.add(x, 0, 0, 1, 1);
		return new VertexBuildData(builder.verts, builder.indes);
	}
	/**
	 * Transforms vertex build data into a standard position and texture coords VAO. The output is NOT reflected by the flag parameter.
	 *
	 * @param data
	 *            - The vertex build data.
	 * @param flags
	 *            - The flags used to construct the vertex data.
	 * @return The VAO.
	 */
	public static VAO convertToVAO(VertexBuildData data, PrimitiveFlags flags){
		float[] vertLocations = data.getVertexLocations();
		float[] vertices = new float[vertLocations.length/flags.size*5];
		for(int i = 0; i<vertices.length; i += 5){
			vertices[i+0] = vertLocations[i/5*flags.size+0];
			vertices[i+1] = vertLocations[i/5*flags.size+1];
			vertices[i+2] = vertLocations[i/5*flags.size+2];
			vertices[i+3] = vertLocations[i/5*flags.size+3];
			vertices[i+4] = vertLocations[i/5*flags.size+4];
		}
		VaoArray[] parts = new VaoArray[]{
			new VaoArray(3, false), new VaoArray(2, false)
		};
		return new VAO(vertices, data.getIndexLocations(), parts);
	}
	public static PrimitiveFlags POSITION_FLAG_ONLY = new PrimitiveFlags(true, false);
	public static PrimitiveFlags ALL_FLAGS = new PrimitiveFlags(true, true);
}
