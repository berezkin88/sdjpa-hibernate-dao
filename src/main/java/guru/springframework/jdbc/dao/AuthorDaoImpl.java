package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

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
    public Author findAuthorByNameCriteria(String firstName, String lastName) {
        var entityManager = getEntityManager();

        try {
            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(Author.class);

            var root = criteriaQuery.from(Author.class);

            var firstNameExpressionParameter = criteriaBuilder.parameter(String.class);
            var lastNameExpressionParameter = criteriaBuilder.parameter(String.class);

            var firstNamePredicate = criteriaBuilder.equal(root.get("firstName"), firstNameExpressionParameter);
            var lastNamePredicate = criteriaBuilder.equal(root.get("lastName"), lastNameExpressionParameter);

            criteriaQuery.select(root).where(criteriaBuilder.and(firstNamePredicate, lastNamePredicate));

            var typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setParameter(firstNameExpressionParameter, firstName);
            typedQuery.setParameter(lastNameExpressionParameter, lastName);

            return typedQuery.getSingleResult();
        } finally {
            entityManager.close();
        }

    }

    @Override
    public List<Author> findAll() {
        var entityManager = getEntityManager();

        try {
            var typedQuery = entityManager.createNamedQuery("author_find_all", Author.class);

            return typedQuery.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Author> listAuthorByLastNameLike(String lastName) {
        var entityManager = getEntityManager();

        try {
            var query = entityManager.createQuery("select a from Author a where a.lastName like :last_name");
            query.setParameter("last_name", lastName + "%");

            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Author getById(Long id) {
        var entityManager = getEntityManager();

        try {
            return getEntityManager().find(Author.class, id);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        var entityManager = getEntityManager();

        try {
            var typedQuery = entityManager.createNamedQuery("find_by_name", Author.class);

            typedQuery.setParameter("first_name", firstName);
            typedQuery.setParameter("last_name", lastName);

            return typedQuery.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Author saveNewAuthor(Author author) {
        var entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(author);
        entityManager.flush();
        entityManager.getTransaction().commit();

        entityManager.close();
        return author;
    }

    @Override
    public Author updateAuthor(Author author) {
        var entityManager = getEntityManager();

        entityManager.joinTransaction();
        entityManager.merge(author);
        entityManager.flush();
        entityManager.clear();

        var updatedAuthor = entityManager.find(Author.class, author.getId());

        entityManager.close();
        return updatedAuthor;
    }

    @Override
    public void deleteAuthorById(Long id) {
        var entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        var author = entityManager.find(Author.class, id);
        entityManager.remove(author);
        entityManager.flush();
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
