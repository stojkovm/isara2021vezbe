package rs.ac.uns.ftn.informatika.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import rs.ac.uns.ftn.informatika.jpa.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{

	//-----JSON START--------------------------------------------------
	/*
	 * Native Postgres upit:
	 * -> operator vraca JSON polje po kljucu u formi JSON objekta
	 * ->> operator vraca JSON polje po tekstu u formi cistog teksta
	 * https://www.postgresqltutorial.com/postgresql-json/
	 * 
	 * Moze se primetiti da su upiti koji trebaju da iskoriste vrednosti iz JSON objekta dosta kompleksni
	 * i zato operacije nad JSON objektima treba izbegavati. Dovoljno je da se radi jednostavno cuvanje/citanje
	 * celog JSON objekta bez dodatne obrade jer je ogranicena i podrska koju baze mogu da pruze.
	 * 
	 * Metoda pronalazi korisnika koji u JSON objektu medju adresama ima prosledjenu.
	 */
	@Query(value = "SELECT u.* FROM json_users u WHERE jsonb_exists(u.contact_info->'addresses', ?1)", nativeQuery = true)
	public User findByStreet(String street);
	//-----JSON END--------------------------------------------------


	//-----SOFT DELETE START-----------------------------------------
	//-------Primeri baratanja sa Hibernate soft delete - logickim brisanjem
	@Query(value = "UPDATE User u SET u.deleted = false WHERE u.id = ?1")
	@Modifying
	public void restoreById(Long id);

	//mora biti native query a ne JPQL da bi se izbeglo da Hibernate doda WHERE klauzulu sa deleted = false
	@Query(value = "SELECT * FROM json_users", nativeQuery = true)
	public List<User> findAllIncludingDeleted();

	//mora biti native query a ne JPQL da bi se izbeglo da Hibernate doda WHERE klauzulu sa deleted = false
	@Query(value = "SELECT * FROM json_users AS u WHERE u.deleted = true", nativeQuery = true)
	public List<User> findAllOnlyDeleted();
	//-----SOFT DELETE END-----------------------------------------

}
