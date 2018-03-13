package br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor;

import java.lang.reflect.Parameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MalformedParameterException extends Exception {
    private static final long serialVersionUID = 1L;

    public MalformedParameterException(Parameter parameter, String message) {
        super("[" + parameter + "] " + message);
    }

    public MalformedParameterException(Parameter parameter, String message, Throwable cause) {
        super("[" + parameter + "] " + message, cause);
    }
}
