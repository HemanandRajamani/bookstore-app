package com.mytutor.bookstoreapp.service;

import com.mytutor.bookstoreapp.model.Purchase;

public interface BookService {
    /**
     * Purchase a book by its type and quantity.
     *
     * @param purchase
     * @return String - message to be returned
     */
    String purchaseBook(Purchase purchase);

    /**
     * Prints the report
     */
    String printReport();
}
