package com.mytutor.bookstoreapp.controller;

import com.mytutor.bookstoreapp.model.Purchase;
import com.mytutor.bookstoreapp.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = BookController.INITIAL_PATH)
public class BookController {
	static final String INITIAL_PATH = "/api/books/v1.0";

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping(consumes = "application/json", path = "/purchase")
    public String purchaseBook(@Valid @RequestBody Purchase purchase) {
        return bookService.purchaseBook(purchase);
    }

    @GetMapping(path = "/report")
    public String printReport() {
       return bookService.printReport();
    }
}
