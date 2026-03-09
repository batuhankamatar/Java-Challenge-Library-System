package com.library.entity;

// Abstract temel sınıf.
// Kimse sadece insan değildir. O yüzden bu sınıftan nesne üretilmeyecek.

public abstract class Person {
    private final long id;
    private String name;
    private String surname;

    public Person(long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public long getId() {
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

        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id: " + id +
                ", name: " + name +
                ", surname: " + surname + "}";
    }
}
