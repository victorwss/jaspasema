package ninja.javahacker.jaspasema.sampleproject.book;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class BookData {

    private long id;

    @NonNull
    private String title;

    @NonNull
    private String publisher;

    private Integer year;

    private Integer edition;

    @NonNull
    private List<String> authors;
}
