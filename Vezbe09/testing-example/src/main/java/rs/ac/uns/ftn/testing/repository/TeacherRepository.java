package rs.ac.uns.ftn.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.testing.model.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long>{

}
