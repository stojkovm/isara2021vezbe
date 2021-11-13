package rs.ac.uns.ftn.informatika.jpa;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import rs.ac.uns.ftn.informatika.jpa.domain.ContactInfo;
import rs.ac.uns.ftn.informatika.jpa.domain.User;
import rs.ac.uns.ftn.informatika.jpa.service.UserService;

@SpringBootApplication
public class JpaBestPracticesApplication {

	public static void main(String[] args) {
		
		ApplicationContext ctx = SpringApplication.run(JpaBestPracticesApplication.class, args);
		
		//Primer upisa i citanja JSON objekta u kolonu jednog reda u bazi
		UserService userService = ctx.getBean(UserService.class);
		User user1 = new User();
		user1.setName("Pera Peric");
		//------- Kreiranje prvog objekta koji ce se kao JSON upisati u bazu
		ContactInfo cInfo = new ContactInfo();
		cInfo.setAddresses(Arrays.asList("Ulica 1", "Ulica 2"));
		cInfo.setEmailAdresses(Arrays.asList("pera@pera.com", "pera@gmail.com"));
		cInfo.setPhoneNumbers(Arrays.asList("555-333", "555-334"));
		//
		user1.setContactInfo(cInfo);
		//testiranje upisa JSON objekta i audit podrske
		user1 = userService.saveUser(user1);
		//-------------------------------------------------------------------
		
		//------- Kreiranje drugog objekta koji ce se kao JSON upisati u bazu
		User user2 = new User();
		user2.setName("Mika Mikic");
		cInfo = new ContactInfo();
		cInfo.setAddresses(Arrays.asList("Ulica 3", "Ulica 4"));
		cInfo.setEmailAdresses(Arrays.asList("mika@mika.com", "mika@gmail.com"));
		cInfo.setPhoneNumbers(Arrays.asList("555-444", "555-445"));
		user2.setContactInfo(cInfo);
		//testiranje upisa JSON objekta i audit podrske
		user2 = userService.saveUser(user2);
		//--------------------------------------------------------------------
		
		//--------Testiranje komplikovanijeg upita koji koristi deo JSON objekta iz baze
		System.out.println("\n>>>>>>\n>>>>>>>>>> JSONB QUERY: " + userService.findUserByStreetName("Ulica 3") + "\n>>>>>>");
		//--------------------------------------------------------------------
		
		//--------Testiranje logickog brisanja
		userService.deleteUser(user1);
		System.out.println("\n>>>>>>\n>>>>>>>>>> Svi aktivni korisnici: " + userService.findAllUsers() + "\n>>>>>>");
		System.out.println("\n>>>>>>\n>>>>>>>>>> Svi obrisani korisnici: " + userService.findAllDeletedUsers() + "\n>>>>>>");
		System.out.println("\n>>>>>>\n>>>>>>>>>> Svi aktivni i obrisani korisnici: " + userService.findAllUsersIncludingDeleted() + "\n>>>>>>");
		userService.restoreDeletedUser(1L);
		System.out.println("\n>>>>>>\n>>>>>>>>>> Svi aktivni korisnici posle vracanja obrisanog: " + userService.findAllUsers() + "\n>>>>>>");
		//--------------------------------------------------------------------
	}

}
