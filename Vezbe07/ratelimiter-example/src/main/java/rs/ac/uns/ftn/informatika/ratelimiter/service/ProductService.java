package rs.ac.uns.ftn.informatika.ratelimiter.service;

import rs.ac.uns.ftn.informatika.ratelimiter.domain.Product;

import java.util.List;

public interface ProductService {

	List<Product> findAll();
	void saveProduct(Product product);
	void delete(long id);
}
