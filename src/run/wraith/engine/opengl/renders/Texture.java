/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.renders;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

/**
 * @author thedudefromci
 */
public class Texture{
	public static void unbind(){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	private static ByteBuffer generatePixelBuffer(BufferedImage image){
		int[] pixels = new int[image.getWidth()*image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth()*image.getHeight()*4);
		int x, y;
		for(y = image.getHeight()-1; y>=0; y--){
			for(x = 0; x<image.getWidth(); x++){
				int pixel = pixels[y*image.getWidth()+x];
				buffer.put((byte)(pixel>>16&0xFF));
				buffer.put((byte)(pixel>>8&0xFF));
				buffer.put((byte)(pixel&0xFF));
				buffer.put((byte)(pixel>>24&0xFF));
			}
		}
		buffer.flip();
		return buffer;
	}
	private static BufferedImage loadImage(File file){
		try{
			return ImageIO.read(file);
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return null;
	}
	private final int textureId;
	public Texture(BufferedImage image, boolean mipmaps){
		textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		if(mipmaps){
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		}else{
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		}
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
			Texture.generatePixelBuffer(image));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	public Texture(File file, boolean mipmaps){
		this(loadImage(file), mipmaps);
	}
	public void bind(){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}
	public void dispose(){
		GL11.glDeleteTextures(textureId);
	}
	public void reloadTexture(BufferedImage image){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, image.getWidth(), image.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
			generatePixelBuffer(image));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}
