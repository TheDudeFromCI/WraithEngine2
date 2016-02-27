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
		public PrimitiveFlags(boolean position, boolean texture){
			this.position = position;
			this.texture = texture;
		}
	}
	private static class Builder{
		private final PrimitiveFlags flags;
		private final int vertDataSize;
		private float[] verts = new float[0];
		private short[] indes = new short[0];
		private Builder(PrimitiveFlags flags){
			this.flags = flags;
			int size = 0;
			if(flags.position){
				size += 3;
			}
			if(flags.texture){
				size += 2;
			}
			vertDataSize = size;
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
			for(int i = 0; i<verts.length; i += vertDataSize){
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
				return i/vertDataSize;
			}
			int pos = verts.length;
			verts = Arrays.copyOf(verts, verts.length+vertDataSize);
			if(flags.position){
				verts[pos++] = x;
				verts[pos++] = y;
				verts[pos++] = z;
			}
			if(flags.texture){
				verts[pos++] = s;
				verts[pos++] = t;
			}
			return pos/vertDataSize-1;
		}
	}
	public static VertexBuildData generateBox(float x, float y, float z, PrimitiveFlags flags){
		Builder builder = new Builder(flags);
		builder.add(-x, -y, -z, 0, 0);
		builder.add(x, -y, z, 1, 1);
		builder.add(-x, -y, z, 1, 0);
		builder.add(-x, -y, -z, 0, 0);
		builder.add(x, -y, -z, 0, 1);
		builder.add(x, -y, z, 1, 1);
		System.out.println("Generated box, ["+x*2+"x"+y*2+"x"+z*2+"] V:"+builder.verts.length+" I:"+builder.indes.length);
		// builder.place(-radiusX, -radiusY, -radiusZ, 0, 0);
		// builder.place(-radiusX, -radiusY, radiusZ, 0, 0);
		// builder.place(-radiusX, radiusY, -radiusZ, 0, 0);
		// builder.place(-radiusX, radiusY, radiusZ, 0, 0);
		// builder.place(radiusX, -radiusY, -radiusZ, 0, 0);
		// builder.place(radiusX, -radiusY, radiusZ, 0, 0);
		// builder.place(radiusX, radiusY, -radiusZ, 0, 0);
		// builder.place(radiusX, radiusY, radiusZ, 1, 1);
		// builder.placeTri(0, 5, 1);
		// builder.placeTri(0, 4, 5);
		// builder.placeTri(0, 2, 6);
		// builder.placeTri(0, 6, 4);
		// builder.placeTri(0, 1, 2);
		// builder.placeTri(1, 3, 2);
		// builder.placeTri(1, 7, 3);
		// builder.placeTri(1, 5, 7);
		// builder.placeTri(4, 6, 5);
		// builder.placeTri(5, 6, 7);
		// builder.placeTri(2, 3, 7);
		// builder.placeTri(2, 7, 6);
		return new VertexBuildData(builder.verts, builder.indes);
	}
	public static VAO convertToVAO(VertexBuildData data){
		float[] vertLocations = data.getVertexLocations();
		float[] vertices = new float[vertLocations.length/5*10];
		for(int i = 0; i<vertices.length; i += 10){
			vertices[i+0] = vertLocations[i/10*5+0];
			vertices[i+1] = vertLocations[i/10*5+1];
			vertices[i+2] = vertLocations[i/10*5+2];
			vertices[i+3] = 1.0f;
			vertices[i+4] = (float)Math.random();
			vertices[i+5] = (float)Math.random();
			vertices[i+6] = (float)Math.random();
			vertices[i+7] = 1.0f;
			vertices[i+8] = vertLocations[i/10*5+3];
			vertices[i+9] = vertLocations[i/10*5+4];
		}
		VaoArray[] parts = new VaoArray[]{
			new VaoArray(4, false), new VaoArray(4, false), new VaoArray(2, false)
		};
		return new VAO(vertices, data.getIndexLocations(), parts);
	}
	public static PrimitiveFlags POSITION_ONLY = new PrimitiveFlags(true, false);
	public static PrimitiveFlags ALL = new PrimitiveFlags(true, true);
}
