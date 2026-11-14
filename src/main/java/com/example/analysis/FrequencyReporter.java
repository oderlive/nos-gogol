package com.example.analysis;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;
import java.util.List;

public class FrequencyReporter {

    /**
     * Выводим топ-N наиболее частых элементов.
     * counts: RDD пар (ключ, частота).
     */
    public static void printTop(JavaPairRDD<String, Integer> counts,
                                int n, String title) {
        // Меняем местами: (count, key), сортируем по убыванию частоты,
        // затем возвращаем обратно: (key, count)
        List<Tuple2<String, Integer>> top = counts
                .mapToPair(t -> new Tuple2<>(t._2(), t._1()))     // (count, key)
                .sortByKey(false)                                // по убыванию count
                .mapToPair(t -> new Tuple2<>(t._2(), t._1()))    // обратно (key, count)
                .take(n);

        System.out.println("\n=== " + title + " (топ-" + n + ") ===");
        int rank = 1;
        for (Tuple2<String, Integer> t : top) {
            System.out.printf("%3d. %-30s %d%n", rank++, t._1(), t._2());
        }
    }

    /**
     * Выводим антитоп-N (самые редкие элементы).
     * Сортировка по возрастанию частоты; при равенстве — по ключу для стабильности.
     */
    public static void printBottom(JavaPairRDD<String, Integer> counts,
                                   int n, String title) {
        List<Tuple2<String, Integer>> bottom = counts
                .mapToPair(t -> new Tuple2<>(t._2(), t._1()))    // (count, key)
                .sortByKey(true)                                 // по возрастанию count
                .mapToPair(t -> new Tuple2<>(t._2(), t._1()))    // обратно (key, count)
                .take(n);

        System.out.println("\n=== " + title + " (антитоп-" + n + ", самые редкие) ===");
        int rank = 1;
        for (Tuple2<String, Integer> t : bottom) {
            System.out.printf("%3d. %-30s %d%n", rank++, t._1(), t._2());
        }
    }
}