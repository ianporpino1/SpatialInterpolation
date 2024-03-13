import java.util.ArrayList;
import java.util.List;

public class SpatialInterpolation {
    public static List<Double> inverseDistanceWeighting(List<Double> x_known, List<Double> y_known, List<Double> z_known, List<Double> x_unknown, List<Double> y_unknown, double power) {
        List<Double> z_unknown = new ArrayList<>(x_unknown.size());
        for (int i = 0; i < x_unknown.size(); i++) {
            double sumWeights = 0.0;
            double sumWeightedValues = 0.0;
            for (int j = 0; j < x_known.size(); j++) {
                double distance = Math.sqrt(Math.pow(x_known.get(j) - x_unknown.get(i), 2) + Math.pow(y_known.get(j) - y_unknown.get(i), 2));
                double weight = 1.0 / Math.pow(distance, power);
                sumWeights += weight;
                sumWeightedValues += weight * z_known.get(j);
            }
            z_unknown.add(sumWeightedValues / sumWeights);
        }
        return z_unknown;
    }
}
