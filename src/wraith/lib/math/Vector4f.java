/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.math;

import java.nio.FloatBuffer;

/**
 * @author thedudefromci
 */
public class Vector4f{
	public float x;
	public float y;
	public float z;
	public float w;
	public Vector4f(){
		x = 0;
		y = 0;
		z = 0;
		w = 0;
	}
	public Vector4f(float x, float y, float z, float w){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public void add(Vector4f v){
		x += v.x;
		y += v.y;
		z += v.z;
		w += v.w;
	}
	public void divide(float v){
		x /= v;
		y /= v;
		z /= v;
		w /= v;
	}
	public float dot(Vector4f v){
		return x*v.x+y*v.y+z*v.z+w*v.w;
	}
	public void store(FloatBuffer buffer){
		buffer.put(x).put(y).put(z).put(w);
	}
	public float length(){
		return (float)Math.sqrt(lengthSquared());
	}
	public float lengthSquared(){
		return x*x+y*y+z*z+w*w;
	}
	public void lerp(Vector4f v, float a){
		float o = 1-a;
		x = o*x+v.x*a;
		y = o*y+v.y*a;
		z = o*z+v.z*a;
		w = o*w+v.w*a;
	}
	public void negate(){
		x = -x;
		y = -y;
		z = -z;
		w = -w;
	}
	public void normalize(){
		divide(length());
	}
	public void multiply(float v){
		x *= v;
		y *= v;
		z *= v;
		w *= v;
	}
	public void subtract(Vector4f v){
		x -= v.x;
		y -= v.y;
		z -= v.z;
		w -= v.w;
	}
}
