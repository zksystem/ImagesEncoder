package com.ferradesign.imagesencoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.System.exit;

public class Decoder {

    private final String imageUrl;
    private final String outputUrl;
    private BufferedImage image = null;

    private byte[] targetData;

    public Decoder(String imageFilename, String outputFilename) {
        this.imageUrl = imageFilename;
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

    public void decode() {
        loadImage();

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        int[] pixel = new int[imgHeight * imgWidth * 4];
        byte[] header = new byte[5];
        int sourceDataLength = -1;
        WritableRaster raster = image.getRaster();

        int bytesOffset = 0;
        int headerOffset = 0;
        boolean eof = false;
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                raster.getPixel(x, y, pixel);
                int r = pixel[0];
                int g = pixel[1];
                int b = pixel[2];

                int data = ((r & 0b00000011) << 6) | ((g & 0b00000111) << 3) | (b & 0b00000111);

                if (headerOffset < header.length) {
                    header[headerOffset++] = (byte) data;
                    continue;
                } else if (sourceDataLength == -1) {
                    if (header[0] != 'Z') {
                        System.out.println("Error. Invalid file signature.");
                        exit(4);
                    }
                    sourceDataLength = (int) header[1] + (int) header[2] * 0x100 + (int) header[3] * 0x10000 + (int) header[4] * 0x1000000;
                    targetData = new byte[(int) sourceDataLength];
                }

                if (bytesOffset < sourceDataLength) {
                    targetData[bytesOffset++] = (byte) data;
                } else {
                    eof = true;
                    break;
                }
            }
            if (eof) {
                break;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(outputUrl)) {
            fos.write(targetData);
            System.out.println("File decoded successfully: " + targetData.length + " bytes");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Error. Can't write output file.");
            e.printStackTrace();
        }

    }
}
