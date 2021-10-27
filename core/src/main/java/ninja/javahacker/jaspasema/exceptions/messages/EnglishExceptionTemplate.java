package ninja.javahacker.jaspasema.exceptions.messages;

import static ninja.javahacker.jaspasema.exceptions.messages.LanguageExceptionTemplate.template;

import java.util.Map;
import java.util.function.Function;
import lombok.experimental.Delegate;
import lombok.experimental.PackagePrivate;
import ninja.javahacker.jaspasema.exceptions.badmapping.AllowedTypes;
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
import ninja.javahacker.jaspasema.exceptions.badmapping.ParameterTypeRestrictionViolationException;
import ninja.javahacker.jaspasema.exceptions.badmapping.RemapperConstructorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ReturnTypeRestrictionViolationException;
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
@PackagePrivate
enum EnglishExceptionTemplate implements ExceptionTemplate {
    INSTANCE;

    @Delegate
    private final LanguageExceptionTemplate template;

    private EnglishExceptionTemplate() {
        this.template = new LanguageExceptionTemplate(Inner.TEMPLATES, Inner.NAMES);
    }

    private static final class Inner {
        private static final Map<Class<? extends Throwable>, Function<Throwable, String>> TEMPLATES = Map.ofEntries(
                Map.entry(
                        ConflictingAnnotationsReturnException.class,
                        template("Conflicting @ResultSerializer-annotated annotations on method for return type.")
                ),
                Map.entry(
                        ConflictingAnnotationsThrowsException.class,
                        template("Conflicting @ResultSerializer-annotated annotations on method for exception $X$.")
                ),
                Map.entry(ConflictingMappingOnParameterException.class, template("Conflicting mapping on parameter.")),
                Map.entry(ConflictingMappingOnReturnTypeException.class, template("Conflicting mapping on return type.")),
                Map.entry(DontKnowHowToHandleAnnotationException.class, template("Don't know how to handle @$A$.")),
                Map.entry(EmptyDateFormatException.class, template("Empty date format at @$A$ annotation.")),
                Map.entry(ExceptionMappingOnReturnException.class, template("Can't be used for normal returns. Only for exceptions.")),
                Map.entry(
                        ImplicitWithJsVarException.class,
                        template("The @$A$ annotation shouldn't have jsVar not empty and be implicit.")
                ),
                Map.entry(InvalidDateFormatException.class, template("Invalid date format \"$F$\" at @$A$ annotation.")),
                Map.entry(MissingPathException.class, template("Missing mandatory @Path annotation.")),
                Map.entry(
                        MultipleHttpMethodAnnotationsException.class,
                        template("Multiple @HttpMethod-annotated annotations on method.")
                ),
                Map.entry(NoHttpMethodAnnotationsException.class, template("No @HttpMethod-annotated annotations on method.")),
                Map.entry(NoMappingOnParameterException.class, template("No mapping on parameter.")),
                Map.entry(NoMappingOnReturnTypeException.class, template("No mapping on return type.")),
                Map.entry(RemapperConstructorException.class, template("The remapper constructor of class $R$ throwed an exception.")),
                Map.entry(
                        ParameterTypeRestrictionViolationException.class,
                        template("The @$A$ annotation must be used only on $V$ parameters. The found type was $T$.")
                ),
                Map.entry(
                        ReturnTypeRestrictionViolationException.class,
                        template("The @$A$ annotation must be used only on $V$ return methods. The found type was $T$.")
                ),
                Map.entry(UninstantiableRemapperException.class, template("The exception remapper $R$ is not an instantiable class.")),
                Map.entry(
                        UnmatcheableParameterException.class,
                        template("The parameter value do not matches anything in method's @Path value.")
                ),
                Map.entry(
                        VoidWithValueReturnTypeException.class,
                        template("Methods returning void should not feature @$A$-annotated annotations.")
                ),
                Map.entry(AlreadyExistsException.class, template("[409] The object $TYPE$ with the key $KEY$ already exists.")),
                Map.entry(BadRequestException.class, template("[400] The request is malformed. The reason is \"$CAUSE$\".")),
                Map.entry(EntityDeletedException.class, template("[410] The object $TYPE$ with the key $KEY$ was deleted.")),
                Map.entry(HttpException.class, template("An HTTP exception [$STATUS$] happened. The cause is \"$CAUSE$\".")),
                Map.entry(NotAllowedException.class, template("[403] You don't have permission to execute this operation.")),
                Map.entry(NotFoundException.class, template("[404] The object $TYPE$ with the key $KEY$ was not found.")),
                Map.entry(UnexpectedHttpException.class, template("[500] An unexpected error happened. Cause is \"$CAUSE$\".")),
                Map.entry(
                        UnprocessableRequestException.class,
                        template("[422] The request can't be processed. The reason is \"$CAUSE$\".")
                ),
                Map.entry(
                        IncompatibleParameterProcessorException.class,
                        template("The parameter processor $R$ assigned for annotation @$A$ do not understands it.")
                ),
                Map.entry(
                        ParameterProcessorConstructorException.class,
                        template("The parameter processor constructor of class $R$ for annotation @$A$ throwed an exception.")
                ),
                Map.entry(
                        UninstantiableParameterProcessorException.class,
                        template("The parameter processor $R$ for annotation @$A$ is not an instantiable class.")
                ),
                Map.entry(AbsentRequiredParameterException.class, template("The required parameter value was absent.")),
                Map.entry(MalformedJsonBodyException.class, template("The body request data failed to be parseable as JSON.")),
                Map.entry(
                        MalformedParameterValueException.class,
                        template("The value \"$V$\" is invalid for a @$A$-annotated parameter.")
                ),
                Map.entry(
                        BadExitDiscriminatorMethodException.class,
                        template("The annotation @$A$ have an ill-formed @ExitDiscriminator method.")
                ),
                Map.entry(
                        IncompatibleReturnProcessorException.class,
                        template("The return processor $R$ assigned for annotation @$A$ do not understands it.")
                ),
                Map.entry(
                        MultipleReturnProcessorsException.class,
                        template("The annotation @$A$ should not have more than one @ExitDiscriminator method.")
                ),
                Map.entry(
                        ReturnProcessorConstructorException.class,
                        template("The return processor constructor of class $R$ for annotation @$A$ throwed an exception.")
                ),
                Map.entry(
                        ReturnProcessorNotFoundException.class,
                        template("The annotation @$A$ do not have any @ExitDiscriminator method.")
                ),
                Map.entry(
                        UninstantiableReturnProcessorException.class,
                        template("The return processor $R$ for annotation @$A$ is not an instantiable class.")
                ),
                Map.entry(MalformedJsonReturnValueException.class, template("The returned value couldn't be converted to JSON.")),
                Map.entry(StackOverflowError.class, template("OPS, the stack blew up. Details: \"$MESSAGE$\".")),
                Map.entry(OutOfMemoryError.class, template("OPS, the memory blew up. Details: \"$MESSAGE$\".")),
                Map.entry(
                        Throwable.class,
                        template("OPS, something fishy happened. Exception: \"$CLASS$\". Details: \"$MESSAGE$\".")
                )
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

        private Inner() {}
    }
}
