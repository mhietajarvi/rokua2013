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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.ImageIOImageData;

// things that should stay static for one frame
public class View {

	int envCubeTexture;
	int envCubeSampler = 0;

	// load cube texture to texture unit 0
	public void loadTexture(String file, int textureUnit) {
		
	}
	int[] cube_map_side = {
			GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
			GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
			GL_TEXTURE_CUBE_MAP_POSITIVE_Z, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };
	String[] cube_map_file = {
			"right.jpg", "left.jpg", "top.jpg", "bottom.jpg", "back.jpg", "front.jpg" };
	
	byte[] gen = new byte[1024*1024*3];
	{
		Arrays.fill(gen, (byte)255);
	}

	// load texture to: texture unit, generate id
	// select texture to be used
	
	public void loadCubeTexture(String directory) throws IOException {

    	glActiveTexture(GL_TEXTURE0 + envCubeSampler);
    	
    	envCubeTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, envCubeTexture);
		
		Random r = new Random(234);
		
		WTF? this works if side == 1, what is going on???
		
		int side = 3;
		int n = side*side*3;
		byte[] tmp = new byte[n];
    	
		for (int i = 0; i < 6; i++) {

			ByteBuffer b = BufferUtils.createByteBuffer(n); //imageData.loadImage(new BufferedInputStream(is), false, null); // new int[]{}
			r.nextBytes(tmp);
			b.put(tmp);
//			for (int j = 0; j < 4; j++) {
//				b.put(new byte[]{ (byte)r.nextBytes(bytes);Radn55, (byte)55, (byte)255 });
//			}
			b.flip();
	        glTexImage2D(cube_map_side[i], 0,
            GL_RGB,
            side,
            side,
            0, 
            GL_RGB,
            GL_UNSIGNED_BYTE,
            b);
			
			
//			InputStream is = new FileInputStream(new File(directory, cube_map_file[i]));
//			ImageIOImageData imageData = new ImageIOImageData();
//	    	ByteBuffer textureBuffer = imageData.loadImage(new BufferedInputStream(is), false, null); // new int[]{}
//	    	
//	    	Log.d("tex: pos="+textureBuffer.position()+", limit="+textureBuffer.limit());
//	    	Log.d("img: width="+imageData.getWidth()+", height="+imageData.getHeight());
//	    	Log.d("img: twidth="+imageData.getTexWidth()+", theight="+imageData.getTexHeight());
//	    	Log.d("img: depth="+imageData.getDepth());
//
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
//	    	
//	    	// GL33.glGenSamplers()
//	    	
//			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
//			
//	        glTexImage2D(cube_map_side[i], 0,
//	                GL_RGB,
//	                imageData.getTexWidth(),
//	                imageData.getTexHeight(),
//	                0, 
//	                imageData.getDepth() == 32 ? GL_RGBA : GL_RGB,
//	                GL_UNSIGNED_BYTE,
//	                textureBuffer);
		}
        
    	
		
		//TextureLoader.getTexture("JPG", in)
		
		//BufferedImage img = ImageIO.read(new File(directory, "top.jpg"));
		//Raster r = img.getData();
		// r.
		//glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, internalformat, width, height, border, format, type, pixels_buffer_offset);
		
	}
	
	public final Matrix4f world_to_view = new Matrix4f();
	public final Matrix4f projection = new Matrix4f();
	
	// lights temporarily here...
	// point light in view space
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
	
	void setViewLight(float x, float y, float z) {

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
		orthogonalize();
	}
	
	void rotateView(float dx, float dy) {
		
		Vector3f vec = new Vector3f(-dy, dx, 0);
		float len = vec.length();
		vec.normalise();
		Matrix4f rotation = new Matrix4f();
		rotation.rotate(len, vec);
		Matrix4f.mul(rotation, world_to_view, world_to_view);
		orthogonalize();
	}
	
	void orthogonalize() {
		// TODO: orthogonalize world_to_view matrix to reset accumulated error
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
