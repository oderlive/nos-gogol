package com.example.analysis;
import com.example.text.TextCleaner;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BigramAnalyzer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TextCleaner cleaner;
    public BigramAnalyzer(TextCleaner cleaner) {
        this.cleaner = cleaner;
    }

    public JavaPairRDD<String, Integer> countBigrams(JavaRDD<String> lines) {
        final TextCleaner localCleaner = this.cleaner;

        JavaRDD<String> bigrams = lines.flatMap(line -> {
            String[] tokens = localCleaner.cleanAndTokenize(line);
            List<String> result = new ArrayList<>();
            for (int i = 0; i < tokens.length - 1; i++) {
                result.add(tokens[i] + " " + tokens[i + 1]);
            }
            return result.iterator();
        });

        return bigrams
                .mapToPair(bg -> new Tuple2<>(bg, 1))
                .reduceByKey(Integer::sum);
    }
}