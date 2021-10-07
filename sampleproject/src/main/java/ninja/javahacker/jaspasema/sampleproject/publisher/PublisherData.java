package ninja.javahacker.jaspasema.sampleproject.publisher;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class PublisherData {

    private long id;

    @NonNull
    private String name;

    @NonNull
    private List<PublisherBookData> books;

    @Value
    public static class PublisherBookData {
        private long id;

        @NonNull
        private String title;

        private Integer year;

        private Integer edition;

        @NonNull
        private List<PublisherBookAuthorData> authors;
    }

    @Value
    public static class PublisherBookAuthorData {
        private long id;

        @NonNull
        private String name;
    }
}
