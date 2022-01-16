package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@Repository
public class BookDaoImpl implements BookDao {

    private final EntityManagerFactory entityManagerFactory;

    public BookDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<Book> findAll() {
        var entityManager = getEntityManager();

        try {
            var query = entityManager.createNamedQuery("find_all_books", Book.class);

            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Book findByIsbn(String isbn) {
        var entityManager = getEntityManager();

        try {
            var query = entityManager.createQuery("select b from Book b where b.isbn = :isbn", Book.class);
            query.setParameter("isbn", isbn);

            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Book getById(Long id) {
        var entityManager = getEntityManager();

        try {
            return getEntityManager().find(Book.class, id);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Book findBookByTitle(String title) {
        var entityManager = getEntityManager();

        try {
            var query = entityManager.createNamedQuery("find_book_by_title", Book.class);
            query.setParameter("title", title);

            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Book findBookByTitleCriteria(String title) {
        var entityManager = getEntityManager();

        try {
            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Book.class);

            var root = criteriaQuery.from(Book.class);

            var titleExpressionParameter = criteriaBuilder.parameter(String.class);

            var titlePredicate = criteriaBuilder.equal(root.get("title"), titleExpressionParameter);

            criteriaQuery.select(root).where(titlePredicate);

            var typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setParameter(titleExpressionParameter, title);

            return typedQuery.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Book findBookByTitleNative(String title) {
        var entityManager = getEntityManager();

        try {
            var query = entityManager.createNativeQuery("SELECT * FROM book b WHERE b.title = :title", Book.class);

            query.setParameter("title", title);

            return (Book) query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Book saveNewBook(Book book) {
        var entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(book);
        entityManager.flush();
        entityManager.getTransaction().commit();

        entityManager.close();
        return book;
    }

    @Override
    public void updateBook(Book book) {
        var entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.merge(book);
        entityManager.flush();
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    @Override
    public void deleteBookById(Long id) {
        var entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        var book = entityManager.find(Book.class, id);
        entityManager.remove(book);
        entityManager.flush();
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
