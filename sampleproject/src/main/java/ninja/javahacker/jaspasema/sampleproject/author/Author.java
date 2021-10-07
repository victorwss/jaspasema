package ninja.javahacker.jaspasema.sampleproject.author;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.PackagePrivate;
import ninja.javahacker.jaspasema.sampleproject.book.Book;
import ninja.javahacker.jpasimpletransactions.Database;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Entity
@Table(name = "author")
@Accessors(chain = true)
public class Author {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @ManyToMany(mappedBy = "authors")
    private List<Book> books;

    protected Author() {
        books = new ArrayList<>(10);
    }

    public void addBook(Book book) {
        this.books.add(book);
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(new ArrayList<>(books));
    }

    public static Author createNew(@NonNull String name) {
        return new Author().update(name);
    }

    public Author update(@NonNull String name) {
        this.name = name;
        Database.getEntityManager().save(this);
        return this;
    }

    public static Optional<Author> findById(long id) {
        return Optional.ofNullable(Database.getEntityManager().find(Author.class, id));
    }

    public static Stream<Author> streamAll() {
        return Database
                .getEntityManager()
                .createQuery("SELECT a FROM " + Author.class.getSimpleName(), Author.class)
                .getResultStream();
    }

    public static Stream<Author> streamByName(@NonNull String name) {
        return Database
                .getEntityManager()
                .createQuery("SELECT a FROM " + Author.class.getSimpleName() + " a WHERE UPPER(name) LIKE :name", Author.class)
                .setParameter("nome", "%" + name.toUpperCase(Locale.ROOT) + "%")
                .getResultStream();
    }

    public static Optional<Author> findByExactName(@NonNull String nome) {
        List<Author> authors = Database
                .getEntityManager()
                .createQuery("SELECT a FROM " + Author.class.getSimpleName() + " a WHERE UPPER(name) = :name", Author.class)
                .setParameter("nome", "%" + nome.toUpperCase(Locale.ROOT) + "%")
                .getResultList();
        return authors.isEmpty() ? Optional.empty() : Optional.of(authors.get(0));
    }

    @PackagePrivate
    AuthorData output() {
        return new AuthorData(
                getId(),
                getName(),
                getBooks().stream().map(Author::output).collect(Collectors.toList()));
    }

    private static AuthorData.AuthorBookData output(Book book) {
        return new AuthorData.AuthorBookData(
                book.getId(),
                book.getTitle(),
                book.getPublisher().getId(),
                book.getPublisher().getName(),
                book.getYear(),
                book.getEdition());
    }
}
