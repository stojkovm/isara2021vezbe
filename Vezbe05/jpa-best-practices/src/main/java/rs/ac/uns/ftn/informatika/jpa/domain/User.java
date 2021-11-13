package rs.ac.uns.ftn.informatika.jpa.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import rs.ac.uns.ftn.informatika.jpa.audit.LoggedUserGenerator;

@Entity
@Table(name = "json_users")
//-----JSON START--------------------------------------------------
/*
 * Hibernate ORM dolazi sa setom podrzanih Java i SQL tipova koji nije prevelik.
 * Za ostale nepodrzane tipove moze se koristiti Hibernate Types biblioteka koja je dodata u pom.xml - https://github.com/vladmihalcea/hibernate-types
 * 
 */
@TypeDef(
		name = "jsonb", typeClass = JsonBinaryType.class
		)
//-----JSON END--------------------------------------------------
//-----SOFT DELETE START-----------------------------------------
/*
 * Prilikom poziva delete() metode repozitorijuma, okidace se ovaj upit koji radi soft delete
 * tako sto menja status deleted polja sa false na true.
 */
@SQLDelete(sql
	    = "UPDATE json_users "
	    + "SET deleted = true "
	    + "WHERE id = ?")
@Where(clause = "deleted = false")
//-----SOFT DELETE END-----------------------------------------
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	//-----JSON START--------------------------------------------------
	/*
	 * Razlika izmedju json i jsonb - https://www.postgresql.org/docs/9.4/datatype-json.html
	 * Hocemo da sacuvamo ovaj podatak u koloni tabele json_users kao tip JSONB koji je podrzan u Postgres RDBMS
	 * a mapiranje omoguceno kroz biblioteku dodatu u pom.xml i @TypeDef i @Type anotacije
	 */
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "contact_info")
	private ContactInfo contactInfo;
	//-----JSON END--------------------------------------------------
	
	//-----SOFT DELETE START-----------------------------------------
	//atribut potreban za logicko brisanje (soft delete)
	//i koji se koristi u @Where klauzuli koju Hibernate dodaje pri svakom upitu koji treba da vrati sve neobrisane torke
	@Column(name = "deleted")
	private boolean deleted;
	//-----SOFT DELETE END-----------------------------------------
	
	//-----AUDIT START--------------------------------------------------
	//Hibernate specificna anotacija koja generise in-memory timestamp prvog inserta User-a koriscenjem VM vremena za potrebe audita
	@CreationTimestamp
	@Column(name = "created")
	private LocalDateTime created;

	//Hibernate specificna anotacija koja generise in-memory timestamp svake izmene User-a koriscenjem VM vremena za potrebe audita
    @UpdateTimestamp
    @Column(name = "updated")
    private LocalDateTime lastModified;

    //Hibernate nema svoje anotacije za zapis informacije o korisniku koji manipulise User entitetom
    //pa smo se posluzili sa @GeneratorType anotacijom koja ima atribut when gde navodimo da samo pri prvoj insert
    //operaciji treba da se upise vrednost koju cemo dobiti iz generatora koji simulira ulogovanog koristnika za potrebe audita
    @Column(name = "created_by")
    @GeneratorType(
            type = LoggedUserGenerator.class,
            when = GenerationTime.INSERT
        )
    private String createdBy;
    
    //Hibernate nema svoje anotacije za zapis informacije o korisniku koji manipulise User entitetom
    //pa smo se posluzili sa @GeneratorType anotacijom koja ima atribut when gde navodimo da pri svakoj
    //operaciji treba da se upise vrednost koju cemo dobiti iz generatora koji simulira ulogovanog koristnika za potrebe audita
    @Column(name = "last_modified_by")
    @GeneratorType(
            type = LoggedUserGenerator.class,
            when = GenerationTime.ALWAYS
        )
    private String lastModifiedBy;
  //-----AUDIT END--------------------------------------------------

	public User() {
		super();
	}

	public User(Long id, String name, ContactInfo contactInfo) {
		super();
		this.id = id;
		this.name = name;
		this.contactInfo = contactInfo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}
	

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", contactInfo=" + contactInfo + "]";
	}

}
