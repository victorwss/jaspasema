package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.RemapperConstructorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.UninstantiableRemapperException;
import ninja.javahacker.jaspasema.processor.AnnotatedMethod;
import ninja.javahacker.jaspasema.processor.ResultProcessor;
import ninja.javahacker.jaspasema.processor.ResultSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Repeatable(value = OutputRemapper.Container.class)
@ResultSerializer(processor = OutputRemapper.Processor.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OutputRemapper {
    public String jQueryType() default "text";

    @ResultSerializer.ExitDiscriminator
    public Class<? extends Throwable> on() default ReturnedOk.class;

    public Class<? extends ExceptionRemapper> remapper();

    public static class Processor implements ResultProcessor<OutputRemapper, Object> {

        @NonNull
        @Override
        public <E> Stub<E> prepare(@NonNull AnnotatedMethod<OutputRemapper, E> meth) throws BadServiceMappingException {
            var method = meth.getMethod();
            var annotation = meth.getAnnotation();
            var remapperClass = annotation.remapper();
            ExceptionRemapper f;
            try {
                f = remapperClass.getConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new UninstantiableRemapperException(method, remapperClass, e);
            } catch (InvocationTargetException e) {
                throw new RemapperConstructorException(method, remapperClass, e.getCause());
            }
            f.validate(meth);
            return new Stub<>(f::remap, annotation.jQueryType());
        }
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Container {
        public OutputRemapper[] value();
    }
}
