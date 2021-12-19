package rs.ac.uns.ftn.testing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_COUNT_WITH_LAST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_FIRST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_ID;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_ID_TO_DELETE;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_INDEX;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_LASTNAME_COUNT;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_LAST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.NEW_FIRST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.NEW_INDEX;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.NEW_LAST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.PAGE_SIZE;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import rs.ac.uns.ftn.testing.model.Student;
import rs.ac.uns.ftn.testing.repository.StudentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentServiceTest {
	
	/*
	Mocking mehanizam omogućuje simulaciju ponašanja objekata koje testirani objekat koristi
	Da bi se testirani objekat testirao u izolaciji važno je da referencirani objekti ne unose
	grešku. Potrebno je simulirati da referencirani objekti uvek rade ispravno.
	Da bismo servise testirali u izolaciji (a znamo da servisi koriste metode repozitorijuma)mokujemo repozitorijume
	dodavanjem anotacije @Mock.
	 */
	@Mock
	private StudentRepository studentRepositoryMock;
	
	@Mock
	private Student studentMock;
	
	/*
	Kako servis koristi metode repozitorijuma koji smo mokovali, moramo taj mokovani repozitorijum injektovati
	dodavanjem anotacije @InjectMocks.
	 */
	@InjectMocks
	private StudentService studentService;

/*
	Anotacija @Test naznačava Springu da će se anotirana metoda izvrši prilikom testiranja.
	Ukoliko se ona izostavi, test metoda se neće izvršiti.
*/
	@Test
	public void testFindAll() {
		/*
		Kako za testove koristimo mokovane repository objekte moramo da definišemo šta će se desiti kada se
		pozove određena metoda kombinacijom "when"-"then" Mockito metoda.
		 */
		
		// 1. Definisanje ponašanja mock objekata
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME)));
		
		// 2. Akcija
		List<Student> students = studentService.findAll();
		
		// 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima
		assertThat(students).hasSize(1);
		assertEquals(students.get(0).getFirstName(), DB_FIRST_NAME.toUpperCase());
		
		/*
		Možemo verifikovati ponašanje mokovanih objekata pozivanjem verify* metoda.
		 */
		verify(studentRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
	public void testFindAllPageable() {
		// 1. Definisanje ponašanja mock objekata
		PageRequest pageRequest = PageRequest.of(1, PAGE_SIZE); //second page
		when(studentRepositoryMock.findAll(pageRequest)).thenReturn(new PageImpl<Student>(Arrays.asList(new Student(DB_ID, NEW_INDEX, NEW_FIRST_NAME, NEW_LAST_NAME)).subList(0, 1), pageRequest, 1));
	
		// 2. Akcija
		Page<Student> students = studentService.findAll(pageRequest);
		
		// 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima
		assertThat(students).hasSize(1);
		
		verify(studentRepositoryMock, times(1)).findAll(pageRequest);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test 
	public void testFindOne() {
		// 1. Definisanje ponašanja mock objekata
		when(studentRepositoryMock.findById(DB_ID)).thenReturn(Optional.of(studentMock));
		
		// 2. Akcija
		Student dbStudent = studentService.findOne(DB_ID);
		
		// 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima
		assertEquals(studentMock, dbStudent);
		
        verify(studentRepositoryMock, times(1)).findById(DB_ID);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
/*	
	Test metode ne vrše trajne izmene nad bazom podataka. 
	Test metode anotirane sa @Transactional:
		- za svaku test metodu se pokreće nova transakcija.
		- transakcija se automatski poništava (rollback) na kraju metode. Izmene nad bazom
		  se poništavaju nakon metode iako je rollback podrazumevano ponašanje,
		  može da se stavi i anotacija @Rollback(true) da bi bilo vidljivije.
*/
	@Test
    @Transactional
    @Rollback(true) // uključeno po default-u, ne mora se navesti
	public void testAdd() {
		
		Student student = new Student();
		student.setFirstName(NEW_FIRST_NAME);
		student.setLastName(NEW_LAST_NAME);
		student.setIndex(NEW_INDEX);
		
		// 1. Definisanje ponašanja mock objekata
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME)));
		when(studentRepositoryMock.save(student)).thenReturn(student);
		
		// 2. Akcija
		int dbSizeBeforeAdd = studentService.findAll().size();
		
		Student dbStudent = studentService.save(student);
		
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME), student));
		
		// 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima
		assertThat(dbStudent).isNotNull();
		
        List<Student> students = studentService.findAll();
        assertThat(students).hasSize(dbSizeBeforeAdd + 1); //verifikacija da je novi student upisan u bazu
        
        dbStudent = students.get(students.size() - 1); // preuzimanje poslednjeg studenta
        
        assertThat(dbStudent.getFirstName()).isEqualToIgnoringCase(NEW_FIRST_NAME);
        assertThat(dbStudent.getLastName()).isEqualTo(NEW_LAST_NAME);
        assertThat(dbStudent.getIndex()).isEqualTo(NEW_INDEX);
        
        verify(studentRepositoryMock, times(2)).findAll();
        verify(studentRepositoryMock, times(1)).save(student);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
    @Transactional
	public void testAddv2() {
		// 1. Definisanje ponašanja mock objekata     
        when(studentRepositoryMock.save(studentMock)).thenReturn(studentMock);
        
        // 2. Akcija         
        Student savedStudent = studentService.save(studentMock);
        
        // 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima      
        assertThat(savedStudent, is(equalTo(studentMock)));
	}
	
	@Test
    @Transactional
    @Rollback(true)
	public void testUpdate() {
		// 1. Definisanje ponašanja mock objekata     
		when(studentRepositoryMock.findById(DB_ID)).thenReturn(Optional.of(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME)));

		 // 2. Akcija    
		Student studentForUpdate = studentService.findOne(DB_ID);
		studentForUpdate.setFirstName(NEW_FIRST_NAME);
		studentForUpdate.setLastName(NEW_LAST_NAME);
		studentForUpdate.setIndex(NEW_INDEX);
		
		when(studentRepositoryMock.save(studentForUpdate)).thenReturn(studentForUpdate);
		
		studentForUpdate = studentService.save(studentForUpdate);
		
		// 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima  
		assertThat(studentForUpdate).isNotNull();
		
		studentForUpdate = studentService.findOne(DB_ID); // verifikacija da se u bazi nalaze izmenjeni podaci
        assertThat(studentForUpdate.getFirstName()).isEqualTo(NEW_FIRST_NAME); 
        assertThat(studentForUpdate.getLastName()).isEqualTo(NEW_LAST_NAME);
        assertThat(studentForUpdate.getIndex()).isEqualTo(NEW_INDEX);
        
        verify(studentRepositoryMock, times(2)).findById(DB_ID);
        verify(studentRepositoryMock, times(1)).save(studentForUpdate);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testRemove() {
		// 1. Definisanje ponašanja mock objekata     
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME), new Student(DB_ID_TO_DELETE, NEW_INDEX, NEW_FIRST_NAME, NEW_LAST_NAME)));
		doNothing().when(studentRepositoryMock).deleteById(DB_ID_TO_DELETE);
		when(studentRepositoryMock.findById(DB_ID_TO_DELETE)).thenReturn(Optional.empty());
		
		// 2. Akcija   
		int dbSizeBeforeRemove = studentService.findAll().size();
		studentService.remove(DB_ID_TO_DELETE);
		
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME)));
		
		// 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima  
		List<Student> students = studentService.findAll();
		assertThat(students).hasSize(dbSizeBeforeRemove - 1);
		
		Student dbStudent = studentService.findOne(DB_ID_TO_DELETE);
		assertThat(dbStudent).isNull();
		
		verify(studentRepositoryMock, times(1)).deleteById(DB_ID_TO_DELETE);
		verify(studentRepositoryMock, times(2)).findAll();
        verify(studentRepositoryMock, times(1)).findById(DB_ID_TO_DELETE);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
	public void testFindByIndex() {
		// 1. Definisanje ponašanja mock objekata     
		when(studentRepositoryMock.findOneByIndex(DB_INDEX)).thenReturn(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME));
		
		// 2. Akcija 
		Student dbStudent = studentService.findByIndex(DB_INDEX);
		
		// 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima  
		assertThat(dbStudent).isNotNull();
		assertThat(dbStudent.getId()).isEqualTo(1L);
		assertThat(dbStudent.getFirstName()).isEqualTo(DB_FIRST_NAME);
        assertThat(dbStudent.getLastName()).isEqualTo(DB_LAST_NAME);
        assertThat(dbStudent.getIndex()).isEqualTo(DB_INDEX);
        
        verify(studentRepositoryMock, times(1)).findOneByIndex(DB_INDEX);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
	public void testFindByLastName() {
		// 1. Definisanje ponašanja mock objekata     
		when(studentRepositoryMock.findAllByLastName(DB_LAST_NAME)).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME), new Student(2L, "ra12-2014", "Milica", DB_LAST_NAME)));
		
		// 2. Akcija 
		List<Student> students = studentService.findByLastName(DB_LASTNAME_COUNT);
		
		// 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima  
		assertThat(students).hasSize(DB_COUNT_WITH_LAST_NAME);
		
		verify(studentRepositoryMock, times(1)).findAllByLastName(DB_LAST_NAME);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	/*
	 * NEGATIVNI TESTOVI
	 */
	
