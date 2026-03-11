package com.library.entity.concrete;

import com.library.entity.abstracts.AbstractBaseBook;
import com.library.enums.BookGenre;
import com.library.enums.BookStatus;

public class SciFiBook extends AbstractBaseBook {
    public SciFiBook(String title, String author, double price) {
        // long id parametresi kaldırıldı
        super(title, author, price, BookStatus.AVAILABLE, BookGenre.SCIENCE_FICTION);
    }
}