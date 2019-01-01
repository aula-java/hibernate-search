package br.edu.ifrs.canoas.lucene;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@Component
public class BuildSearchIndex {

    @PersistenceUnit
    EntityManagerFactory emf;

    /**
     * Create an initial Lucene index for the data already present in the
     * database.
     * This method is called when Spring's startup.
     */
    @PostConstruct
    public void startup() throws InterruptedException {
        EntityManager em = emf.createEntityManager();
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        fullTextEntityManager.createIndexer().startAndWait();
    }
}
