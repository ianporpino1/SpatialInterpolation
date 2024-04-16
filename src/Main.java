import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
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

        int numThreads = Runtime.getRuntime().availableProcessors();
        List<Thread> threads = new ArrayList<>(numThreads);

        int totalPoints = x_unknown.size();
        int pointsPerThread = totalPoints / numThreads;
        int extraPoints = totalPoints % numThreads;

        int startIndex = 0;
        for (int i = 0; i < numThreads; i++) {
            int endIndex = startIndex + pointsPerThread;
            if (i < extraPoints) {
                endIndex++;
            }

            List<Double> subXUnknown = x_unknown.subList(startIndex, endIndex);
            List<Double> subYUnknown = y_unknown.subList(startIndex, endIndex);

            Runnable r = () -> {
                List<Double> z_interpolated = SpatialInterpolation.inverseDistanceWeighting(x_known, y_known, z_known, subXUnknown, subYUnknown, 2.0);

                results.addAll(0, z_interpolated);

            };

            var builder = Thread.ofPlatform();
            Thread thread = builder.start(r);
            threads.add(thread);

            startIndex = endIndex;
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.nanoTime();

        double  duration = (endTime - startTime) / 1e9; //com 1000 pontos desconhecidos e 40 milhoes de pontos conhecidos, 113seg

        System.out.println("Tempo de execução: " + duration + " segundos");

        for (double val : results) {
            System.out.println(val);
        }

    }

    public static void readPoints(String filePath, List<Double> xList, List<Double> yList, List<Double> zList) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
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