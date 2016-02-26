/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.renders;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import wraith.lib.math.Matrix4f;
import wraith.lib.math.Vector3f;

/**
 * @author thedudefromci
 */
public class Camera{
	private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	private final Vector3f temp = new Vector3f();
	private final Vector3f temp2 = new Vector3f();
	private final Matrix4f mat;
	private final Matrix4f proMat;
	private final FloatBuffer buf;
	private float x;
	private float y;
	private float z;
	private float rx;
	private float ry;
	public Camera(){
		mat = new Matrix4f();
		proMat = new Matrix4f();
		buf = BufferUtils.createFloatBuffer(16);
		updateMatrix();
	}
	public void dumpToShader(int viewLocation, int projectionLocation){
		mat.store(buf);
		buf.flip();
		GL20.glUniformMatrix4fv(viewLocation, false, buf);
		proMat.store(buf);
		buf.flip();
		GL20.glUniformMatrix4fv(projectionLocation, false, buf);
	}
	public float getRX(){
		return rx;
	}
	public float getRY(){
		return ry;
	}
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
	public float getZ(){
		return z;
	}
	public void lookAt(float x, float y, float z){
		temp.set(x, y, z);
		temp2.set(this.x, this.y, this.z);
		Vector3f f = Vector3f.subtract(temp, temp2);
		f.normalize();
		Vector3f s = Vector3f.cross(f, Y_AXIS);
		s.normalize();
		Vector3f u = Vector3f.cross(s, f);
		mat.setIdentity();
		mat.m00 = s.x;
		mat.m10 = s.y;
		mat.m20 = s.z;
		mat.m01 = u.x;
		mat.m11 = u.y;
		mat.m21 = u.z;
		mat.m02 = -f.x;
		mat.m12 = -f.y;
		mat.m22 = -f.z;
		mat.translate(-temp2.x, -temp2.y, -temp2.z);
	}
	public void move(float x, float y, float z){
		this.x += x;
		this.y += y;
		this.z += z;
		updateMatrix();
	}
	public void moveTo(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		updateMatrix();
	}
	public void rotateTo(float rx, float ry){
		this.rx = rx;
		this.ry = ry;
		updateMatrix();
	}
	public void setOrthographic(float left, float right, float bottom, float top, float near, float far){
		float tx = -(right+left)/(right-left);
		float ty = -(top+bottom)/(top-bottom);
		float tz = -(far+near)/(far-near);
		proMat.setIdentity();
		proMat.m00 = 2f/(right-left);
		proMat.m11 = 2f/(top-bottom);
		proMat.m22 = -2f/(far-near);
		proMat.m03 = tx;
		proMat.m13 = ty;
		proMat.m23 = tz;
	}
	public void setPerspective(float fov, float aspect, float near, float far){
		float y_scale = (float)(1/Math.tan(Math.toRadians(fov/2f)));
		float x_scale = y_scale/aspect;
		float frustum_length = far-near;
		proMat.setIdentity();
		proMat.m00 = x_scale;
		proMat.m11 = y_scale;
		proMat.m22 = -((far+near)/frustum_length);
		proMat.m23 = -1;
		proMat.m32 = -(2*near*far/frustum_length);
		proMat.m33 = 0;
	}
	private void updateMatrix(){
		mat.setIdentity();
		temp.set(x, y, z);
		mat.translate(temp);
		mat.rotate((float)Math.toRadians(rx), X_AXIS);
		mat.rotate((float)Math.toRadians(ry), Y_AXIS);
	}
}
