package br.gov.sp.prefeitura.smit.cgtic.jaspasema;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.BadServiceMappingException;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.JsonTypesProcessor;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.ReturnProcessor;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.ReturnSerializer;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.TargetType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ReturnSerializer(processor = ProducesJson.Processor.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProducesJson {
    public boolean lenient() default false;

    public static class Processor implements ReturnProcessor<ProducesJson> {
        @Override
        public <E> Stub<E> prepare(
                @NonNull TargetType<E> target,
                @NonNull ProducesJson annotation,
                @NonNull Method method)
                throws BadServiceMappingException
        {
            return new Stub<>(v -> JsonTypesProcessor.writeJson(RuntimeException::new, v), "json");
        }
    }
}