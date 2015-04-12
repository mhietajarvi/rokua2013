package renderer.texture;

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
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

class LoadedImage {

	static final ColorModel GL_COLOR_MODEL = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[] { 8, 8, 8, 0 }, false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

	final int width;
	final int height;
	final ByteBuffer buffer;

	LoadedImage(InputStream is, boolean flipx, boolean flipy) throws IOException {

		BufferedImage bi = ImageIO.read(new BufferedInputStream(is));

		// bi.getRaster().getDataBuffer()

		width = bi.getWidth();
		height = bi.getHeight();

		WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 3, null);
		BufferedImage texImage = new BufferedImage(GL_COLOR_MODEL, raster, false, new Hashtable());
		// copy the source image into the produced image
		Graphics2D g = (Graphics2D) texImage.getGraphics();

		// if (flipped) {
		// g.scale(1,-1);
		// g.drawImage(image,0,-height,null);
		// } else {
		g.scale(flipx ? -1 : 1, flipy ? -1 : 1);
		g.drawImage(bi, flipx ? -width : 0, flipy ? -height : 0, null);
		// }
		// g.setFont(new Font("Monospaced", Font.PLAIN, 100));
		// g.setColor(Color.WHITE);
		// g.drawChars(new char[]{'A','B','C','D','E'}, 0, 5, 50, 50);

		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

		buffer = BufferUtils.createByteBuffer(data.length); // ByteBuffer.allocateDirect(data.length);
		// buffer.order(ByteOrder.nativeOrder());
		buffer.put(data, 0, data.length);
		buffer.flip();
		g.dispose();

		// ByteBuffer textureBuffer = imageData.loadImage(new BufferedInputStream(is), false, null); // new int[]{}
	}
}