package ninja.javahacker.jaspasema.sampleproject.book;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.PackagePrivate;
import ninja.javahacker.jaspasema.sampleproject.author.Author;
import ninja.javahacker.jaspasema.sampleproject.publisher.Publisher;
import ninja.javahacker.jpasimpletransactions.Database;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Entity
@Table(name = "book")
@Accessors(chain = true)
public class Book {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter
    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "author_book",
            joinColumns = @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "FK_Book_Author")),
            inverseJoinColumns = @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "FK_Author_Book"))
    )
    private List<Author> authors;

    @Getter
    @Column(name = "year", nullable = true)
    private Integer year;

    @Getter
    @Column(name = "edition", nullable = true)
    private Integer edition;

    @Getter
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "publisher_id", nullable = false, foreignKey = @ForeignKey(name = "FK_Book_Publisher"))
    private Publisher publisher;

    protected Book() {
        authors = new ArrayList<>(10);
    }

    public static Book createNew(
            @NonNull String title,
            @NonNull String publisher,
            Integer year,
            @NonNull List<String> authors)
    {
        return new Book().update(title, publisher, year, authors);
    }

    public Book update(
            @NonNull String title,
            @NonNull String publisher,
            Integer year,
            @NonNull List<String> authors)
    {
        this.title = title;
        this.year = year;

        Publisher e = Publisher
                .findByNameExact(publisher)
                .orElseGet(() -> Publisher.createNew(publisher));
        e.addBook(this);

        for (String author : authors) {
            Author a = Author
                    .findByExactName(author)
                    .orElseGet(() -> Author.createNew(author));
            a.addBook(this);
            this.addAuthor(a);
        }

        Database.getEntityManager().save(this);

        return this;
    }

    public void addAuthor(@NonNull Author author) {
        this.authors.add(author);
    }

    public List<Author> getAuthors() {
        return Collections.unmodifiableList(new ArrayList<>(authors));
    }

    public static Optional<Book> findById(long id) {
        return Optional.ofNullable(Database.getEntityManager().find(Book.class, id));
    }

    public static Stream<Book> streamByTitle(@NonNull String title) {
        return Database
                .getEntityManager()
                .createQuery("SELECT a FROM " + Book.class.getSimpleName() + " a WHERE UPPER(title) LIKE :name", Book.class)
                .setParameter("name", "%" + title.toUpperCase(Locale.ROOT) + "%")
                .getResultStream();
    }

    public static Optional<Book> findByExactTitle(@NonNull String title) {
        List<Book> books = Database
                .getEntityManager()
                .createQuery("SELECT a FROM " + Book.class.getSimpleName() + " a WHERE UPPER(title) = :name", Book.class)
                .setParameter("name", "%" + title.toUpperCase(Locale.ROOT) + "%")
                .getResultList();
        return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
    }

    @PackagePrivate
    BookData output() {
        return new BookData(
                getId(),
                getTitle(),
                getPublisher().getName(),
                getYear(),
                getEdition(),
                getAuthors().stream().map(Author::getName).collect(Collectors.toList()));
    }
}
