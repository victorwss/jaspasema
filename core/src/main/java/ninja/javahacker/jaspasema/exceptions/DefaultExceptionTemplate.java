package ninja.javahacker.jaspasema.exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.PackagePrivate;

/**
 * @author Victor Williams Stafusa da Silva
 */
@PackagePrivate
class DefaultExceptionTemplate {
    public static final ExceptionTemplate DEFAULT_TEMPLATE_EN = defaultEn();

    public static final AtomicReference<ExceptionTemplate> ACTIVE =
            new AtomicReference<>(DEFAULT_TEMPLATE_EN);

    private static ExceptionTemplate defaultEn() {
        try (InputStream is = DefaultExceptionTemplate.class.getResourceAsStream("exceptions.en.json")) {
            return ExceptionTemplate.forJsonMap(is);
        } catch (IOException x) {
            throw new AssertionError(x);
        }
    }
}
