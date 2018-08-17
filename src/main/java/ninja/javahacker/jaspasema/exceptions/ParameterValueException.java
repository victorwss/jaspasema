package ninja.javahacker.jaspasema.exceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import lombok.*;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public abstract class ParameterValueException extends Exception {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Parameter parameter;

    protected ParameterValueException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message) {
        this(parameter, message, null);
    }

    protected ParameterValueException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message, /*@NonNull*/ Throwable cause) {
        super("[" + parameter + "|" + parameter.getDeclaringExecutable() + "] " + message, cause);
        this.parameter = parameter;
    }

    @Getter
    public static class AbsentRequiredParameterException extends ParameterValueException {
        private static final long serialVersionUID = 1L;

        public static final String TEMPLATE = "The required parameter value was absent.";

        protected AbsentRequiredParameterException(/*@NonNull*/ Parameter parameter) {
            super(parameter, TEMPLATE);
        }

        public static AbsentRequiredParameterException create(@NonNull Parameter parameter) {
            return new AbsentRequiredParameterException(parameter);
        }
    }

    @Getter
    public static class MalformedParameterException extends ParameterValueException {
        private static final long serialVersionUID = 1L;

        public static final String TEMPLATE = "The value \"$V$\" is invalid for a @$A$-annotated parameter.";

        @NonNull
        private final Class<? extends Annotation> annotation;

        @NonNull
        private final String rawValue;

        protected MalformedParameterException(
                /*@NonNull*/ Parameter parameter,
                /*@NonNull*/ Class<? extends Annotation> annotation,
                /*@NonNull*/ String rawValue,
                /*@NonNull*/ Throwable cause)
        {
            super(parameter, TEMPLATE.replace("$A$", annotation.getSimpleName()).replace("$V$", rawValue), cause);
            this.annotation = annotation;
            this.rawValue = rawValue;
        }

        public static MalformedParameterException create(
                @NonNull Parameter parameter,
                @NonNull Class<? extends Annotation> annotation,
                @NonNull String rawValue,
                @NonNull Throwable cause)
        {
            return new MalformedParameterException(parameter, annotation, rawValue, cause);
        }
    }

    @Getter
    public static class MalformedJsonBodyException extends ParameterValueException {
        private static final long serialVersionUID = 1L;

        public static final String TEMPLATE = "The body request data failed to be parseable as JSON.";

        protected MalformedJsonBodyException(
                /*@NonNull*/ Parameter parameter,
                /*@NonNull*/ Throwable cause)
        {
            super(parameter, TEMPLATE, cause);
        }

        public static MalformedJsonBodyException create(
                @NonNull Parameter parameter,
                @NonNull Throwable cause)
        {
            return new MalformedJsonBodyException(parameter, cause);
        }
    }
}
