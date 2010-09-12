package net.sf.marineapi.nmea.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Direction;
import net.sf.marineapi.nmea.util.GpsMode;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the RMC sentence parser.
 * 
 * @author Kimmo Tuukkanen
 */
public class RMCTest {

    /** Example sentence */
    public static final String EXAMPLE = "$GPRMC,120044,A,6011.552,N,02501.941,E,000.0,360.0,160705,006.1,E,A*11";

    RMCParser rmc;

    @Before
    public void setUp() {
        try {
            rmc = new RMCParser(EXAMPLE);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getDataStatus()}.
     */
    @Test
    public void testGetDataStatus() {
        assertEquals(DataStatus.INVALID, rmc.getStatus());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#setDataStatus(DataStatus)}.
     */
    @Test
    public void testSetDataStatus() {
        rmc.setStatus(DataStatus.VALID);
        assertEquals(DataStatus.VALID, rmc.getStatus());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getGpsMode()}.
     */
    @Test
    public void testGetGpsMode() {
        assertEquals(GpsMode.AUTOMATIC, rmc.getGpsMode());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#setGpsMode()}.
     */
    @Test
    public void testSetGpsMode() {
        rmc.setGpsMode(GpsMode.SIMULATED);
        assertEquals(GpsMode.SIMULATED, rmc.getGpsMode());
        rmc.setGpsMode(GpsMode.ESTIMATED);
        assertEquals(GpsMode.ESTIMATED, rmc.getGpsMode());
    }

    /**
     * Test method for {@link net.sf.marineapi.nmea.parser.RMCParser#getSpeed()}
     * .
     */
    @Test
    public void testGetSpeed() {
        assertEquals(0.0, rmc.getSpeed(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#setSpeed(double)} .
     */
    @Test
    public void testSetSpeed() {
        rmc.setSpeed(35.2);
        assertEquals(35.2, rmc.getSpeed(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getCourse()} .
     */
    @Test
    public void testGetCourse() {
        assertEquals(360.0, rmc.getCourse(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#setCourse(double)} .
     */
    @Test
    public void testSetCourse() {
        rmc.setCourse(180.5);
        assertEquals(180.5, rmc.getCourse(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getCourse()} .
     */
    @Test
    public void testGetCorrectedCourse() {
        double expected = rmc.getCourse() + rmc.getVariation();
        assertEquals(expected, rmc.getCorrectedCourse(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getVariation()} .
     */
    @Test
    public void testGetMagneticVariation() {
        assertEquals(-6.1, rmc.getVariation(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#setVariation(double)} .
     */
    @Test
    public void testSetVariation() {
        rmc.setVariation(1.5);
        rmc.setDirectionOfVariation(Direction.WEST);
        assertEquals(1.5, rmc.getVariation(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getDirectionOfVariation()}
     * .
     */
    @Test
    public void testGetDirectionOfVariation() {
        assertTrue(rmc.getVariation() < 0);
        assertEquals(Direction.EAST, rmc.getDirectionOfVariation());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getDirectionOfVariation()}
     * .
     */
    @Test
    public void testSetDirectionOfVariation() {
        rmc.setDirectionOfVariation(Direction.WEST);
        assertEquals(Direction.WEST, rmc.getDirectionOfVariation());
        rmc.setDirectionOfVariation(Direction.EAST);
        assertEquals(Direction.EAST, rmc.getDirectionOfVariation());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getDirectionOfVariation()}
     * .
     */
    @Test
    public void testSetDirectionOfVariationWithInvalidDirection() {
        try {
            rmc.setDirectionOfVariation(Direction.NORTH);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getPosition()}.
     */
    @Test
    public void testGetPosition() {
        final double lat = 60 + (11.552 / 60);
        final double lon = 25 + (1.941 / 60);

        Position p = rmc.getPosition();
        assertNotNull(p);
        assertEquals(lat, p.getLatitude(), 0.0000001);
        assertEquals(lon, p.getLongitude(), 0.0000001);
        assertEquals(Direction.NORTH, p.getLatHemisphere());
        assertEquals(Direction.EAST, p.getLonHemisphere());
    }

    @Test
    public void testSetPosition() {
        final double lat = 61 + (1.111 / 60);
        final double lon = 27 + (7.777 / 60);
        Position p = new Position(lat, Direction.NORTH, lon, Direction.EAST);
        rmc.setPosition(p);

        String str = rmc.toString();
        Position wp = rmc.getPosition();

        assertTrue(str.contains(",6101.111,N,02707.777,E,"));
        assertNotNull(wp);
        assertEquals(lat, wp.getLatitude(), 0.0000001);
        assertEquals(lon, wp.getLongitude(), 0.0000001);
        assertEquals(Direction.NORTH, wp.getLatHemisphere());
        assertEquals(Direction.EAST, wp.getLonHemisphere());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getUtcTime()}.
     */
    @Test
    public void testGetUtcTime() {
        assertEquals("120044", rmc.getUtcTime());
    }

    /**
     * Test method for {@link net.sf.marineapi.nmea.parser.RMCParser#getTime()}.
     */
    @Test
    public void testGetTime() {
        Time t = rmc.getTime();
        assertNotNull(t);
        assertEquals(12, t.getHour());
        assertEquals(0, t.getMinutes());
        assertEquals(44.0, t.getSeconds(), 0.1);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getDay()}.
     */
    @Test
    public void testGetUtcDay() {
        assertEquals(16, rmc.getDay());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getMonth()}.
     */
    @Test
    public void testGetUtcMonth() {
        assertEquals(7, rmc.getMonth());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMCParser#getYear()}.
     */
    @Test
    public void testGetUtcYear() {
        assertEquals(2005, rmc.getYear());
    }

    /**
     * Test method for {@link net.sf.marineapi.nmea.parser.RMCParser#getDate()}.
     */
    @Test
    public void testGetDate() {
        GregorianCalendar cal = new GregorianCalendar(2005, 6, 16, 12, 0, 44);
        final Date expected = cal.getTime();
        Date parsed = rmc.getDate();
        assertEquals(expected, parsed);
        assertEquals(expected.getTime(), parsed.getTime());
    }

    /**
     * Test method for {@link net.sf.marineapi.nmea.parser.RMCParser#getTime()}.
     */
    @Test
    public void testSetTime() {
        Time t = new Time(1, 2, 3.4);
        rmc.setTime(t);
        assertTrue(rmc.toString().contains("$GPRMC,010203,A,"));
    }

}
