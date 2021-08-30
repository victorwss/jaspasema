package ninja.javahacker.jaspasema.processor;

/**
 * Unistantiable class used to denote, in exception mapping tables, that some method returned normally
 * instead of throwing an exception.
 * @author Victor Williams Stafusa da Silva
 */
public final class ReturnedOk extends Throwable {

    private static final long serialVersionUID = 1L;

    private ReturnedOk() {
        throw new UnsupportedOperationException();
    }
}
