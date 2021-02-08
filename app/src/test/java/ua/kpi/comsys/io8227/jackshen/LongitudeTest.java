package ua.kpi.comsys.io8227.jackshen;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ua.kpi.comsys.io8227.jackshen.exception.GeoCoordException;


public class LongitudeTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private Longitude lng1;

   @Before
   public void setUp() {
      lng1 = new Longitude(2, 20, 56.45d, Longitude.Direction.EAST);
   }

   @Test
   public void checkMaxValueCorrectness() {
      assertEquals(180, Longitude.MAX_VALUE);
   }

   @Test
   public void checkSetLng() {
      final int degree = 1;
      final int minute = 2;
      final double seconds = 3;
      final Longitude.Direction direction = Longitude.Direction.EAST;

      final Longitude l = new Longitude(degree, minute, seconds, direction);

      assertEquals(degree, l.getDegrees());
      assertEquals(minute, l.getMinutes());
      assertEquals(seconds, l.getSeconds(), 0.0);
      assertEquals(direction, l.getDirection());
   }

   @Test
   public void checkSetLngNeither() {
      final int degree = 0;
      final int minute = 0;
      final double seconds = 0;
      final Longitude.Direction direction = Longitude.Direction.NEITHER;

      final Longitude l = new Longitude(degree, minute, seconds, direction);

      assertEquals(degree, l.getDegrees());
      assertEquals(minute, l.getMinutes());
      assertEquals(seconds, l.getSeconds(), 0.0);
      assertEquals(direction, l.getDirection());
   }

   @Test
   public void checkFailLngNeither() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.DIRECTION_INVALID);

      new Longitude(2, 2, 2, Longitude.Direction.NEITHER);
   }

   @Test
   public void checkLngMaxValue() {
      final Longitude l = new Longitude(Longitude.MAX_VALUE);

      assertEquals(Longitude.MAX_VALUE, l.getDegrees());
      assertEquals(0, l.getMinutes());
      assertEquals(0.0, l.getSeconds(), 0.0);
      assertEquals(Longitude.Direction.EAST, l.getDirection());
      assertEquals((double) Longitude.MAX_VALUE, l.toDouble(), 0.0);
   }

   @Test
   public void checkFailLngMaxValue() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LONGITUDE_RANGE_DECIMAL);

      new Longitude(Longitude.MAX_VALUE + 1);
   }

   @Test
   public void checkFailMinutesSecondsMaxValue() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LONGITUDE_RANGE_DECIMAL);

      new Longitude(Longitude.MAX_VALUE + .000000001d);
   }

   @Test
   public void checkLngMinValue() {
      final Longitude l = new Longitude(-Longitude.MAX_VALUE);

      assertEquals(Longitude.MAX_VALUE, l.getDegrees());
      assertEquals(0, l.getMinutes());
      assertEquals(0.0, l.getSeconds(), 0.0);
      assertEquals(Longitude.Direction.WEST, l.getDirection());
      assertEquals((double) -Longitude.MAX_VALUE, l.toDouble(), 0.0);
   }

   @Test
   public void checkFailLngMinValue() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LONGITUDE_RANGE_DECIMAL);

      new Longitude(-(Longitude.MAX_VALUE + 1));
   }

   @Test
   public void checkFailMinutesSecondsMinValue() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LONGITUDE_RANGE_DECIMAL);

      new Longitude(-(Longitude.MAX_VALUE + .000000001d));
   }

   @Test
   public void checkRecognizeDirectionAsWest() {
      final Longitude l = new Longitude(-40.4406d);

      assertEquals(40, l.getDegrees());
      assertEquals(26, l.getMinutes());
      assertEquals(26.16d, l.getSeconds(), 0.00000000001236d);
      assertEquals(Longitude.Direction.WEST, l.getDirection());
   }

   @Test
   public void ChecktoRadians() {
      assertEquals(Math.toRadians(lng1.toDouble()), lng1.toRadians(), 0.0);
   }

   @Test
   public void ChecktoStringNullLocale() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LOCALE_NULL);

      lng1.toString(null);
   }

   @Test
   public void ChecktoStringWestLocale() {
      final Longitude l = new Longitude(12, 16, 23.45d, Longitude.Direction.WEST);
      assertEquals("12°16'23.45\"W", l.toString(Locale.US));
   }

   @Test
   public void ChecktoStringEastLocaleComma() {
      assertEquals("2°20'56,45\"E", lng1.toString(Locale.FRANCE));
   }

   @Test
   public void ChecktoString() {
      final Longitude l = new Longitude(12, 16, 0, Longitude.Direction.EAST);
      assertEquals("12°16'0\"E", l.toString());
   }

   @Test
   public void ChecktoStringZeroCoord() {
      final Longitude l = new Longitude(0);
      assertEquals("0°0'0\"", l.toString());
   }
}
