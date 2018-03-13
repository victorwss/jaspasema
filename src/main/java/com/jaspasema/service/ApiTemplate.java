package br.gov.sp.prefeitura.smit.cgtic.jaspasema.service;

import spark.Route;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ApiTemplate {
    public Route createStub(ServiceConfigurer sc);
}
