package rs.ac.uns.ftn.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.testing.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

}
