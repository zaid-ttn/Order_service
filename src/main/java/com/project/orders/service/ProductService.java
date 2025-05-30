package com.project.orders.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

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

    }
    public List<Product> fuzzySearchProducts(String name) {
        return productRepository.searchByName(name)
                .stream()
                .map(searchhit->searchhit.getContent())
                .collect(Collectors.toList());
    }

    public List<Product> fuzzySearch(String name) throws IOException {
        SearchResponse<Product> response = elasticsearchClient.search(s -> s
                        .index("product")
                        .query(q -> q
                                .match(m -> m
                                        .field("name")
                                        .query(name)
                                        .fuzziness("AUTO")
                                )
                        ),
                Product.class
        );

        System.out.println("Total hits: " + response.hits().total().value());
        List<Product> resultList = new ArrayList<>();
        List<Hit<Product>> hits = response.hits().hits();

        for (Hit<Product> hit : hits) {
            Product document = hit.source();
            resultList.add(document);
        }

        return resultList;
    }
}
