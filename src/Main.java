import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        
        String fileKnownPoints = "src/data/known_points.csv";
        List<Point> known_points = new ArrayList<>();
        readPoints(fileKnownPoints, known_points, true);

        String fileUnknownPoints = "src/data/unknown_points.csv";
        List<Point> unknown_points = new ArrayList<>();
        readPoints(fileUnknownPoints, unknown_points,false);

        AtomicReferenceArray<Point> results = new AtomicReferenceArray<>(1000);

        

        int numThreads = unknown_points.size();
        List<Thread> threads = new ArrayList<>(numThreads);

        
        for(int i =0; i< unknown_points.size(); i++){
            int finalI = i;
            Runnable r = () -> {
                Point z_interpolated = SpatialInterpolation.inverseDistanceWeighting(known_points, unknown_points.get(finalI), 2.0);

                results.set(finalI,z_interpolated);
            };
            var builder = Thread.ofPlatform();
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

        double  duration = (endTime - startTime) / 1e9; //com 1000 pontos desconhecidos e 40 milhoes de pontos conhecidos, 116seg total

        System.out.println("Tempo de execução: " + duration + " segundos");

        for(int i=0; i< results.length(); i++){
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
