package org.example.apitor.security.tracker;

import org.example.apitor.security.config.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

@Component
public class ProjectTokenUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int UUID_BYTE_LENGTH = 16;
    private static final int HASH_BYTE_LENGTH = 18;
    private static final int TOTAL_TOKEN_LENGTH = UUID_BYTE_LENGTH + HASH_BYTE_LENGTH;
    private final byte[] secretKeyBytes;
    public ProjectTokenUtil(JwtProperties jwtProperties) {
        this.secretKeyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
    }

    public String generateProjectToken(String projectKey) {
        UUID uuid = UUID.fromString(projectKey);
        byte[] uuidBytes = new byte[UUID_BYTE_LENGTH];
        ByteBuffer.wrap(uuidBytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        byte[] hashBytes = generateHmac(uuidBytes);
        byte[] tokenBytes = new byte[TOTAL_TOKEN_LENGTH];
        System.arraycopy(uuidBytes, 0, tokenBytes, 0, UUID_BYTE_LENGTH);
        System.arraycopy(hashBytes, 0, tokenBytes, UUID_BYTE_LENGTH, HASH_BYTE_LENGTH);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public String extractProjectToken(String token) {
        try {
            byte[] tokenBytes = Base64.getUrlDecoder().decode(token);
            if (tokenBytes.length != TOTAL_TOKEN_LENGTH) {
                return null; 
            }
            byte[] uuidBytes = Arrays.copyOfRange(tokenBytes, 0, UUID_BYTE_LENGTH);
            byte[] clientHashBytes = Arrays.copyOfRange(tokenBytes, UUID_BYTE_LENGTH, TOTAL_TOKEN_LENGTH);
            byte[] expectedHashBytes = generateHmac(uuidBytes);
            if (!MessageDigest.isEqual(clientHashBytes, expectedHashBytes)) {
                return null; 
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(uuidBytes);
            long highBits = byteBuffer.getLong();
            long lowBits = byteBuffer.getLong();
            return new UUID(highBits, lowBits).toString();

        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    private byte[] generateHmac(byte[] data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] fullHash = mac.doFinal(data);

            return Arrays.copyOf(fullHash, HASH_BYTE_LENGTH);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Failed to initialize cryptographic HMAC utility", e);
        }
    }
}