package com.example.config;

public final class AppConfig {

    private AppConfig() {}

    /** Режим Spark: локальный, все доступные ядра */
    public static final String SPARK_MASTER = "local[*]";
    public static final String APP_NAME = "GogolNosSparkAnalysis";

    /** URL страницы с текстом повести «Нос» на Wikisource */
    public static final String WIKISOURCE_NOS_URL =
            "https://ru.wikisource.org/wiki/Нос_(Гоголь)";

    /** Количество выводимых слов (топ и антитоп) */
    public static final int TOP_WORDS_LIMIT = 50;
    public static final int TOP_BIGRAMS_LIMIT = 20;

    /** Путь к ресурсу со стоп-словами внутри classpath */
    public static final String STOPWORDS_RESOURCE = "stopwords-ru.txt";

    /** Минимальная длина слова (отсекаем предлоги, союзы и мусор) */
    public static final int MIN_WORD_LENGTH = 2;
}