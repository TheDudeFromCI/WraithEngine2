/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.renders;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

/**
 * @author thedudefromci
 */
public class Camera{
	private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	private final Matrix4f mat;
	private final Matrix4f proMat;
	private final FloatBuffer buf;
	private final ArrayList<CameraListener> listeners = new ArrayList(0);
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
		mat.get(buf);
		GL20.glUniformMatrix4fv(viewLocation, false, buf);
		proMat.get(buf);
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
		mat.lookAt(this.x, this.y, this.z, x, y, z, 0, 1, 0);
	}
	public void move(float x, float y, float z){
		this.x += x;
		this.y += y;
		this.z += z;
		updateMatrix();
		for(CameraListener l : listeners){
			l.cameraMoved(this.x, this.y, this.z);
		}
	}
	public void moveTo(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		updateMatrix();
		for(CameraListener l : listeners){
			l.cameraMoved(this.x, this.y, this.z);
		}
	}
	public void rotateTo(float rx, float ry){
		this.rx = rx;
		this.ry = ry;
		updateMatrix();
	}
	public void setOrthographic(float left, float right, float bottom, float top, float near, float far){
		proMat.setOrtho(left, right, bottom, top, near, far);
	}
	public void setPerspective(float fov, float aspect, float near, float far){
		proMat.setPerspective(fov, aspect, near, far);
	}
	private void updateMatrix(){
		mat.identity();
		mat.translate(-x, -y, -z);
		mat.rotate((float)Math.toRadians(-rx), X_AXIS);
		mat.rotate((float)Math.toRadians(-ry), Y_AXIS);
	}
	public void addListener(CameraListener listener){
		listeners.add(listener);
	}
	public void removeListener(CameraListener listener){
		listeners.remove(listener);
	}
}
