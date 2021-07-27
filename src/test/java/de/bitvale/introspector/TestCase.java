package de.bitvale.introspector;

import de.bitvale.introspector.bean.BeanIntrospector;
import de.bitvale.introspector.bean.BeanModel;
import de.bitvale.introspector.bean.BeanProperty;
import de.bitvale.introspector.type.TypeResolver;
import de.bitvale.introspector.type.resolved.ResolvedMethod;
import de.bitvale.introspector.type.resolved.ResolvedType;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCase {

    @Test
    public void testAnnotations() {
        BeanModel<Person> model = BeanIntrospector.create(Person.class);

        BeanProperty<Person, String> firstName = model.get("firstName", String.class);

        NotNull notNull = firstName.getAnnotation(NotNull.class);

        assertNotNull(notNull);

        Min min = firstName.getAnnotation(Min.class);

        assertNotNull(min);
    }

    @Test
    public void testInvocation() {

        Person person = new Person();

        BeanModel<Person> model = BeanIntrospector.create(Person.class);

        BeanProperty<Person, String> firstName = model.get("firstName", String.class);

        firstName.accept(person, "Mustermann");

        assertEquals(person.getFirstName(), "Mustermann");

    }

    @Test
    public void testFindMethod() {

        ResolvedType<Person> resolvedType = TypeResolver.resolve(Person.class);

        ResolvedMethod<?> getFirstName = resolvedType.find("getFirstName");

        assertNotNull(getFirstName);

        ResolvedMethod<?> setFirstName = resolvedType.find("setFirstName", String.class);

        assertNotNull(setFirstName);

    }
}
