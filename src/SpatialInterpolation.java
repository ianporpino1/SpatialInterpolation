import java.util.ArrayList;
import java.util.List;

public class SpatialInterpolation {
    
    public static List<Double> inverseDistanceWeighting(List<Point> known, Point unknown, double power) {
        List<Double> weights = new ArrayList<>();
        Double sumWeights = 0.0;
        Double sumWeightedValues = 0.0;
        for (Point k : known) {
            Double distance = Math.sqrt(Math.pow(k.x() - unknown.x(), 2) + Math.pow(k.y() - unknown.y(), 2));
            Double weight = 1.0 / Math.pow(distance, power);
            sumWeights += weight;
            sumWeightedValues += weight * k.z();
        }
        weights.add(sumWeightedValues);
        weights.add(sumWeights);
        return weights;
    }
}