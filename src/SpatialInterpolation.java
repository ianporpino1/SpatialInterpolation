import java.util.ArrayList;
import java.util.List;

public class SpatialInterpolation {

    public static Point inverseDistanceWeighting(List<Point> known, Point unknown, double power) {
            double sumWeights = 0.0;
            double sumWeightedValues = 0.0;
            for (Point k : known) {
                double distance = Math.sqrt(Math.pow(k.x() - unknown.x(), 2) + Math.pow(k.y() - unknown.y(), 2));
                double weight = 1.0 / Math.pow(distance, power);
                sumWeights += weight;
                sumWeightedValues += weight * k.z();
            }
        return new Point(unknown.x(), unknown.y(), sumWeightedValues / sumWeights);
    }
}