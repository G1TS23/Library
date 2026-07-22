package org.library.resource;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Map;

public class JwtTestProfile implements QuarkusTestProfile {
    public static final PrivateKey PRIVATE_KEY;
    private static final String PUBLIC_KEY_B64;

    static {
        try {
            SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");
            rng.setSeed("library-jwt-test-seed".getBytes(StandardCharsets.UTF_8));
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048, rng);
            KeyPair pair = gen.generateKeyPair();
            PRIVATE_KEY = pair.getPrivate();
            PUBLIC_KEY_B64 = Base64.getEncoder()
                    .encodeToString(pair.getPublic().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot generate RSA key pair for tests", e);
        }
    }

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("mp.jwt.verify.publickey", PUBLIC_KEY_B64);
    }
}
