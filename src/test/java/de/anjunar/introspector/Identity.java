package de.anjunar.introspector;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

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
