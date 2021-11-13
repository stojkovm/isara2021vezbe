# Vežbe 5

## aop-example

Primer Spring aplikacije u kojoj je definisan jedan aspekt (__TimeLoggingAspect__).

Podrška za aspekte je automatski uključena u Spring Boot aplikacije putem _@SpringBootApplication_ anotacije.  U slučaju da se nije koristio Spring Boot, podrška za aspekte bi se mogla uključiti dodavanjem anotacije _@EnableAspectJAutoProxy_ na konfiguracionu klasu ili _<aop:aspectj-autoproxy/>_ u slučaju XML konfiguracije.

###### Materijali koje je neophodno proučiti da bi se primer mogao uspešno ispratiti:

* [Aspect-oriented programming](https://www.youtube.com/watch?v=3KKUP7-o3ps)
* [AOP.pdf](https://github.com/stojkovm/isara2021vezbe/blob/main/Vezbe05/AOP.pdf) iz foldera _Vezbe05_

###### Definisanje aspekta

Da bi se definisao aspekt, potrebno je Java klasu anotirati ___@Aspect___ anotacijom. Zatim svaku metodu ove klase anotirati anotacijom koja će opisati u kom trenutku će se aspekt izvršiti, a kao atribut ove anotacije potrebno je navesti __pointcut__ izraz kojim se definiše konkretno mesto u aplikaciji na kojem će aspekt biti primenjen. Anotacije kojim se anotiraju metode su sledeće:

* ___@Before___: pre poziva metode na koju se aspekt odnosi
* ___@After___: nakon metode (bez obzira na ishod metode)
* ___@AfterReturning___: nakon uspešnog završetka metode
* ___@AfterThrowing___ : nakon što metoda izazove izuzetak
* ___@Around___: omotač oko metode, tako što se deo koda izvršava pre, a deo posle metode.

__Pointcut__ izrazom se definiše __šablon__, što znači da se aspekt primenjuje na __svaku__ metodu koja se uklapa u definisani šablon!

###### Dodatni materijali:

* [Primer još jedne Spring aplikacije sa aspektima](https://www.journaldev.com/2583/spring-aop-example-tutorial-aspect-advice-pointcut-joinpoint-annotations)
* [AOP i Spring Dokumentacija](https://docs.spring.io/spring/docs/2.0.x/reference/aop.html)
* [Specifikacija AsspectJ jezika](https://www.eclipse.org/aspectj/doc/released/progguide/language.html)

###### Pokretanje primera (Eclipse)

* importovati projekat u workspace: Import -> Maven -> Existing Maven Project
* instalirati sve dependency-je iz pom.xml
* desni klik na projekat -> Run as -> Java Application / Spring Boot app (ako je instaliran STS plugin sa Eclipse marketplace)

## async-example

Asinhrono procesiranje je u Spring radnom okviru pojednostavljeno korišćenjem specijalnih anotacija.
Potrebno je uključiti podršku za asinhrono izvršavanje metoda pomoću anotacije `@EnableAsync` i anotirati metodu koja treba asinhrono da se izvršava pomoću anotacije `@Async`.
Kada se metoda anotira `@Async` anotacijom, Spring će izdvojiti izvršavanje te metode u odvojenu nit iz TaskExecutor thread pool-a, a pozivalac metode neće morati da čeka na njeno izvršavanje.

U **async-example** primeru prikazano je jednostavan kod za slanje e-maila sinhrono i asinhrono. Dodat je `Thread.sleep()` kako bi se istakao efekat dugotrajne operacije koja ima smisla da se izvršava asinhrono i kakav efekat takvo izvršavanje ima za korisnika. Za potrebe slanja e-maila koristi se objekat klase `JavaMailSender`. Konekcioni parametri za programsko slanje e-maila zadati su kroz `application.properties`.

Napomena: Za programsko slanje e-maila u primeru je korišćen Gmail nalog. Kako bi primer radio, potrebno je na nalogu koji ste postavili u `application.properties` dozvoliti rad sa "manje bezbednim aplikacijama". Na [linku](https://support.google.com/accounts/answer/6010255?hl=en) se nalazi uputstvo gde treba štiklirati __Off__ za "Less secure app access".

Dodatni materijali za razumevanje asinhronog izvršavanja metoda u Springu:

1. [Task Execution and Scheduling](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#scheduling)
2. [Effective Advice on Spring Async: Part 1](https://dzone.com/articles/effective-advice-on-spring-async-part-1)
3. [Effective Advice on Spring Async (ExceptionHandler): Part 2](https://dzone.com/articles/effective-advice-on-spring-async-exceptionhandler-1)
4. [Effective Advice on Spring @Async: Final Part](https://dzone.com/articles/effective-advice-on-spring-async-final-part-1)
5. [How To Do @Async in Spring](https://www.baeldung.com/spring-async)

###### Pokretanje primera (Eclipse)

* importovati projekat u workspace: Import -> Maven -> Existing Maven Project
* instalirati sve dependency-je iz pom.xml
* desni klik na projekat -> Run as -> Java Application / Spring Boot app (ako je instaliran STS plugin sa Eclipse marketplace)

## jpa-best-practices

### Logičko brisanje

U primeru je demonstrirano logičko brisanje ili _soft delete_ u Hibernate izvedbi.
Potrebno je u svakoj klasi za koju se implementira logičko brisanje definisati atribut tipa `boolean` naziva npr. `deleted` koji će nositi informaciju da li se entitet izbrisan ili ne (ako je potrebno podržati logičko brisanje u svim entitetima onda se može kreirati zajednička roditeljska klasa koja bi imala ovaj atribut).
Nad entitetom za koji se vrši brisanje treba dodati nativni upit:
```
@SQLDelete(sql = "UPDATE <naziv_tabele> SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
```
Hibernate će za svaki upit dodati `WHERE` klauzulu i izuzeti "izbrisane" entitete.
Ako su potrebni logički izbrisani entiteti mora se pisati nativni upit a ne JPQL jer Hibernate dodaje prethodno navedenu `WHERE` klauzulu automatski.
U interfejsu `UserRepository` su navedeni primeri takvih upita.

### Čuvanje informacije u JSON obliku u relacionoj bazi

U primeru je demonstrirano pisanje Java objekta u tabelu Postgres baze u obliku JSON objekta.
Hibernate ORM dolazi sa setom podržanih Java i SQL tipova koji nije prevelik.
Za ostale nepodržane tipove može se koristiti [Hibernate Types biblioteka](https://github.com/vladmihalcea/hibernate-types) koja je dodata u `pom.xml`.
Kako se u primeru koristi [JSONB](https://www.postgresql.org/docs/9.4/datatype-json.html), korišćenjem anotacija iz navedene biblioteke možemo izvršiti mapiranje.
Dodavanjem anotacije `@TypeDef` nad entitetom kažemo u šta će se vršiti konverzija.
```
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
```
Objekat klase koji hoćemo da zapišemo u JSON obliku potrebno je navesti kao atribut osnovne klase (u primeru je to klasa `User`) i anotirati je sa odgovarajućim tipom. Npr:
```
@Type(type = "jsonb")
@Column(columnDefinition = "jsonb", name = "contact_info")
private ContactInfo contactInfo;
```
Upiti koji trebaju da iskoriste vrednosti iz JSON objekta su dosta kompleksni i zato egzotične operacije nad JSON objektima treba izbegavati. Dovoljno je da se radi jednostavno čuvanje/čitanje celog JSON objekta bez dodatne obrade jer je ograničena i podrška koju baze mogu da pruže. Primer upita iz kojeg se može ovaj zaključak izvući (u interfejsu `UserRepository`):
```
//Metoda pronalazi korisnika koji u JSON objektu među adresama ima prosleđenu.
@Query(value = "SELECT u.* FROM json_users u WHERE jsonb_exists(u.contact_info->'addresses', ?1)", nativeQuery = true)
public User findByStreet(String street);
```

### Podrška za svrhe nadgledanja aktivnosti (auditing)

U primeru je demonstrirano uvođenje podrške za auditing svrhe - ko je kada kreirao ili izmenio entitet u bazi.
Za potrebe vremenske odrednice Hibernate ima ugrađene anotacije kojima se automatski mogu upisati te informacije anotiranjem atributa klase.
```
//Hibernate specificna anotacija koja generise in-memory timestamp prvog inserta User-a koriscenjem VM vremena za potrebe audita
@CreationTimestamp
@Column(name = "created")
private LocalDateTime created;

//Hibernate specificna anotacija koja generise in-memory timestamp svake izmene User-a koriscenjem VM vremena za potrebe audita
@UpdateTimestamp
@Column(name = "updated")
private LocalDateTime lastModified;
```

Hibernate nema anotacije za informaciju koji korisnik je uradio kreiranje ili izmenu (Spring Data u kombinaciji sa Spring Security projektom ima).
Za potrebe primera dodata je klasa `LoggedUserGenerator` koja simulira vraćanje trenutno ulogovanog korisnika.
U kombinaciji sa anotacijom `@GeneratorType` nad atributima klase za čije torke se radi auditing dobijamo željeni efekat.
```
@Column(name = "created_by")
@GeneratorType(type = LoggedUserGenerator.class, when = GenerationTime.INSERT)
private String createdBy;

@Column(name = "last_modified_by")
@GeneratorType(type = LoggedUserGenerator.class, when = GenerationTime.ALWAYS)
private String lastModifiedBy;
```

###### Pokretanje primera (Eclipse)

* importovati projekat u workspace: Import -> Maven -> Existing Maven Project
* instalirati sve dependency-je iz pom.xml
* u `application.properties` promeniti kredencijale za konekciju na bazu
* kreirati šemu baze u Postgres pod nazivom `jpadb`
* desni klik na projekat -> Run as -> Java Application / Spring Boot app (ako je instaliran STS plugin sa Eclipse marketplace)