package ma.ens.securite.repositories;

import ma.ens.securite.modeles.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);
    boolean existsByNomUtilisateur(String nomUtilisateur);
    Optional<Utilisateur> findByEmail(String email);
}