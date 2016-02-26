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
public class Matrix4f{
	public static Matrix4f frustum(float left, float right, float bottom, float top, float near, float far){
		Matrix4f frustum = new Matrix4f();
		float a = (right+left)/(right-left);
		float b = (top+bottom)/(top-bottom);
		float c = -(far+near)/(far-near);
		float d = -(2f*far*near)/(far-near);
		frustum.m00 = 2f*near/(right-left);
		frustum.m11 = 2f*near/(top-bottom);
		frustum.m02 = a;
		frustum.m12 = b;
		frustum.m22 = c;
		frustum.m32 = -1f;
		frustum.m23 = d;
		frustum.m33 = 0f;
		return frustum;
	}
	public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far){
		Matrix4f ortho = new Matrix4f();
		float tx = -(right+left)/(right-left);
		float ty = -(top+bottom)/(top-bottom);
		float tz = -(far+near)/(far-near);
		ortho.m00 = 2f/(right-left);
		ortho.m11 = 2f/(top-bottom);
		ortho.m22 = -2f/(far-near);
		ortho.m03 = tx;
		ortho.m13 = ty;
		ortho.m23 = tz;
		return ortho;
	}
	public static Matrix4f perspective(float fovy, float aspect, float near, float far){
		Matrix4f perspective = new Matrix4f();
		float y = (float)(1/Math.tan(Math.toRadians(fovy/2f)));
		float x = y/aspect;
		float frus = far-near;
		perspective.m00 = x;
		perspective.m11 = y;
		perspective.m22 = -((far+near)/frus);
		perspective.m23 = -1;
		perspective.m32 = -((2*near*far)/frus);
		perspective.m33 = 0;
		return perspective;
	}
	public static Matrix4f lookAt(Vector3f eye, Vector3f center, Vector3f up){
		Vector3f f = Vector3f.normalize(Vector3f.subtract(center, eye));
		Vector3f u = Vector3f.normalize(up);
		Vector3f s = Vector3f.normalize(Vector3f.cross(f, u));
		u = Vector3f.cross(s, f);
		Matrix4f res = new Matrix4f();
		res.m00 = s.x;
		res.m10 = s.y;
		res.m20 = s.z;
		res.m01 = u.x;
		res.m11 = u.y;
		res.m21 = u.z;
		res.m02 = -f.x;
		res.m12 = -f.y;
		res.m22 = -f.z;
		res.translate(-eye.x, -eye.y, -eye.z);
		return res;
	}
	public static Matrix4f createRotationMatrix(float angle, float x, float y, float z){
		Matrix4f rotation = new Matrix4f();
		float c = (float)Math.cos(Math.toRadians(angle));
		float s = (float)Math.sin(Math.toRadians(angle));
		Vector3f vec = new Vector3f(x, y, z);
		if(vec.length()!=1f){
			vec.normalize();
			x = vec.x;
			y = vec.y;
			z = vec.z;
		}
		rotation.m00 = x*x*(1f-c)+c;
		rotation.m10 = y*x*(1f-c)+z*s;
		rotation.m20 = x*z*(1f-c)-y*s;
		rotation.m01 = x*y*(1f-c)-z*s;
		rotation.m11 = y*y*(1f-c)+c;
		rotation.m21 = y*z*(1f-c)+x*s;
		rotation.m02 = x*z*(1f-c)+y*s;
		rotation.m12 = y*z*(1f-c)-x*s;
		rotation.m22 = z*z*(1f-c)+c;
		return rotation;
	}
	public static Matrix4f scale(float x, float y, float z){
		Matrix4f scaling = new Matrix4f();
		scaling.m00 = x;
		scaling.m11 = y;
		scaling.m22 = z;
		return scaling;
	}
	public static Matrix4f multiply(Matrix4f left, Matrix4f right){
		Matrix4f result = new Matrix4f(left);
		result.multiply(right);
		return result;
	}
	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;
	public Matrix4f(){
		setIdentity();
	}
	public Matrix4f(Matrix4f other){
		copy(other);
	}
	public Matrix4f(Vector4f col1, Vector4f col2, Vector4f col3, Vector4f col4){
		m00 = col1.x;
		m10 = col1.y;
		m20 = col1.z;
		m30 = col1.w;
		m01 = col2.x;
		m11 = col2.y;
		m21 = col2.z;
		m31 = col2.w;
		m02 = col3.x;
		m12 = col3.y;
		m22 = col3.z;
		m32 = col3.w;
		m03 = col4.x;
		m13 = col4.y;
		m23 = col4.z;
		m33 = col4.w;
	}
	public void rotate(float angle, float x, float y, float z){
		Matrix4f mat = createRotationMatrix(angle, x, y, z);
		multiply(mat);
	}
	public void rotate(float angle, Vector3f v){
		Matrix4f mat = createRotationMatrix(angle, v.x, v.y, v.z);
		multiply(mat);
	}
	public void add(Matrix4f other){
		m00 += other.m00;
		m01 += other.m01;
		m02 += other.m02;
		m03 += other.m03;
		m10 += other.m10;
		m11 += other.m11;
		m12 += other.m12;
		m13 += other.m13;
		m20 += other.m20;
		m21 += other.m21;
		m22 += other.m22;
		m23 += other.m23;
		m30 += other.m30;
		m31 += other.m31;
		m32 += other.m32;
		m33 += other.m33;
	}
	public void store(FloatBuffer buffer){
		buffer.put(m00).put(m10).put(m20).put(m30);
		buffer.put(m01).put(m11).put(m21).put(m31);
		buffer.put(m02).put(m12).put(m22).put(m32);
		buffer.put(m03).put(m13).put(m23).put(m33);
	}
	public void multiply(float s){
		m00 *= s;
		m01 *= s;
		m02 *= s;
		m03 *= s;
		m10 *= s;
		m11 *= s;
		m12 *= s;
		m13 *= s;
		m20 *= s;
		m21 *= s;
		m22 *= s;
		m23 *= s;
		m30 *= s;
		m31 *= s;
		m32 *= s;
		m33 *= s;
	}
	public void multiply(Matrix4f other){
		Matrix4f result = new Matrix4f();
		result.m00 = m00*other.m00+m01*other.m10+m02*other.m20+m03*other.m30;
		result.m10 = m10*other.m00+m11*other.m10+m12*other.m20+m13*other.m30;
		result.m20 = m20*other.m00+m21*other.m10+m22*other.m20+m23*other.m30;
		result.m30 = m30*other.m00+m31*other.m10+m32*other.m20+m33*other.m30;
		result.m01 = m00*other.m01+m01*other.m11+m02*other.m21+m03*other.m31;
		result.m11 = m10*other.m01+m11*other.m11+m12*other.m21+m13*other.m31;
		result.m21 = m20*other.m01+m21*other.m11+m22*other.m21+m23*other.m31;
		result.m31 = m30*other.m01+m31*other.m11+m32*other.m21+m33*other.m31;
		result.m02 = m00*other.m02+m01*other.m12+m02*other.m22+m03*other.m32;
		result.m12 = m10*other.m02+m11*other.m12+m12*other.m22+m13*other.m32;
		result.m22 = m20*other.m02+m21*other.m12+m22*other.m22+m23*other.m32;
		result.m32 = m30*other.m02+m31*other.m12+m32*other.m22+m33*other.m32;
		result.m03 = m00*other.m03+m01*other.m13+m02*other.m23+m03*other.m33;
		result.m13 = m10*other.m03+m11*other.m13+m12*other.m23+m13*other.m33;
		result.m23 = m20*other.m03+m21*other.m13+m22*other.m23+m23*other.m33;
		result.m33 = m30*other.m03+m31*other.m13+m32*other.m23+m33*other.m33;
		copy(result);
	}
	public void copy(Matrix4f other){
		m00 = other.m00;
		m01 = other.m01;
		m02 = other.m02;
		m03 = other.m03;
		m10 = other.m10;
		m11 = other.m11;
		m12 = other.m12;
		m13 = other.m13;
		m20 = other.m20;
		m21 = other.m21;
		m22 = other.m22;
		m23 = other.m23;
		m30 = other.m30;
		m31 = other.m31;
		m32 = other.m32;
		m33 = other.m33;
	}
	public void multiply(Vector4f vector){
		float x = m00*vector.x+m01*vector.y+m02*vector.z+m03*vector.w;
		float y = m10*vector.x+m11*vector.y+m12*vector.z+m13*vector.w;
		float z = m20*vector.x+m21*vector.y+m22*vector.z+m23*vector.w;
		float w = m30*vector.x+m31*vector.y+m32*vector.z+m33*vector.w;
		vector.x = x;
		vector.y = y;
		vector.z = z;
		vector.w = w;
	}
	public void negate(){
		multiply(-1f);
	}
	public void setIdentity(){
		m00 = 1f;
		m11 = 1f;
		m22 = 1f;
		m33 = 1f;
		m01 = 0f;
		m02 = 0f;
		m03 = 0f;
		m10 = 0f;
		m12 = 0f;
		m13 = 0f;
		m20 = 0f;
		m21 = 0f;
		m23 = 0f;
		m30 = 0f;
		m31 = 0f;
		m32 = 0f;
	}
	public void translate(float x, float y, float z){
		m03 += x;
		m13 += y;
		m23 += z;
	}
	public void translate(Vector3f v){
		m03 += v.x;
		m13 += v.y;
		m23 += v.z;
	}
	public void subtract(Matrix4f other){
		m00 -= other.m00;
		m01 -= other.m01;
		m02 -= other.m02;
		m03 -= other.m03;
		m10 -= other.m10;
		m11 -= other.m11;
		m12 -= other.m12;
		m13 -= other.m13;
		m20 -= other.m20;
		m21 -= other.m21;
		m22 -= other.m22;
		m23 -= other.m23;
		m30 -= other.m30;
		m31 -= other.m31;
		m32 -= other.m32;
		m33 -= other.m33;
	}
	public void transpose(){
		Matrix4f result = new Matrix4f();
		result.m00 = m00;
		result.m10 = m01;
		result.m20 = m02;
		result.m30 = m03;
		result.m01 = m10;
		result.m11 = m11;
		result.m21 = m12;
		result.m31 = m13;
		result.m02 = m20;
		result.m12 = m21;
		result.m22 = m22;
		result.m32 = m23;
		result.m03 = m30;
		result.m13 = m31;
		result.m23 = m32;
		result.m33 = m33;
		copy(result);
	}
}
