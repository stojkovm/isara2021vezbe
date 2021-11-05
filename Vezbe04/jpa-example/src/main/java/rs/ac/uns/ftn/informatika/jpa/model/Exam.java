package rs.ac.uns.ftn.informatika.jpa.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Exam {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "grade", nullable = false)
	private Integer grade;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	//fetchtype EAGER je los za performanse, te ga treba koristiti samo kada je to neophodno
	@ManyToOne(fetch = FetchType.EAGER)
	private Course course;

	/*
	 * Druga strana bidirekcione veze 1:n
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	private Student student;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	@Override
	public String toString() {
		return "Exam [id=" + id + ", grade=" + grade + ", date=" + date + "]";
	}
	
	
}
