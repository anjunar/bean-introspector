# Bean Introspector

This Bean Introspector introspects every Bean class for Properties. The right order of Properties as in the source code is implemented. Support for Generics is implemented too. The Bean Model is OOP like. You will find methods witch are implemented in the Superclass in the Type Hierarchy of every Resolved Method.

For accessing normal Classes the TypeResolver is present. The TypeResolver is also capable of Generics like the Bean Introspector.

```java
public abstract class Identity {

    @NotNull
    private UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public abstract String getFirstName();

    public abstract void setFirstName(@NotNull String firstName);

    public abstract String getLastName();

    public abstract void setLastName(@NotNull String lastName);

    public abstract LocalDate getBirthdate();

    public abstract void setBirthdate(@NotNull LocalDate birthdate);
}
```

```java
public class Person extends Identity {

    @Min(3)
    private String firstName;

    @Min(3)
    private String lastName;

    private LocalDate birthdate;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public LocalDate getBirthdate() {
        return birthdate;
    }

    @Override
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

}
```

```java
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
```