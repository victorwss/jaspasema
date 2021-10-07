package ninja.javahacker.jaspasema.sampleproject.author;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class AuthorData {

    private long id;

    @NonNull
    private String name;

    @NonNull
    private List<AuthorBookData> books;

    @Value
    public static class AuthorBookData {
        private long id;

        @NonNull
        private String title;

        private long idPublisher;

        @NonNull
        private String namePublisher;

        private Integer year;

        private Integer edition;
    }
}
