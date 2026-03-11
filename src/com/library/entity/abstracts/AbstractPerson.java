package com.library.entity.abstracts;

import com.library.entity.interfaces.Identifiable;
import com.library.entity.interfaces.Searchable;

import java.util.Objects;
import java.util.UUID;

// Abstract temel sınıf.
// Kimse sadece insan değildir. O yüzden bu sınıftan nesne üretilmeyecek.

public abstract class AbstractPerson implements Identifiable, Searchable {
    private final String id; // long -> String
    private String name;
    private String surname;

    public AbstractPerson(String name, String surname) {
        this.id = UUID.randomUUID().toString(); // ID otomatik üretiliyor
        this.name = name;
        this.surname = surname;
    }

    public String getId() { // return tipi String oldu
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public void setName(String name) {
        if(name == null || name.isBlank())
            throw new IllegalArgumentException("'name' cannot be null or blank!");

        this.name = name;
    }

    public void setSurname(String surname) {
        if(surname == null || surname.isBlank())
            throw new IllegalArgumentException("'surname' cannot be null or blank!");

        this.surname = surname;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        AbstractPerson person = (AbstractPerson) o;
        return Objects.equals(id, person.id); // String karşılaştırması için güncellendi
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id); // String hash kodu için güncellendi
    }

    @Override
    public String toString() {
        return "Person{" +
                "id: " + id +
                ", name: " + name +
                ", surname: " + surname + "}";
    }

    @Override
    public String getSearchKey() {
        return name + " " + surname;
    }
}