package de.bitvale.introspector;

import javax.validation.constraints.Min;
import java.time.LocalDate;

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
