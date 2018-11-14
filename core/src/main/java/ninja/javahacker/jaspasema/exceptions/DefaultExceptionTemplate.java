package ninja.javahacker.jaspasema.exceptions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    private static ExceptionTemplate defaultEn() {
        try (InputStream is = DefaultExceptionTemplate.class.getResourceAsStream("exceptions.en.json")) {
            return ExceptionTemplate.forJsonMap(is);
        } catch (IOException x) {
            throw new AssertionError(x);
        }
    }
}
