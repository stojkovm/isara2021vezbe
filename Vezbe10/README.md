# Vežbe 10

## Message Queue

Za potrebe razumevanja primera potrebno je prethodno pročitati [MessageQueue.pdf](https://github.com/stojkovm/isara2021vezbe/blob/main/Vezbe10/MessageQueue.pdf) iz foldera Vezbe10.

Kratak pregled prednosti korišćenja MQ nalazi se na [linku](https://blog.iron.io/top-10-uses-for-message-queue/).

Dodatne informacije o konceptima i različitim implementacijama MQ možete pročitati na [1](https://blog.codepath.com/2013/01/06/asynchronous-processing-in-web-applications-part-2-developers-need-to-understand-message-queues/) i [2](https://www.rabbitmq.com/tutorials/amqp-concepts.html).

### RabbitMQ

Primer komunikacije zasnovane na razmeni poruka između dve Spring aplikacije i rada sa [RabbitMQ](https://www.rabbitmq.com/download.html) nalaze se u _rabbitmq-producer-example_ i _rabbitmq-consumer-example_ projektima. Za pokretanje primera potrebno je instalirati [RabbitMQ](https://www.rabbitmq.com/download.html). Kada se server instalira potrebno ga je startovati.

Podrška za korišćenje RabbitMQ u Spring aplikaciji se može uključiti dodavanjem odgovarajuće zavisnosti u `pom.xml`:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

#### rabbitmq-producer-example

Primer Spring aplikacije koja dodaje poruke u red.

Pre svega, potrebno je uspostaviti konekciju sa MQ serverom. MQ server je zadužen za čuvanje pristiglih poruka. U _RabbitmqProducerExampleApplication_ klasi se u `connectionFactory()` metodi uspostavlja konekcija sa MQ serverom. U primeru koristimo lokalni RabbitMQ server, ali to može da bude i neki cloud server, poput [CloudaMQP](https://www.cloudamqp.com/). U ovoj klasi se definišu i dva reda

```
@Bean
Queue queue(){
    return new Queue(queue, false);
}
```

i

```
@Bean
Queue queue2(){
    return new Queue(queue2, false);
}
```

gde je drugi parametar konstruktora vrednost `durable` atributa. Nazivi atributa su definisani u `application.properties` fajlu, gde se uz pomoć `@Value` anotacije injektuju u String varijable.

Definisan je jedan `binding rule`:

```
@Bean
DirectExchange exchange() {
    return new DirectExchange(exchange);
}

@Bean
Binding binding(Queue queue2, DirectExchange exchange) {
    return BindingBuilder.bind(queue2).to(exchange).with(routingkey);
}
```

Ovim se `queue2` vezuje za _exchange_ (parametar _to()_ metode je naziv) pod definisanim ključem (parametar _with()_ metode je ključ, odnosno _routing key_). Definisan je _Direct Exchange_, a podržani tipovi su predstavljeni klasom [Exchange Type](https://docs.spring.io/spring-amqp/api/org/springframework/amqp/core/ExchangeTypes.html). Kako za `queue` nije definisan _exchange_, on se vezuje za _Default Exchange_, gde je njegov naziv zapravo i _Routing Key_.

U klasi _Producer_ su implementirane dve metode:

1. `sendTo()` metoda koja šalje poruku na _Default Exchange_. Parametri metode su _routing key_ i poruka koja se šalje
2. `sendToExchange()` metoda koja šalje poruku na _Exchange_. Parametri metode su _exchange_, _routing key_ i poruka koja se šalje.

Sve poruke se šalju preko [RabbitTemplate](https://docs.spring.io/spring-amqp/docs/current/api/org/springframework/amqp/rabbit/core/RabbitTemplate.html) preko kojeg se ostvaruje komunikacija sa RabbitMQ serverom i pruža mogućnost rutiranja, slanja i primanja poruka.

Klasa _ProducerController_ je REST _controller_ sa dva _endpoint_-a:

1. _endpoint_ na koji se šalju poruke koje se dalje prosleđuju na _Default Exchange_ i
2. _endpoint_ na koji se šalju poruke koje se dalje prosleđuju na određeni _Exchange_.

Ovo znači da imamo jednu Spring aplikaciju koja koristi dva različita načina komunikacije: REST i Message Queue!

#### rabbitmq-consumer-example

Primer Spring aplikacije koja čita poruke iz reda.

Pre svega, potrebno je uspostaviti konekciju sa MQ serverom. MQ server je zadužen za čuvanje pristiglih poruka. U _RabbitmqConsumerExampleApplication_ klasi se u `connectionFactory()` metodi uspostavlja konekcija sa MQ serverom. U primeru koristimo lokalni RabbitMQ server, ali to može da bude i neki cloud server, poput [CloudaMQP](https://www.cloudamqp.com/).

Definisana su dva _Consumer_-a koji čitaju poruke koji su predstavljeni u dve različite klase koje imaju jednu metodu `public void handler(String message)` koja je anotirana `@RabbitListener` anotacijom. Ova anotacija ima jedan parametar `queues=` čija vrednost označava nazive redova sa kojih _Consumer_ čita poruke. U primeru, prvi _Consumer_ čita poruke sa _spring-boot1_ reda, a drugi sa _spring-boot2_ reda.

Listener će konvertovati poruku u odgovarajući tip koristeći odgovarajući konvertor poruka (implementacija [MessageConverter interfejsa](https://docs.spring.io/spring-amqp/api/org/springframework/amqp/support/converter/MessageConverter.html)).

#### Pokretanje primera

Da bi se primer uspešno demonstrirao, neophodno je da _producer_ i _consumer_ budu povezani na isti RabbitMQ server, jer se svi redovi i poruke čuvaju na jednom serveru.

1. Pokrenuti _rabbitmq-producer-example_ (radi na portu 8080)
2. Preko _Postman_-a poslati poruku na _Default Exchange_, red _spring-boot1_ (slika 1)
3. Pogledati ispis u konzoli: `Sending> ... Message=[ hello! ] RoutingKey=[spring-boot1]`
4. Pokrenuti _rabbitmq-consumer-example_ (radi na portu 8081)
5. Odmah nakon pokretanja, u konzoli se ispisuje `Consumer> hello!` zato što prilikom startovanja aplikacije, _consumer_ se automatski pretplati na red na koji je poslata poruka u koraku 3, a kako ta poruka nije obrađena, odmah je čita i obrađuje
6. Preko _Postman_-a poslati još jednu poruku na _Exchange_ pod nazivom _myexchange_, red _spring-boot2_ (slika 2)
7. Pogledati ispis u konzoli _rabbitmq-producer-example_ aplikacije: `Sending> ... Message=[ hello hello ] Exchange=[myexchange] RoutingKey=[spring-boot2]`
8. Pogledati ispis u konzoli _rabbitmq-consumer-example_ aplikacije: `Consumer2> hello hello`
9. Preko _Postman_-a poslati još jednu poruku na _Exchange_ pod nazivom _myexchange_, ali na red _spring-boot1_ koji je vezan za _Default Exchange_ (slika 3)
10. Pogledati ispis u konzoli _rabbitmq-producer-example_ aplikacije: `Sending> ... Message=[ hello hello ] Exchange=[myexchange] RoutingKey=[spring-boot1]`
11. Na konzoli _rabbitmq-consumer-example_ aplikacije nema ispisa da je neki _consumer_ obradio poruku jer za _myexchange_ ne postoji _routing key_ sa vrednošću _spring-boot1_

Takođe, možete da pristupite lokalnoj konzoli RabbitMQ servera tako što ćete u _Browser_-u ukucati http://localhost:15672/#/, kredencijali su _username: guest, password: guest_. Ovde možete da vidite sve redove koji su definisani, da pratite razmenu poruka, šaljete poruke...

Napomena: važno je da se ili ručno kreira red preko konzole ili da se pošalje bar jedna poruka na bilo koji red pre nego što se pokrene _rabbitmq-consumer-example_ jer red mora da postoji na serveru da bi se moglo na njega pretplatiti putem `@RabbitListener` anotacije!

![Slika 1](https://i.imgur.com/mxMCxZo.png "Slika 1")
Slika 1

![Slika 2](https://i.imgur.com/XgleBua.pngj "Slika 2")
Slika 2

![Slika 3](https://i.imgur.com/NwqtPD1.pngj "Slika 3")
Slika 3

### Apache ActiveMQ

Primer komunikacije zasnovane na razmeni poruka između dve Spring aplikacije i rada sa [Apache ActiveMQ](https://activemq.apache.org/) nalaze se u _activemq-producer-example_ i _activemq-consumer-example_ projektima. Apache ActiveMQ je _open source message broker_ napisan u Java programskom jeziku koji u navedenim primerima koristimo zajedno sa klijentom koji je implementiran u skladu sa [Java Message Service (JMS)](https://www.oracle.com/java/technologies/java-message-service.html) API specifikacijom. Za pokretanje primera potrebno je instalirati [Apache ActiveMQ](https://activemq.apache.org/components/classic/download/). Kada se server instalira potrebno ga je startovati.

Podrška za korišćenje ActiveMQ u Spring aplikaciji se može uključiti dodavanjem odgovarajuće zavisnosti u `pom.xml`:

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-activemq</artifactId>
</dependency>
```

#### activemq-producer-example

Primer Spring aplikacije koja dodaje poruke u red.

Pre svega, potrebno je uspostaviti konekciju sa MQ serverom. MQ server je zadužen za čuvanje pristiglih poruka. U _JmsConfig_ klasi se u `connectionFactory()` metodi uspostavlja konekcija sa MQ serverom. U primeru koristimo lokalni ActiveMQ server.

U istoj klasi metodom `jmsTemplate()` definisan je [JmsTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jms/core/JmsTemplate.html) koji će služiti kao pomoćna klasa koja uprošćava sinhronizovani pristup Java Message Service-u (JMS) kao klijentu za Apache ActiveMQ za slanje i primanje poruka.

U ovoj klasi se definišе i red:

```
@Bean
public Queue queue() {
    return new ActiveMQQueue(queueName);
}
```

Nazivi atributa su definisani u `application.properties` fajlu, gde se uz pomoć `@Value` anotacije injektuju u String varijable.

Klasa _MessageController_ je REST kontroler sa jednim _endpoint-om_ na koji se šalju tekstualne poruke na prethodno definisan _queue_. Sve poruke se šalju preko _JmsTemplate-a_.

#### activemq-consumer-example

Primer Spring aplikacije koja čita poruke iz reda.

Pre svega, potrebno je uspostaviti konekciju sa MQ serverom. MQ server je zadužen za čuvanje pristiglih poruka. U _JmsConfig_ klasi se u `connectionFactory()` metodi uspostavlja konekcija sa MQ serverom. U primeru koristimo lokalni ActiveMQ server. Potrebno je kreirati i _listener_ kontejner metodom `jmsListenerContainerFactory()`.

Ova konfiguraciona klasa anotirana je anotacijom [@EnableJms](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jms/annotation/EnableJms.html) kojom se detektuju sve [@JmsListener](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jms/annotation/JmsListener.html) anotacije unutar bilo koje komponente u nadleznosti Spring kontejnera.

Definisan je _consumer_ koji čita poruke predstavljen _MessageConsumer_ klasom koja ima jednu metodu `public void listener(String message)` koja je anotirala [@JmsListener](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jms/annotation/JmsListener.html) anotacijom, gde `destination` atribut predstavlja naziv destinacija sa koje bi trebalo da se osluškuju pristigle poruke, dok `containerFactory` atribut referencira _JmsListenerContainerFactory_, koji će se koristiti za kreiranje _message listener container-a_. U ovom primeru vrednost _destination_-a sa kojeg se čitaju poruke je `messages-queue`.

#### Pokretanje primera

Da bi se primer uspešno demonstrirao, neophodno je da _producer_ i _consumer_ budu povezani na isti ActiveMQ server, jer se svi redovi i poruke čuvaju na jednom serveru.

1. Pokrenuti _activemq-producer-example_ (radi na portu 8080).
2. Preko _Postman_-a poslati poruku na red _messages-queue_ (slika 4).
3. Pogledati ispis u ActiveMQ konzoli na linku: [http://localhost:8161/admin/queues.jsp](http://localhost:8161/admin/queues.jsp): username: admin, password: admin. Tu se vidi da je kreiran jedan red: `messages-queue` koji ima jednu poruku na čekanju koja je ušla u red, ali da je broj _consumer_-a i dalje 0 (slika 5).
4. Pokrenuti _activemq-consumer-example_ (radi na portu 8081).
5. Odmah nakon pokretanja, u konzoli se ispisuje `>> Message received: Hello, dear ActiveMQ Consumer!` zato što prilikom startovanja aplikacije, _consumer_ se automatski pretplati na red na koji je poslata poruka u koraku 2, a kako ta poruka nije obrađena, odmah je čita i obrađuje.
6. Preko _Postman_-a poslati još jednu poruku na red _messages-queue_.
7. Pogledati ispis u konzoli _activemq-consumer-example_ aplikacije: `>> Message received: Hello, dear ActiveMQ Consumer!`
8. Pogledati ispis u ActiveMQ konzoli (slika 6). Sada se vidi da je broj _consumer_-a 1, broj poruka koje su ušle u red je 2, kao i broj poruka koja su izašle iz reda, što znači da su obe poruke uspešno primljene.

![Slika 4](https://i.imgur.com/vvFU7eb.png "Slika 4")

Slika 4

![Slika 5](https://i.imgur.com/HV6U9dm.png "Slika 5")

Slika 5

![Slika 6](https://i.imgur.com/3xPikmg.png "Slika 6")

Slika 6

### Redis

Primer komunikacije zasnovane na razmeni poruka unutar jedne Spring aplikacije i rada sa [Redis](https://redis.io/) nalaze se u _redis-pub-sub-example_ projektu. Za pokretanje primera potrebno je instalirati [Redis](https://redis.io/download). Kada se server instalira potrebno ga je startovati.

Startovanjem Redis servera - pokretanjem `redis-server.exe`, otvara se njegova konzola:

![slika-redis-server](https://i.imgur.com/cp1bs3h.png "Slika 7 - redis-server")

Slika 7 - konzola pokrenutog Redis servera

Za potrebe monitoringa razmene poruka potrebno je pokrenuti `redis-cli.exe`. Komandom `monitor` obezbeđuje se monitoring razmenjenih poruka:

![slika-redis-cli](https://i.imgur.com/BafK9rT.png "Slika 8 - redis-cli")

Slika 8 - redis-cli konzola sa `monitor` komandom

Podrška za korišćenje Redis u Spring aplikaciji se može uključiti dodavanjem odgovarajuće zavisnosti u `pom.xml`:

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

Za Redis postoje razne klijentske biblioteke koje se mogu naći na ovom [linku](https://redis.io/clients#java/). Klijentska biblioteka koja se koristi u ovom primeru je [Jedis](https://github.com/redis/jedis). Za nju je potrebno obezbediti podršku u vidu odgovarajuće zavisnosti u `pom.xml`:

```
<dependency>
	<groupId>redis.clients</groupId>
	<artifactId>jedis</artifactId>
</dependency>
```

#### redis-pub-sub-example

Primer Spring aplikacije koja i dodaje poruke u _topic_ i čita poruke sa _topic_-a.

Pre svega, potrebno je uspostaviti konekciju sa Radis serverom. Redis server je zadužen za čuvanje pristiglih poruka. U _RedisConfiguration_ klasi se u `redisConnectionFactory()` metodi uspostavlja konekcija sa Redis serverom. U primeru koristimo lokalni Redis server.

U istoj klasi metodom `redisTemplate()` definisan je [RedisTemplate](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/RedisTemplate.html) koji će služiti kao pomoćna klasa koja uprošćava sinhronizovani pristup Redis serveru za slanje i primanje poruka.

U ovoj klasi se definišе i _topic_:

```
@Bean
public ChannelTopic topic() {
    return new ChannelTopic(topicName);
}
```

U ovoj konfiguracionoj klasi se definišu i _publisher_ i _subscriber_ koji su ključni za [_Publish/Subscribe Messaging Model_](https://redis.io/topics/pubsub). Potrebno je kreirati i _RedisMessageListenerContainer_ metodom `public RedisMessageListenerContainer container(MessageListenerAdapter messageListenerAdapter)`.

Nazivi atributa su definisani u `application.properties` fajlu, gde se uz pomoć `@Value` anotacije injektuju u String varijable.

Implementacija _publisher_-a nalazi se u klasi _RedisMessagePublisher_, gde se u metodi `public void publish(String message)` šalju tekstualne poruke na prethodno definisan _topic_. Sve poruke se šalju preko _RedisTemplate-a_.

Implementacija _subscriber_-a nalazi se u klasi _RedisMessageSubscriber_, gde se u metodi `public void onMessage(Message message, byte[] bytes)` pročitane poruke sa _topic_-a smeštaju u statičku promenljivu preko koje ćemo dobiti sve poruke tog _subscriber_-a.

Klasa _RedisController_ je REST _controller_ sa dva _endpoint_-a:

1. _endpoint_ na koji se šalju poruke koje se dalje prosleđuju na _topic_ i
2. _endpoint_ preko kojeg se dobija lista svih pristiglih poruka za definisanog _subscriber_-a.

#### Pokretanje primera

1. Pokrenuti _redis-pub-sub-example_ primer.
2. Preko _Postman_-a poslati poruku na _topic_ _messages_ (slika 9).
3. Pogledati ispis u konzoli: `>> Publishing: Message{data='Hello, it's me!', author='Jovan Jovic'}`, a zatim: `>> Receiving: Message{data='Hello, it's me!', author='Jovan Jovic'}`
4. Preko _Postman_-a poslati još jednu poruku na _topic_ _messages_.
5. Pogledati ispis u _redis-cli_ konzoli (slika 10). U konzoli se vidi na koji _topic_ smo _subscribe_-ovani i koje poruke su _publish_-ovane.
6. Preko _Postman_-a poslati _GET_ zahtev kako bi se izlistale sve poruke _subscriber_-a (slika 11).

![Slika 9](https://i.imgur.com/zbv0oaL.png "Slika 9")

Slika 9

![Slika 10](https://i.imgur.com/xjFFvol.png "Slika 10")

Slika 10

![Slika 11](https://i.imgur.com/zIYx4rc.png "Slika 11")

Slika 11
