package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.RemapperConstructorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.UninstantiableRemapperException;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;
import ninja.javahacker.jaspasema.processor.ReturnSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Repeatable(value = OutputRemapper.Container.class)
@ReturnSerializer(processor = OutputRemapper.Processor.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OutputRemapper {
    public String jQueryType() default "text";

    @ReturnSerializer.ExitDiscriminator
    public Class<? extends Throwable> on() default ReturnedOk.class;

    public Class<? extends ExceptionRemapper> remapper();

    public static class Processor implements ReturnProcessor<OutputRemapper> {
        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull OutputRemapper annotation,
                @NonNull Method method)
                throws BadServiceMappingException
        {
            ExceptionRemapper f;
            Class<? extends ExceptionRemapper> remapperClass = annotation.remapper();
            try {
                Constructor<? extends ExceptionRemapper> ctor = remapperClass.getConstructor();
                f = ctor.newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new UninstantiableRemapperException(method, remapperClass, e);
            } catch (InvocationTargetException e) {
                throw new RemapperConstructorException(method, remapperClass, e.getCause());
            }
            f.validate(target, annotation, method);
            return new Stub<>(f::remap, annotation.jQueryType());
        }
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Container {
        public OutputRemapper[] value();
    }
}
