
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.DoubleAccumulator;

public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();

        SpatialInterpolation spatialInterpolation = new SpatialInterpolation();
        
        String fileKnownPoints = "src/data/known_points.csv";
        List<Point> knownPoints = new ArrayList<>();
        readPoints(fileKnownPoints, knownPoints, true);

        String fileUnknownPoints = "src/data/unknown_points.csv";
        List<Point> unknownPoints = new ArrayList<>();
        readPoints(fileUnknownPoints, unknownPoints, false);

        List<Point> results = new ArrayList<>();
        
        System.out.println(unknownPoints.size());

        DoubleAccumulator w1 = new DoubleAccumulator(Double::sum, 0.0);
        DoubleAccumulator w2 = new DoubleAccumulator(Double::sum, 0.0);


        int numThreads = Runtime.getRuntime().availableProcessors();

        int totalPoints = knownPoints.size();
        int pointsPerThread = totalPoints / numThreads;
        int extraPoints = totalPoints % numThreads;


        for (Point unknownPoint : unknownPoints) {
            List<Thread> threads = new ArrayList<>(numThreads);
            int startIndex = 0;
            for (int i = 0; i < numThreads; i++) {
                int endIndex = startIndex + pointsPerThread;
                if (i < extraPoints) {
                    endIndex++;
                }

                List<Point> subKnown = knownPoints.subList(startIndex, endIndex);
                Runnable r = () -> {
                    List<Double> weights = spatialInterpolation.inverseDistanceWeighting(subKnown, unknownPoint, 2.0);

                    w1.accumulate(weights.getFirst());
                    w2.accumulate(weights.getLast());
                
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
            Point point = new Point(unknownPoint.x(), unknownPoint.y(), w1.get() / w2.get());
            results.add(point);
            
            w1.reset();
            w2.reset();
        }

        
        long endTime = System.nanoTime();

        double  duration = (endTime - startTime) / 1e9; //com 1000 pontos desconhecidos e 40 milhoes de pontos conhecidos, 116seg total

        System.out.println("Tempo de execução: " + duration + " segundos");

        //System.out.println("Ponto "+ ": " + point);

        for(int i=0; i< results.size(); i++){
            System.out.println("Ponto "+ i+ ": " + results.get(i));
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
