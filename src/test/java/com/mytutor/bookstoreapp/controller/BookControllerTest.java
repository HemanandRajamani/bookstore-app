package com.mytutor.bookstoreapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytutor.bookstoreapp.exception.BookStoreServiceExceptionHandler;
import com.mytutor.bookstoreapp.model.Purchase;
import com.mytutor.bookstoreapp.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(SpringExtension.class)
class BookControllerTest {

	private MockMvc mockMvc;
	private ObjectMapper mapper;

	@Spy
	private BookStoreServiceExceptionHandler exceptionHandler;
	@Mock
	BookService bookService;

	@InjectMocks
	private BookController underTest;

	@BeforeEach
	void setUp() {
		mockMvc = standaloneSetup(getUnderTest()).setControllerAdvice(exceptionHandler).build();
		mapper = new ObjectMapper();
	}

	@Test
	void testPurchaseBook_Success() throws Exception {

		Purchase purchase = Purchase.builder().bookType("Book A").quantity(2).build();

		given(bookService.purchaseBook(purchase)).willReturn("Thank you for your purchase!");

		String json = mapper.writeValueAsString(purchase);

		mockMvc.perform(post("/api/books/v1.0/purchase").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().is2xxSuccessful())
				.andExpect(content().string("Thank you for your purchase!"));
	}

	@Test
	void testPurchaseBook_ThrowsException() throws Exception {
		Purchase purchase = Purchase.builder().bookType("Book X").quantity(2).build();

		given(bookService.purchaseBook(purchase)).willThrow(new NoSuchElementException("Sorry Record Not Found!"));

		String json = mapper.writeValueAsString(purchase);

		mockMvc.perform(post("/api/books/v1.0/purchase").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().is4xxClientError())
				.andExpect(content().string("Sorry Record Not Found!"));
	}

	@Test
	void testPrintReport_Success() throws Exception {

		given(bookService.printReport()).willReturn("moo");

		mockMvc.perform(get("/api/books/v1.0/report"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().string("moo"));
	}

	private Object getUnderTest() {
		return underTest;
	}
}
