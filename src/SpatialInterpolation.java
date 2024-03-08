public class SpatialInterpolation {

    public static double[] inverseDistanceWeighting(double[] x_known, double[] y_known, double[] z_known, double[] x_unknown, double[] y_unknown, double power) {
        double[] z_unknown = new double[x_unknown.length];
        for (int i = 0; i < x_unknown.length; i++) {
            double sumWeights = 0.0;
            double sumWeightedValues = 0.0;
            for (int j = 0; j < x_known.length; j++) {
                double distance = Math.sqrt(Math.pow(x_known[j] - x_unknown[i], 2) + Math.pow(y_known[j] - y_unknown[i], 2));
                double weight = 1.0 / Math.pow(distance, power);
                sumWeights += weight;
                sumWeightedValues += weight * z_known[j];
            }
            z_unknown[i] = sumWeightedValues / sumWeights;
        }
        return z_unknown;
    }
}