import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class SpatialInterpolationTask extends RecursiveTask<List<Point>> {
    private static final int THRESHOLD = 20;
    private final List<Point> knownPoints;
    private final List<Point> unknownPoints;

    public SpatialInterpolationTask(List<Point> knownPoints, List<Point> unknownPoints) {
        this.knownPoints = knownPoints;
        this.unknownPoints = unknownPoints;
    }

    @Override
    protected List<Point> compute() {
        if (unknownPoints.size() <= THRESHOLD) {
            return computeDirectly();
        } else {
            int mid = unknownPoints.size() / 2;
            List<Point> leftUnknownPoints = unknownPoints.subList(0, mid);
            List<Point> rightUnknownPoints = unknownPoints.subList(mid, unknownPoints.size());

            SpatialInterpolationTask leftTask = new SpatialInterpolationTask(knownPoints, leftUnknownPoints);
            SpatialInterpolationTask rightTask = new SpatialInterpolationTask(knownPoints, rightUnknownPoints);

            leftTask.fork();
            List<Point> rightResult = rightTask.compute();
            List<Point> leftResult = leftTask.join();

            List<Point> combinedResults = new ArrayList<>();
            combinedResults.addAll(leftResult);
            combinedResults.addAll(rightResult);

            return combinedResults;
        }
    }
    private List<Point> computeDirectly() {
        return SpatialInterpolation.inverseDistanceWeighting(knownPoints, unknownPoints, 2.0);
    }
}
