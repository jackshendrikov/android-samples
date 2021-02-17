package ua.kpi.comsys.io8227.jackshen.coordinateJS.calculator.coordinateJS;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.Latitude;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.Longitude;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.CoordinateJS;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.calculator.DistanceCalculator;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.calculator.DistanceCalculator.Unit;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.exception.GeoCoordException;


public class DistanceCalculatorTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private CoordinateJS point1, point2;

   private double fpDelta = 1E-10;


   @Before
   public void setUp() {
      Latitude latitude1 = new Latitude(40, 42, 46, Latitude.Direction.NORTH);
      Longitude longitude1 = new Longitude(74, 0, 21, Longitude.Direction.WEST);

      Latitude latitude2 = new Latitude(38, 54, 17, Latitude.Direction.NORTH);
      Longitude longitude2 = new Longitude(77, 0, 59, Longitude.Direction.WEST);

      point1 = new CoordinateJS(latitude1, longitude1);
      point2 = new CoordinateJS(latitude2, longitude2);
   }

   @Test
   public void checkDistanceNoCoord() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage("Need to provide at least 2 coordinates");

      DistanceCalculator.distance(Unit.KILOMETERS);
   }

   @Test
   public void checkDistanceOneCoord() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage("Need to provide at least 2 coordinates");

      DistanceCalculator.distance(Unit.KILOMETERS, point1);
   }

   @Test
   public void checkDistanceNullCoord() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage("coordinates 0 is null");

      DistanceCalculator.distance( Unit.KILOMETERS, null, point2 );
   }

   @Test
   public void checkDistanceCentimeters() {
      final Latitude lat1 = new Latitude(12.34);
      final Longitude lon1 = new Longitude(56.78);

      final Latitude lat2 = new Latitude(12.349);
      final Longitude lon2 = new Longitude(56.78);

      final CoordinateJS point1 = new CoordinateJS(lat1, lon1);
      final CoordinateJS point2 = new CoordinateJS(lat2, lon2);

      final double distance = DistanceCalculator.distance(Unit.CENTIMETERS, point1, point2);
      assertEquals(100088.0003507393d, distance, fpDelta);
   }

   @Test
   public void checkDistanceMeters() {
      final Latitude lat1 = new Latitude(12.34);
      final Longitude lon1 = new Longitude(56.78);

      final Latitude lat2 = new Latitude(12.349);
      final Longitude lon2 = new Longitude(56.78);

      final CoordinateJS point1 = new CoordinateJS(lat1, lon1);
      final CoordinateJS point2 = new CoordinateJS(lat2, lon2);

      final double distance = DistanceCalculator.distance(Unit.METERS, point1, point2);
      assertEquals(1000.880003507393d, distance, fpDelta);
   }

   @Test
   public void checkDistanceKilometers() {
      final double distance = DistanceCalculator.distance(Unit.KILOMETERS, point1, point2);
      assertEquals(326.42442749621955d, distance, fpDelta);
   }


   @Test
   public void checkDistanceMiles() {
      final double distance = DistanceCalculator.distance(Unit.MILES, point1, point2);
      assertEquals(202.83073568871512d, distance, fpDelta);
   }


   @Test
   public void checkAvgCoord() {
      CoordinateJS point1, point2;

      Latitude latitude1 = new Latitude(40, 42, 46, Latitude.Direction.NORTH);
      Longitude longitude1 = new Longitude(74, 0, 21, Longitude.Direction.WEST);

      Latitude latitude2 = new Latitude(38, 54, 17, Latitude.Direction.NORTH);
      Longitude longitude2 = new Longitude(77, 0, 59, Longitude.Direction.WEST);

      point1 = new CoordinateJS(latitude1, longitude1);
      point2 = new CoordinateJS(latitude2, longitude2);

      assertEquals("{39°48'31,5\"N , 75°30'40\"W}", DistanceCalculator.avarageCoord(point1, point2).toString());
   }
}
