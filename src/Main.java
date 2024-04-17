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
        List<Point> known_points = new ArrayList<>();
        readPoints(fileKnownPoints, known_points, true);

        String fileUnknownPoints = "src/data/unknown_points.csv";
        List<Point> unknown_points = new ArrayList<>();
        readPoints(fileUnknownPoints, unknown_points,false);

        List<Point> results = new ArrayList<>();

        long startTime = System.nanoTime();

        int numThreads = unknown_points.size();
        List<Thread> threads = new ArrayList<>(numThreads);

        for (int i = 0; i < numThreads; i++) {
            int finalI = i;
            Runnable r = () -> {
                List<Point> z_interpolated = SpatialInterpolation.inverseDistanceWeighting(known_points, Collections.singletonList(unknown_points.get(finalI)), 2.0);

                results.addAll(z_interpolated);
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


        int i=0;
        for (Point val : results) {
            System.out.println("Ponto " + i + " : " + val);
            i++;
        }

    }

    public static void readPoints(String filePath, List<Point> points, Boolean flag) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Ignora a primeira linha (se for um cabeçalho)
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
