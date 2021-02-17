package ua.kpi.comsys.io8227.jackshen.coordinateJS;

import ua.kpi.comsys.io8227.jackshen.coordinateJS.exception.GeoCoordException;

/**
 * This class is a little wrapper of Latitude and Longitude.
 */
public class CoordinateJS {
   private Latitude lat;
   private Longitude lng;

   private String name;

   /**
    * Creates a new CoordinateJS object
    *
    * @param lat - Latitude object
    * @param lng - Longitude object
    *
    * @throws GeoCoordException if either parameter is null
    */
   public CoordinateJS(final Latitude lat, final Longitude lng) {
      setLatitude(lat);
      setLongitude(lng);
   }

   /**
    * Creates a new CoordinateJS object
    *
    * @param lat - Latitude object
    * @param lng - Longitude object
    * @param name - Name for identification, like displaying a caption on a map.
    *
    * @throws GeoCoordException if any parameter is null
    */
   public CoordinateJS(final Latitude lat, final Longitude lng, final String name) {
      this(lat, lng);
      setName(name);
   }

   public Latitude getLatitude() {
      return lat;
   }

   private void setLatitude(final Latitude lat) {
      if (lat == null) {
         throw new GeoCoordException(GeoCoordException.Messages.LATITUDE_NULL);
      }

      this.lat = lat;
   }

   public Longitude getLongitude() {
      return lng;
   }

   private void setLongitude(final Longitude lng) {
      if (lng == null ) {
         throw new GeoCoordException(GeoCoordException.Messages.LONGITUDE_NULL);
      }

      this.lng = lng;
   }

   public String getName() {
      return name;
   }


   private void setName(final String name) {
      if (name == null) {
         throw new GeoCoordException( GeoCoordException.Messages.NAME_NULL );
      }

      this.name = name;
   }

   /**
    * Compares this object to an CoordinateJS.  All fields are compared.
    *
    * @param obj - The object to compare to
    *
    * @return True if equal, False if not
    */
   @Override
   public boolean equals(final Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (!(obj instanceof CoordinateJS)) return false;

      final CoordinateJS other = (CoordinateJS) obj;

      if (getLatitude() == null) {
         if (other.getLatitude() != null) {
            return false;
         }
      } else if (!getLatitude().equals(other.getLatitude())) {
         return false;
      }

      if (getLongitude() == null) {
         if (other.getLongitude() != null) {
            return false;
         }
      } else if (!getLongitude().equals(other.getLongitude())) {
         return false;
      }

      if (getName() == null) {
         return other.getName() == null;
      } else return getName().equals(other.getName());

   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder(80);

      if (getName() != null) {
         sb.append(getName());
         sb.append(" ");
      }

      sb.append("{");

      sb.append(getLatitude());
      sb.append(" , ");
      sb.append(getLongitude());

      sb.append("}");

      return sb.toString();
   }
}
