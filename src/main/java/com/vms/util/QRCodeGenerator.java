package com.vms.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility component for generating QR code tokens and images.
 *
 * <p>Uses the ZXing (Zebra Crossing) library to produce 300×300 pixel
 * PNG images encoded with UTF-8 character set and minimal margin.</p>
 *
 * @see com.google.zxing.qrcode.QRCodeWriter
 */
@Component
public class QRCodeGenerator {

    /** Default QR code image width in pixels. */
    private static final int QR_CODE_WIDTH = 300;

    /** Default QR code image height in pixels. */
    private static final int QR_CODE_HEIGHT = 300;

    /**
     * Generates a unique QR code token using a random UUID.
     *
     * @return a UUID string to be embedded in a QR code
     */
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a QR code image as PNG bytes from the given content string.
     *
     * @param content the text to encode in the QR code
     * @return the QR code image as a PNG byte array
     * @throws WriterException if encoding fails
     * @throws IOException     if writing the image stream fails
     */
    public byte[] generateQRCodeImage(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(
                content, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }
}
