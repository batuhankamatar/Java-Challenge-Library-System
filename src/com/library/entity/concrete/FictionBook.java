package com.library.entity.concrete;

import com.library.entity.abstracts.AbstractBaseBook;
import com.library.enums.BookGenre;
import com.library.enums.BookStatus;

public class FictionBook extends AbstractBaseBook {
    public FictionBook(String title, String author, double price) {
        // long id parametresi kaldırıldı
        super(title, author, price, BookStatus.AVAILABLE, BookGenre.FICTION);
    }
}

// Entry sırasında kitabın türünü dışardan almaya gerek yok. FictionBook AcademicBook olamaz.