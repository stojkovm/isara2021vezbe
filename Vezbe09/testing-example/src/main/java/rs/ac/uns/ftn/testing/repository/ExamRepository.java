package rs.ac.uns.ftn.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.testing.model.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {

}
