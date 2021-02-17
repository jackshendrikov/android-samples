package ua.kpi.comsys.io8227.jackshen.coordinateJS.calculator.coordinateJS;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ua.kpi.comsys.io8227.jackshen.coordinateJS.Latitude;
import ua.kpi.comsys.io8227.jackshen.coordinateJS.exception.GeoCoordException;


public class LatitudeTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private Latitude lat1;

   @Before
   public void setUp() {
      lat1 = new Latitude(48, 51, 52.97d, Latitude.Direction.NORTH);
   }

   @Test
   public void checkMaxValueCorrectness() {
      assertEquals(90, Latitude.MAX_VALUE);
   }

   @Test
   public void checkSetLat() {
      final int degree = 5;
      final int minute = 5;
      final double seconds = 5;
      final Latitude.Direction direction = Latitude.Direction.NORTH;

      final Latitude l = new Latitude(degree, minute, seconds, direction);

      assertEquals(degree, l.getDegrees());
      assertEquals(minute, l.getMinutes());
      assertEquals(seconds, l.getSeconds(), 0.0);
      assertEquals(direction, l.getDirection());
   }


   @Test
   public void checkSetLatNeither() {
      final int degree = 0;
      final int minute = 0;
      final double seconds = 0;
      final Latitude.Direction direction = Latitude.Direction.NEITHER;

      final Latitude l = new Latitude(degree, minute, seconds, direction);

      assertEquals(degree, l.getDegrees());
      assertEquals(minute, l.getMinutes());
      assertEquals(seconds, l.getSeconds(), 0.0);
      assertEquals(direction, l.getDirection());
   }

   @Test
   public void checkFailLatNeither() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.DIRECTION_INVALID);

      new Latitude(3, 3, 3, Latitude.Direction.NEITHER);
   }

   @Test
   public void checkLatMaxValue() {
      final Latitude l = new Latitude(90);

      assertEquals(90, l.getDegrees());
      assertEquals(0, l.getMinutes());
      assertEquals(0.0, l.getSeconds(), 0.0);
      assertEquals(Latitude.Direction.NORTH, l.getDirection());
      assertEquals(90.0, l.toDouble(), 0.0);
   }

   @Test
   public void checkFailLatMaxValue() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LATITUDE_RANGE_DECIMAL);

      new Latitude(91);
   }

   @Test
   public void checkFailMinutesSecondsMaxValue() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LATITUDE_RANGE_DECIMAL);

      new Latitude(90.000000001d);
   }

   @Test
   public void checkLatMinValue() {
      final Latitude l = new Latitude(-90);

      assertEquals(90, l.getDegrees());  // degrees are not negative - direction indicates sign
      assertEquals(0, l.getMinutes());
      assertEquals(0.0, l.getSeconds(), 0.0);
      assertEquals(Latitude.Direction.SOUTH, l.getDirection());
      assertEquals(-90.0, l.toDouble(), 0.0);
   }

   @Test
   public void checkFailLatMinValue() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LATITUDE_RANGE_DECIMAL);

      new Latitude(-91);
   }

   @Test
   public void checkFailMinutesSecondsMinValue() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LATITUDE_RANGE_DECIMAL);

      new Latitude(-90.000000001d);
   }

   @Test
   public void checkRecognizeDirectionAsNorth() {
      final Latitude l = new Latitude(40.4406d);

      assertEquals(40, l.getDegrees());
      assertEquals(26, l.getMinutes());
      assertEquals(26.16d, l.getSeconds(), 0.00000000001236d);
      assertEquals(Latitude.Direction.NORTH, l.getDirection());
   }

   @Test
   public void ChecktoRadians() {
      assertEquals(Math.toRadians(lat1.toDouble()), lat1.toRadians(), 0.0);
   }

   @Test
   public void ChecktoDoubleSouth() {
      final Latitude l = new Latitude(40, 26, 26.16d, Latitude.Direction.SOUTH);
      assertEquals(-40.4406d, l.toDouble(), 0.00000016666667d);
   }

   @Test
   public void ChecktoStringNorthLocale() {
      assertEquals("48°51'52.97\"N", lat1.toString(Locale.US));
   }

   @Test
   public void ChecktoStringSouthLocaleComma() {
      final Latitude l = new Latitude(12, 16, 23.45d, Latitude.Direction.SOUTH);
      assertEquals("12°16'23,45\"S", l.toString(Locale.FRANCE));
   }

   @Test
   public void ChecktoString() {
      final Latitude l = new Latitude(12, 16, 0, Latitude.Direction.NORTH);
      assertEquals("12°16'0\"N", l.toString());
   }

   @Test
   public void ChecktoStringZeroCoord() {
      final Latitude l = new Latitude(0);
      assertEquals("0°0'0\"", l.toString());
   }
}
