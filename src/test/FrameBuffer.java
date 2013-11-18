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

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;

import test.Textures.Texture;

// RGB + DEPTH frame buffer
// (refactor to be more configurable when I know typical variations)
public class FrameBuffer {

	final int w,h;
	final int frameBuffer;
	final Texture texture;

	public static void setDefaultRenderTarget() {
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void selectAsRenderTarget() {
		
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
	}
	
	public FrameBuffer(int w, int h) {
		
		this.w = w;
		this.h = h;
		
//			void set(View view, int w, int h) {
//				
//				view.setProjection(60, 0.1f, 1000f, w, h);
//				glViewport(0, 0, w, h);
//			}
		

//			glGenFramebuffers(1, &m_fbo);
//			glGenTextures(1, &m_shadowMap);
//			glBindTexture(GL_TEXTURE_2D, m_shadowMap);
//			glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, WindowWidth, WindowHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
//			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
//			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);		
		
		// The framebuffer, which regroups 0, 1, or more textures, and 0 or 1 depth buffer.
		frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		
		// The texture we're going to render to
		texture = new Texture(GL_TEXTURE_2D);
		 
//		int pixelBuffer = glGenBuffers();
//		glBindBuffer(GL_PIXEL_UNPACK_BUFFER, pixelBuffer);
//		glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
		
		// LWJGLUtil.CHECKS = false;
		// Give an empty image to OpenGL ( the last "0" )
		
//		ByteBuffer buf = BufferUtils.createByteBuffer(3*w*h);
//		for (int x = 0; x < w; x++) {
//			for (int y = 0; y < h; y++) {
//				buf.put((byte)x);
//				buf.put((byte)y);
//				buf.put((byte)255);
//			}
//		}
//		buf.flip();
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
		 
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);		
		
		// The depth buffer
		int depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, w, h);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
		
		// Set "renderedTexture" as our colour attachement #0
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, texture.getName(), 0);
		 
		// Set the list of draw buffers.
		glDrawBuffers(GL_COLOR_ATTACHMENT0); // "1" is the size of DrawBuffers
		
		// Always check that our framebuffer is ok
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebuffer not complete"); 
		}
		
		

		
		// after rendering switch back to screen framebuffer
		
		// Render to the screen
		//glBindFramebuffer(GL_FRAMEBUFFER, 0);
		//glViewport(0,0,1024,768); 		
		// TODO Auto-generated constructor stub
	}
}
