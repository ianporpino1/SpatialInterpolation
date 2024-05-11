
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.DoubleAccumulator;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long startTime = System.nanoTime();
        
        String fileKnownPoints = "src/data/known_points.csv";
        List<Point> knownPoints = new ArrayList<>();
        readPoints(fileKnownPoints, knownPoints, true);

        String fileUnknownPoints = "src/data/unknown_points.csv";
        List<Point> unknownPoints = new ArrayList<>();
        readPoints(fileUnknownPoints, unknownPoints, false);

        List<Point> results = new ArrayList<>();
      

        DoubleAccumulator w1 = new DoubleAccumulator(Double::sum, 0.0);
        DoubleAccumulator w2 = new DoubleAccumulator(Double::sum, 0.0);

        int numThreads = Runtime.getRuntime().availableProcessors();

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        
        int totalPoints = knownPoints.size();
        int pointsPerThread = totalPoints / numThreads;
        int extraPoints = totalPoints % numThreads;


        for (Point unknownPoint : unknownPoints) {
            int startIndex = 0;
            List<Callable<List<Double>>> callables = new ArrayList<>();
            
            for (int i = 0; i < numThreads; i++) {
                int endIndex = startIndex + pointsPerThread;
                if (i < extraPoints) {
                    endIndex++;
                }

                final List<Point> subKnown = knownPoints.subList(startIndex, endIndex);
                
                callables.add(() -> SpatialInterpolation.inverseDistanceWeighting(subKnown,unknownPoint,2.0));
                
                startIndex = endIndex;
            }
            List<Future<List<Double>>> futures = executorService.invokeAll(callables);
            for (Future<List<Double>> future : futures) {
                List<Double> weights = future.get();
                if (weights.size() == 2) {
                    w1.accumulate(weights.get(0));
                    w2.accumulate(weights.get(1));
                }
            }
            
            Point point = new Point(unknownPoint.x(), unknownPoint.y(), w1.get() / w2.get());
            results.add(point);
            
            w1.reset();
            w2.reset();
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
