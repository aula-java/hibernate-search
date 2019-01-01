package br.edu.ifrs.canoas.lucene;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookDaoTest {

    @Autowired
    BookDao dao;

    @Autowired
    BookRepository repo;

    @Autowired
    AuthorRepository authorRepo;

    @Before
    public void setup(){
        repo.deleteAll();

        Book book = Book.builder().title("Cidade de Deus").subtitle("God's city").build();
        Author author = Author.builder().name("Rodrigo").build();
        authorRepo.save(author);
        Set<Author> authors = new HashSet<>();
        authors.add(author);
        book.setAuthors(authors);

        repo.save(book);
        repo.save(Book.builder().title("Senhor dos Aneis").subtitle("Anel").build());
        repo.save(Book.builder().title("Senhor dos Deuses").subtitle("Anel").build());
        repo.save(Book.builder().title("Cidade dos Aneis").subtitle("Anel").build());
    }

    @Test
    public void testFuzzy(){
        List<Book> list = dao.search("CiadDE de DEUS");
        Assertions.assertThat(list)
                .extracting(Book::getTitle)
                .contains("Cidade de Deus");
    }

    @Test
    public void testBoolean(){
        List<Book> list = dao.search("-CIDADE +ANEIS");
        Assertions.assertThat(list).extracting(Book::getTitle)
                .contains("Senhor dos Aneis")
                .doesNotContain("Cidade dos Aneis");
    }

    @Test
    public void testSearch(){
        List<Book> list = dao.search("\"SenhOR DOS AneIS\"");
        Assertions.assertThat(list).extracting(Book::getTitle)
                .contains("Senhor dos Aneis")
                .size().isLessThanOrEqualTo(1);
    }

    @Test
    public void testAuthor(){
        List<Book> list = dao.search("Rodrigo");
        Assertions.assertThat(list).extracting(Book::getTitle).contains("Cidade de Deus");
    }

}