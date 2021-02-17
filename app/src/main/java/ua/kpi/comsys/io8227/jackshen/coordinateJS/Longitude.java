package ua.kpi.comsys.io8227.jackshen.coordinateJS;

import ua.kpi.comsys.io8227.jackshen.coordinateJS.exception.GeoCoordException;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.coordinate.AbsGeoCoordinate;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.coordinate.LatLngDirection;

/**
 * Longitude indicates whether a location is East or West of the prime meridian (located at longitude 0).
 */
public class Longitude extends AbsGeoCoordinate {

   public enum Direction implements LatLngDirection {

      /**
       * Indicates that the location is East of the prime meridian
       */
      EAST("E"),

      /**
       * Indicates that the location is West of the prime meridian
       */
      WEST("W"),

      /**
       * Indicates that the location is on the prime meridian
       */
      NEITHER("");

      private String abbreviation;

      Direction(final String abbr) {
         this.abbreviation = abbr;
      }

      @Override
      public String getAbbreviation() {
         return abbreviation;
      }
   }

   private Direction direction;

   /**
    * When expressed as a float number, valid longitudes values fit in a range of +/- 180.0.
    */
   public static final int MAX_VALUE = 180;


   /**
    * Creates a new Longitude object
    *
    * @param longitude - signed value. Positive values are East; negative values are West.
    *
    * @throws GeoCoordException if value outside of range.
    */
   public Longitude(final double longitude) {
      super(longitude);

      if(longitude == 0.0d) {
         setDirection( Direction.NEITHER );
      } else {
         setDirection( longitude > 0.0d ? Direction.EAST : Direction.WEST );
      }
   }


   /**
    * Creates a new Longitude object
    *
    * @param degrees   - Accepted range [0-180]
    * @param minutes   - Accepted range [0-59] unless degrees is 180, in which case minutes must be 0
    * @param seconds   - Accepted range [0-59.9999999999999] unless degrees is 180, in which case seconds must be 0
    * @param direction - A Longitude.Direction object
    *
    * @throws GeoCoordException if any of values outside of range.
    */
   public Longitude(final int degrees, final int minutes, final double seconds, final Direction direction) {
      super(degrees, minutes, seconds);

      setDirection(direction);
   }

   private void setDirection(final Direction direction) {
      if (direction == null)  throw new GeoCoordException(GeoCoordException.Messages.DIRECTION_NULL);

      if (direction == Direction.NEITHER && !(getDegrees() == 0 && getMinutes() == 0 && getSeconds() == 0.0d)) {
         throw new GeoCoordException( GeoCoordException.Messages.DIRECTION_INVALID );
      }

      this.direction = direction;
   }


   @Override
   public Direction getDirection() {
      return direction;
   }

   @Override
   public double toDouble() {
      if (getDirection() == null) {
         final IllegalStateException stateEx = new IllegalStateException(GeoCoordException.Messages.DIRECTION_NULL);
         throw new GeoCoordException(stateEx.getMessage(), stateEx);
      }

      final double decimal = getDegrees() + (getMinutes() / 60.0d) + (getSeconds() / 3600.0d);

      return getDirection() == Direction.EAST ? decimal : -decimal;
   }

   /**
    * Compares this object to an Longitude object. All Longitude fields are compared.
    *
    * @param compareTo - The object to compare to
    *
    * @return True if equal, False if not
    */
   @Override
   public boolean equals( final Object compareTo ) {
      final Longitude other;

      if (this == compareTo) return true;
      if (compareTo == null) return false;

      if (!(compareTo instanceof Longitude)) return false;

      other = (Longitude) compareTo;

      if (getDirection() == null && other.getDirection() != null) return false;
      if (getDirection() != null && other.getDirection() == null) return false;
      if (!getDirection().equals(other.getDirection())) return false;

      return super.equals(other);
   }
}
