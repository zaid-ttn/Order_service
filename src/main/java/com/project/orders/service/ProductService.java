package com.project.orders.service;

import com.project.orders.model.Product;
import com.project.orders.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @RabbitListener(queues = "product.upsert.queue")
    public void upsertProduct(Product product) {
        logger.info("Upsert product to queue");
        Product addProduct=new Product();
        addProduct.setId(product.getId());
        addProduct.setName(product.getName());
        addProduct.setPrice(product.getPrice());
        addProduct.setQuantity(product.getQuantity());
        addProduct.setImageUrl(product.getImageUrl());
        productRepository.save(addProduct);
        logger.info("product.upsert.queue: {}\n", product);
    }

    @RabbitListener(queues = "product.delete.queue")
    public void deleteProduct(Long productId) {
        logger.info("product.delete.queue");
        productRepository.deleteById(productId);
        logger.info("product deleted");
    }

    public List<Product> fetchProducts() {
        logger.info("fetch Products");
        Iterable<Product>products= productRepository.findAll();
        List<Product> productList= new ArrayList<>();
        for(Product product:products){
            productList.add(product);
        }
        logger.info("fetch Products done");
        return productList;
    }

    public List<Product> fetchListProducts(int page, int size,String sortDirection) {
        logger.info("fetch list Products");
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "id"));

        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.getContent();
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
//        Page<Product> products = productRepository.findAll(pageable);
//        List<Product> productList = new ArrayList<>();
//        for (Product product : products.getContent()) {
//            Product addProduct = new Product();
//            addProduct.setId(product.getId());
//            addProduct.setName(product.getName());
//            addProduct.setPrice(product.getPrice());
//            addProduct.setQuantity(product.getQuantity());
//            addProduct.setImageUrl(product.getImageUrl());
//        }
//        logger.info("fetch list Products done");
//        return productList;
    }
}
