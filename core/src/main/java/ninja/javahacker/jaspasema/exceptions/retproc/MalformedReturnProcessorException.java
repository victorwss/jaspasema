package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;

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

    public static interface Factory {
        public IncompatibleReturnProcessorException incompatible(
                Class<? extends Annotation> ac,
                Class<? extends ReturnProcessor<?>> cc,
                Throwable e);

        public ReturnProcessorConstructorException exception(
                Class<? extends Annotation> ac,
                Class<? extends ReturnProcessor<?>> cc,
                Throwable e);

        public UninstantiableReturnProcessorException uninstantiable(
                Class<? extends Annotation> ac,
                Class<? extends ReturnProcessor<?>> cc,
                Throwable e);

        public MultipleReturnProcessorsException multiple(
                Class<? extends Annotation> ac,
                Class<? extends ReturnProcessor<?>> cc);

        public BadExitDiscriminatorMethodException badExit(
                Class<? extends Annotation> ac,
                Class<? extends ReturnProcessor<?>> cc);

        public ReturnProcessorNotFoundException notFound(
                Class<? extends Annotation> ac,
                Class<? extends ReturnProcessor<?>> cc);
    }

    public static Factory onClass(Class<?> declaringClass) {
        return new Factory() {
            @Override
            public IncompatibleReturnProcessorException incompatible(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc,
                    Throwable e)
            {
                return new IncompatibleReturnProcessorException(declaringClass, ac, cc, e);
            }

            @Override
            public ReturnProcessorConstructorException exception(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc,
                    Throwable e)
            {
                return new ReturnProcessorConstructorException(declaringClass, ac, cc, e);
            }

            @Override
            public UninstantiableReturnProcessorException uninstantiable(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc,
                    Throwable e)
            {
                return new UninstantiableReturnProcessorException(declaringClass, ac, cc, e);
            }

            @Override
            public MultipleReturnProcessorsException multiple(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc)
            {
                return new MultipleReturnProcessorsException(declaringClass, ac, cc);
            }

            @Override
            public BadExitDiscriminatorMethodException badExit(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc)
            {
                return new BadExitDiscriminatorMethodException(declaringClass, ac, cc);
            }

            @Override
            public ReturnProcessorNotFoundException notFound(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc)
            {
                return new ReturnProcessorNotFoundException(declaringClass, ac, cc);
            }
        };
    }

    public static Factory onMethod(Method method) {
        return new Factory() {
            @Override
            public IncompatibleReturnProcessorException incompatible(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc,
                    Throwable e)
            {
                return new IncompatibleReturnProcessorException(method, ac, cc, e);
            }

            @Override
            public ReturnProcessorConstructorException exception(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc,
                    Throwable e)
            {
                return new ReturnProcessorConstructorException(method, ac, cc, e);
            }

            @Override
            public UninstantiableReturnProcessorException uninstantiable(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc,
                    Throwable e)
            {
                return new UninstantiableReturnProcessorException(method, ac, cc, e);
            }

            @Override
            public MultipleReturnProcessorsException multiple(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc)
            {
                return new MultipleReturnProcessorsException(method, ac, cc);
            }

            @Override
            public BadExitDiscriminatorMethodException badExit(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc)
            {
                return new BadExitDiscriminatorMethodException(method, ac, cc);
            }

            @Override
            public ReturnProcessorNotFoundException notFound(
                    Class<? extends Annotation> ac,
                    Class<? extends ReturnProcessor<?>> cc)
            {
                return new ReturnProcessorNotFoundException(method, ac, cc);
            }
        };
    }
}
