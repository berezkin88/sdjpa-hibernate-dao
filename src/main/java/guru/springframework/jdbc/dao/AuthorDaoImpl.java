package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 * Created by jt on 8/28/21.
 */
@Component
public class AuthorDaoImpl implements AuthorDao {

    private final EntityManagerFactory entityManagerFactory;

    public AuthorDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Author getById(Long id) {
        return getEntityManager().find(Author.class, id);
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        var query = getEntityManager().createQuery("select a from Author a " +
            "where a.firstName = :first_name and a.lastName = :last_name", Author.class);
        query.setParameter("first_name", firstName);
        query.setParameter("last_name", lastName);

        return query.getSingleResult();
    }

    @Override
    public Author saveNewAuthor(Author author) {
        var entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(author);
        entityManager.flush();
        entityManager.getTransaction().commit();

        return author;
    }

    @Override
    public Author updateAuthor(Author author) {
        var entityManager = getEntityManager();

        entityManager.joinTransaction();
        entityManager.merge(author);
        entityManager.flush();
        entityManager.clear();

        return entityManager.find(Author.class, author.getId());
    }

    @Override
    public void deleteAuthorById(Long id) {

    }

    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
