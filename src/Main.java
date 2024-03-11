import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileKnownPoints = "src/data/known_points.csv";
        List<Double> x_known = new ArrayList<>();
        List<Double> y_known = new ArrayList<>();
        List<Double> z_known = new ArrayList<>();
        //long startTime = System.nanoTime();

        try (BufferedReader br = new BufferedReader(new FileReader(fileKnownPoints))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                x_known.add(Double.parseDouble(parts[0]));
                y_known.add(Double.parseDouble(parts[1]));
                z_known.add(Double.parseDouble(parts[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileUnknownPoints = "src/data/unknown_points.csv";
        List<Double> x_unknown = new ArrayList<>();
        List<Double> y_unknown = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileUnknownPoints))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                x_unknown.add(Double.parseDouble(parts[0]));
                y_unknown.add(Double.parseDouble(parts[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        long startTime = System.nanoTime(); //conta somente o tempo do algoritmo

        List<Double> z_interpolated = SpatialInterpolation.inverseDistanceWeighting(x_known, y_known, z_known, x_unknown, y_unknown, 2.0);

        long endTime = System.nanoTime();

        double  duration = (endTime - startTime) / 1e9; //com 1000 pontos desconhecidos e 40 milhoes de pontos conhecidos, 7min

        System.out.println("Tempo de execução: " + duration + " segundos");

       for (double val : z_interpolated) {
           System.out.println(val);
       }
    }
}