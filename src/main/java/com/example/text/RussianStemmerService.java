package com.example.text;
import java.io.Serializable;

public class RussianStemmerService implements Serializable {
    private static final long serialVersionUID = 1L;

    public String stem(String word) {
        if (word == null || word.length() <= 3) {
            return word;
        }

        // Регулярное выражение для отсечения самых частых окончаний
        // (падежи, числа, глаголы)
        String stemmed = word.replaceAll(
                "(ыми|ими|ого|ему|ами|ями|ов|ев|ий|ый|ая|ое|ые|ие|ую|юю|ом|ем|ах|ях|а|я|о|е|ы|и|у|ю)$", "");

        // Если после обрезки осталось слишком мало символов — возвращаем как было
        if (stemmed.length() >= 3) {
            return stemmed;
        }
        return word;
    }
}