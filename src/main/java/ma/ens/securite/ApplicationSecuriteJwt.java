package ma.ens.securite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationSecuriteJwt {
	public static void main(String[] args) {
		SpringApplication.run(ApplicationSecuriteJwt.class, args);
		System.out.println("=========================================");
		System.out.println("API Sécurisée JWT - Malak Nait Haddou");
		System.out.println("Application démarrée avec succès !");
		System.out.println("=========================================");
	}
}