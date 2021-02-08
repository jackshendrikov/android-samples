package ua.kpi.comsys.io8227.jackshen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ua.kpi.comsys.io8227.jackshen.exception.GeoCoordException;

public class CoordinateJSTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private Latitude latitude1;

   private Longitude longitude1;

   private CoordinateJS point1;


   @Before
   public void setUp() {
      latitude1 = new Latitude(48, 51, 52.97, Latitude.Direction.NORTH);
      longitude1 = new Longitude(2, 20, 56.45, Longitude.Direction.EAST);

      point1 = new CoordinateJS(latitude1, longitude1, "Paris");
   }

   @Test
   public void checkSetLatLng() {
      assertSame(latitude1, point1.getLatitude());
      assertSame(longitude1, point1.getLongitude());
      assertSame("Paris", point1.getName());
   }

   @Test
   public void checkNullLat2Arg() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LATITUDE_NULL);

      new CoordinateJS(null, longitude1);
   }

   @Test
   public void checkNullLng2Arg() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LONGITUDE_NULL);

      new CoordinateJS(latitude1, null);
   }

   @Test
   public void checkNullLat3Arg() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(GeoCoordException.Messages.LATITUDE_NULL);

      new CoordinateJS(null, longitude1, "Paris");
   }

   @Test
   public void checkNullLng3Arg() {
      thrown.expect(GeoCoordException.class );
      thrown.expectMessage(GeoCoordException.Messages.LONGITUDE_NULL);

      new CoordinateJS(latitude1, null, "Paris");
   }

   @Test
   public void checkNullName3Arg() {
      thrown.expect(GeoCoordException.class);
      thrown.expectMessage(CoreMatchers.endsWith(GeoCoordException.Messages.NAME_NULL));

      new CoordinateJS(latitude1, longitude1, null);
   }

   @Test
   public void checkAddressEquality() {
      assertEquals(point1, point1);
   }

   @Test
   public void checkFullAddressEquality() {
      final Latitude lat  = new Latitude(point1.getLatitude().getDegrees(), point1.getLatitude().getMinutes(),
                                         point1.getLatitude().getSeconds(), point1.getLatitude().getDirection());

      final Longitude lng = new Longitude(point1.getLongitude().getDegrees(), point1.getLongitude().getMinutes(),
                                          point1.getLongitude().getSeconds(), point1.getLongitude().getDirection());

      assertEquals(point1, new CoordinateJS(lat, lng, "Paris"));
   }

   @Test
   public void checkNotEqualLat() {
      final Latitude lat = new Latitude(latitude1.getDegrees() + 1, latitude1.getMinutes(), latitude1.getSeconds(), latitude1.getDirection());
      final CoordinateJS newPoint = new CoordinateJS(lat, point1.getLongitude());

      assertNotEquals(point1, newPoint);
   }

   @Test
   public void checkNotEqualLng() {
      final Longitude lng = new Longitude(longitude1.getDegrees() + 1, longitude1.getMinutes(), longitude1.getSeconds(), longitude1.getDirection());
      final CoordinateJS newPoint = new CoordinateJS( point1.getLatitude(), lng);

      assertNotEquals(point1, newPoint);
   }
}
