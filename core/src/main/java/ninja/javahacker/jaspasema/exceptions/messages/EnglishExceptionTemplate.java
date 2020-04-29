package ninja.javahacker.jaspasema.exceptions.messages;

import java.util.Map;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.JaspasemaException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ConflictingAnnotationsReturnException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ConflictingAnnotationsThrowsException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ConflictingMappingOnParameterException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ConflictingMappingOnReturnTypeException;
import ninja.javahacker.jaspasema.exceptions.badmapping.DontKnowHowToHandleAnnotationException;
import ninja.javahacker.jaspasema.exceptions.badmapping.EmptyDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ExceptionMappingOnReturnException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ImplicitWithJsVarException;
import ninja.javahacker.jaspasema.exceptions.badmapping.InvalidDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.MissingPathException;
import ninja.javahacker.jaspasema.exceptions.badmapping.MultipleHttpMethodAnnotationsException;
import ninja.javahacker.jaspasema.exceptions.badmapping.NoHttpMethodAnnotationsException;
import ninja.javahacker.jaspasema.exceptions.badmapping.NoMappingOnParameterException;
import ninja.javahacker.jaspasema.exceptions.badmapping.NoMappingOnReturnTypeException;
import ninja.javahacker.jaspasema.exceptions.badmapping.RemapperConstructorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException.AllowedTypes;
import ninja.javahacker.jaspasema.exceptions.badmapping.UninstantiableRemapperException;
import ninja.javahacker.jaspasema.exceptions.badmapping.UnmatcheableParameterException;
import ninja.javahacker.jaspasema.exceptions.badmapping.VoidWithValueReturnTypeException;
import ninja.javahacker.jaspasema.exceptions.http.AlreadyExistsException;
import ninja.javahacker.jaspasema.exceptions.http.BadRequestException;
import ninja.javahacker.jaspasema.exceptions.http.EntityDeletedException;
import ninja.javahacker.jaspasema.exceptions.http.HttpException;
import ninja.javahacker.jaspasema.exceptions.http.NotAllowedException;
import ninja.javahacker.jaspasema.exceptions.http.NotFoundException;
import ninja.javahacker.jaspasema.exceptions.http.UnexpectedHttpException;
import ninja.javahacker.jaspasema.exceptions.http.UnprocessableRequestException;
import ninja.javahacker.jaspasema.exceptions.paramproc.IncompatibleParameterProcessorException;
import ninja.javahacker.jaspasema.exceptions.paramproc.ParameterProcessorConstructorException;
import ninja.javahacker.jaspasema.exceptions.paramproc.UninstantiableParameterProcessorException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.AbsentRequiredParameterException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedJsonBodyException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedParameterValueException;
import ninja.javahacker.jaspasema.exceptions.retproc.BadExitDiscriminatorMethodException;
import ninja.javahacker.jaspasema.exceptions.retproc.IncompatibleReturnProcessorException;
import ninja.javahacker.jaspasema.exceptions.retproc.MultipleReturnProcessorsException;
import ninja.javahacker.jaspasema.exceptions.retproc.ReturnProcessorConstructorException;
import ninja.javahacker.jaspasema.exceptions.retproc.ReturnProcessorNotFoundException;
import ninja.javahacker.jaspasema.exceptions.retproc.UninstantiableReturnProcessorException;
import ninja.javahacker.jaspasema.exceptions.retvalue.MalformedJsonReturnValueException;

/**
 * @author Victor Williams Stafusa da Silva
 */
public enum EnglishExceptionTemplate implements ExceptionTemplate {
    INSTANCE;

