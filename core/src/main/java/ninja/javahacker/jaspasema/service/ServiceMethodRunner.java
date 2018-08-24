package ninja.javahacker.jaspasema.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
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
public final class ServiceMethodRunner<T> implements JaspasemaRoute {

    private static final String PANIC = ""
            + "<!DOCTYPE html>"
            + "<html>"
            + "  <head>"
            + "    <title>SERIOUS ERROR 500</title>"
            + "  </head>"
            + "  <body>"
            + "    <p>A very serious error happened when trying to handle another error. Sorry.</p>"
            + "    <pre>$ERROR$</pre>"
            + "  </body>"
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
            throws BadServiceMappingException
    {
        this.target = target;
        this.instance = instance;
        this.method = method;
        this.parameterProcessors = parameterProcessors;
        this.returnProcessor = returnProcessor;

        method.setAccessible(true);
    }

    @Override
    public void handleIt(@NonNull Request rq, @NonNull Response rp)
            throws InvocationTargetException, ParameterValueException, MalformedReturnValueException
    {
        // Can't use streams here due to MalformedParameterException that run(rq, rp) might throw.
        List<Object> parameters = new ArrayList<>(parameterProcessors.size());
        Throwable badThing = null;
        try {
            try {
                for (ParamProcessor.Stub<?> ppw : parameterProcessors) {
                    parameters.add(ppw.getWorker().run(rq, rp));
                }
            } catch (ParameterValueException e) {
                badThing = e;
                returnProcessor.onException(e).getWorker().run(rq, rp, e);
                throw e;
            }

            try {
                T result = invoke(parameters);
                returnProcessor.onReturn().getWorker().run(rq, rp, result);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                returnProcessor.onException(cause).getWorker().run(rq, rp, cause);
                throw e;
            } catch (MalformedReturnValueException e) {
                badThing = e;
                returnProcessor.onException(e).getWorker().run(rq, rp, e);
                throw e;
            }
        } catch (MalformedReturnValueException e) {
            if (badThing == null) throw e;
            e.addSuppressed(badThing);
            rp.status(500);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                // Java 10: replaces "UTF-8" with StandardCharsets.UTF_8 and get rid of the UnsupportedEncodingException.
                PrintStream pw = new PrintStream(baos, true, "UTF-8");
                e.printStackTrace(pw);
                rp.body(PANIC.replace("$ERROR$", baos.toString("UTF-8")));
            } catch (UnsupportedEncodingException x) {
                throw new AssertionError(x);
            }
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
