package renderer.texture;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import renderer.Log;
import renderer.Util;

public class TextureCubeMap extends Texture {

	private static final int[] CUBE_MAP_SIDE = { GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
			GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
			GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };
	private static final String[] CUBE_MAP_PATT = { "right.*", "left.*", "top.*", "bottom.*", "back.*", "front.*" };
	// temp fix to flip the images i am using...
	private static final boolean[] CUBE_MAP_FX = { true, true, false, false, true, true };
	private static final boolean[] CUBE_MAP_FY = { false, false, true, true, false, false };

	public TextureCubeMap(String directory) throws IOException {

		super(GL_TEXTURE_CUBE_MAP);

		for (int i = 0; i < 6; i++) {

			File file = Util.find(directory, CUBE_MAP_PATT[i]);
			if (file == null) {
				throw new IllegalArgumentException("Could not find match for " + CUBE_MAP_PATT[i] + " in " + directory);
			}
			InputStream is = new FileInputStream(file);
			LoadedImage img = new LoadedImage(is, CUBE_MAP_FX[i], CUBE_MAP_FY[i]);
			Log.d("tex: pos=" + img.buffer.position() + ", limit=" + img.buffer.limit());
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexImage2D(CUBE_MAP_SIDE[i], 0, GL_RGB, img.width, img.height, 0, GL_RGB, GL_UNSIGNED_BYTE, img.buffer);
		}
	}
}