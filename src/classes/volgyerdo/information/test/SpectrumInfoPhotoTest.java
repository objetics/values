/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;
import volgyerdo.commons.primitive.PrimitiveUtils;
import volgyerdo.information.ShannonInformation;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class SpectrumInfoPhotoTest {

    private static DecimalFormat format = new DecimalFormat("0");
    private static int r = 7;
    private static int maxInfo = (2 * r - 1) * (2 * r - 1) * 8;

    public static void main(String[] args) {
        coloredPhotoInfo("map.jpg", "map-info.jpg");
        grayPhotoInfo("map.jpg", "map-info-gray.jpg");
    }

    private static void coloredPhotoInfo(String image, String resultImage) {
        try {
            BufferedImage source = ImageIO.read(new File(SpectrumInfoPhotoTest.class.getResource(image).getFile()));
            BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
            int[][] red = new int[r * 2][r * 2];
            int[][] green = new int[r * 2][r * 2];
            int[][] blue = new int[r * 2][r * 2];
            for (int x = r; x < source.getWidth() - r; x++) {
                for (int y = r; y < source.getHeight() - r; y++) {
                    for (int i = -r; i < r; i++) {
                        for (int j = -r; j < r; j++) {
                            int rgb = source.getRGB(x + i, y + j);
                            red[i + r][j + r] = (rgb >> 16) & 0x000000FF;
                            green[i + r][j + r] = (rgb >> 8) & 0x000000FF;
                            blue[i + r][j + r] = (rgb) & 0x000000FF;
                        }
                    }
                    int red1 = PrimitiveUtils.toInt((ShannonInformation.information(red) - 1538.) / maxInfo * 255.);
                    int green1 = PrimitiveUtils.toInt((ShannonInformation.information(green) - 1538.) / maxInfo * 255.);
                    int blue1 = PrimitiveUtils.toInt((ShannonInformation.information(blue) - 1538.) / maxInfo * 255.);
                    int rgb1 = (red1 << 16) | (green1 << 8) | blue1;
                    result.setRGB(x, y, rgb1);
                }
            }
            ImageIO.write(result, "jpg", new File(resultImage));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void grayPhotoInfo(String image, String resultImage) {
        try {
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_EXPONENT;
            BufferedImage source = ImageIO.read(new File(SpectrumInfoPhotoTest.class.getResource(image).getFile()));
            BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
            int[][] gray = new int[r * 2][r * 2];
            for (int x = r; x < source.getWidth() - r; x++) {
                for (int y = r; y < source.getHeight() - r; y++) {
                    for (int i = -r; i < r; i++) {
                        for (int j = -r; j < r; j++) {
                            int rgb = source.getRGB(x + i, y + j);
                            int red = (rgb >> 16) & 0x000000FF;
                            int green = (rgb >> 8) & 0x000000FF;
                            int blue = (rgb) & 0x000000FF;
                            gray[i + r][j + r] = (red + green + blue) / 3;
                        }
                    }
                    double grayInfo = (ShannonInformation.information(gray) - 1538.) / maxInfo * 255.;
                    if (grayInfo < min) {
                        min = grayInfo;
                    }
                    if (grayInfo > max) {
                        max = grayInfo;
                    }
                    int gray1 = PrimitiveUtils.toInt(grayInfo);
                    int rgb1 = (gray1 << 16) | (gray1 << 8) | gray1;
                    result.setRGB(x, y, rgb1);
                }
            }
            System.out.println(format.format(min) + " - " + format.format(max));
            ImageIO.write(result, "jpg", new File(resultImage));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
