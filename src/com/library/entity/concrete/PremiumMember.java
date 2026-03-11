package com.library.entity.concrete;

import com.library.entity.abstracts.AbstractMember;
import com.library.enums.MemberType;

public class PremiumMember extends AbstractMember {
    public PremiumMember(String name, String surname) {
        // long id parametresi kaldırıldı
        super(name, surname, MemberType.PREMIUM);
    }
}