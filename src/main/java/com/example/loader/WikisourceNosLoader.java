package com.example.loader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WikisourceNosLoader {

    // Прямые ссылки на три главы повести
    private static final String[] CHAPTER_URLS = {
            "https://ru.wikisource.org/wiki/%D0%9D%D0%BE%D1%81_(%D0%93%D0%BE%D0%B3%D0%BE%D0%BB%D1%8C)/%D0%93%D0%BB%D0%B0%D0%B2%D0%B0_I",
            "https://ru.wikisource.org/wiki/%D0%9D%D0%BE%D1%81_(%D0%93%D0%BE%D0%B3%D0%BE%D0%BB%D1%8C)/%D0%93%D0%BB%D0%B0%D0%B2%D0%B0_II",
            "https://ru.wikisource.org/wiki/%D0%9D%D0%BE%D1%81_(%D0%93%D0%BE%D0%B3%D0%BE%D0%BB%D1%8C)/%D0%93%D0%BB%D0%B0%D0%B2%D0%B0_III"
    };

    public static List<String> loadLines() throws IOException {
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < CHAPTER_URLS.length; i++) {
            System.out.println("Загрузка главы " + (i + 1) + "...");
            Document doc = Jsoup.connect(CHAPTER_URLS[i])
                    .userAgent("Mozilla/5.0")
                    .timeout(30000)
                    .maxBodySize(0)
                    .get();

            // В главах текст лежит в основном контейнере
            Element content = doc.selectFirst("div.mw-parser-output");
            if (content == null) {
                content = doc.selectFirst("div#mw-content-text");
            }

            if (content != null) {
                // На страницах глав текст разбит на абзацы <p> или блочные <div> в поэмах
                Elements paragraphs = content.select("p, div.poem > div");
                for (Element p : paragraphs) {
                    String text = p.text().trim();
                    // Отсеиваем служебный мусор и пустые строки
                    if (text.length() > 30 && !isNoise(text)) {
                        lines.add(text);
                    }
                }
            }
        }

        if (lines.isEmpty()) {
            throw new IOException("Не удалось извлечь текст (проверьте соединение с интернетом)");
        }
        return lines;
    }
    private static boolean isNoise(String text) {
        String low = text.toLowerCase();
        return low.contains("материал из викитеки")
                || low.contains("категории")
                || low.contains("эта страница входит в")
                || low.contains("публикуется в соответствии");
    }
}