package test;

import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;

import org.lwjgl.util.vector.*;

public class PointCloudFont {

    private static final  ColorModel bwColorModel =
    		new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[] {8,8,8,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);
    
    public static void main(String[] args) throws IOException {
    
    	PointCloudFont f = new PointCloudFont("Monaco", 25, 1, 1, 1, 1);
    	
    	Log.d(""+f.getGlyph('�'));
    	
    	
	}
    
    public static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖabcdefghijklmnopqrstuvwxyz";
	
    private final Map<Character, List<Vector3f>> glyphs;
    private final float dx;
    private final float dy;
    private final float dz;
    private final int zlayers;
    private final String fontname;
    private final int size;
    
    public PointCloudFont(String fontname, int size, float dx, float dy, float dz, int zlayers) throws IOException {
    	
    	glyphs = new HashMap<>(chars.length());
    	this.dx = dx;
    	this.dy = dy;
    	this.dz = dz;
    	this.zlayers = zlayers;
    	this.fontname = fontname;
    	this.size = size;
    	init();
    }
    static final List<Vector3f> missingGlyph = Arrays.asList(new Vector3f());
    
    // glyph corresponding to given char as point cloud
    public List<Vector3f> getGlyph(char ch) {
    	List<Vector3f> glyph = glyphs.get(ch);
    	return glyph != null ? glyph : missingGlyph;
    }
    
    // 3d glyph needs object for each pixel
    // glyph provides local transforms for 
    
    List<Vector3f> createGlyph(int[] pixels, int w, int h, int ox, int oy) {
    	
    	List<Vector3f> glyph = new ArrayList<>();
    	
    	int n = 0;
    	String s = "";
    	for (int y = 0; y < h; y++) {
        	for (int x = 0; x < w; x++) {
        		char p = pixels[y*w+x] == 0 ? '-' : '*';
        		s += p;
        		if (p == '*') {
        			n++;
        			for (int z = 0; z < zlayers; z++) {
            			glyph.add(new Vector3f((x - ox)*dx, -(y - oy)*dy, z*dz));
        			}
        		}
        	}
        	s += "\n";
    	}
    	Log.d("glyph:\n"+s);
    	Log.d("set pixels: "+n);
    	return glyph;
    	
    	//om.new Object();
    }
    
	void init() throws IOException {

        BufferedImage im = new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = (Graphics2D) im.getGraphics();
        
        g.setFont(new Font(this.fontname, Font.PLAIN, this.size));
        g.setColor(Color.WHITE);
        
        FontMetrics fm = g.getFontMetrics();
        int desc = fm.getDescent();
        int height = fm.getHeight();
        int asc = fm.getAscent();
        int lead = fm.getLeading();
        int madv = fm.getMaxAdvance();
        int masc = fm.getMaxAscent();
        int mdes = fm.getMaxDescent();
        Log.d("fm: hei="+height);
        Log.d("fm: asc="+asc);
        Log.d("fm: des="+desc);
        Log.d("fm: lea="+lead);
        Log.d("fm: masc="+masc);
        Log.d("fm: mdes="+mdes);
        Log.d("fm: madv="+madv);
        Log.d("fm: w="+Arrays.toString(g.getFontMetrics().getWidths()));
        
        // for each char, clear buffer, draw char, extract WxH rect,
        for (int i = 0; i < chars.length(); i++) {
        	char ch = chars.charAt(i);
        	g.clearRect(0, 0, im.getWidth(), im.getHeight());
        	g.drawChars(new char[]{ch}, 0, 1, 0, im.getHeight() - mdes );
        	int w = madv;
        	int h = masc+mdes;
        	int[] pixels = im.getRaster().getPixels(0, im.getHeight() - h, w, h, (int[])null);
        	
        	glyphs.put(ch, createGlyph(pixels, w, h, 0, h - mdes));
        	Log.d("pixel count = "+pixels.length);
            //ImageIO.write(im, "PNG", new File("img_"+ch+".png"));
        }
        
        //g.drawChars(new char[]{'A','B','C','D','E'}, 0, 5, 0, 99);
        
        //Raster r = texImage.getRaster();
        //ImageIO.write(texImag, "PNG", new File("img.png"));
        
        
        //byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData(); 
	}
}
