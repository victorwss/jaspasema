
package ninja.javahacker.jaspasema.sampleproject.book;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import ninja.javahacker.jaspasema.JsonBodyProperty;
import ninja.javahacker.jaspasema.Path;
import ninja.javahacker.jaspasema.ProducesJson;
import ninja.javahacker.jaspasema.QueryPart;
import ninja.javahacker.jaspasema.UriPart;
import ninja.javahacker.jaspasema.verbs.Get;
import ninja.javahacker.jaspasema.verbs.Post;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class BookService {

    @Post
    @ProducesJson
    @Path("/book/new")
    public BookData createBook(
            @JsonBodyProperty(required = true) @NonNull String title,
            @JsonBodyProperty(required = true) @NonNull String publisher,
            @JsonBodyProperty Integer year,
            @JsonBodyProperty(required = true) @NonNull List<String> authors)
    {
        return Book.createNew(title, publisher, year, authors).output();
    }

    @Post
    @ProducesJson
    @Path("/book/:id")
    public BookData updateBook(
            @UriPart long id,
            @JsonBodyProperty(required = true) @NonNull String title,
            @JsonBodyProperty(required = true) @NonNull String publisher,
            @JsonBodyProperty Integer year,
            @JsonBodyProperty(required = true) @NonNull List<String> authors)
    {
        return Book
                .findById(id)
                .orElseThrow(IllegalArgumentException::new)
                .update(title, publisher, year, authors)
                .output();
    }

    @Get
    @ProducesJson
    @Path("/book/search")
    public List<BookData> listBooksByTitle(
            @QueryPart @NonNull String title)
    {
        return Book.streamByTitle(title).map(Book::output).collect(Collectors.toList());
    }

    @Get
    @ProducesJson
    @Path("/book/:id")
    public Optional<BookData> findBookById(@UriPart long id) {
        return Book.findById(id).map(Book::output);
    }
}
