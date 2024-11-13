package ninja.javahacker.jaspasema.service;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.javalin.http.Context;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.jaspasema.exceptions.retvalue.MalformedReturnValueException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public final class ServiceMethodRunner<T> implements JaspasemaRoute {

    private static final String PANIC = 
            """
            <!DOCTYPE html>
            <html>
              <head>
                <title>SERIOUS ERROR 500</title>
              </head>
              <body>
                <p>A very serious error happened when trying to handle another error. Sorry.</p>
                <pre>$ERROR$</pre>
              </body>
            </html>
            """;

    @NonNull
    private final ReifiedGeneric<T> target;

    @NonNull
    private final Method method;

    @NonNull
    private final List<ParamProcessor.Stub<?>> parameterProcessors;

    @NonNull
    private final ReturnMapper<T> returnProcessor;

    @NonNull
    private final Object instance;

    @SuppressFBWarnings("RFI_SET_ACCESSIBLE")
    public ServiceMethodRunner(
            @NonNull ReifiedGeneric<T> target,
            @NonNull Object instance,
            @NonNull Method method,
            @NonNull List<ParamProcessor.Stub<?>> parameterProcessors,
            @NonNull ReturnMapper<T> returnProcessor)
    {
        this.target = target;
        this.instance = instance;
        this.method = method;
        this.parameterProcessors = parameterProcessors;
        this.returnProcessor = returnProcessor;

        method.setAccessible(true);
    }

    @Override
    public void handle(@NonNull Context ctx)
            throws InvocationTargetException, ParameterValueException, MalformedReturnValueException
    {
        // Can't use streams here due to ParameterValueException that run(ctx) might throw.
        List<Object> parameters = new ArrayList<>(parameterProcessors.size());
        Throwable badThing = null;
        try {
            try {
                for (var ppw : parameterProcessors) {
                    parameters.add(ppw.getWorker().run(ctx));
                }
            } catch (ParameterValueException e) {
                badThing = e;
                returnProcessor.onException(e).getWorker().run(ctx, e);
                throw e;
            }

            try {
                var result = invoke(parameters);
                returnProcessor.onReturn().getWorker().run(ctx, result);
            } catch (InvocationTargetException e) {
                var cause = e.getCause();
                returnProcessor.onException(cause).getWorker().run(ctx, cause);
                throw e;
            } catch (MalformedReturnValueException e) {
                badThing = e;
                returnProcessor.onException(e).getWorker().run(ctx, e);
                throw e;
            }
        } catch (MalformedReturnValueException e) {
            if (badThing == null) throw e;
            e.addSuppressed(badThing);
            ctx.status(500);
            var baos = new ByteArrayOutputStream();
            var pw = new PrintStream(baos, true, StandardCharsets.UTF_8);
            e.printStackTrace(pw);
            ctx.result(PANIC.replace("$ERROR$", baos.toString(StandardCharsets.UTF_8)));
            ctx.contentType("text/html;charset=utf-8");
            throw e;
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private T invoke(@NonNull List<Object> parameters) throws InvocationTargetException {
        try {
            return (T) method.invoke(instance, parameters.toArray());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new AssertionError(method + " " + parameters, e);
        }
    }
}
