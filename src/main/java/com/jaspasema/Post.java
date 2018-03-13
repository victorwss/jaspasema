package br.gov.sp.prefeitura.smit.cgtic.jaspasema;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.HttpMethod;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Victor Williams Stafusa da Silva
 */
@HttpMethod
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Post {
}
