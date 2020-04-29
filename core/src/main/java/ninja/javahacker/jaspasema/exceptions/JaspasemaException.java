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
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public abstract class JaspasemaException extends Exception {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Optional<Parameter> parameter;

    @NonNull
    private final Optional<Method> method;

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

    protected JaspasemaException(@NonNull Parameter parameter) {
        this(of(parameter));
    }

    protected JaspasemaException(@NonNull Parameter parameter, @NonNull Throwable cause) {
        this(of(parameter).cause(cause));
    }

    protected JaspasemaException(@NonNull Method method) {
        this(of(method));
    }

    protected JaspasemaException(@NonNull Method method, @NonNull Throwable cause) {
        this(of(method).cause(cause));
    }

    protected JaspasemaException(@NonNull Class<?> declaringClass) {
        this(of(declaringClass));
    }

    protected JaspasemaException(@NonNull Class<?> declaringClass, @NonNull Throwable cause) {
        this(of(declaringClass).cause(cause));
    }

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

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface TemplateField {
        public String value();
    }

    @Override
    public final JaspasemaException initCause(Throwable cause) {
        throw new IllegalStateException();
    }
}