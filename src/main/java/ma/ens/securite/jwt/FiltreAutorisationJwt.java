package ma.ens.securite.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class FiltreAutorisationJwt extends OncePerRequestFilter {

    @Value("${jwt.prefix.passage}")
    private String prefixPassage;

    private final GestionnaireJwt gestionnaireJwt;
    private final UserDetailsService userDetailsService;

    public FiltreAutorisationJwt(GestionnaireJwt gestionnaireJwt, UserDetailsService userDetailsService) {
        this.gestionnaireJwt = gestionnaireJwt;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String enTeteAuth = request.getHeader("Authorization");

        if (enTeteAuth != null && enTeteAuth.startsWith(prefixPassage + " ")) {
            String token = enTeteAuth.substring(7);
            String nomUtilisateur = gestionnaireJwt.extraireNomUtilisateur(token);

            if (nomUtilisateur != null &&
                    gestionnaireJwt.validerToken(token) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails utilisateur = userDetailsService.loadUserByUsername(nomUtilisateur);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(utilisateur, null, utilisateur.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}