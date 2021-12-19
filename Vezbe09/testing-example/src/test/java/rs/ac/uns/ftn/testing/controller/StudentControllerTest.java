package rs.ac.uns.ftn.testing.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_COUNT;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_COUNT_STUDENT_EXAMS;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_COUNT_WITH_LAST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_FIRST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_INDEX;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_LASTNAME_COUNT;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_LAST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.NEW_FIRST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.NEW_INDEX;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.NEW_LAST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.PAGE_SIZE;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import rs.ac.uns.ftn.testing.constants.ExamConstants;
import rs.ac.uns.ftn.testing.constants.StudentConstants;
import rs.ac.uns.ftn.testing.model.Student;
import rs.ac.uns.ftn.testing.util.TestUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentControllerTest {

	private static final String URL_PREFIX = "/api/students";

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	/*
	Mocking mehanizam omogućuje simulaciju ponašanja objekata koje testirani objekat koristi
	Da bi se testirani objekat testirao u izolaciji važno je da referencirani objekti ne unose
	grešku. Potrebno je simulirati da referencirani objekti uvek rade ispravno.

	Programski poziv REST servisa se vrši putem Spring MockMvc klase.

	MockMvc simulira kompletnu Spring veb MVC arhitekturu.
	Nije mock objekat u ranije korišćenom značenju!
	Omogućuje stvarno, a ne lažno predefinisano ponašanje kao kod mock objekata koje koristi 
	Mockito (najpopularniji java radni okvir za implementaciju mocking mehanizma).

	 */	
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	/*	
 	Spring pruža matcher metode u klasi MockMvcResultMatchers koje proveravaju status, 
 	tip sadržaja i sadržaj. Sadržaj su u ovom primeru JSON objekti.
	Sadržaj JSON objekata se proverava jsonPath matcher metodom koja
	prima JSONPath izraz. JSONPath je jezik za opis sadržaja JSON objekta. Omogućuje selekciju
	delova JSON objekta i ima istu ulogu kao XPath za XML.
	http://goessner.net/articles/JsonPath/
	 */
	@Test
	public void testGetAllStudents() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/all")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(DB_COUNT)))
		.andExpect(jsonPath("$.[*].id").value(hasItem(StudentConstants.DB_ID.intValue())))
		.andExpect(jsonPath("$.[*].firstName").value(hasItem(DB_FIRST_NAME.toUpperCase())))
		.andExpect(jsonPath("$.[*].lastName").value(hasItem(DB_LAST_NAME)))
		.andExpect(jsonPath("$.[*].index").value(hasItem(DB_INDEX)));
	}

	@Test
	public void testGetStudentsPage() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "?page=0&size=" + PAGE_SIZE)).andExpect(status().isOk())
		.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(PAGE_SIZE)))
		.andExpect(jsonPath("$.[*].id").value(hasItem(StudentConstants.DB_ID.intValue())))
		.andExpect(jsonPath("$.[*].firstName").value(hasItem(DB_FIRST_NAME)))
		.andExpect(jsonPath("$.[*].lastName").value(hasItem(DB_LAST_NAME)))
		.andExpect(jsonPath("$.[*].index").value(hasItem(DB_INDEX)));
	}

	@Test
	public void testGetStudent() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/" + StudentConstants.DB_ID)).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(StudentConstants.DB_ID.intValue()))
		.andExpect(jsonPath("$.firstName").value(DB_FIRST_NAME))
		.andExpect(jsonPath("$.lastName").value(DB_LAST_NAME))
		.andExpect(jsonPath("$.index").value(DB_INDEX));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveStudent() throws Exception {
		Student student = new Student();
		student.setFirstName(NEW_FIRST_NAME);
		student.setLastName(NEW_LAST_NAME);
		student.setIndex(NEW_INDEX);

		String json = TestUtil.json(student);
		this.mockMvc.perform(post(URL_PREFIX).contentType(contentType).content(json)).andExpect(status().isCreated());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateStudent() throws Exception {
		Student student = new Student();
		student.setId(StudentConstants.DB_ID);
		student.setFirstName(NEW_FIRST_NAME);
		student.setLastName(NEW_LAST_NAME);
		student.setIndex(NEW_INDEX);

		String json = TestUtil.json(student);
		this.mockMvc.perform(put(URL_PREFIX).contentType(contentType).content(json)).andExpect(status().isOk());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteStudent() throws Exception {
		this.mockMvc.perform(delete(URL_PREFIX + "/" + StudentConstants.DB_ID)).andExpect(status().isOk());
	}
	
	@Test
	public void testGetStudentByIndex() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/findIndex?index=" + DB_INDEX)).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(StudentConstants.DB_ID.intValue()))
		.andExpect(jsonPath("$.firstName").value(DB_FIRST_NAME))
		.andExpect(jsonPath("$.lastName").value(DB_LAST_NAME))
		.andExpect(jsonPath("$.index").value(DB_INDEX));
	}

	@Test
	public void testGetStudentByLastName() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/findLastName?lastName=" + DB_LASTNAME_COUNT)).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$", hasSize(DB_COUNT_WITH_LAST_NAME)));
	}

	@Test
	public void testGetStudentExams() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/" + StudentConstants.DB_ID_REFERENCED + "/exams")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(DB_COUNT_STUDENT_EXAMS)))
		.andExpect(jsonPath("$.[*].id").value(hasItem(ExamConstants.DB_ID.intValue())))
		.andExpect(jsonPath("$.[*].date").value(hasItem(ExamConstants.DB_DATE.toString())))
		.andExpect(jsonPath("$.[*].grade").value(hasItem(ExamConstants.DB_GRADE)))
		.andExpect(jsonPath("$.[*].course.id").value(hasItem(ExamConstants.DB_COURSE_ID.intValue())));
	}
}
