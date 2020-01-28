package com.mytutor.bookstoreapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book  {
    private Integer id;
    private String type;
    private BookPricingDetails price;
    private Integer available;
    private Integer copiesSold;
}
