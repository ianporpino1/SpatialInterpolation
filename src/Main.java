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
        
        
        int numThreads = Runtime.getRuntime().availableProcessors(); //com 8 eh o melhor

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Callable<List<Point>>> callables = new ArrayList<>();

        int totalPoints = unknown_points.size();
        int pointsPerThread = totalPoints / numThreads;
        int extraPoints = totalPoints % numThreads;

        int startIndex = 0;
        for (int i = 0; i < numThreads; i++) {
            int endIndex = startIndex + pointsPerThread;
            if (i < extraPoints) {
                endIndex++;
            }

            final List<Point> subUnknown = unknown_points.subList(startIndex, endIndex);
            
            callables.add(() -> SpatialInterpolation.inverseDistanceWeighting(known_points, subUnknown, 2.0));
        }
        
        List<Future<List<Point>>> futures = executorService.invokeAll(callables);
        for(Future<List<Point>> future : futures) {
            results.addAll(future.get());
        }
        
        executorService.shutdown();

        long endTime = System.nanoTime();

        double  duration = (endTime - startTime) / 1e9;

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
