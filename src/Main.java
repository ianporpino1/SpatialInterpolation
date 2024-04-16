import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileKnownPoints = "src/data/known_points.csv";
        List<Double> x_known = new ArrayList<>();
        List<Double> y_known = new ArrayList<>();
        List<Double> z_known = new ArrayList<>();
        //long startTime = System.nanoTime();

        readPoints(fileKnownPoints, x_known, y_known, z_known);


        String fileUnknownPoints = "src/data/unknown_points.csv";
        List<Double> x_unknown = new ArrayList<>();
        List<Double> y_unknown = new ArrayList<>();

        readPoints(fileUnknownPoints, x_unknown, y_unknown, null);

        List<Double> results = new ArrayList<>();

        long startTime = System.nanoTime();

        int numThreads = x_unknown.size();
        List<Thread> threads = new ArrayList<>(numThreads);

        for (int i = 0; i < numThreads; i++) {
            int finalI = i;
            Runnable r = () -> {
                List<Double> z_interpolated = SpatialInterpolation.inverseDistanceWeighting(x_known, y_known, z_known, Collections.singletonList(x_unknown.get(finalI)), Collections.singletonList(y_unknown.get(finalI)), 2.0);

                results.addAll(0, z_interpolated);

            };

            var builder = Thread.ofVirtual().name(String.valueOf(i));
            Thread thread = builder.start(r);
            threads.add(thread);
        }
        for (Thread thread : threads) {
            try {
                thread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.nanoTime();

        double  duration = (endTime - startTime) / 1e9; //96seg

        System.out.println("Tempo de execução: " + duration + " segundos");

        for (double val : results) {
            System.out.println(val);
        }

    }

    public static void readPoints(String filePath, List<Double> xList, List<Double> yList, List<Double> zList) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Ignora a primeira linha (se for um cabeçalho)
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                xList.add(Double.parseDouble(parts[0]));
                yList.add(Double.parseDouble(parts[1]));
                if (zList != null && parts.length > 2) {
                    zList.add(Double.parseDouble(parts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

