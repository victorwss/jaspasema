
package ninja.javahacker.jaspasema.sampleproject.publisher;

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
public class PublisherService {

    @Post
    @ProducesJson
    @Path("/publisher/new")
    public PublisherData createPublisher(
            @JsonBodyProperty(required = true) @NonNull String name)
    {
        return Publisher.createNew(name).output();
    }

    @Post
    @ProducesJson
    @Path("/publisher/:id")
    public PublisherData updatePublisher(
            @UriPart long id,
            @JsonBodyProperty(required = true) @NonNull String name)
    {
        return Publisher.findById(id).orElseThrow(IllegalArgumentException::new).update(name).output();
    }

    @Get
    @ProducesJson
    @Path("/publisher/search")
    public List<PublisherData> findPublishersByName(
            @QueryPart @NonNull String name)
    {
        return Publisher.streamByName(name).map(Publisher::output).collect(Collectors.toList());
    }

    @Get
    @ProducesJson
    @Path("/publisher/list")
    public List<PublisherData> listAll() {
        return Publisher.streamAll().map(Publisher::output).collect(Collectors.toList());
    }

    @Get
    @ProducesJson
    @Path("/publisher/:id")
    public Optional<PublisherData> findPublisherById(@UriPart long id) {
        return Publisher.findById(id).map(Publisher::output);
    }
}
