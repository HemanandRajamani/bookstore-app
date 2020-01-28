package com.mytutor.bookstoreapp.service;

import com.mytutor.bookstoreapp.model.Purchase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class BookServiceImplTest {

	@InjectMocks
	private BookServiceImpl underTest;

	@BeforeEach
	void setUp() {
		underTest.init();
	}

	@Test
	void testPurchaseBook_Success() {
		String result = underTest.purchaseBook(Purchase.builder().bookType("Book A").quantity(2).build());
		assertEquals(result, "Thank you for your purchase!");
	}

	@Test
	void testPurchaseBook_OutOfStock() {
		String result = underTest.purchaseBook(Purchase.builder().bookType("Book B").quantity(22).build());
		assertEquals(result, "Sorry, we are out of stock - only 10 available");
	}

	@Test
	void testPurchaseBookNotFound_ThrowsBadRequestException() {
		NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> underTest.purchaseBook(Purchase.builder().bookType("moo").quantity(1).build()));
		assertEquals(exception.getMessage(), "Sorry Record Not Found!");
	}

	@Test
	void testPrintReport_Success() {
		assertDoesNotThrow(() -> underTest.printReport());
	}
}
