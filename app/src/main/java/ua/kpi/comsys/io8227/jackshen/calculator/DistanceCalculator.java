package ua.kpi.comsys.io8227.jackshen.calculator;

import ua.kpi.comsys.io8227.jackshen.Latitude;
import ua.kpi.comsys.io8227.jackshen.Longitude;
import ua.kpi.comsys.io8227.jackshen.CoordinateJS;
import ua.kpi.comsys.io8227.jackshen.exception.GeoCoordException;

/**
 * This class calculates the distance between the coordinates using the Haversine formula, which I
 * chose because of the simplicity of implementation
 *
 * The Earth radius used in calculations is 6371.8 km.
 *
 * @see <a href="https://uk.wikipedia.org/wiki/%D0%A4%D0%BE%D1%80%D0%BC%D1%83%D0%BB%D0%B0_%D0%B3%D0%B0%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D0%BD%D1%83%D1%81%D0%B0">Haversine formula</a>
 */
class DistanceCalculator {

   public enum Unit {

      CENTIMETERS(100000.0d),

      METERS(1000),

      KILOMETERS(1),

      MILES(1.0d / 1.609344d);

      private double perKilometer;

      Unit(final double perKilometer) {
         this.perKilometer = perKilometer;
      }
   }

   private static final double EARTH_RADIUS_KILOMETERS = 6371.8;

   /**
    * Gets the total distance between an unlimited number of CoordinateJS. For example, if the
    * distance from point A to point B is 10, and the distance from point B to point C is 5, the total
    * distance will be 15.
    *
    * @param unit - unit of distance
    *
    * @param coordinates - vararg of CoordinateJS points arranged in the order in which they are visited.
    *
    * @return The total distance traveled, expressed in terms of unit
    */
   static double distance(final Unit unit, final CoordinateJS ... coordinates) {
      if (unit == null) {
         throw new GeoCoordException("Unit is null");
      }

      if (coordinates == null) {
         throw new GeoCoordException("Coordinates are null");
      }

      if (coordinates.length < 2) {
         throw new GeoCoordException("Need to provide at least 2 coordinates");
      }

      double distance = 0;
      CoordinateJS previous = coordinates[0];

      for (int i = 1; i < coordinates.length; i++) {
         final CoordinateJS current = coordinates[i];

         if (previous == null) throw new GeoCoordException("coordinates " + (i - 1) + " is null");
         if (current == null) throw new GeoCoordException("coordinates " + i + " is null");

         final Latitude latitude1 = previous.getLatitude(), latitude2 = current.getLatitude();
         final Longitude longitude1 = previous.getLongitude(), longitude2 = current.getLongitude();

         if (latitude1 == null) throw new GeoCoordException("Latitude 1 is null");
         if (latitude2 == null) throw new GeoCoordException("Latitude 2 is null");
         if (longitude1 == null) throw new GeoCoordException("Longitude 1 is null");
         if (longitude2 == null) throw new GeoCoordException("Longitude 2 is null");

         final double lat1 = latitude1.toRadians(),
                      lat2 = latitude2.toRadians(),

                      lng1 = longitude1.toRadians(),
                      lng2 = longitude2.toRadians(),

                      deltaLat = lat2 - lat1,
                      deltaLng = lng2 - lng1;

         // distance between point A and point B using the formula
         final double d = (2.0d * EARTH_RADIUS_KILOMETERS) * Math.asin(Math.sqrt(Math.pow(Math.sin(deltaLat / 2.0d), 2.0d)
                        + (Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(deltaLng / 2.0d), 2.0d))));

         distance += d * unit.perKilometer;
         previous = current;
      }

      return distance;
   }

   /**
    * Gets the average coordinate between two of CoordinateJS objects.
    *
    * @return new CoordinateJS object, string representation will be in the form like {xx°yy'zz"Z , xx°yy'zz"Z}
    */
   static CoordinateJS avarageCoord(final CoordinateJS point1, final CoordinateJS point2) {

      if (point1 == null || point2 == null) throw new GeoCoordException("Coordinates are null");

      final Latitude latitude1 = point1.getLatitude(), latitude2 = point2.getLatitude();
      final Longitude longitude1 = point1.getLongitude(), longitude2 = point2.getLongitude();

      if (latitude1 == null) throw new GeoCoordException("Latitude 1 is null");
      if (latitude2 == null) throw new GeoCoordException("Latitude 2 is null");
      if (longitude1 == null) throw new GeoCoordException("Longitude 1 is null");
      if (longitude2 == null) throw new GeoCoordException("Longitude 2 is null");

      final double lat1 = latitude1.toDouble(), lat2 = latitude2.toDouble(),
                   lng1 = longitude1.toDouble(), lng2 = longitude2.toDouble();

      return new CoordinateJS(new Latitude((lat1 + lat2) / 2), new Longitude((lng1 + lng2) / 2));
   }
}
