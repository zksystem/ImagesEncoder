package com.ferradesign.imagesencoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.lang.System.exit;

public class Encoder {

    private final String imageUrl;
    private final String outputUrl;
    private final String dataUrl;
    private BufferedImage image = null;
    private  byte[] sourceData;

    public Encoder(String imageFilename, String dataFilename, String outputFilename) {
        this.imageUrl = imageFilename;
        this.dataUrl = dataFilename;
        this.outputUrl = outputFilename;
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File(imageUrl));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            exit(1);
        }
    }

    private void loadFile() {
        File file = new File(dataUrl);
        long fileLength = file.length();
        if(fileLength > Integer.MAX_VALUE) {
            System.out.println("Error. The maximum file length is: " + Integer.MAX_VALUE);
            exit(3);
        }
        sourceData = new byte[(int) fileLength];

        try(FileInputStream fis = new FileInputStream(file)) {
            fis.read(sourceData);
        } catch (IOException e) {
            System.out.println("Error. Can't read data fil. " + e.getMessage());
            exit(3);
        }
    }

    public void encode() {
        loadImage();
        loadFile();

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        int[] pixel = new int[imgHeight * imgWidth * 4];
        byte[] header = new byte[5];

        int fileSizeLimit = imgHeight * imgWidth - header.length;
        int fileSize = sourceData.length;

        System.out.println("Image size: " + imgWidth + "x" + imgHeight + " total space: " + fileSizeLimit);
        System.out.println("File size: " + fileSize);

        if(fileSize > fileSizeLimit) {
            System.out.println("Error. File doesn't fits to image space");
            exit(2);
        }

        header[0] = 'Z';
        header[1] = (byte)(fileSize & 0x000000ff);
        header[2] = (byte)((fileSize & 0x0000ff00) >> 8);
        header[3] = (byte)((fileSize & 0x00ff0000) >> 16);
        header[4] = (byte)((fileSize & 0xff000000) >> 24);

        WritableRaster raster = image.getRaster();

        int bytesOffset = 0;
        int headerOffset = 0;
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                raster.getPixel(x, y, pixel);
                int r = pixel[0];
                int g = pixel[1];
                int b = pixel[2];

                int data = 0;

                if(headerOffset < header.length) {
                    data = header[headerOffset++];
                } else if(bytesOffset < sourceData.length) {
                    data = sourceData[bytesOffset++];
                }

                pixel[0] = r & 0b11111100 | ((data & 0b11000000) >> 6); //RR......
                pixel[1] = g & 0b11111000 | ((data & 0b00111000) >> 3); //..GGG...
                pixel[2] = b & 0b11111000 | (data & 0b00000111);        //.....BBB

                raster.setPixel(x, y, pixel);
            }
        }

        writeImage(image, outputUrl, "png");
    }

    public static void writeImage(Image image, String filename, String ext) {
        try {
            ImageIO.write((RenderedImage) image, ext, new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preview() {
        JFrame frame = new JFrame("Preview");
        frame.setSize(1024, 768);
        JLabel label = new JLabel("", new ImageIcon(image), 0);
        frame.add(label);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
