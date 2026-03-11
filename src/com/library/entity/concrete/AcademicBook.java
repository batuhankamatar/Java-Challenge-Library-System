package com.library.entity.concrete;

import com.library.entity.abstracts.AbstractBaseBook;
import com.library.enums.BookGenre;
import com.library.enums.BookStatus;

public class AcademicBook extends AbstractBaseBook {
    public AcademicBook(String title, String author, double price) {
        // long id parametresi kaldırıldı, super artık ID'yi içeride üretecek.
        super(title, author, price, BookStatus.AVAILABLE, BookGenre.ACADEMIC);
    }
}