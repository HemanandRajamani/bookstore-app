package com.mytutor.bookstoreapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookPricingDetails {
    private Integer id;
    private BigDecimal buyingPrice;
    private BigDecimal pricingFactor;
    private BigDecimal sellingPrice;
}
