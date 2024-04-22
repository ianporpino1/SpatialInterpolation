import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    static volatile List<Point> results = new ArrayList<>();
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        
        String fileKnownPoints = "src/data/known_points.csv";
        List<Point> known_points = new ArrayList<>();
        readPoints(fileKnownPoints, known_points, true);

        String fileUnknownPoints = "src/data/unknown_points.csv";
        List<Point> unknown_points = new ArrayList<>();
        readPoints(fileUnknownPoints, unknown_points,false);
        
        int numThreads = unknown_points.size();
        List<Thread> threads = new ArrayList<>(numThreads);

        for(Point p: unknown_points){
            Runnable r = () -> {
                Point z_interpolated = SpatialInterpolation.inverseDistanceWeighting(known_points, p, 2.0);
                
                results.add(z_interpolated);
                
            };
            var builder = Thread.ofVirtual();
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

        double  duration = (endTime - startTime) / 1e9; //129seg

        System.out.println("Tempo de execução: " + duration + " segundos");

        for (Point val : results) {
            System.out.println(val);
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

