package test;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.*;

import org.lwjgl.*;
import org.lwjgl.util.vector.*;

// things that should stay static for one frame
public class View {

	int envCubeTexture;
	int envCubeSampler = 0;

	// load cube texture to texture unit 0
	public void loadTexture(String file, int textureUnit) {
		
		// TODO
		
	}
	
	int[] cube_map_side = {
			GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
			GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
			GL_TEXTURE_CUBE_MAP_POSITIVE_Z, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };
	String[] cube_map_patt = {
			"right.*", "left.*", "top.*", "bottom.*", "back.*", "front.*" };
	// temp fix to flip the images i am using...
	boolean[] cube_map_fx = { true,true,false,false,true,true };
	boolean[] cube_map_fy = { false,false,true,true,false,false };
	
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

    
	public void loadCubeTexture(String directory) throws IOException {

    	glActiveTexture(GL_TEXTURE0 + envCubeSampler);
    	
    	envCubeTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, envCubeTexture);
		int filter = GL_LINEAR;
		//int filter = GL_NEAREST;
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, filter);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, filter);
        
		//Random r = new Random(234);
		
//		WTF? this works if side == 1, what is going on???
		
		int side = 1024; //256;
//		int n = side*side*3;
//		byte[] tmp = new byte[n];
		
		for (int i = 0; i < 6; i++) {

			
//			ByteBuffer b = BufferUtils.createByteBuffer(n); //imageData.loadImage(new BufferedInputStream(is), false, null); // new int[]{}
//			r.nextBytes(tmp);
//			b.put(tmp);
//			for (int j = 0; j < 4; j++) {
//				b.put(new byte[]{ (byte)r.nextBytes(bytes);Radn55, (byte)55, (byte)255 });
//			}
//			b.flip();
/*			
	        glTexImage2D(cube_map_side[i], 0,
            GL_RGB,
            side,
            side,
            0, 
            GL_RGB,
            GL_UNSIGNED_BYTE,
            new ImageGen(side, side).img1((i+1)*5000, (i+1)*50).buffer());
*/			
			
			File file = Util.find(directory, cube_map_patt[i]);
			if (file == null) {
				throw new IllegalArgumentException("Could not find match for "+cube_map_patt[i]+" in "+directory);
			}
			InputStream is = new FileInputStream(file); //new File(directory, cube_map_file[i]));
			
//			ImageIOImageData imageData = new ImageIOImageData();
//			BufferedImage bi = ImageIO.read(new BufferedInputStream(is));
//			
//			int w = bi.getWidth();
//			int h = bi.getHeight();
//	        WritableRaster raster;
//	        BufferedImage texImage;
//            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,w,h,3,null);
//            texImage = new BufferedImage(glColorModel,raster,false,new Hashtable());
//            // copy the source image into the produced image
//            Graphics2D g = (Graphics2D) texImage.getGraphics();
//            
////            if (flipped) {
////            	g.scale(1,-1);
////            	g.drawImage(image,0,-height,null);
////            } else {
//            	g.drawImage(bi,0,0,null);
////            }
//                byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData(); 
//                
//                ByteBuffer imageBuffer = ByteBuffer.allocateDirect(data.length); 
//                imageBuffer.order(ByteOrder.nativeOrder()); 
//                imageBuffer.put(data, 0, data.length); 
//                imageBuffer.flip();
//                g.dispose();
//                
//	    	ByteBuffer textureBuffer = imageData.loadImage(new BufferedInputStream(is), false, null); // new int[]{}
			LoadedImage img = new LoadedImage(is, cube_map_fx[i], cube_map_fy[i]);
			//ByteBuffer textureBuffer = loadImage(is); // new int[]{}
	    	
	    	Log.d("tex: pos="+img.buffer.position()+", limit="+img.buffer.limit());
//	    	Log.d("img: width="+imageData.getWidth()+", height="+imageData.getHeight());
//	    	Log.d("img: twidth="+imageData.getTexWidth()+", theight="+imageData.getTexHeight());
//	    	Log.d("img: depth="+imageData.getDepth());

//	    	textureBuffer.put(gen);
//	    	textureBuffer.rewind();
//	    	
//	    	//textureBuffer.pu
//	    	for (int j = 0; j < 10; j++) {
//	    		for (int k = 0; k < 3; k++) {
//	    			Log.d("color: ("+textureBuffer.get()+","+textureBuffer.get()+","+textureBuffer.get()+")");
//	    		}
//	    	}
//	    	
//	    	textureBuffer.rewind();
	    	
	    	// GL33.glGenSamplers()
	    	
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
        
    	
		
		//TextureLoader.getTexture("JPG", in)
		
		//BufferedImage img = ImageIO.read(new File(directory, "top.jpg"));
		//Raster r = img.getData();
		// r.
		//glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, internalformat, width, height, border, format, type, pixels_buffer_offset);
		
	}
	
	public final Matrix4f world_to_view = new Matrix4f();
	public final Matrix4f view_to_world = new Matrix4f();
	public final Matrix4f projection = new Matrix4f();
	
	// point light in world space
	public final Vector4f point_light_1 = new Vector4f(); 

	View() {
		
	}
	
	private final double PI = 3.14159265358979323846;
	private float degreesToRadians(float degrees) {
		return degrees * (float)(PI / 180d);
	}
	private float coTangent(float angle) {
		return (float)(1f / Math.tan(angle));
	}
	
	void setWorldLight(float x, float y, float z) {

		// directly set a point light in view coordinates
		point_light_1.x = x;
		point_light_1.y = y;
		point_light_1.z = z;
	}
	// convert point light given in world coordinates to view coordinates 
	
	void setView() {
		// e.g. look at something from somewhere 
	}
	
	void translateView(float dx, float dy, float dz) {
		
		Matrix4f translation = new Matrix4f();
		translation.translate(new Vector3f(dx, dy, dz));
		Matrix4f.mul(translation, world_to_view, world_to_view);
		recalc();
	}
	
	void rotateView(float dx, float dy) {
		
		Vector3f vec = new Vector3f(-dy, dx, 0);
		float len = vec.length();
		vec.normalise();
		Matrix4f rotation = new Matrix4f();
		rotation.rotate(len, vec);
		Matrix4f.mul(rotation, world_to_view, world_to_view);
		recalc();
	}
	
	private void recalc() {
		// TODO: orthogonalize world_to_view matrix to reset accumulated error
		// TODO: or better yet, use lookat vector and keep view x axis parallel to world x-z plane
		Matrix4f.invert(world_to_view, view_to_world);
	}

	void setProjection(float fieldOfView, float nearPlane, float farPlane, int viewportWidth, int viewportHeight) {
		
		float aspectRatio = (float)viewportWidth / (float)viewportHeight;
		float yScale = this.coTangent(degreesToRadians(fieldOfView / 2f));
		float xScale = yScale / aspectRatio;
		float frustumLength = farPlane - nearPlane;
		 
		projection.m00 = xScale;
		projection.m11 = yScale;
		projection.m22 = -((farPlane + nearPlane) / frustumLength);
		projection.m23 = -1;
		projection.m32 = -((2 * nearPlane * farPlane) / frustumLength);
		projection.m33 = 0;
	}
}
