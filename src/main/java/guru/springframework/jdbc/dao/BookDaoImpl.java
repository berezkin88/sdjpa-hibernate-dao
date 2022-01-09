package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Repository
public class BookDaoImpl implements BookDao {

    private final EntityManagerFactory entityManagerFactory;

    public BookDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Book getById(Long id) {
        return getEntityManager().find(Book.class, id);
    }

    @Override
    public Book findBookByTitle(String title) {
        var query = getEntityManager().createQuery("select b from Book b " +
            "where b.title = :title", Book.class);
        query.setParameter("title", title);

        return query.getSingleResult();
    }

    @Override
    public Book saveNewBook(Book book) {
        var entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(book);
        entityManager.flush();
        entityManager.getTransaction().commit();

        return book;
    }

    @Override
    public void updateBook(Book book) {
        var entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.merge(book);
        entityManager.flush();
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteBookById(Long id) {
        var entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        var book = entityManager.find(Book.class, id);
        entityManager.remove(book);
        entityManager.flush();
        entityManager.getTransaction().commit();
    }

    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
