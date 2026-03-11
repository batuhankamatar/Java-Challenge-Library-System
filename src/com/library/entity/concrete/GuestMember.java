package com.library.entity.concrete;

import com.library.entity.abstracts.AbstractMember;
import com.library.enums.MemberType;

public class GuestMember extends AbstractMember {
    public GuestMember(String name, String surname) {
        // long id parametresi kaldırıldı
        super(name, surname, MemberType.GUEST);
    }
}