/*	
	Test može da definiše da očekuje da se pri pozivu metode desi određeni izuzetak.
	Test prolazi ako se takav izuzetak desi. Ovo se koristi najčešće za negativne testove 
	kada verifikujemo ponašanje na nevalidne ulaze.
*/
	@Test(expected = DataIntegrityViolationException.class)
    @Transactional
    @Rollback(true)
	public void testAddNonUniqueIndex() {
		Student student = new Student();
		student.setFirstName(NEW_FIRST_NAME);
		student.setLastName(NEW_LAST_NAME);
		student.setIndex(DB_INDEX); // index već postoji -> Unique constraint violation
		
		// 1. Definisanje ponašanja mock objekata     
		when(studentRepositoryMock.save(student)).thenThrow(DataIntegrityViolationException.class);
		
		// 2. Akcija 
		studentService.save(student);
		
		// 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima  
		verify(studentRepositoryMock, times(1)).save(student);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	@Transactional
	@Rollback(true)
	public void testAddNullIndex() {
		Student student = new Student();
		student.setFirstName(NEW_FIRST_NAME);
		student.setLastName(NEW_LAST_NAME);
		// ne navodi se index koji je po modelu obavezan (nullable = false) -> Not null constraint violation
		
		// 1. Definisanje ponašanja mock objekata     
		when(studentRepositoryMock.save(student)).thenThrow(DataIntegrityViolationException.class);
		
		// 2. Akcija 
		studentService.save(student);
		
		// 3. Verifikacija: asertacije i/ili verifikacija interakcije sa mock objektima  
		verify(studentRepositoryMock, times(1)).save(student);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
}
