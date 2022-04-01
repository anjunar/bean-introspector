package de.anjunar.introspector;

import de.anjunar.introspector.bean.BeanIntrospector;
import de.anjunar.introspector.bean.BeanModel;
import de.anjunar.introspector.bean.BeanProperty;
import de.anjunar.introspector.type.TypeResolver;
import de.anjunar.introspector.type.resolved.ResolvedMethod;
import de.anjunar.introspector.type.resolved.ResolvedType;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    @Test
    public void testInterfaces() {

        ResolvedType<Person> resolvedType = TypeResolver.resolve(Person.class);

        ResolvedMethod<?> getFirstName = resolvedType.find("getFirstName");



    }
}
