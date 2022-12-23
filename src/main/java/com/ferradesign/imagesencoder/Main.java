package com.ferradesign.imagesencoder;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting...");

        Encoder encoder = new Encoder(
                "/Users/konstantinzuykov/Desktop/encoder_test/image.png",
                "/Users/konstantinzuykov/Desktop/encoder_test/sample-2mb-text-file.txt",
                "/Users/konstantinzuykov/Desktop/encoder_test/out.png");
        encoder.encode();
        encoder.preview();

        System.out.println("Normal shutdown");
    }
}