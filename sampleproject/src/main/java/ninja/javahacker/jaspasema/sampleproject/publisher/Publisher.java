package ninja.javahacker.jaspasema.sampleproject.publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.PackagePrivate;
import ninja.javahacker.jaspasema.sampleproject.author.Author;
import ninja.javahacker.jaspasema.sampleproject.book.Book;
import ninja.javahacker.jpasimpletransactions.Database;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Entity
@Table(name = "publisher")
@Accessors(chain = true)
public class Publisher {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @OneToMany(mappedBy = "publisher")
    private List<Book> books;

    protected Publisher() {
        books = new ArrayList<>(10);
    }

    public void addBook(@NonNull Book book) {
        this.books.add(book);
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(new ArrayList<>(books));
    }

    public static Publisher createNew(@NonNull String name) {
        return new Publisher().update(name);
    }

    public Publisher update(@NonNull String name) {
        this.name = name;
        Database.getEntityManager().save(this);
        return this;
    }

    public static Optional<Publisher> findById(long id) {
        return Optional.ofNullable(Database.getEntityManager().find(Publisher.class, id));
    }

    public static Stream<Publisher> streamAll() {
        return Database
                .getEntityManager()
                .createQuery("SELECT a FROM " + Publisher.class.getSimpleName(), Publisher.class)
                .getResultStream();
    }

    public static Stream<Publisher> streamByName(@NonNull String name) {
        return Database
                .getEntityManager()
                .createQuery("SELECT a FROM " + Publisher.class.getSimpleName() + " a WHERE UPPER(name) LIKE :name", Publisher.class)
                .setParameter("name", "%" + name.toUpperCase(Locale.ROOT) + "%")
                .getResultStream();
    }

    public static Optional<Publisher> findByNameExact(@NonNull String name) {
        EntityManager em = Database.getEntityManager();
        List<Publisher> publishers = em
                .createQuery("SELECT e FROM " + Publisher.class.getSimpleName() + " e WHERE UPPER(name) = :name", Publisher.class)
                .setParameter("name", name.toUpperCase(Locale.ROOT))
                .getResultList();
        if (publishers.isEmpty()) return Optional.empty();
        return Optional.of(publishers.get(0));
    }

    @PackagePrivate
    PublisherData output() {
        return new PublisherData(
                getId(),
                getName(),
                getBooks().stream().map(Publisher::output).collect(Collectors.toList()));
    }

    private static PublisherData.PublisherBookData output(Book book) {
        return new PublisherData.PublisherBookData(
                book.getId(),
                book.getTitle(),
                book.getYear(),
                book.getEdition(),
                book.getAuthors().stream().map(Publisher::output).collect(Collectors.toList()));
    }

    private static PublisherData.PublisherBookAuthorData output(Author autor) {
        return new PublisherData.PublisherBookAuthorData(
                autor.getId(),
                autor.getName());
    }
}
