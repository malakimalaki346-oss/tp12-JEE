package ma.ens.securite.controleurs;

import ma.ens.securite.jwt.GestionnaireJwt;
import ma.ens.securite.modeles.Utilisateur;
import ma.ens.securite.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthentificationControleur {

    @Autowired
    private AuthenticationManager gestionnaireAuthentification;

    @Autowired
    private UserDetailsService serviceUtilisateurDetails;

    @Autowired
    private GestionnaireJwt gestionnaireJwt;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder encodeurMotDePasse;

    @PostMapping("/connexion")
    public ResponseEntity<Map<String, Object>> seConnecter(@RequestBody Map<String, String> requete) {
        String nomUtilisateur = requete.get("username");
        String motDePasse = requete.get("password");

        gestionnaireAuthentification.authenticate(
                new UsernamePasswordAuthenticationToken(nomUtilisateur, motDePasse)
        );

        UserDetails utilisateur = serviceUtilisateurDetails.loadUserByUsername(nomUtilisateur);
        String token = gestionnaireJwt.genererToken(utilisateur.getUsername());

        Map<String, Object> reponse = new HashMap<>();
        reponse.put("jeton", token);
        reponse.put("nomUtilisateur", nomUtilisateur);
        reponse.put("type", "Bearer");
        reponse.put("statut", "success");
        reponse.put("message", "Authentification réussie");

        return ResponseEntity.ok(reponse);
    }

    @PostMapping("/inscription")
    public ResponseEntity<Map<String, Object>> inscrireUtilisateur(@RequestBody Map<String, String> requete) {
        String nomUtilisateur = requete.get("username");
        String motDePasse = requete.get("password");
        String email = requete.get("email");

        if (utilisateurRepository.existsByNomUtilisateur(nomUtilisateur)) {
            Map<String, Object> erreur = new HashMap<>();
            erreur.put("statut", "error");
            erreur.put("message", "Nom d'utilisateur déjà existant");
            return ResponseEntity.badRequest().body(erreur);
        }

        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setNomUtilisateur(nomUtilisateur);
        nouvelUtilisateur.setMotDePasse(encodeurMotDePasse.encode(motDePasse));
        nouvelUtilisateur.setEmail(email);
        nouvelUtilisateur.setActif(true);

        utilisateurRepository.save(nouvelUtilisateur);

        Map<String, Object> reponse = new HashMap<>();
        reponse.put("statut", "success");
        reponse.put("message", "Inscription réussie");
        reponse.put("nomUtilisateur", nomUtilisateur);

        return ResponseEntity.ok(reponse);
    }

    @PostMapping("/verifier")
    public ResponseEntity<Map<String, Object>> verifierToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> reponse = new HashMap<>();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean valide = gestionnaireJwt.validerToken(token);

            reponse.put("valide", valide);
            if (valide) {
                String nomUtilisateur = gestionnaireJwt.extraireNomUtilisateur(token);
                reponse.put("nomUtilisateur", nomUtilisateur);
                reponse.put("expire", gestionnaireJwt.estTokenExpire(token));
            }
        } else {
            reponse.put("valide", false);
            reponse.put("message", "Token non fourni");
        }

        return ResponseEntity.ok(reponse);
    }
}