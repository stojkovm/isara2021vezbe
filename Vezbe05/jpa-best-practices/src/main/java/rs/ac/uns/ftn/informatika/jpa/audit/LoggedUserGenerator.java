package rs.ac.uns.ftn.informatika.jpa.audit;

import java.util.Arrays;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

//-----AUDIT START--------------------------------------------------
/*
 * Klasa simulira vracanje trenutno ulogovanog korisnika
 * Inspiracija sa https://vladmihalcea.com/how-to-emulate-createdby-and-lastmodifiedby-from-spring-data-using-the-generatortype-hibernate-annotation/
 */
public class LoggedUserGenerator implements ValueGenerator<String> {
	
	@Override
	public String generateValue(Session session, Object owner) {
		//Cela klasa je suvisna ako se koristi Spring Security koji vec podrzava @CreatedBy i @LastModifiedBy anotacije
		//kojima bi se dobavio trenutno ulogovani korisnik pomocu
		//SecurityContextHolder.getContext().getAuthentication().getPrincipal().getUsername()
		return Arrays.asList("evaj", "andjelat", "ivanak", "lukad", "vladimirb", "milans").get(new Random().nextInt(3));
	}
//-----AUDIT END--------------------------------------------------
}
