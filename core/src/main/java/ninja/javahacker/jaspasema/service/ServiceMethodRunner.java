package ninja.javahacker.jaspasema.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.jaspasema.exceptions.retvalue.MalformedReturnValueException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import spark.Request;
import spark.Response;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
@SuppressFBWarnings("RFI_SET_ACCESSIBLE")
public final class ServiceMethodRunner<T> implements JaspasemaRoute {

    private static final String PANIC = ""
            + "<!DOCTYPE html>\n"
            + "<html>\n"
            + "  <head>\n"
            + "    <title>SERIOUS ERROR 500</title>\n"
            + "  </head>\n"
            + "  <body>\n"
            + "    <p>A very serious error happened when trying to handle another error. Sorry.</p>\n"
            + "    <pre>$ERROR$</pre>\n"
            + "  </body>\n"
            + "</html>";

    @NonNull
    private final ReifiedGeneric<T> target;

    @NonNull
    private final Method method;

    @NonNull
    private final List<ParamProcessor.Stub<?>> parameterProcessors;

    @NonNull
    private final ReturnMapper.ReturnMap<T> returnProcessor;

    @NonNull
    private final Object instance;

    public ServiceMethodRunner(
            @NonNull ReifiedGeneric<T> target,
            @NonNull Object instance,
            @NonNull Method method,
            @NonNull List<ParamProcessor.Stub<?>> parameterProcessors,
            @NonNull ReturnMapper.ReturnMap<T> returnProcessor)
    {
        this.target = target;
        this.instance = instance;
        this.method = method;
        this.parameterProcessors = parameterProcessors;
        this.returnProcessor = returnProcessor;

        PrivilegedAction<Void> action = () -> {
            method.setAccessible(true);
            return null;
        };
        AccessController.doPrivileged(action);
    }

    @Override
    public void handleIt(@NonNull Request rq, @NonNull Response rp)
            throws InvocationTargetException, ParameterValueException, MalformedReturnValueException
    {
        // Can't use streams here due to ParameterValueException that run(rq, rp) might throw.
        List<Object> parameters = new ArrayList<>(parameterProcessors.size());
        Throwable badThing = null;
        try {
            try {
                for (ParamProcessor.Stub<?> ppw : parameterProcessors) {
                    parameters.add(ppw.getWorker().run(rq, rp));
                }
            } catch (ParameterValueException e) {
                badThing = e;
                returnProcessor.onException(e).getWorker().run(method, rq, rp, e);
                throw e;
            }

            try {
                T result = invoke(parameters);
                returnProcessor.onReturn().getWorker().run(method, rq, rp, result);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                returnProcessor.onException(cause).getWorker().run(method, rq, rp, cause);
                throw e;
            } catch (MalformedReturnValueException e) {
                badThing = e;
                returnProcessor.onException(e).getWorker().run(method, rq, rp, e);
                throw e;
            }
        } catch (MalformedReturnValueException e) {
            if (badThing == null) throw e;
            e.addSuppressed(badThing);
            rp.status(500);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream pw = new PrintStream(baos, true, StandardCharsets.UTF_8);
            e.printStackTrace(pw);
            rp.body(PANIC.replace("$ERROR$", baos.toString(StandardCharsets.UTF_8)));
            rp.type("text/html;charset=utf-8");
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private T invoke(@NonNull List<Object> parameters) throws InvocationTargetException {
        try {
            return (T) method.invoke(instance, parameters.toArray());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new AssertionError(method + " " + parameters, e);
        }
    }
}
