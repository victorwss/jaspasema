package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.processor.ResultProcessor;

/**
 * @author Victor Williams Stafusa da Silva
 */
public abstract class MalformedReturnProcessorException extends MalformedProcessorException {
    private static final long serialVersionUID = 1L;

    protected MalformedReturnProcessorException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(method, badAnnotation, processorClass, cause);
    }

    protected MalformedReturnProcessorException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<?> processorClass)
    {
        super(method, badAnnotation, processorClass);
    }

    protected MalformedReturnProcessorException(
            /*@NonNull*/ Class<?> declaringClass,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(declaringClass, badAnnotation, processorClass, cause);
    }

    protected MalformedReturnProcessorException(
            /*@NonNull*/ Class<?> declaringClass,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<?> processorClass)
    {
        super(declaringClass, badAnnotation, processorClass);
    }

    @NonNull
    public static interface Factory {
        @NonNull
        public IncompatibleReturnProcessorException incompatible(
                @NonNull Class<? extends Annotation> ac,
                @NonNull Class<? extends ResultProcessor<?, ?>> cc,
                @NonNull Throwable e);

        @NonNull
        public ReturnProcessorConstructorException exception(
                @NonNull Class<? extends Annotation> ac,
                @NonNull Class<? extends ResultProcessor<?, ?>> cc,
                @NonNull Throwable e);

        @NonNull
        public UninstantiableReturnProcessorException uninstantiable(
                @NonNull Class<? extends Annotation> ac,
                @NonNull Class<? extends ResultProcessor<?, ?>> cc,
                @NonNull Throwable e);

        @NonNull
        public MultipleReturnProcessorsException multiple(
                @NonNull Class<? extends Annotation> ac,
                @NonNull Class<? extends ResultProcessor<?, ?>> cc);

        @NonNull
        public BadExitDiscriminatorMethodException badExit(
                @NonNull Class<? extends Annotation> ac,
                @NonNull Class<? extends ResultProcessor<?, ?>> cc);

        @NonNull
        public ReturnProcessorNotFoundException notFound(
                @NonNull Class<? extends Annotation> ac,
                @NonNull Class<? extends ResultProcessor<?, ?>> cc);
    }

    @NonNull
    public static Factory onClass(@NonNull Class<?> declaringClass) {
        return new Factory() {
            @Override
            public IncompatibleReturnProcessorException incompatible(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc,
                    @NonNull Throwable e)
            {
                return new IncompatibleReturnProcessorException(declaringClass, ac, cc, e);
            }

            @Override
            public ReturnProcessorConstructorException exception(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc,
                    @NonNull Throwable e)
            {
                return new ReturnProcessorConstructorException(declaringClass, ac, cc, e);
            }

            @Override
            public UninstantiableReturnProcessorException uninstantiable(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc,
                    @NonNull Throwable e)
            {
                return new UninstantiableReturnProcessorException(declaringClass, ac, cc, e);
            }

            @Override
            public MultipleReturnProcessorsException multiple(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc)
            {
                return new MultipleReturnProcessorsException(declaringClass, ac, cc);
            }

            @Override
            public BadExitDiscriminatorMethodException badExit(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc)
            {
                return new BadExitDiscriminatorMethodException(declaringClass, ac, cc);
            }

            @Override
            public ReturnProcessorNotFoundException notFound(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc)
            {
                return new ReturnProcessorNotFoundException(declaringClass, ac, cc);
            }
        };
    }

    public static Factory onMethod(@NonNull Method method) {
        return new Factory() {

            @NonNull
            @Override
            public IncompatibleReturnProcessorException incompatible(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc,
                    @NonNull Throwable e)
            {
                return new IncompatibleReturnProcessorException(method, ac, cc, e);
            }

            @NonNull
            @Override
            public ReturnProcessorConstructorException exception(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc,
                    @NonNull Throwable e)
            {
                return new ReturnProcessorConstructorException(method, ac, cc, e);
            }

            @NonNull
            @Override
            public UninstantiableReturnProcessorException uninstantiable(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc,
                    @NonNull Throwable e)
            {
                return new UninstantiableReturnProcessorException(method, ac, cc, e);
            }

            @NonNull
            @Override
            public MultipleReturnProcessorsException multiple(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc)
            {
                return new MultipleReturnProcessorsException(method, ac, cc);
            }

            @NonNull
            @Override
            public BadExitDiscriminatorMethodException badExit(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc)
            {
                return new BadExitDiscriminatorMethodException(method, ac, cc);
            }

            @NonNull
            @Override
            public ReturnProcessorNotFoundException notFound(
                    @NonNull Class<? extends Annotation> ac,
                    @NonNull Class<? extends ResultProcessor<?, ?>> cc)
            {
                return new ReturnProcessorNotFoundException(method, ac, cc);
            }
        };
    }
}
