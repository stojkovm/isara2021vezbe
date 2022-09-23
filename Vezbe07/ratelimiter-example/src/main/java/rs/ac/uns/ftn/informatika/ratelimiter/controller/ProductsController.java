package rs.ac.uns.ftn.informatika.ratelimiter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.informatika.ratelimiter.domain.Product;
import rs.ac.uns.ftn.informatika.ratelimiter.service.ProductService;

import java.util.List;

@RestController
@RequestMapping(value = "/products")
public class ProductsController {

	@Autowired
	private ProductService productService;

	@PostMapping(
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Product addProduct(@RequestBody Product product){
		productService.saveProduct(product);
		return product;
	}

	@DeleteMapping(value = "/{id}")
	public void deleteProduct(@PathVariable int id) {
		productService.delete(id);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Product> getProducts() {
		return productService.findAll();
	}
}
