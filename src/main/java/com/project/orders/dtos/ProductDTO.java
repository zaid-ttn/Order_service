package com.project.orders.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder

public class ProductDTO {

    private String id;
    private String name;
    private int quantity;
    private int price;
    private String imageUrl;
}
