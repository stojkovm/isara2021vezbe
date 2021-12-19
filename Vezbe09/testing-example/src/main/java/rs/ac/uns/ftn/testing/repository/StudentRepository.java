package rs.ac.uns.ftn.testing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.testing.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
	Student findOneByIndex(String index);
    List<Student> findAllByLastName(String lastName);
}
