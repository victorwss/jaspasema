package ninja.javahacker.jaspasema.sampleproject.author;

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
public class AuthorService {

    @Post
    @ProducesJson
    @Path("/author/new")
    public AuthorData createAuthor(
            @JsonBodyProperty(required = true) @NonNull String name)
    {
        return Author.createNew(name).output();
    }

    @Post
    @ProducesJson
    @Path("/author/:id")
    public AuthorData updateAuthor(
            @UriPart long id,
            @JsonBodyProperty(required = true) @NonNull String name)
    {
        return Author.findById(id).orElseThrow(IllegalArgumentException::new).update(name).output();
    }

    @Get
    @ProducesJson
    @Path("/author/search")
    public List<AuthorData> findAuthorsByName(
            @QueryPart @NonNull String name)
    {
        return Author.streamByName(name).map(Author::output).collect(Collectors.toList());
    }

    @Get
    @ProducesJson
    @Path("/author/list")
    public List<AuthorData> listAll() {
        return Author.streamAll().map(Author::output).collect(Collectors.toList());
    }

    @Get
    @ProducesJson
    @Path("/author/:id")
    public Optional<AuthorData> findAuthorById(@UriPart long id) {
        return Author.findById(id).map(Author::output);
    }
}
