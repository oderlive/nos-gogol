package com.example.analysis;
import com.example.text.RussianStemmerService;
import com.example.text.TextCleaner;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;
import java.io.Serializable;
import java.util.Arrays;

public class WordCountAnalyzer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TextCleaner cleaner;
    private final boolean useStemming;
    private final RussianStemmerService stemmer;

    public WordCountAnalyzer(TextCleaner cleaner, boolean useStemming) {
        this.cleaner = cleaner;
        this.useStemming = useStemming;
        this.stemmer = useStemming ? new RussianStemmerService() : null;
    }

    public JavaPairRDD<String, Integer> countWords(JavaRDD<String> lines) {
        // Берем локальные ссылки, чтобы Spark сериализовал только их, а не весь объект
        final TextCleaner localCleaner = this.cleaner;
        final boolean doStemming = this.useStemming;
        final RussianStemmerService localStemmer = this.stemmer;
        JavaRDD<String> words = lines.flatMap(line ->
                Arrays.asList(localCleaner.cleanAndTokenize(line)).iterator());

        if (doStemming && localStemmer != null) {
            words = words.map(localStemmer::stem);
        }

        return words
                .mapToPair(w -> new Tuple2<>(w, 1))
                .reduceByKey(Integer::sum);
    }
}