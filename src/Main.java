import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        
        String fileKnownPoints = "src/data/known_points.csv";
        List<Point> known_points = new ArrayList<>();
        readPoints(fileKnownPoints, known_points, true);

        String fileUnknownPoints = "src/data/unknown_points.csv";
        List<Point> unknown_points = new ArrayList<>();
        readPoints(fileUnknownPoints, unknown_points,false);

        List<Point> results = new ArrayList<>();

        int numThreads = Runtime.getRuntime().availableProcessors();
        List<Thread> threads = new ArrayList<>(numThreads);

        int totalPoints = unknown_points.size();
        int pointsPerThread = totalPoints / numThreads;
        int extraPoints = totalPoints % numThreads;

        int startIndex = 0;
        for (int i = 0; i < numThreads; i++) {
            int endIndex = startIndex + pointsPerThread;
            if (i < extraPoints) {
                endIndex++;
            }

            List<Point> subUnknown = unknown_points.subList(startIndex, endIndex);

            Runnable r = () -> {
                List<Point> z_interpolated = SpatialInterpolation.inverseDistanceWeighting(known_points, subUnknown, 2.0);

                results.addAll(z_interpolated);

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

        double  duration = (endTime - startTime) / 1e9; //com 1000 pontos desconhecidos e 40 milhoes de pontos conhecidos, 12 threads, 129seg total

        System.out.println("Tempo de execução: " + duration + " segundos");

        int i=0;
        for (Point val : results) {
            System.out.println(i + ":" + val);
            i++;
        }

    }

    public static void readPoints(String filePath, List<Point> points, Boolean flag) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Point point;
                if(flag){
                    point = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),Double.parseDouble(parts[2]));
                }
                else {
                    point = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),null);
                }
                points.add(point);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
