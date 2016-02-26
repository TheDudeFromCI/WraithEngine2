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
public class Vector3f{
	public static Vector3f subtract(Vector3f a, Vector3f b){
		return new Vector3f(a.x-b.x, a.y-b.y, a.z-b.z);
	}
	public static Vector3f normalize(Vector3f a){
		Vector3f o = a.copy();
		o.normalize();
		return o;
	}
	public static Vector3f cross(Vector3f a, Vector3f b){
		Vector3f o = a.copy();
		o.cross(b);
		return o;
	}
	public float x;
	public float y;
	public float z;
	public Vector3f(){
		x = 0;
		y = 0;
		z = 0;
	}
	public Vector3f(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void add(Vector3f v){
		x += v.x;
		y += v.y;
		z += v.z;
	}
	public void cross(Vector3f v){
		float x1 = y*v.z-z*v.y;
		float y1 = z*v.x-x*v.z;
		float z1 = x*v.y-y*v.x;
		x = x1;
		y = y1;
		z = z1;
	}
	public void divide(float v){
		x /= v;
		y /= v;
		z /= v;
	}
	public float dot(Vector3f v){
		return x*v.x+y*v.y+z*v.z;
	}
	public void store(FloatBuffer buffer){
		buffer.put(x).put(y).put(z);
	}
	public float length(){
		return (float)Math.sqrt(lengthSquared());
	}
	public float lengthSquared(){
		return x*x+y*y+z*z;
	}
	public void negate(){
		x = -x;
		y = -y;
		z = -z;
	}
	public void normalize(){
		divide(length());
	}
	public void multiply(float v){
		x *= v;
		y *= v;
		z *= v;
	}
	public void set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3f copy(){
		return new Vector3f(x, y, z);
	}
	public void subtract(Vector3f v){
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}
}
