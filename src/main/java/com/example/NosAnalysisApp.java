package com.example;
import com.example.analysis.BigramAnalyzer;
import com.example.analysis.FrequencyReporter;
import com.example.analysis.WordCountAnalyzer;
import com.example.config.AppConfig;
import com.example.loader.WikisourceNosLoader;
import com.example.text.TextCleaner;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.List;

public class NosAnalysisApp {

    private static void banner(String s) {
        System.out.println();
        System.out.println("====================================================================");
        System.out.println("  " + s);
        System.out.println("====================================================================");
    }

    public static void main(String[] args) throws Exception {
        banner("ЗАГРУЗКА ТЕКСТА: Н.В. Гоголь — «Нос» (Wikisource)");
        List<String> rawLines = WikisourceNosLoader.loadLines();
        System.out.println("Загружено абзацев: " + rawLines.size());
        System.out.println("Первая строка: " + rawLines.get(0).substring(0,
                Math.min(120, rawLines.get(0).length())) + "...");
        SparkConf conf = new SparkConf()
                .setAppName(AppConfig.APP_NAME)
                .setMaster(AppConfig.SPARK_MASTER)
                .set("spark.ui.enabled", "false");
        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            JavaRDD<String> lines = sc.parallelize(rawLines, Math.max(2, rawLines.size() / 5));
            TextCleaner cleaner = new TextCleaner();

            // ---------- ПУНКТ 1 ----------
            banner("ПУНКТ 1. Очистка текста");
            System.out.println("Нижний регистр, удаление пунктуации и цифр,");
            System.out.println("фильтрация стоп-слов (stopwords-ru.txt), отсев слов короче "
                    + AppConfig.MIN_WORD_LENGTH + " символов.");
            System.out.println("\nПример очистки первых 3 абзацев:");
            rawLines.stream().limit(3).forEach(l -> {
                String[] words = cleaner.cleanAndTokenize(l);
                System.out.println("  Исходный: " + l.substring(0, Math.min(90, l.length())) + "...");
                System.out.println("  Очищено: " + String.join(", ", words));
            });

            // ---------- ПУНКТ 2 ----------
            banner("ПУНКТ 2. WordCount (частота слов) на Spark RDD");
            WordCountAnalyzer plain = new WordCountAnalyzer(cleaner, false);
            JavaPairRDD<String, Integer> wordCounts = plain.countWords(lines);
            long totalWords = wordCounts.map(t -> (long) t._2()).reduce(Long::sum);
            System.out.println("Уникальных слов: " + wordCounts.count());
            System.out.println("Всего слов: " + totalWords);

            // ---------- ПУНКТ 4 (выводим до биграмм — логично) ----------
            banner("ПУНКТ 4. Топ-50 и антитоп-50 слов (без стемминга)");
            FrequencyReporter.printTop(wordCounts, AppConfig.TOP_WORDS_LIMIT,
                    "Топ-50 наиболее частых слов");
            FrequencyReporter.printBottom(wordCounts, AppConfig.TOP_WORDS_LIMIT,
                    "Топ-50 наиболее редких слов");

            // ---------- ПУНКТ 3 ----------
            banner("ПУНКТ 3. Биграммы внутри строк (последовательность двух соседних слов)");
            BigramAnalyzer ba = new BigramAnalyzer(cleaner);
            JavaPairRDD<String, Integer> bigrams = ba.countBigrams(lines);
            System.out.println("Уникальных биграмм: " + bigrams.count());

            // ---------- ПУНКТ 5 ----------
            banner("ПУНКТ 5. Топ-20 биграмм");
            FrequencyReporter.printTop(bigrams, AppConfig.TOP_BIGRAMS_LIMIT,
                    "Топ-20 наиболее частых биграмм");

            // ---------- ПУНКТ 6 ----------
            banner("ПУНКТ 6. Стемминг (приведение слов к основе)");
            System.out.println("Используется собственный стеммер (алгоритм Портера для русского,");
            System.out.println("отсечение падежных, числовых и глагольных окончаний).");
            WordCountAnalyzer stemmed = new WordCountAnalyzer(cleaner, true);
            JavaPairRDD<String, Integer> stemCounts = stemmed.countWords(lines);
            System.out.println("Уникальных стемов: " + stemCounts.count());

            // ---------- ПУНКТ 7 ----------
            banner("ПУНКТ 7. Топ-50 и антитоп-50 слов после стемминга");
            FrequencyReporter.printTop(stemCounts, AppConfig.TOP_WORDS_LIMIT,
                    "Топ-50 слов после стемминга");
            FrequencyReporter.printBottom(stemCounts, AppConfig.TOP_WORDS_LIMIT,
                    "Топ-50 редких слов после стемминга");
        }

        banner("АНАЛИЗ ЗАВЕРШЁН");
    }
}