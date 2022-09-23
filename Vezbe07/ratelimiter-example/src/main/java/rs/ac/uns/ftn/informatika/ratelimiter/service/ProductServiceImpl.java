package rs.ac.uns.ftn.informatika.ratelimiter.service;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.ratelimiter.domain.Product;
import rs.ac.uns.ftn.informatika.ratelimiter.repository.ProductRepository;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
	
	private final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
	
	@Autowired
	private ProductRepository productRepository;

	/*
	 * Deklarativno oznacavanje da metodu mozemo izvrsiti
	 * ogranicen broj puta u okviru vremenskog intervala.
	 * Od znacajnih atributa koji se mogu postaviti izdvajaju se:
	 * - name - ime rateLimiter instance definisano u konfiguraciji
	 * - fallbackMethod - ime metode koja se poziva u slucaju da dodje do izuzetka
	 */
	@RateLimiter(name = "basic", fallbackMethod = "basicFallback")
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	// Metoda koja ce se pozvati u slucaju RequestNotPermitted exception-a
	public List<Product> basicFallback(RequestNotPermitted rnp) {
		LOG.warn("Prevazidjen broj poziva u ogranicenom vremenskom intervalu");
		throw rnp;
	}

	public void saveProduct(Product product) {
		productRepository.save(product);
	}

	public void delete(long id) {
		productRepository.deleteById(id);
	}

}
