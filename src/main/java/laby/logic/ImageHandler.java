package laby.logic;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author sakorpi
 */
public class ImageHandler {
    BufferedImage image;
    int width;
    int height;
    int[] pixels;
    
    public ImageHandler(String path) {
        doFile(path);  
    }
    
     private void doFile(String path) {
        try {
            System.out.println("Käsitellään kuvaa...");
            image = ImageIO.read(new File(path));
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];

            PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
            pg.grabPixels();
            
            int[][] map = writeArray(pixels, width, height);
            
            PathFinder pf = new PathFinder(map);
            map = pf.getMapWithPath();
            System.out.println("Kuva luettu.");
            int[] data = writeArrayToTable(map);
            mapToImage("images/output.bmp", width, height, data); 
            System.out.println("Valmis! Katso tiedosto output.bmp kansiossa 'images'");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void mapToImage(String path, int width, int height, int[] data) throws IOException {
        MemoryImageSource mi = new MemoryImageSource(width, height, data, 0, width);
        Image im = Toolkit.getDefaultToolkit().createImage(mi);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bi.getGraphics().drawImage(im, 0, 0, null);
        ImageIO.write(bi, "bmp", new File(path));
    }

    private int[][] writeArray(int[] data, int width, int height) throws IOException {
        int[][] map = new int[height][width];
        int xy = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = data[xy];
                //map[y][x] = colorData(map[y][x]);
                xy++;
            }
        }
        return map;
    }
    
    private int[] writeArrayToTable(int[][] array) {
        int[] data = new int[array.length * array[0].length];
        int xy = 0;
        for (int[] y : array) {
            for (int x : y) {
                data[xy] = x;
                xy++;
            }
        }
        return data;
    }
}
