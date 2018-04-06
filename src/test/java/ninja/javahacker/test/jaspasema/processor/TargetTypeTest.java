package ninja.javahacker.test.jaspasema.processor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import ninja.javahacker.jaspasema.processor.TargetType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class TargetTypeTest {

    @Test
    public void testClassGeneric1() {
        TargetType<String> s1 = new TargetType<String>() {};
        Assertions.assertEquals(String.class, s1.getGeneric());
    }

    @Test
    public void testClassGeneric2() {
        TargetType<String> s2 = TargetType.forClass(String.class);
        Assertions.assertEquals(String.class, s2.getGeneric());
    }

    @Test
    public void testClassGeneric3() {
        TargetType<?> s2 = TargetType.forType(String.class);
        Assertions.assertEquals(String.class, s2.getGeneric());
    }

    @Test
    public void testParameterizedGeneric() {
        TargetType<List<String>> s1 = new TargetType<List<String>>() {};
        Assertions.assertEquals("java.util.List<java.lang.String>", s1.getGeneric().getTypeName());
        TargetType<?> s2 = TargetType.forType(s1.getGeneric());
        Assertions.assertEquals("java.util.List<java.lang.String>", s2.getGeneric().getTypeName());
    }

    @Test
    public void testComplexParameterizedGeneric() {
        TargetType<List<? extends String>> s1 = new TargetType<List<? extends String>>() {};
        Assertions.assertEquals("java.util.List<? extends java.lang.String>", s1.getGeneric().getTypeName());
        TargetType<?> s2 = TargetType.forType(s1.getGeneric());
        Assertions.assertEquals("java.util.List<? extends java.lang.String>", s2.getGeneric().getTypeName());
    }

    @Test
    public <X> void errorWithTypeVariableGeneric() {
        try {
            new TargetType<X>() {};
            Assertions.fail("Não deveria ser instanciável sem o tipo genérico.");
        } catch (IllegalStateException expected) {
            Assertions.assertEquals("O tipo genérico deve ser instanciável.", expected.getMessage());
        }
    }

    private <X> X foo1() {
        return null;
    }

    private <X> X[] foo2() {
        return null;
    }

    private List<?> foo3() {
        return null;
    }

    @Test
    public <X> void errorWithForTypeVariableGeneric() throws NoSuchMethodException {
        try {
            TargetType.forType(TargetTypeTest.class.getDeclaredMethod("foo1").getGenericReturnType());
            Assertions.fail("Não deveria ser instanciável com variável de tipo como tipo genérico.");
        } catch (IllegalStateException expected) {
            Assertions.assertEquals("O tipo genérico deve ser instanciável.", expected.getMessage());
        }
    }

    @Test
    public <X> void errorWithGenericArray() {
        try {
            new TargetType<X[]>() {};
            Assertions.fail("Não deveria ser instanciável com array no tipo genérico.");
        } catch (IllegalStateException expected) {
            Assertions.assertEquals("O tipo genérico deve ser instanciável.", expected.getMessage());
        }
    }

    @Test
    public <X> void errorWithForTypeGenericArray() throws NoSuchMethodException {
        try {
            TargetType.forType(TargetTypeTest.class.getDeclaredMethod("foo2").getGenericReturnType());
            Assertions.fail("Não deveria ser instanciável com array genérico.");
        } catch (IllegalStateException expected) {
            Assertions.assertEquals("O tipo genérico deve ser instanciável.", expected.getMessage());
        }
    }

    @Test
    public <X> void errorWithForTypeWildcard() throws NoSuchMethodException {
        try {
            Type genericReturn = TargetTypeTest.class.getDeclaredMethod("foo3").getGenericReturnType();
            TargetType.forType(((ParameterizedType) genericReturn).getActualTypeArguments()[0]);
            Assertions.fail("Não deveria ser instanciável com coringa.");
        } catch (IllegalStateException expected) {
            Assertions.assertEquals("O tipo genérico deve ser instanciável.", expected.getMessage());
        }
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void errorWithRawType() {
        try {
            new TargetType() {};
            Assertions.fail("Não deveria ser instanciável sem o tipo genérico.");
        } catch (IllegalStateException expected) {
            Assertions.assertEquals("O tipo genérico não foi definido adequadamente.", expected.getMessage());
        }
    }

    @Test
    public void errorForClassNull() {
        try {
            TargetType.forClass(null);
            Assertions.fail("Não deveria ser instanciável com null.");
        } catch (IllegalStateException expected) {
            Assertions.assertEquals("O tipo genérico deve ser instanciável.", expected.getMessage());
        }
    }

    @Test
    public void errorForTypeNull() {
        try {
            TargetType.forType(null);
            Assertions.fail("Não deveria ser instanciável com null.");
        } catch (IllegalStateException expected) {
            Assertions.assertEquals("O tipo genérico deve ser instanciável.", expected.getMessage());
        }
    }
}
