package com.library.entity.interfaces;

import com.library.enums.BookStatus;

public interface Loanable {
    void setStatus(BookStatus status);
    BookStatus getStatus();
    double getPrice();
}