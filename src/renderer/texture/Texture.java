package renderer.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;

import java.util.ArrayDeque;

import renderer.Log;

import com.carrotsearch.hppcrt.lists.IntArrayDeque;

public class Texture {

	public static final int INVALID_TEXTURE_NAME = -1;
	public static final int INVALID_TEXTURE_UNIT = 0;

	// for keeping track of available/used texture units
	private static final IntArrayDeque freeTexUnits;
	private static final ArrayDeque<Texture> boundTextures;
	static {
		int maxTexUnits = glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS);
		Log.i("GL_MAX_TEXTURE_IMAGE_UNITS: %d", maxTexUnits);
		freeTexUnits = new IntArrayDeque(maxTexUnits);
		boundTextures = new ArrayDeque<>(maxTexUnits);
		for (int i = 0; i < maxTexUnits; i++) {
			freeTexUnits.add(GL_TEXTURE0 + i);
		}
	}

	// allocate and activate texture unit and bind texture to the unit
	private void bind() {
		// use free unit if available or steal unit from least recently bound texture
		unit = freeTexUnits.isEmpty() ? boundTextures.removeFirst().takeUnit() : freeTexUnits.removeFirst();
		boundTextures.add(this);
		glActiveTexture(unit);
		glBindTexture(target, name);
	}

	// return the unit this texture is bound to and mark texture as having no unit
	private int takeUnit() {
		int tmp = unit;
		unit = INVALID_TEXTURE_UNIT;
		return tmp;
	}

	// fully dispose of all resources allocated to this texture
	public void delete() {
		if (unit != INVALID_TEXTURE_UNIT) {
			boundTextures.remove(this); // slow op
			freeTexUnits.add(unit);
			unit = INVALID_TEXTURE_UNIT;
		}
		glDeleteTextures(name);
		name = INVALID_TEXTURE_NAME;
		// freeTexUnitTarget(unit, target);
	}

	// make sure that texture is bound and return sampler index
	public int getSampler() {
		if (unit == INVALID_TEXTURE_UNIT) {
			bind();
		}
		return unit - GL_TEXTURE0;
	}

	private final int target;
	private int name;
	private final int filter = GL_LINEAR; // GL_NEAREST; //
	private int unit;

	public int getName() {
		return name;
	}

	// NOTE! all textures that are used by same shader at the same time
	// must be bound to different texture units

	// So allocating texture unit like I am doing right now is bound to fail
	// (when two different kinds of textures map to same unit, which fails when
	// trying to render)
	// (possibly some problems earlier were because of this?)
	//

	// so textures should be rebound to units when we really want to use them...

	// bind texture initially,
	// remember what is bound
	// only rebind if binding has been lost
	// (due to tex unit pressure or reset (can this happen?))

	public Texture(int target) {

		this.target = target;
		name = glGenTextures();
		bind();
		// unit = allocTexUnitForTarget(target);
		// glActiveTexture(unit);
		// glBindTexture(target, name);
		glTexParameteri(target, GL_TEXTURE_MIN_FILTER, filter);
		glTexParameteri(target, GL_TEXTURE_MAG_FILTER, filter);
		// glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT,
		// &largest_supported_anisotropy);
		// glTexParameterf(target,
		// EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 32);
		// glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
		// glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);
	}
}