package ua.kpi.comsys.io8227.jackshen;

import ua.kpi.comsys.io8227.jackshen.exception.GeoCoordException;
import ua.kpi.comsys.io8227.jackshen.coordinate.AbsGeoCoordinate;
import ua.kpi.comsys.io8227.jackshen.coordinate.LatLngDirection;

/**
 * Latitude indicates whether a location is North or South of the Equator (located at latitude 0).
 */
public class Latitude extends AbsGeoCoordinate {

   public enum Direction implements LatLngDirection {

      /**
       * Indicates that the location is North of the Equator
       */
      NORTH("N"),

      /**
       * Indicates that the location is South of the Equator
       */
      SOUTH("S"),

      /**
       * Indicates that the location is on the Equator
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
    * When expressed as a float number, valid latitude values fit in a range of +/- 90.0.
    */
   public static final int MAX_VALUE = 90;


   /**
    * Creates a new Latitude object
    *
    * @param latitude - signed value. Positive values are North; negative values are South.
    *
    * @throws GeoCoordException if value outside of range.
    */
   public Latitude(final double latitude) {
      super(latitude);

      if (latitude == 0.0d) {
         setDirection(Direction.NEITHER);
      } else {
         setDirection(latitude > 0.0d ? Direction.NORTH : Direction.SOUTH);
      }
   }


   /**
    * Creates a new Latitude object
    *
    * @param degrees   - Accepted range [0-90]
    * @param minutes   - Accepted range [0-59] unless degrees is 90, in which case minutes must be 0
    * @param seconds   - Accepted range [0-59.9999999999999] unless degrees is 90, in which case seconds must be 0
    * @param direction - A Latitude.Direction object
    *
    * @throws GeoCoordException if any of values outside of range.
    */
   public Latitude(final int degrees, final int minutes, final double seconds, final Direction direction) {
      super(degrees, minutes, seconds);

      setDirection(direction);
   }


   private void setDirection(final Direction direction) {
      if (direction == null) {
         throw new GeoCoordException(GeoCoordException.Messages.DIRECTION_NULL);
      }

      if (direction == Direction.NEITHER && !(getDegrees() == 0 && getMinutes() == 0 && getSeconds() == 0.0d)) {
         throw new GeoCoordException(GeoCoordException.Messages.DIRECTION_INVALID);
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

      return getDirection() == Direction.NORTH ? decimal : -decimal;
   }


   /**
    * Compares this object to an Latitude object. All Latitude fields are compared.
    *
    * @param compareTo - The object to compare to
    *
    * @return True if equal, False if not
    */
   @Override
   public boolean equals(final Object compareTo) {
      final Latitude other;

      if (this == compareTo) return true;
      if (compareTo == null) return false;

      if (!(compareTo instanceof Latitude)) return false;

      other = (Latitude) compareTo;

      if (getDirection() == null && other.getDirection() != null) return false;
      if (getDirection() != null && other.getDirection() == null) return false;
      if (!getDirection().equals(other.getDirection())) return false;

      return super.equals(other);
   }
}
