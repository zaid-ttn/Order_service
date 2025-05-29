package com.project.orders.repository;

import com.project.orders.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, Long> {

    @Override
    Page<Product> findAll(Pageable pageable);

    SearchPage<Product> findByName(String name, Pageable pageable);
    @Query("""
            {
            "match":{
            "name":{
            "query":"#{#name}",
            "fuzziness":2
                    }
                }
            }
            """)
    SearchHits<Product> searchByName(String name);
}
