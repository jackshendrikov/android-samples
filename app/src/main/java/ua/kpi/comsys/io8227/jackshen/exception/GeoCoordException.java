package ua.kpi.comsys.io8227.jackshen.exception;


public class GeoCoordException extends RuntimeException {

   public static final class Messages {
      public static final String DEGREES_RANGE = " degrees must be in a range of 0-",
                                 LATITUDE_RANGE_DECIMAL = "Latitude must be in a range of -90 to 90",
                                 LONGITUDE_RANGE_DECIMAL = "Longitude must be in a range of -180 to 180",
                                 MINUTES_AND_SECONDS_MUST_BE_ZERO = " minutes and seconds must be 0 when degrees is ",
                                 MINUTES_RANGE = " minutes must be in a range of 0-59",
                                 SECONDS_RANGE = " seconds must be in a range of 0-59.9[..]";

      public static final String DIRECTION_INVALID  = "Direction is invalid",
                                 DIRECTION_NULL     = "Direction is null",
                                 DISALLOWED_EXTENDS = "This class may only be extended by Latitude or Longitude",
                                 LATITUDE_NULL      = "Latitude is null",
                                 LOCALE_NULL        = "Locale is null",
                                 LONGITUDE_NULL     = "Longitude is null",
                                 NAME_NULL          = "Name is null";
   }

   public GeoCoordException() {
      super();
   }

   public GeoCoordException(final String msg ) {
      super(msg);
   }

   public GeoCoordException(final Throwable t ) {
      super(t);
   }

   public GeoCoordException(final String msg, final Throwable t) {
      super(msg, t);
   }
}
