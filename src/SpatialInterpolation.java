import java.util.ArrayList;
import java.util.List;

public class SpatialInterpolation {

    public static List<Point> inverseDistanceWeighting(List<Point> known, List<Point> unknown, double power) {
        List<Point> interpolated_points = new ArrayList<>(unknown.size());
        for (Point u : unknown) {
            double sumWeights = 0.0;
            double sumWeightedValues = 0.0;
            for (Point k : known) {
                double distance = Math.sqrt(Math.pow(k.x() - u.x(), 2) + Math.pow(k.y() - u.y(), 2));
                double weight = 1.0 / Math.pow(distance, power);
                sumWeights += weight;
                sumWeightedValues += weight * k.z();
            }
            Point p = new Point(u.x(), u.y(), sumWeightedValues / sumWeights);
            interpolated_points.add(p);
        }
        return interpolated_points;
    }
}
