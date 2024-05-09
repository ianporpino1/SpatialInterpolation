import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        
        long startTime = System.nanoTime();
        
        String fileKnownPoints = "src/data/known_points.csv";
        List<Point> known_points = new ArrayList<>();
        readPoints(fileKnownPoints, known_points, true);

        String fileUnknownPoints = "src/data/unknown_points.csv";
        List<Point> unknown_points = new ArrayList<>();
        readPoints(fileUnknownPoints, unknown_points,false);

        System.out.println("pontos unknown: " + unknown_points.size());

        System.out.println("pontos known: " + known_points.size());

        List<Point> results = new ArrayList<>();

        

        int numThreads = unknown_points.size();
        List<Thread> threads = new ArrayList<>(numThreads);

        for(Point p: unknown_points){
            Runnable r = () -> {
                Point z_interpolated = SpatialInterpolation.inverseDistanceWeighting(known_points, p, 2.0);

                synchronized (results){
                    results.add(z_interpolated);
                }
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

        double  duration = (endTime - startTime) / 1e9; //com 1000 pontos desconhecidos e 40 milhoes de pontos conhecidos, 117seg total

        System.out.println("Tempo de execução: " + duration + " segundos");

        for (Point val : results) {
            System.out.println(val);
        }

    }

    public static void readPoints(String filePath, List<Point> points, Boolean flag) throws IOException {
        int numThreads = (int) (Runtime.getRuntime().availableProcessors() / (1- 0.9));
        List<Thread> threads = new ArrayList<>(numThreads);
        long fileSize = Files.size(new File(filePath).toPath());
        long chunkSize = fileSize / numThreads;
        long extraChunkSize = fileSize % numThreads;

        for (int i = 0; i < numThreads; i++) {
            long start = i * chunkSize;
            long end = (i == numThreads - 1) ? fileSize : (i + 1) * chunkSize;
            Runnable r = () -> {
                List<Point> pointss = readChunks(filePath, start, end, flag);
                
                synchronized (points){
                    points.addAll(pointss);
                }
                
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
        
    }
    
    public static List<Point> readChunks(String filePath, long start, long end, boolean flag) {
        List<Point> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Ignora a primeira linha (cabeçalho)
            String line;
            long bytesRead = 0;

            while ((line = br.readLine()) != null && bytesRead <= end) {
                bytesRead += line.length() + 1; // Conta também o tamanho do caractere de nova linha

                if (bytesRead >= start && !line.isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) { // Verifica se existem pelo menos dois elementos na linha
                        Point point;
                        if (flag) {
                            point = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
                        } else {
                            point = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), null);
                        }
                        points.add(point);
                    } else {
                        System.err.println("Linha inválida: " + line);
                        // Lida com linhas inválidas, como ignorá-las ou registrar o erro
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return points;
    }


}