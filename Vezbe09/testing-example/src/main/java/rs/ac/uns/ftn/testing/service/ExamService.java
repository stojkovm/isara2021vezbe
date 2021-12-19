package rs.ac.uns.ftn.testing.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.testing.model.Exam;
import rs.ac.uns.ftn.testing.repository.ExamRepository;

@Service
public class ExamService {
	@Autowired
	ExamRepository examRepository;
	
	public Exam findOne(Long id) {
		return examRepository.findById(id).orElseGet(null);
	}

	public List<Exam> findAll() {
		return examRepository.findAll();
	}
	
	public Exam save(Exam exam) {
		return examRepository.save(exam);
	}

	public void remove(Long id) {
		examRepository.deleteById(id);
	}
}
