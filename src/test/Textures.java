package test;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;

public class Textures {
	
//	private static final int[] TARGETS = {
//			 GL_TEXTURE_1D, GL_TEXTURE_2D, GL_TEXTURE_3D,
//			 GL_TEXTURE_1D_ARRAY, GL_TEXTURE_2D_ARRAY,
//			 GL_TEXTURE_RECTANGLE,
//			 GL_TEXTURE_CUBE_MAP,
//			 GL_TEXTURE_BUFFER,
//			 GL_TEXTURE_2D_MULTISAMPLE,
//			 GL_TEXTURE_2D_MULTISAMPLE_ARRAY			
//	};
	
	private static List<Set<Integer>> usedTargets = new ArrayList<>();
	
	public static int allocTexUnitForTarget(int target) {
		
		for (int i = 0; i < usedTargets.size(); i++) {
			Set<Integer> used = usedTargets.get(i);
			if (!used.contains(target)) {
				used.add(target);
				return GL_TEXTURE0 + i;
			}
		}
		usedTargets.add(new HashSet<>(Arrays.asList(target)));
		return GL_TEXTURE0 + usedTargets.size() - 1;
	}
	
	public static void freeTexUnitTarget(int texUnit, int target) {
		
		int i = texUnit - GL_TEXTURE0;
		if (i < 0 || i >= usedTargets.size()) {
			throw new IllegalArgumentException("No targets allocated in texture unit "+i);
		}
		Set<Integer> used = usedTargets.get(i);
		if (used.remove(target)) {
			return;
		}
		throw new IllegalArgumentException("No target "+target+" allocated in texture unit "+i);
	}

	
	private static final int[] cube_map_side = {
			GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
			GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
			GL_TEXTURE_CUBE_MAP_POSITIVE_Z, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };
	private static final String[] cube_map_patt = {
			"right.*", "left.*", "top.*", "bottom.*", "back.*", "front.*" };
	// temp fix to flip the images i am using...
	private static final boolean[] cube_map_fx = { true,true,false,false,true,true };
	private static final boolean[] cube_map_fy = { false,false,true,true,false,false };
	
//	byte[] gen = new byte[1024*1024*3];
//	{
//		Arrays.fill(gen, (byte)255);
//	}

	// load texture to: texture unit, generate id
	// select texture to be used
    private static final  ColorModel glColorModel =
    		new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);
	
    static class LoadedImage {
    	final int width;
    	final int height;
    	final ByteBuffer buffer;
    	LoadedImage(InputStream is, boolean flipx, boolean flipy) throws IOException {
    	
			BufferedImage bi = ImageIO.read(new BufferedInputStream(is));
			
			width = bi.getWidth();
			height = bi.getHeight();
			
	        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,width,height,3,null);
	        BufferedImage texImage = new BufferedImage(glColorModel,raster,false,new Hashtable());
	        // copy the source image into the produced image
	        Graphics2D g = (Graphics2D) texImage.getGraphics();
	        
	//        if (flipped) {
	//        	g.scale(1,-1);
	//        	g.drawImage(image,0,-height,null);
	//        } else {
	       	g.scale(flipx ? -1 : 1, flipy ? -1 : 1);
	        g.drawImage(bi,flipx ? -width : 0,flipy ? -height : 0,null);
	//        }
//	        g.setFont(new Font("Monospaced", Font.PLAIN, 100));
//	        g.setColor(Color.WHITE);
//	        g.drawChars(new char[]{'A','B','C','D','E'}, 0, 5, 50, 50);
	       
	        	
	        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData(); 
	        
	        buffer = BufferUtils.createByteBuffer(data.length); //ByteBuffer.allocateDirect(data.length); 
	        //buffer.order(ByteOrder.nativeOrder()); 
	        buffer.put(data, 0, data.length); 
	        buffer.flip();
	        g.dispose();
            
    	// ByteBuffer textureBuffer = imageData.loadImage(new BufferedInputStream(is), false, null); // new int[]{}
    	}
    }

	public static class Texture {

		private final int unit;
		private final int target;
		private final int name;
		private final int filter = GL_NEAREST; //GL_LINEAR;

		public int getSampler() {
			return unit - GL_TEXTURE0;
		}

		public int getName() {
			return name;
		}
		
		public void delete() {
			
			glDeleteTextures(name);
			freeTexUnitTarget(unit, target);
		}

		public Texture(int target) {

			this.target = target;
			unit = allocTexUnitForTarget(target);
			glActiveTexture(unit);
			name = glGenTextures();
			glBindTexture(target, name);
			glTexParameteri(target, GL_TEXTURE_MIN_FILTER, filter);
			glTexParameteri(target, GL_TEXTURE_MAG_FILTER, filter);
			
			//glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, &largest_supported_anisotropy);
			//glTexParameterf(target, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 32);			
			//glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
			//glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);
		}
	}		

    public static class Texture2D extends Texture {

		public Texture2D(String file) throws IOException {
			super(GL_TEXTURE_2D);
			LoadedImage img = new LoadedImage(new FileInputStream(file), false, false);
			Log.d("TexImage2D: w=" + img.width + ", h=" + img.height + "");
			// glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, img.width, img.height, 0, GL_RGB, GL_UNSIGNED_BYTE, img.buffer);
		}
	}		
		
//		float pixels[] = {
//			    0.5f, 0.5f, 0.5f,   1.0f, 1.0f, 1.0f,
//			    1.0f, 1.0f, 1.0f,   0.5f, 0.5f, 0.5f
//			};
//		FloatBuffer fb = BufferUtils.createFloatBuffer(pixels.length);
//		fb.put(pixels);
//		fb.flip();
		
//		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 2, 2, 0, GL_RGB, GL_FLOAT, fb);
			
    
    public static class TextureCubeMap extends Texture {
    	
    	public TextureCubeMap(String directory) throws IOException {
    		
    		super(GL_TEXTURE_CUBE_MAP);

    		for (int i = 0; i < 6; i++) {

				File file = Util.find(directory, cube_map_patt[i]);
				if (file == null) {
					throw new IllegalArgumentException("Could not find match for "+cube_map_patt[i]+" in "+directory);
				}
				InputStream is = new FileInputStream(file);
				LoadedImage img = new LoadedImage(is, cube_map_fx[i], cube_map_fy[i]);
				Log.d("tex: pos="+img.buffer.position()+", limit="+img.buffer.limit());
				glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		        glTexImage2D(cube_map_side[i], 0,
		                GL_RGB,
		                img.width,
		                img.height,
		                0, 
		                GL_RGB,
		                GL_UNSIGNED_BYTE,
		                img.buffer);
    		}
    	}
    }
}
