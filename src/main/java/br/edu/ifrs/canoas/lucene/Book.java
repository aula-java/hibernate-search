package br.edu.ifrs.canoas.lucene;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue
    private Integer id;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String title;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String subtitle;

    @Field(index=Index.YES, analyze=Analyze.NO, store=Store.YES)
    @DateBridge(resolution=Resolution.DAY)
    private LocalDate publicationDate;

    @IndexedEmbedded
    @ManyToMany
    private Set<Author> authors = new HashSet<Author>();

}