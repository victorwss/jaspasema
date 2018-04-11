package ninja.javahacker.jaspasema.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.MalformedParameterException;
import ninja.javahacker.jaspasema.processor.MalformedReturnValueException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;
import ninja.javahacker.jaspasema.processor.TargetType;
import spark.Request;
import spark.Response;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ServiceMethodRunner<T> implements JaspasemaRoute {

    @Getter
    @NonNull
    private final TargetType<T> target;

    @Getter
    @NonNull
    private final Method method;

    @Getter
    @NonNull
    private final List<ParamProcessor.Stub<?>> parameterProcessors;

    @Getter
    @NonNull
    private final ReturnProcessor.Stub<T> returnProcessor;

    @Getter
    @NonNull
    private final Object instance;

    public ServiceMethodRunner(
            @NonNull TargetType<T> target,
            @NonNull Object instance,
            @NonNull Method method,
            @NonNull List<ParamProcessor.Stub<?>> parameterProcessors,
            @NonNull ReturnProcessor.Stub<T> returnProcessor)
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
    public void handleIt(
            @NonNull Request rq,
            @NonNull Response rp)
            throws InvocationTargetException,
            MalformedParameterException,
            MalformedReturnValueException
    {
        // Can't use streams here due to MalformedParameterException that run(rq, rp) might throw.
        List<Object> parameters = new ArrayList<>(parameterProcessors.size());
        for (ParamProcessor.Stub<?> ppw : parameterProcessors) {
            parameters.add(ppw.getWorker().run(rq, rp));
        }

        returnProcessor.getWorker().run(rq, rp, invoke(parameters));
    }

    @SuppressWarnings("unchecked")
    private T invoke(@NonNull List<Object> parameters) throws InvocationTargetException {
        try {
            return (T) method.invoke(instance, parameters.toArray());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new AssertionError(e);
        }
    }
}
