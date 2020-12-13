package laby.logic;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.scene.control.Label;

/**
 *
 * @author sakorpi
 */
public class ImageHandler {
    BufferedImage image;
    int width;
    int height;
    int[] pixels;
    int[][] map;
    Label info;
    
    public ImageHandler(String path, Label info) {
        this.info = info;
        readFile(path);  
    }
    
    private void readFile(String path) {
        try {
            info.setText("Käsitellään kuvaa...");
            image = ImageIO.read(new File(path));
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];

            PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
            pg.grabPixels();
            
            map = writeArray(pixels, width, height);
            info.setText("Kuva luettu.");
           
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
     
    public void doFile(int[][] modifiedMap, int thickness) {
        try {
            this.map = modifiedMap;
            PathFinder pf = new PathFinder(map, info, thickness);
            map = pf.getMapWithPath();
            if (map == null) return;
            int[] data = writeArrayToTable(map);
            mapToImage("images/output.bmp", width, height, data);
            if (info.getText().contains("Reittiä ei löytynyt")) return;
            info.setText("Valmis! Jos haluat reitin talteen,\nkopioi kuva images/output.bmp");
        } catch (Exception e) {
            info.setText("Error: " + e.getMessage());
        }
    }
     
    public int[][] getImageAsMap() {
        return map;
    }
    

    private void mapToImage(String path, int width, int height, int[] data) {
        try {
            MemoryImageSource mi = new MemoryImageSource(width, height, data, 0, width);
            Image im = Toolkit.getDefaultToolkit().createImage(mi);
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bi.getGraphics().drawImage(im, 0, 0, null);
            ImageIO.write(bi, "bmp", new File(path));
        } catch (Exception e) {
            info.setText("Error: " + e.getMessage());
        }
    }

    private int[][] writeArray(int[] data, int width, int height) {
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
