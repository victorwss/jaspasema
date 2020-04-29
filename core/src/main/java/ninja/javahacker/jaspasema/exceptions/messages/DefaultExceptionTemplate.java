package ninja.javahacker.jaspasema.exceptions.messages;

import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.PackagePrivate;
import lombok.experimental.UtilityClass;

/**
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
@PackagePrivate
class DefaultExceptionTemplate {
    public static final AtomicReference<ExceptionTemplate> ACTIVE =
            new AtomicReference<>(EnglishExceptionTemplate.INSTANCE);
}
