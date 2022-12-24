package com.ferradesign.imagesencoder;

import java.util.Objects;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {

            if (Objects.equals(args[0], "-e") && args.length == 4) {
                final String sourceImageUrl = args[1];
                final String sourceFileUrl = args[2];
                final String outputFileUrl = args[3];
                Encoder encoder = new Encoder(sourceImageUrl, sourceFileUrl, outputFileUrl);
                encoder.encode();
                //encoder.preview(); //enable for image preview on exit
                exit(0);
            } else if (Objects.equals(args[0], "-d") && args.length == 3) {
                final String encodedImageUrl = args[1];
                final String decodedFileUrl = args[2];
                Decoder decoder = new Decoder(encodedImageUrl, decodedFileUrl);
                decoder.decode();
                exit(0);
            }
        }

        System.out.println("Usage: -e [encode] imageFilename sourceFileName encodedFileName");
        System.out.println("Usage: -d [decode] encodedImageFilename decodedFileName");
    }
}