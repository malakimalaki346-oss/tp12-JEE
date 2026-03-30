package ma.ens.securite.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class GestionnaireJwt {

    @Value("${jwt.cle.secrete}")
    private String cleSecrete;

    @Value("${jwt.duree.expiration}")
    private long dureeExpiration;

    private Key getCleSignature() {
        return Keys.hmacShaKeyFor(cleSecrete.getBytes());
    }

    public String genererToken(String nomUtilisateur) {
        Date maintenant = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + dureeExpiration);

        return Jwts.builder()
                .setSubject(nomUtilisateur)
                .setIssuedAt(maintenant)
                .setExpiration(expiration)
                .signWith(getCleSignature(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraireNomUtilisateur(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getCleSignature())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validerToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getCleSignature())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Token expiré: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token non supporté: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token mal formé: " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("Signature invalide: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Token invalide: " + e.getMessage());
        }
        return false;
    }

    public boolean estTokenExpire(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getCleSignature())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}