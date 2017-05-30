import java.util.List;

/**
 * Created by Robo on 04.05.2017.
 */

public class PointsDistance {


    //HAVERSINE
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        return dist;
    }

    public static double[] GetCentralGeoCoordinate(List<Node> geoCoordinates) {
        double[] p = new double[2];
        if (geoCoordinates.size() == 1)
        {

            p[0] = geoCoordinates.get(0).coords[0];
            p[1] = geoCoordinates.get(0).coords[1];
            return p;
        }

        double x = 0;
        double y = 0;
        double z = 0;

        for (Node geoCoordinate:geoCoordinates)
        {
            double latitude = geoCoordinate.coords[0] * Math.PI / 180;
            double longitude = geoCoordinate.coords[1] * Math.PI / 180;

            x += Math.cos(latitude) * Math.cos(longitude);
            y += Math.cos(latitude) * Math.sin(longitude);
            z += Math.sin(latitude);
        }

        int total = geoCoordinates.size();

        x = x / total;
        y = y / total;
        z = z / total;

        double centralLongitude = Math.atan2(y, x);
        double centralSquareRoot = Math.sqrt(x * x + y * y);
        double centralLatitude = Math.atan2(z, centralSquareRoot);

        p[0] = centralLatitude * 180 / Math.PI;
        p[1] = centralLongitude * 180 / Math.PI;

        return p;
    }
}
