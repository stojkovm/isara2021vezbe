package rs.ac.uns.ftn.informatika.jpa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rs.ac.uns.ftn.informatika.jpa.domain.User;
import rs.ac.uns.ftn.informatika.jpa.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {
	
	private final UserRepository userRepository;
	
	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public User findUserByStreetName(String streetName) {
		return userRepository.findByStreet(streetName);
	}
	
	//-----SOFT DELETE START-----------------------------------------
	//Za potrebe restore User objekta koji je logicki obrisan upit mora biti deo transakcije, te smo zato dodali podrsku
	//o transakcijama cemo detaljnije diskutovati u narednim primerima. readOnly flag mora biti false jer modifikujemo entitet, tj. ne citamo ga
	@Transactional(readOnly = false)
	public void restoreDeletedUser(Long id) {
		userRepository.restoreById(id);
	}
	
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}
	
	public List<User> findAllDeletedUsers() {
		return userRepository.findAllOnlyDeleted();
	}
	
	public List<User> findAllUsersIncludingDeleted() {
		return userRepository.findAllIncludingDeleted();
	}
	
	@Transactional(readOnly = false)
	public void deleteUser(User user) {
		userRepository.delete(user);
	}
	//-----SOFT DELETE START-----------------------------------------
	
	@Transactional(readOnly = false)
	public User saveUser(User user) {
		return userRepository.save(user);
	}

}
