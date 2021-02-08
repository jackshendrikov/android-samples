package ua.kpi.comsys.io8227.jackshen.coordinate;

public interface GeoCoordinate {
   int getDegrees();
   int getMinutes();
   double getSeconds();
   LatLngDirection getDirection();

   double toDouble();
   double toRadians();
}
