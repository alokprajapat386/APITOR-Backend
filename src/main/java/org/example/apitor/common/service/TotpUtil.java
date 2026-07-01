package org.example.apitor.common.service;

import org.example.apitor.security.config.JwtProperties;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.time.Instant;
import java.util.Base64;

@Service
public class TotpUtil {
    private final JwtProperties jwtProperties;
    public TotpUtil(JwtProperties jwtProperties){
        this.jwtProperties=jwtProperties;
    }

    public String generateOtp(String email, long expiryTimestamp){
        try {
            String message = email + "_" + expiryTimestamp;

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] hashBytes = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            int number = Math.abs(java.util.Arrays.hashCode(hashBytes));
            return String.format("%06d", number % 1000000);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ProviderException("Crypto execution failed", e);
        }
    }

    public String verifyOtpAndExtractEmail(String inputOtp, String token) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String[] payloadData = new String(decodedBytes).split(":");

            String tokenEmail = payloadData[0];
            long tokenExpiry = Long.parseLong(payloadData[1]);

            if (Instant.now().getEpochSecond() > tokenExpiry) {
                throw new BadCredentialsException("OTP expired");
            }
            String computedOtp = generateOtp(tokenEmail, tokenExpiry);
            if(computedOtp.equals(inputOtp)){
                return tokenEmail;
            }else{
                throw  new BadCredentialsException("Invalid OTP");
            }
        } catch ( IllegalArgumentException | IndexOutOfBoundsException | NullPointerException e  ) {
            throw new BadCredentialsException("Invalid token payload data");
        }
    }
}