    private static final Map<Class<? extends JaspasemaException>, String> TEMPLATES = Map.ofEntries(
            Map.entry(
                    ConflictingAnnotationsReturnException.class,
                    "Conflicting @ResultSerializer-annotated annotations on method for return type."),
            Map.entry(
                    ConflictingAnnotationsThrowsException.class,
                    "Conflicting @ResultSerializer-annotated annotations on method for exception $X$."),
            Map.entry(ConflictingMappingOnParameterException.class, "Conflicting mapping on parameter."),
            Map.entry(ConflictingMappingOnReturnTypeException.class, "Conflicting mapping on return type."),
            Map.entry(DontKnowHowToHandleAnnotationException.class, "Don't know how to handle @$A$."),
            Map.entry(EmptyDateFormatException.class, "Empty date format at @$A$ annotation."),
            Map.entry(ExceptionMappingOnReturnException.class, "Can't be used for normal returns. Only for exceptions."),
            Map.entry(ImplicitWithJsVarException.class, "The @$A$ annotation shouldn't have jsVar not empty and be implicit."),
            Map.entry(InvalidDateFormatException.class, "Invalid date format \"$F$\" at @$A$ annotation."),
            Map.entry(MissingPathException.class, "Missing mandatory @Path annotation."),
            Map.entry(MultipleHttpMethodAnnotationsException.class, "Multiple @HttpMethod-annotated annotations on method."),
            Map.entry(NoHttpMethodAnnotationsException.class, "No @HttpMethod-annotated annotations on method."),
            Map.entry(NoMappingOnParameterException.class, "No mapping on parameter."),
            Map.entry(NoMappingOnReturnTypeException.class, "No mapping on return type."),
            Map.entry(RemapperConstructorException.class, "The remapper constructor of class $R$ throwed an exception."),
            Map.entry(
                    TypeRestrictionViolationException.class,
                    "The @$A$ annotation must be used only on $V$ $U$. The found type was $T$."),
            Map.entry(UninstantiableRemapperException.class, "The exception remapper $R$ is not an instantiable class."),
            Map.entry(UnmatcheableParameterException.class, "The parameter value do not matches anything in method's @Path value."),
            Map.entry(VoidWithValueReturnTypeException.class, "Methods returning void should not feature @$A$-annotated annotations."),
            Map.entry(AlreadyExistsException.class, "[409] The object $TYPE$ with the key $KEY$ already exists."),
            Map.entry(BadRequestException.class, "[400] The request is malformed. The reason is \"$CAUSE$\"."),
            Map.entry(EntityDeletedException.class, "[410] The object $TYPE$ with the key $KEY$ was deleted."),
            Map.entry(HttpException.class, "An HTTP exception [$STATUS$] happened. The cause is \"$CAUSE$\"."),
            Map.entry(NotAllowedException.class, "[403] You don't have permission to execute this operation."),
            Map.entry(NotFoundException.class, "[404] The object $TYPE$ with the key $KEY$ was not found."),
            Map.entry(UnexpectedHttpException.class, "[500] An unexpected error happened. Cause is \"$CAUSE$\"."),
            Map.entry(UnprocessableRequestException.class, "[422] The request can't be processed. The reason is \"$CAUSE$\"."),
            Map.entry(
                    IncompatibleParameterProcessorException.class,
                    "The parameter processor $R$ assigned for annotation @$A$ do not understands it."),
            Map.entry(
                    ParameterProcessorConstructorException.class,
                    "The parameter processor constructor of class $R$ for annotation @$A$ throwed an exception."),
            Map.entry(
                    UninstantiableParameterProcessorException.class,
                    "The parameter processor $R$ for annotation @$A$ is not an instantiable class."),
            Map.entry(AbsentRequiredParameterException.class, "The required parameter value was absent."),
            Map.entry(MalformedJsonBodyException.class, "The body request data failed to be parseable as JSON."),
            Map.entry(MalformedParameterValueException.class, "The value \"$V$\" is invalid for a @$A$-annotated parameter."),
            Map.entry(BadExitDiscriminatorMethodException.class, "The annotation @$A$ have an ill-formed @ExitDiscriminator method."),
            Map.entry(
                    IncompatibleReturnProcessorException.class,
                    "The return processor $R$ assigned for annotation @$A$ do not understands it."),
            Map.entry(
                    MultipleReturnProcessorsException.class,
                    "The annotation @$A$ should not have more than one @ExitDiscriminator method."),
            Map.entry(
                    ReturnProcessorConstructorException.class,
                    "The return processor constructor of class $R$ for annotation @$A$ throwed an exception."),
            Map.entry(ReturnProcessorNotFoundException.class, "The annotation @$A$ do not have any @ExitDiscriminator method."),
            Map.entry(
                    UninstantiableReturnProcessorException.class,
                    "The return processor $R$ for annotation @$A$ is not an instantiable class."),
            Map.entry(MalformedJsonReturnValueException.class, "The returned value couldn't be converted to JSON.")
    );

    private static final Map<AllowedTypes, String> NAMES = Map.ofEntries(
            Map.entry(AllowedTypes.SIMPLE, "primitives, primitive wrappers, Strings and date/time"),
            Map.entry(AllowedTypes.SIMPLE_LIST, "lists of primitive wrappers, Strings and date/time"),
            Map.entry(AllowedTypes.SIMPLE_AND_LIST, "primitives, primitive wrappers, Strings, date/time and lists of those"),
            Map.entry(AllowedTypes.DATE_TIME, "date/time"),
            Map.entry(AllowedTypes.DATE_TIME_LIST, "lists of date/time"),
            Map.entry(AllowedTypes.LIST, "list"),
            Map.entry(AllowedTypes.HTTP, "Request, Response or Session")
    );

    @NonNull
    @Override
    public String templateFor(@NonNull Class<? extends JaspasemaException> type) {
        var s = TEMPLATES.get(type);
        if (s == null) return "";
        return s;
    }

    @NonNull
    @Override
    public String nameFor(@NonNull AllowedTypes type) {
        var s = NAMES.get(type);
        if (s == null) throw new AssertionError();
        return s;
    }

    @NonNull
    @Override
    public String getReturningMethods() {
        return "returning methods";
    }

    @NonNull
    @Override
    public String getParameters() {
        return "parameters";
    }
}
