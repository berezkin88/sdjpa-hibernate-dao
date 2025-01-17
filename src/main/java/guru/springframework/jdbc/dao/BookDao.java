package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;

import java.util.List;

public interface BookDao {

    Book findByIsbn(String isbn);

    Book getById(Long id);

    Book findBookByTitle(String title);

    Book findBookByTitleCriteria(String title);

    Book findBookByTitleNative(String title);

    Book saveNewBook(Book book);

    void updateBook(Book book);

    void deleteBookById(Long id);

    List<Book> findAll();
}
