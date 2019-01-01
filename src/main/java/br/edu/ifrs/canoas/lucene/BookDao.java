package br.edu.ifrs.canoas.lucene;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class BookDao {

    @PersistenceUnit
    EntityManagerFactory emf;


    public List searchFuzzy(String criteria)
    {
        EntityManager em = emf.createEntityManager();
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        em.getTransaction().begin();

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Book.class)
                .get();

        org.apache.lucene.search.Query luceneQuery = queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(1)
                .withPrefixLength(0)
                .onFields("title", "subtitle", "authors.name")
                .matching(criteria)
                .createQuery();

        org.hibernate.search.jpa.FullTextQuery jpaQuery
                = fullTextEntityManager.createFullTextQuery(luceneQuery, Book.class);

        List result = jpaQuery.getResultList();

        em.getTransaction().commit();
        em.close();
        return result;
    }

    public List searchBoolean(String criteria){

        EntityManager em = emf.createEntityManager();
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        em.getTransaction().begin();

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Book.class)
                .get();


        org.apache.lucene.search.Query luceneQuery = queryBuilder
                .simpleQueryString()
                .onFields("title", "subtitle", "authors.name")
                .matching(criteria)
                .createQuery();

        org.hibernate.search.jpa.FullTextQuery jpaQuery
                = fullTextEntityManager.createFullTextQuery(luceneQuery, Book.class);

        List result = jpaQuery.getResultList();

        em.getTransaction().commit();
        em.close();
        return result;
    }

    public List search(String criteria){

        if (criteria == null)
            return new ArrayList();

        if (criteria.contains("(")
                || criteria.contains("+")
                || criteria.contains("-")
                || criteria.contains("|")
                || criteria.contains("\"")
                || criteria.contains("~")
        )
            return searchBoolean(criteria);

        return searchFuzzy(criteria);
    }

}
