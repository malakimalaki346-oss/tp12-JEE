package ma.ens.securite.services;

import ma.ens.securite.modeles.Utilisateur;
import ma.ens.securite.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class ServiceUtilisateurDetails implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String nomUtilisateur) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(nomUtilisateur)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + nomUtilisateur));

        return new User(
                utilisateur.getNomUtilisateur(),
                utilisateur.getMotDePasse(),
                utilisateur.isActif(),
                true,
                true,
                true,
                utilisateur.getProfils().stream()
                        .map(profil -> new SimpleGrantedAuthority("ROLE_" + profil.getNom()))
                        .collect(Collectors.toList())
        );
    }
}