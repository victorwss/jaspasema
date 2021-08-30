package ninja.javahacker.jaspasema.exceptions;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.Value;
import ninja.javahacker.jaspasema.exceptions.messages.ExceptionTemplate;

/**
 * Superclass of all checked exceptions on Jaspasema, which are mostly reflection-based.
 * @author Victor Williams Stafusa da Silva
 */
public abstract class JaspasemaException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * The method parameter that is related to this exception.
     * -- GETTER --
     * Gives the method parameter that is related to this exception.
     * @return The method parameter that is related to this exception.
     */
    @Getter
    @NonNull
    private final Optional<Parameter> parameter;

    /**
     * The method that is related to this exception.
     * -- GETTER --
     * Gives the method that is related to this exception.
     * @return The method that is related to this exception.
     */
    @Getter
    @NonNull
    private final Optional<Method> method;

    /**
     * The class that is related to this exception.
     * -- GETTER --
     * Gives the class that is related to this exception.
     * @return The class that is related to this exception.
     */
    @Getter
    @NonNull
    private final Class<?> declaringClass;

    @Nullable
    private transient String cachedMessage;

    @Value
    private static class Init {
        @NonNull private final Optional<Parameter> parameter;
        @NonNull private final Optional<Method> method;
        @NonNull private final Class<?> declaringClass;
        @NonNull private final Optional<Throwable> cause;

        public Init cause(@NonNull Throwable t) {
            return new Init(parameter, method, declaringClass, Optional.of(t));
        }
    }

    private static Init of(@NonNull Parameter p) {
        return new Init(
                Optional.of(p),
                Optional.of((Method) p.getDeclaringExecutable()),
                p.getDeclaringExecutable().getDeclaringClass(),
                Optional.empty());
    }

    private static Init of(@NonNull Method m) {
        return new Init(Optional.empty(), Optional.of(m), m.getDeclaringClass(), Optional.empty());
    }

    private static Init of(@NonNull Class<?> c) {
        return new Init(Optional.empty(), Optional.empty(), c, Optional.empty());
    }

    private JaspasemaException(/*@NonNull*/ Init init) {
        super(init.getCause().orElse(null));
        this.parameter = init.getParameter();
        this.method = init.getMethod();
        this.declaringClass = init.getDeclaringClass();
    }

    /**
     * Constructs an instance specifiying a method parameter as the cause of this exception.
     * @param parameter The method parameter that is related to this exception.
     * @throws IllegalArgumentException If {@code parameter} is {@code null}.
     */
    protected JaspasemaException(@NonNull Parameter parameter) {
        this(of(parameter));
    }

    /**
     * Constructs an instance specifiying both a method parameter and another exception as the cause of this exception.
     * @param parameter The method parameter that is related to this exception.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If any of {@code parameter} or {@code cause} are {@code null}.
     */
    protected JaspasemaException(@NonNull Parameter parameter, @NonNull Throwable cause) {
        this(of(parameter).cause(cause));
    }

    /**
     * Constructs an instance specifiying a method as the cause of this exception.
     * @param method The method that is related to this exception.
     * @throws IllegalArgumentException If {@code method} is {@code null}.
     */
    protected JaspasemaException(@NonNull Method method) {
        this(of(method));
    }

    /**
     * Constructs an instance specifiying both a method and another exception as the cause of this exception.
     * @param method The method that is related to this exception.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If any of {@code method} or {@code cause} are {@code null}.
     */
    protected JaspasemaException(@NonNull Method method, @NonNull Throwable cause) {
        this(of(method).cause(cause));
    }

    /**
     * Constructs an instance specifiying a class as the cause of this exception.
     * @param declaringClass The class that is related to this exception.
     * @throws IllegalArgumentException If {@code declaringClass} is {@code null}.
     */
    protected JaspasemaException(@NonNull Class<?> declaringClass) {
        this(of(declaringClass));
    }

    /**
     * Constructs an instance specifiying both a class and another exception as the cause of this exception.
     * @param declaringClass The class that is related to this exception.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If any of {@code declaringClass} or {@code cause} are {@code null}.
     */
    protected JaspasemaException(@NonNull Class<?> declaringClass, @NonNull Throwable cause) {
        this(of(declaringClass).cause(cause));
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    @Synchronized
    public String getMessage() {
        if (cachedMessage == null) cachedMessage = formatMessage();
        return cachedMessage;
    }

    private String formatMessage() {
        var prefix = parameter
                .map(p -> p.getDeclaringExecutable() + "|" + p.toString())
                .or(() -> method.map(Method::toString))
                .orElseGet(declaringClass::getName);

        var template = ExceptionTemplate.getExceptionTemplate().templateFor(getClass());
        for (Class<?> k = getClass(); k != Object.class; k = k.getSuperclass()) {
            for (var m : this.getClass().getMethods()) {
                var t = m.getAnnotation(TemplateField.class);
                if (t == null) continue;
                String replacement;
                try {
                    replacement = (String) m.invoke(this);
                } catch (Throwable e) {
                    replacement = "<ERROR>";
                }
                template = template.replace("$" + t.value() + "$", replacement);
            }
        }
        return "[" + prefix + "] " + template;
    }

    /**
     * Used to annotate methods in the exception that gives the value for some variable present in its template message.
     * Most exceptions have a fixed template message. However, it might contains a few variable parts.
     * <p>For example, a message like {@code "The method foo received the bad parameter bar."} might be used based on a template
     * message {@code "The method $M$ received the bad parameter $P$."}, where {@code "$M$"} and {@code "$P$"} are variables
     * which would be filled by calling some getters on the exception. The getters would be those respectively annotated
     * with {@code TemplateField("M")} and {@code TemplateField("P")}.</p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface TemplateField {
        /**
         * The name of the variable in the template message to which the annotated methods binds to.
         * @return The name of the variable in the template message to which the annotated methods binds to.
         */
        public String value();
    }

    /**
     * Do not call this method.
     * This was overriden from the {@link Throwable} class in order to be disallowed.
     * @param cause The cause exception used to set as a cause for this exception. However, since this method is not
     *     supported and should not be used, this parameter is not used.
     * @return Never returns normally.
     * @deprecated Do not call this method.
     * @throws UnsupportedOperationException Always.
     */
    @Override
    @Deprecated
    public final JaspasemaException initCause(Throwable cause) {
        throw new UnsupportedOperationException();
    }
}