package rs.ac.uns.ftn.testing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.testing.dto.ExamDTO;
import rs.ac.uns.ftn.testing.model.Course;
import rs.ac.uns.ftn.testing.model.Exam;
import rs.ac.uns.ftn.testing.model.Student;
import rs.ac.uns.ftn.testing.service.CourseService;
import rs.ac.uns.ftn.testing.service.ExamService;
import rs.ac.uns.ftn.testing.service.StudentService;

@RestController
@RequestMapping(value = "api/exams")
public class ExamController {
	@Autowired
	ExamService examService;

	@Autowired
	StudentService studentService;

	@Autowired
	CourseService courseService;

	
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<ExamDTO> createExam(@RequestBody ExamDTO examDTO) {
		
		// a new exam must have student and course defined
		if (examDTO.getStudent() == null || examDTO.getCourse() == null) {
			return new ResponseEntity<ExamDTO>(HttpStatus.BAD_REQUEST);
		}
		
		Student student = studentService.findOne(examDTO.getStudent().getId());
		Course course = courseService.findOne(examDTO.getCourse().getId());
		
		if (student == null || course == null) {
			return new ResponseEntity<ExamDTO>(HttpStatus.BAD_REQUEST);
		}

		Exam exam = new Exam();
		exam.setDate(examDTO.getDate());
		exam.setGrade(examDTO.getGrade());
		exam.setStudent(student);
		exam.setCourse(course);

		exam = examService.save(exam);
		return new ResponseEntity<ExamDTO>(new ExamDTO(exam), HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
	public ResponseEntity<ExamDTO> updateExam(@RequestBody ExamDTO examDTO) {
		
		// an exam must exist
		Exam exam = examService.findOne(examDTO.getId());
		if (exam == null) {
			return new ResponseEntity<ExamDTO>(HttpStatus.BAD_REQUEST);
		}
		// we allow changing date and points for an exam only
		exam.setDate(examDTO.getDate());
		exam.setGrade(examDTO.getGrade());

		exam = examService.save(exam);
		return new ResponseEntity<ExamDTO>(new ExamDTO(exam), HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
		
		Exam exam = examService.findOne(id);
		
		if (exam != null) {
			examService.remove(id);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} else {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
}
