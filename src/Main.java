import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileName = "src/data/data.csv";
        List<Double> x_knownList = new ArrayList<>();
        List<Double> y_knownList = new ArrayList<>();
        List<Double> z_knownList = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                x_knownList.add(Double.parseDouble(parts[0]));
                y_knownList.add(Double.parseDouble(parts[1]));
                z_knownList.add(Double.parseDouble(parts[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[] x_known = x_knownList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] y_known = y_knownList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] z_known = z_knownList.stream().mapToDouble(Double::doubleValue).toArray();

        double[] x_unknown = {1.5, 2.5, 3.5};
        double[] y_unknown = {1.5, 2.5, 3.5};


        double[] z_interpolated = SpatialInterpolation.inverseDistanceWeighting(x_known, y_known, z_known, x_unknown, y_unknown, 2.0);

        for (double val : z_interpolated) {
            System.out.println(val);
        }
    }
}