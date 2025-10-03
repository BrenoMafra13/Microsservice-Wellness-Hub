package ca.gbc.productservice.service;

import ca.gbc.productservice.model.Product;
import ca.gbc.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Optional<Product> findById(String id) {
        return repository.findById(id);
    }

    public Product create(Product p) {
        p.setId(null);
        return repository.save(p);
    }

    public boolean update(String id, Product replacement) {
        return repository.findById(id).map(existing -> {
            existing.setName(replacement.getName());
            existing.setDescription(replacement.getDescription());
            existing.setPrice(replacement.getPrice());
            repository.save(existing);
            return true;
        }).orElse(false);
    }

    public boolean delete(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
