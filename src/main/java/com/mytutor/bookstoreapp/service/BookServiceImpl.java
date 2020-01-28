package com.mytutor.bookstoreapp.service;

import com.mytutor.bookstoreapp.model.Book;
import com.mytutor.bookstoreapp.model.BookPricingDetails;
import com.mytutor.bookstoreapp.model.Purchase;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class BookServiceImpl implements BookService {

	private static final Integer DEFAULT_STOCK_UPDATE_QTY = 10;
	private static final Integer INITIAL_STOCK_QTY = 10;
	private static final BigDecimal PRICING_FACTOR = BigDecimal.valueOf(0.7);
	private static final BigDecimal BUDGET = BigDecimal.valueOf(500);
	private static final Integer STOCK_REPLENISH_THRESHOLD = 3;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private Map<Integer, Book> bookMap = new ConcurrentHashMap<>();
	private static AtomicReference<BigDecimal> atomicBudget = new AtomicReference<>(BUDGET);

	@PostConstruct
	public void init() {
		initializeData(1, "Book A", BigDecimal.valueOf(25));
		initializeData(2, "Book B", BigDecimal.valueOf(20));
		initializeData(3, "Book C", BigDecimal.valueOf(23));
		initializeData(4, "Book D", BigDecimal.valueOf(30));
		initializeData(5, "Book E", BigDecimal.valueOf(27));
	}

	@Override
	public String purchaseBook(Purchase purchase) {
		Optional<Book> book = bookMap.values()
				.stream()
				.filter(bookMap -> bookMap.getType().contentEquals(purchase.getBookType()))
				.findFirst();

		if (book.isPresent()) {

			Integer availableStock = book.get().getAvailable();
			if (availableStock < purchase.getQuantity()) {
				return String.format("Sorry, we are out of stock - only %s available", availableStock);
			}

			updateStockAndCopiesSold(book.get(), purchase.getQuantity());

			if (book.get().getAvailable() < STOCK_REPLENISH_THRESHOLD) {
				placeOrder(book.get());
				updateBalanceOnProcurement(book.get(), purchase.getQuantity());
			}

			updateBalanceOnSale(book.get(), purchase.getQuantity());
			return "Thank you for your purchase!";
		}

		throw new NoSuchElementException("Sorry Record Not Found!");
	}

	@Override
	public String printReport() {

		List<Book> bookList = new ArrayList<>(bookMap.values());

		bookList.sort(Collections.reverseOrder((book1, book2) -> {

			Integer copiesSold1 = book1.getCopiesSold();
			Integer copiesSold2 = book2.getCopiesSold();

			int copiesSoldCompareResult = copiesSold1.compareTo(copiesSold2);

			//Compare by copies sold first (primary)
			if (copiesSoldCompareResult != 0) {
				return copiesSoldCompareResult;
			}

			BigDecimal profit1 = calculateProfit(book1);
			BigDecimal profit2 = calculateProfit(book2);

			//when copies sold == 0 , then compare profit (secondary)
			return profit1.compareTo(profit2);

		}));

		StringBuilder sb = new StringBuilder();
		sb.append("MyTutor Bookshop Balance : £").append(atomicBudget).append(LINE_SEPARATOR).append(LINE_SEPARATOR);

		bookList.forEach(
				book -> sb.append(book.getType()).append(" | ").append(book.getCopiesSold()).append(" Copies Sold ").append(" | £")
						.append(calculateTotalProfit(book)).append(" Total Profit").append(LINE_SEPARATOR));

		return sb.toString();
	}

	private void updateStockAndCopiesSold(Book book, Integer qty) {
		book.setAvailable(book.getAvailable() - qty);
		book.setCopiesSold(book.getCopiesSold() + qty);
	}

	private void initializeData(Integer id, String bookType, BigDecimal price) {
		BookPricingDetails pricingDetails = BookPricingDetails.builder().sellingPrice(price).pricingFactor(PRICING_FACTOR)
				.buyingPrice(price.multiply(PRICING_FACTOR)).build();

		bookMap.put(id, Book.builder().id(id).type(bookType).price(pricingDetails).available(INITIAL_STOCK_QTY).copiesSold(0).build());
	}

	private CompletableFuture<Book> placeOrder(Book book) {
		return CompletableFuture.supplyAsync(() -> bookMap.computeIfPresent(book.getId(), (key, value) -> update(value)));
	}

	/**
	 * Updates the balance on Sale from the main Budget.
	 *
	 * @param book
	 * @param qty
	 * @return Book
	 */
	private Book updateBalanceOnSale(Book book, Integer qty) {
		BigDecimal budget = atomicBudget.get();
		atomicBudget.updateAndGet(bg -> budget.add(book.getPrice().getSellingPrice().multiply(BigDecimal.valueOf(qty))));
		return book;
	}

	/**
	 * Updates Balance after placing the order with the Supplier.
	 * @param book
	 * @param qty
	 * @return Book
	 */
	private Book updateBalanceOnProcurement(Book book, Integer qty) {
		BigDecimal budget = atomicBudget.get();
		atomicBudget.updateAndGet(bg -> budget.subtract(book.getPrice().getSellingPrice().multiply(BigDecimal.valueOf(qty))));
		return book;
	}

	private Book update(Book book) {
		book.setAvailable(DEFAULT_STOCK_UPDATE_QTY + book.getAvailable());
		return book;
	}

	/**
	 * Calculates the profit-per-item based on the book buying price and selling price.
	 *
	 * @param book
	 * @return BigDecimal
	 */
	private BigDecimal calculateProfit(Book book) {
		return book.getPrice().getSellingPrice()
				.subtract(book.getPrice().getBuyingPrice()).setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the total profit of the books sold by quantity.
	 *
	 * @param book
	 * @return BigDecimal
	 */
	private BigDecimal calculateTotalProfit(Book book) {
		return BigDecimal.valueOf(book.getCopiesSold()).multiply(calculateProfit(book));
	}
}
