package renderer.texture;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexImage2D;

import java.io.FileInputStream;
import java.io.IOException;

import renderer.Log;

public class Texture2D extends Texture {

	public Texture2D(String file) throws IOException {
		super(GL_TEXTURE_2D);
		LoadedImage img = new LoadedImage(new FileInputStream(file), false, false);
		Log.d("TexImage2D: w=" + img.width + ", h=" + img.height + "");
		// glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, img.width, img.height, 0, GL_RGB, GL_UNSIGNED_BYTE, img.buffer);
	}
}