package test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import org.lwjgl.BufferUtils;

// utility for generating images
public class ImageGen {
	
	final Random rnd = new Random(1234);
	final int w;
	final int h;
	final int bpp;
	final byte[] i;
	
	public ImageGen(int w, int h) {
		this.w = w;
		this.h = h;
		this.bpp = 3;
		i = new byte[w*h*3];
		Arrays.fill(i, (byte)0);
	}
	
	public ByteBuffer buffer() {
		
		ByteBuffer b = BufferUtils.createByteBuffer(i.length);
		b.put(i);
		b.flip();
		return b;
	}
	public ImageGen img1(int lines, int len) {
		randomAdditiveLines(lines, len, 25, 25, 42);
		return this;
	}
	
	public ImageGen randomAdditiveLines(int count, int len, int r, int g, int b) {
		
		for (int i = 0; i < count; i++) {
			int x = rnd.nextInt(w);
			int y = rnd.nextInt(h);
			int stride = bpp*((i & 1) == 0 ? 1 : w);
			addline(x, y, len, r, g, b, stride);
		}
		return this;
	}
	
	public void addline(int x, int y, int len, int r, int g, int b, int stride) {
		
		int o = (y*w + x)*bpp;
		for (int p = 0; p < len; p++) {
			i[o] += r;
			i[o+1] += g;
			i[o+2] += b;
			o += stride;
			if (o >= i.length) {
				break;
			}
		}
	}

}
