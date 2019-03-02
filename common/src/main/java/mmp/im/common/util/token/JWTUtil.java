package mmp.im.common.util.token;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class JWTUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JWTUtil.class);

    private final static String base64Secret = Base64.getEncoder().encodeToString("JWT".getBytes(Charset.defaultCharset()));

    private final static int expiresSecond = 5 * 60 * 60 * 1000;

    public static Map parseJWT(String jsonWebToken) {
        Map claims = null;
        try {
            claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(base64Secret))
                    .parseClaimsJws(jsonWebToken).getBody();

        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
        return claims;
    }

    public static String createJWT(Map<String, Object> map) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();

        // 生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // 添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setClaims(map).signWith(signatureAlgorithm, signingKey);
        // 添加Token过期时间
        if (expiresSecond >= 0) {
            long expMillis = nowMillis + expiresSecond;
            builder.setExpiration(new Date(expMillis)).setNotBefore(new Date(nowMillis));
        }

        // 生成JWT
        return builder.compact();
    }


}
