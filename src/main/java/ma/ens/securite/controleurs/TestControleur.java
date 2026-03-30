package ma.ens.securite.controleurs;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestControleur {

    @GetMapping("/public/accueil")
    public Map<String, String> accueilPublic() {
        Map<String, String> reponse = new HashMap<>();
        reponse.put("message", "Bienvenue sur l'API publique");
        reponse.put("statut", "accessible");
        return reponse;
    }

    @GetMapping("/utilisateur/profil")
    @PreAuthorize("hasAnyRole('UTILISATEUR', 'GESTIONNAIRE', 'ADMINISTRATEUR')")
    public Map<String, Object> profilUtilisateur() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> reponse = new HashMap<>();
        reponse.put("message", "Accès autorisé à l'espace utilisateur");
        reponse.put("utilisateur", auth.getName());
        reponse.put("autorites", auth.getAuthorities());
        return reponse;
    }

    @GetMapping("/gestionnaire/tableau-bord")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE', 'ADMINISTRATEUR')")
    public Map<String, Object> tableauBordGestionnaire() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> reponse = new HashMap<>();
        reponse.put("message", "Tableau de bord gestionnaire");
        reponse.put("utilisateur", auth.getName());
        reponse.put("statistiques", Map.of(
                "totalUtilisateurs", 150,
                "totalCommandes", 45,
                "chiffreAffaires", 12500.50
        ));
        return reponse;
    }

    @GetMapping("/admin/parametres")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public Map<String, Object> parametresAdministration() {
        Map<String, Object> reponse = new HashMap<>();
        reponse.put("message", "Configuration système");
        reponse.put("version", "2.0.0");
        reponse.put("modeMaintenance", false);
        reponse.put("logsActives", true);
        return reponse;
    }

    @GetMapping("/admin/utilisateurs")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public Map<String, Object> listerUtilisateurs() {
        Map<String, Object> reponse = new HashMap<>();
        reponse.put("message", "Liste des utilisateurs (réservé admin)");
        reponse.put("utilisateurs", new String[]{"admin", "gestionnaire1", "utilisateur1"});
        return reponse;
    }
}