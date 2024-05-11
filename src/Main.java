import jdk.dynalink.beans.StaticClass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long startTime = System.nanoTime();
        
        String fileKnownPoints = "src/data/known_points.csv";
        final List<Point> known_points = new ArrayList<>();
        readPoints(fileKnownPoints, known_points, true);

        String fileUnknownPoints = "src/data/unknown_points.csv";
        final List<Point> unknown_points = new ArrayList<>();
        readPoints(fileUnknownPoints, unknown_points,false);

        List<Point> results = new ArrayList<>();
        
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        List<Callable<Point>> callables = new ArrayList<>();
        
        for(Point p: unknown_points){
            
            callables.add(() -> SpatialInterpolation.inverseDistanceWeighting(known_points, p, 2.0));
        }
        
        List<Future<Point>> futures = executorService.invokeAll(callables);
        for(Future<Point> future : futures) {
            results.add(future.get());
        }
        
        executorService.shutdown();

        long endTime = System.nanoTime();

        double  duration = (endTime - startTime) / 1e9;

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
