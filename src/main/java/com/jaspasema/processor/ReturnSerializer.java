package br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ReturnSerializer {
    public Class<? extends ReturnProcessor<?>> processor();
}
