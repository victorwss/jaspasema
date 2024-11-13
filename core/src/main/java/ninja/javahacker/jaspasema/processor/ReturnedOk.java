package ninja.javahacker.jaspasema.processor;

/**
 * Unistantiable class used to denote, in exception mapping tables, that some method returned normally
 * instead of throwing an exception.
 * @author Victor Williams Stafusa da Silva
 */
@SuppressWarnings({
    "pmd:MissingStaticMethodInNonInstantiatableClass",
    "pmd:DoNotExtendJavaLangThrowable"
})
public final class ReturnedOk extends Throwable {

    private static final long serialVersionUID = 1L;

    /**
     * This class can't be instantiated.
     */
    private ReturnedOk() {
        throw new UnsupportedOperationException();
    }
}
