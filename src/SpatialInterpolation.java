import java.util.List;
import java.util.function.Function;

public class SpatialInterpolation {

    public static Function<Point, Point> inverseDistanceWeightingFunction(List<Point> known, double power) {
        return u -> {
            double sumWeights = 0.0;
            double sumWeightedValues = 0.0;
            for (Point k : known) {
                double distance = Math.sqrt(Math.pow(k.x() - u.x(), 2) + Math.pow(k.y() - u.y(), 2));
                double weight = 1.0 / Math.pow(distance, power);
                sumWeights += weight;
                sumWeightedValues += weight * k.z();
            }
            u.setZ(sumWeightedValues / sumWeights);
            return u;
        };
    }
}