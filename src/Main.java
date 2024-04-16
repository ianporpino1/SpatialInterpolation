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

        readPoints(fileKnownPoints, x_known, y_known, z_known);

        String fileUnknownPoints = "src/data/unknown_points.csv";
        List<Double> x_unknown = new ArrayList<>();
        List<Double> y_unknown = new ArrayList<>();

        readPoints(fileUnknownPoints, x_unknown, y_unknown, null);


        List<Double> z_interpolated = SpatialInterpolation.inverseDistanceWeighting(x_known, y_known, z_known, x_unknown, y_unknown, 2.0);

       for (double val : z_interpolated) {
           System.out.println(val);
       }
    }
    public static void readPoints(String filePath, List<Double> xList, List<Double> yList, List<Double> zList) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                xList.add(Double.parseDouble(parts[0]));
                yList.add(Double.parseDouble(parts[1]));
                if (zList != null && parts.length > 2) {
                    zList.add(Double.parseDouble(parts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}