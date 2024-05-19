import jdk.dynalink.beans.StaticClass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long startTime = System.nanoTime();
        
        String fileKnownPoints = "src/data/known_points.csv";
        final List<Point> known_points = new ArrayList<>();
        readPoints(fileKnownPoints, known_points, true);

        String fileUnknownPoints = "src/data/unknown_points.csv";
        final List<Point> unknown_points = new ArrayList<>();
        readPoints(fileUnknownPoints, unknown_points,false);

        List<Point> results = new ArrayList<>();

        List<CompletableFuture<Point>> futures = new ArrayList<>();
        for (Point unknown : unknown_points) {
            CompletableFuture<Point> future = CompletableFuture.supplyAsync(() -> SpatialInterpolation.inverseDistanceWeighting(known_points,unknown,2.0));
            futures.add(future);
        }

        
        for (CompletableFuture<Point> future : futures) {
            results.add(future.get());
        }
        
        
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
