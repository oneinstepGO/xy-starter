package com.oneinstep.starter.security.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.oneinstep.starter.security.bean.dto.res.GoogleAuthDTO;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * 谷歌验证器服务
 */
@Service
@Slf4j
public class GoogleAuthenticatorService {

    private final GoogleAuthenticator googleAuthenticator;

    public GoogleAuthDTO genGoogleAuth(Long accountOwnerId) {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        String secretKey = key.getKey();
        String qrCode = generateQRCode(secretKey, String.valueOf(accountOwnerId));
        GoogleAuthDTO googleAuthDTO = new GoogleAuthDTO();
        googleAuthDTO.setSecretKey(secretKey);
        googleAuthDTO.setQrCode(qrCode);
        return googleAuthDTO;
    }

    public GoogleAuthenticatorService() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig
                .GoogleAuthenticatorConfigBuilder()
                .build();
        googleAuthenticator = new GoogleAuthenticator(config);
    }

    public String generateSecretKey() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        return key.getKey();
    }

    public boolean validateCode(String secretKey, int code) {
        return googleAuthenticator.authorize(secretKey, code);
    }

    private String generateQRCode(String secretKey, String accountName) {
        String otpAuthURL = getOtpAuthURL(accountName, secretKey);
        return generateQRCodeURL(otpAuthURL);
    }

    private String getOtpAuthURL(String accountName, String secretKey) {
        return "otpauth://totp/" + accountName + "?secret=" + secretKey + "&issuer=tbGame";
    }

    private String generateQRCodeURL(String otpAuthURL) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage qrCodeImage = toBufferedImage(bitMatrix);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", outputStream);
            byte[] qrCodeBytes = outputStream.toByteArray();
            String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
            return "data:image/png;base64," + qrCodeBase64;
        } catch (Exception e) {
            log.info("generateQRCodeURL error: {}", e.getMessage());
        }
        return null;
    }

    private BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

}
