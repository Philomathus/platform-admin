package com.qiqilm.server.admin.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Hashtable;

public abstract class QRCodeGenerator {
    public static BufferedImage generateQRCodeImage( String text, int width, int height ) {
        final QRCodeWriter qrCodeWriter = new QRCodeWriter();
        final Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put( EncodeHintType.CHARACTER_SET, "UTF-8" );

        final BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode( text, BarcodeFormat.QR_CODE, width, height, hints );
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        final BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                int grayValue = bitMatrix.get(x, y) ? 0 : 0xFFFFFF;
                image.setRGB( x, y, grayValue );
            }
        }

        return image;
    }

    public static String generateQRCodeBase64( String data, int width, int height ) {
        final BufferedImage qrImage = generateQRCodeImage( data, width, height );

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(qrImage, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final byte[] imageBytes = baos.toByteArray();

        return Base64.getEncoder().encodeToString( imageBytes );
    }
}
