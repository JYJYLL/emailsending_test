package user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class JWTUtils {
	
    private static final String SECRET_KEY = "your-secret-key"; // JWT 비밀키 (수정할것)

    // JWT 토큰에서 이메일 추출
    public static String getEmailFromToken(String token) {
        try {
        	
        	// 토큰을 파싱하여 클레임(Claims)을 가져옴
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY) // 토큰을 검증할 비밀키 설정
                    .parseClaimsJws(token) // 토큰 파싱 및 서명 검증
                    .getBody(); // 클레임 본문 가져오기

            return claims.getSubject(); // 일반적으로 subject에 이메일이 저장
        } catch (SignatureException e) { // 서명 검증 실패 시 예외 처리
            throw new RuntimeException("Invalid JWT signature");
        }
    }
}

