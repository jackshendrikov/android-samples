package ua.kpi.comsys.io8227.jackshen.coordinateJS.coordinate;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import ua.kpi.comsys.io8227.jackshen.coordinateJS.Latitude;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.Longitude;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.exception.GeoCoordException;

/**
 * This class is used as an internal implementation logic between Latitude and Longitude.
 */
public abstract class AbsGeoCoordinate implements GeoCoordinate {

   private int degrees;
   private int minutes;
   private double seconds;

   private int maxValueDegrees;

   private static final int MAX_VALUE_MINUTES = 59;
   private static final double MAX_VALUE_SECONDS = 59.9999999999999d;

   private static final int MAX_FRACTION_DIGITS = 10;


   /**
    * @param degrees degrees
    * @param minutes minutes
    * @param seconds seconds
    *
    * @throws GeoCoordException if you extend this class yourself
    */
   public AbsGeoCoordinate(final int degrees, final int minutes, final double seconds) {
      if (!(this instanceof Latitude) && !(this instanceof Longitude)) {
         throw new GeoCoordException(GeoCoordException.Messages.DISALLOWED_EXTENDS);
      }

      setMaxValueDegrees(this instanceof Latitude ? Latitude.MAX_VALUE : Longitude.MAX_VALUE);

      setDegrees(degrees);
      setMinutes(minutes);
      setSeconds(seconds);
   }

   /**
    * @param value - double value
    *
    * @throws GeoCoordException if you extend this class yourself
    */
   public AbsGeoCoordinate(final double value) {
      if (!(this instanceof Latitude) && !(this instanceof Longitude)) {
         throw new GeoCoordException( GeoCoordException.Messages.DISALLOWED_EXTENDS );
      }

      setMaxValueDegrees( this instanceof Latitude ? Latitude.MAX_VALUE : Longitude.MAX_VALUE );

      if (value < -getMaxValueDegrees() || value > getMaxValueDegrees()) {
         throw new GeoCoordException(this instanceof Latitude
                 ? GeoCoordException.Messages.LATITUDE_RANGE_DECIMAL
                 : GeoCoordException.Messages.LONGITUDE_RANGE_DECIMAL);
      }

      setDegrees((int) Math.abs(value));
      setMinutes((int) ((Math.abs(value) - (int)Math.abs(value)) * 60.0d));
      setSeconds((((Math.abs(value) - (int)Math.abs(value)) * 60.0d) % 1.0d) * 60.0d);
   }

   @Override
   public int getDegrees() {
      return degrees;
   }

   @Override
   public int getMinutes() {
      return minutes;
   }

   @Override
   public double getSeconds() {
      return seconds;
   }

   @Override
   public double toRadians() {
      return Math.toRadians(toDouble());
   }

   /**
    * Compares this object to an AbsGeoCoordinate object. All fields are compared.
    *
    * @param compareTo - The object to compare to
    *
    * @return True if equivalent, False if not
    */
   @Override
   public boolean equals(final Object compareTo) {
      final AbsGeoCoordinate other;

      if (this == compareTo) return true;

      if (!(compareTo instanceof AbsGeoCoordinate)) return false;

      other = (AbsGeoCoordinate) compareTo;

      if (getDegrees() != other.getDegrees()) return false;

      if (getMaxValueDegrees() != other.getMaxValueDegrees()) return false;

      if (getMinutes() != other.getMinutes()) return false;

      if (getSeconds() != other.getSeconds()) return false;

      return true;
   }


   /**
    * Returns a degrees-minutes-seconds formatted string for the default locale.
    * For example,
    *        In the US:  48°51'52.97"N
    *        In France:  48°51'52,97"N
    *
    * @throws GeoCoordException if getDirection() returs null
    */
   @Override
   public String toString() {
      return toString(Locale.getDefault());
   }


   /**
    * Returns a degrees-minutes-seconds formatted string for the specified locale.
    * For example,
    *        For {@linkplain Locale#US}:       48°51'52.97"N
    *        For {@linkplain Locale#FRANCE}:   48°51'52,97"N
    *
    * @param locale - The locale to localize to
    *
    * @throws GeoCoordException If locale is null or getDirection() returns null
    *
    * @return String representation of this object
    */
   public String toString( final Locale locale ) {
      final DecimalFormat myFmt;
      final LatLngDirection direction = getDirection();

      String str;

      if (locale == null) throw new GeoCoordException(GeoCoordException.Messages.LOCALE_NULL);

      if (direction == null) {
         final IllegalStateException ise = new IllegalStateException(GeoCoordException.Messages.DIRECTION_NULL);
         throw new GeoCoordException(ise.getMessage(), ise);
      }

      myFmt = new DecimalFormat("0", DecimalFormatSymbols.getInstance(locale));
      myFmt.setMaximumFractionDigits(MAX_FRACTION_DIGITS);

      str = String.format(locale,"%d°%d'%s\"%s", getDegrees(), getMinutes(),
              myFmt.format(getSeconds()), direction.getAbbreviation());

      return str;
   }

   private void setMaxValueDegrees(final int max) {
      maxValueDegrees = max;
   }

   private int getMaxValueDegrees() {
      return maxValueDegrees;
   }

   private void setDegrees(final int degrees) {
      if (degrees < 0 || degrees > getMaxValueDegrees()) {
         throw new GeoCoordException(this.getClass().getSimpleName() + GeoCoordException.Messages.DEGREES_RANGE + getMaxValueDegrees());
      }

      if (degrees == getMaxValueDegrees() && (getMinutes() != 0 || getSeconds() != 0)) {
         throw new GeoCoordException(this.getClass().getSimpleName() + GeoCoordException.Messages.MINUTES_AND_SECONDS_MUST_BE_ZERO + getMaxValueDegrees());
      }

      this.degrees = degrees;
   }

   private void setMinutes(final int mins) {
      if (mins < 0 || mins > MAX_VALUE_MINUTES) {
         throw new GeoCoordException( this.getClass().getSimpleName() + GeoCoordException.Messages.MINUTES_RANGE );
      }

      if (getDegrees() == getMaxValueDegrees() && mins != 0) {
         throw new GeoCoordException(this.getClass().getSimpleName() + GeoCoordException.Messages.MINUTES_AND_SECONDS_MUST_BE_ZERO + getMaxValueDegrees());
      }

      this.minutes = mins;
   }

   private void setSeconds(final double seconds) {
      if (seconds < 0.0d || seconds > MAX_VALUE_SECONDS) {
         throw new GeoCoordException( this.getClass().getSimpleName() + GeoCoordException.Messages.SECONDS_RANGE );
      }

      if (getDegrees() == getMaxValueDegrees() && seconds != 0.0d) {
         throw new GeoCoordException( this.getClass().getSimpleName() + GeoCoordException.Messages.MINUTES_AND_SECONDS_MUST_BE_ZERO + getMaxValueDegrees());
      }

      this.seconds = seconds;
   }
}
