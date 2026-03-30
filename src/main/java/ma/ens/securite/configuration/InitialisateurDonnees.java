package ma.ens.securite.configuration;

import ma.ens.securite.modeles.Profil;
import ma.ens.securite.modeles.Utilisateur;
import ma.ens.securite.repositories.ProfilRepository;
import ma.ens.securite.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class InitialisateurDonnees implements CommandLineRunner {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProfilRepository profilRepository;

    @Autowired
    private PasswordEncoder encodeurMotDePasse;

    @Override
    public void run(String... args) throws Exception {
        // Création des profils
        Profil adminProfil = profilRepository.findByNom("ADMINISTRATEUR")
                .orElseGet(() -> profilRepository.save(new Profil("ADMINISTRATEUR")));

        Profil gestionnaireProfil = profilRepository.findByNom("GESTIONNAIRE")
                .orElseGet(() -> profilRepository.save(new Profil("GESTIONNAIRE")));

        Profil utilisateurProfil = profilRepository.findByNom("UTILISATEUR")
                .orElseGet(() -> profilRepository.save(new Profil("UTILISATEUR")));

        // Création d'un utilisateur admin par défaut
        if (!utilisateurRepository.existsByNomUtilisateur("malak_admin")) {
            Utilisateur admin = new Utilisateur();
            admin.setNomUtilisateur("malak_admin");
            admin.setMotDePasse(encodeurMotDePasse.encode("Admin123!"));
            admin.setNomComplet("Malak Nait Haddou");
            admin.setEmail("malak@example.com");
            admin.setActif(true);
            admin.setProfils(Arrays.asList(adminProfil, gestionnaireProfil));
            utilisateurRepository.save(admin);
            System.out.println("Utilisateur administrateur créé: malak_admin");
        }

        // Création d'un utilisateur standard
        if (!utilisateurRepository.existsByNomUtilisateur("malak_user")) {
            Utilisateur user = new Utilisateur();
            user.setNomUtilisateur("malak_user");
            user.setMotDePasse(encodeurMotDePasse.encode("User123!"));
            user.setNomComplet("Malak Utilisateur");
            user.setEmail("malak.user@example.com");
            user.setActif(true);
            user.setProfils(Arrays.asList(utilisateurProfil));
            utilisateurRepository.save(user);
            System.out.println("Utilisateur standard créé: malak_user");
        }

        System.out.println("=== Initialisation des données terminée ===");
        System.out.println("Profils disponibles: ADMINISTRATEUR, GESTIONNAIRE, UTILISATEUR");
        System.out.println("Utilisateurs: malak_admin / Admin123! , malak_user / User123!");
    }
}