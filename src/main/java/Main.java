import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {

        new Thread(() -> {

            for (int i = 0; i < 1000; i++) {

                int countR = 0;
                int countQueue = 0;
                int countQueueMax = 0;

                String s = generateRoute("RLRFR", 100);

                char[] sArray = s.toCharArray();

                for (int j = 0; j < sArray.length; j++) {

                    if (sArray[j] == 'R') {
                        countR++;
                        countQueue++;
                    } else if (sArray[j] != 'R' && countQueue > countQueueMax) {
                        countQueueMax = countQueue;
                        countQueue = 0;
                    } else if (sArray[j] != 'R') {
                        countQueue = 0;
                    }
                }

                synchronized (sizeToFreq) {

                    try {
                        sizeToFreq.notify();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    sizeToFreq.put(countR, countQueueMax);
                }
            }
        }).start();


        new Thread (() -> {

            List<Map.Entry<Integer, Integer>> sortedList;

            synchronized (sizeToFreq) {

                try {
                    sizeToFreq.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                sortedList = sizeToFreq.entrySet().stream()
                        .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()).toList();

                if (!sortedList.isEmpty()) {
                    Map.Entry<Integer, Integer> mostFrequentEntry = sortedList.get(0);
                    System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n",
                            mostFrequentEntry.getKey(), mostFrequentEntry.getValue());

                    System.out.println("Другие размеры:");
                    for (int i = 1; i < sortedList.size(); i++) {
                        Map.Entry<Integer, Integer> entry = sortedList.get(i);
                        System.out.printf("- %d (%d раз)\n", entry.getKey(), entry.getValue());
                    }
                }
            }

        }).start();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
