package org.lpw.tephra.util;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class NumericTest extends CoreTestSupport {
    @Inject
    private Numeric numeric;

    @Test
    public void toInt() {
        Assert.assertEquals(0, numeric.toInt(null));
        Assert.assertEquals(0, numeric.toInt(""));
        Assert.assertEquals(-1, numeric.toInt(null, -1));
        Assert.assertEquals(-1, numeric.toInt("", -1));

        Object object = new Object();
        Assert.assertEquals(0, numeric.toInt(object));
        Assert.assertEquals(0, numeric.toInt("1a"));
        Assert.assertEquals(-1, numeric.toInt(object, -1));
        Assert.assertEquals(-1, numeric.toInt("1a", -1));

        Integer n1 = 10;
        Assert.assertEquals(10, numeric.toInt(n1));
        Assert.assertEquals(10, numeric.toInt(n1, 1));

        int n2 = 20;
        Assert.assertEquals(20, numeric.toInt(n2));
        Assert.assertEquals(20, numeric.toInt(n2, 1));

        Long l1 = 10L;
        Assert.assertEquals(10, numeric.toInt(l1));
        Assert.assertEquals(10, numeric.toInt(l1, 1));

        long l2 = 20L;
        Assert.assertEquals(20, numeric.toInt(l2));
        Assert.assertEquals(20, numeric.toInt(l2, 1));

        Float f1 = 2.2F;
        Assert.assertEquals(2, numeric.toInt(f1));
        Assert.assertEquals(2, numeric.toInt(f1, 1));

        float f2 = 5.5F;
        Assert.assertEquals(6, numeric.toInt(f2));
        Assert.assertEquals(6, numeric.toInt(f2, 1));

        Double d1 = 2.2D;
        Assert.assertEquals(2, numeric.toInt(d1));
        Assert.assertEquals(2, numeric.toInt(d1, 1));

        double d2 = 5.5D;
        Assert.assertEquals(6, numeric.toInt(d2));
        Assert.assertEquals(6, numeric.toInt(d2, 1));

        Assert.assertEquals(8, numeric.toInt("7.7"));
        Assert.assertEquals(8, numeric.toInt("7.7", 1));

        Assert.assertEquals(1235, numeric.toInt("1,234.5678"));
        Assert.assertEquals(1235, numeric.toInt("1,234.5678", 1));

        Assert.assertEquals(1234, numeric.toInt("1,234"));
        Assert.assertEquals(1234, numeric.toInt("1,234", 1));
    }

    @Test
    public void toLong() {
        Assert.assertEquals(0L, numeric.toLong(null));
        Assert.assertEquals(0L, numeric.toLong(""));
        Assert.assertEquals(-1L, numeric.toLong(null, -1));
        Assert.assertEquals(-1L, numeric.toLong("", -1));

        Object object = new Object();
        Assert.assertEquals(0L, numeric.toLong(object));
        Assert.assertEquals(0L, numeric.toLong("1a"));
        Assert.assertEquals(-1L, numeric.toLong(object, -1));
        Assert.assertEquals(-1L, numeric.toLong("1a", -1));

        Integer n1 = 10;
        Assert.assertEquals(10L, numeric.toLong(n1));
        Assert.assertEquals(10L, numeric.toLong(n1, 1));

        int n2 = 20;
        Assert.assertEquals(20L, numeric.toLong(n2));
        Assert.assertEquals(20L, numeric.toLong(n2, 1));

        Long l1 = 10L;
        Assert.assertEquals(10L, numeric.toLong(l1));
        Assert.assertEquals(10L, numeric.toLong(l1, 1));

        long l2 = 20L;
        Assert.assertEquals(20L, numeric.toLong(l2));
        Assert.assertEquals(20L, numeric.toLong(l2, 1));

        Float f1 = 2.2F;
        Assert.assertEquals(2L, numeric.toLong(f1));
        Assert.assertEquals(2L, numeric.toLong(f1, 1));

        float f2 = 5.5F;
        Assert.assertEquals(6L, numeric.toLong(f2));
        Assert.assertEquals(6L, numeric.toLong(f2, 1));

        Double d1 = 2.2D;
        Assert.assertEquals(2L, numeric.toLong(d1));
        Assert.assertEquals(2L, numeric.toLong(d1, 1));

        double d2 = 5.5D;
        Assert.assertEquals(6L, numeric.toLong(d2));
        Assert.assertEquals(6L, numeric.toLong(d2, 1));

        Assert.assertEquals(8L, numeric.toLong("7.7"));
        Assert.assertEquals(8L, numeric.toLong("7.7", 1));

        Assert.assertEquals(1235L, numeric.toLong("1,234.5678"));
        Assert.assertEquals(1235L, numeric.toLong("1,234.5678", 1));

        Assert.assertEquals(1234L, numeric.toLong("1,234"));
        Assert.assertEquals(1234L, numeric.toLong("1,234", 1));
    }

    @Test
    public void toFloat() {
        Assert.assertEquals(0.0F, numeric.toFloat(null), 0.0F);
        Assert.assertEquals(0.0F, numeric.toFloat(""), 0.0F);
        Assert.assertEquals(-1.0F, numeric.toFloat(null, -1.0F), 0.0F);
        Assert.assertEquals(-1.0F, numeric.toFloat("", -1.0F), 0.0F);

        Object object = new Object();
        Assert.assertEquals(0.0F, numeric.toFloat(object), 0.0F);
        Assert.assertEquals(0.0F, numeric.toFloat("1a"), 0.0F);
        Assert.assertEquals(-1.0F, numeric.toFloat(object, -1.0F), 0.0F);
        Assert.assertEquals(-1.0F, numeric.toFloat("1a", -1.0F), 0.0F);

        Integer n1 = 10;
        Assert.assertEquals(10.0F, numeric.toFloat(n1), 0.0F);
        Assert.assertEquals(10.0F, numeric.toFloat(n1, 1), 0.0F);

        int n2 = 20;
        Assert.assertEquals(20.0F, numeric.toFloat(n2), 0.0F);
        Assert.assertEquals(20.0F, numeric.toFloat(n2, 1), 0.0F);

        Long l1 = 10L;
        Assert.assertEquals(10.0F, numeric.toFloat(l1), 0.0F);
        Assert.assertEquals(10.0F, numeric.toFloat(l1, 1), 0.0F);

        long l2 = 20L;
        Assert.assertEquals(20.0F, numeric.toFloat(l2), 0.0F);
        Assert.assertEquals(20.0F, numeric.toFloat(l2, 1), 0.0F);

        Float f1 = 2.2F;
        Assert.assertEquals(2.2F, numeric.toFloat(f1), 0.0F);
        Assert.assertEquals(2.2F, numeric.toFloat(f1, 1), 0.0F);

        float f2 = 5.5F;
        Assert.assertEquals(5.5F, numeric.toFloat(f2), 0.0F);
        Assert.assertEquals(5.5F, numeric.toFloat(f2, 1), 0.0F);

        Double d1 = 2.2D;
        Assert.assertEquals(2.2F, numeric.toFloat(d1), 0.0F);
        Assert.assertEquals(2.2F, numeric.toFloat(d1, 1), 0.0F);

        double d2 = 5.5D;
        Assert.assertEquals(5.5F, numeric.toFloat(d2), 0.0F);
        Assert.assertEquals(5.5F, numeric.toFloat(d2, 1), 0.0F);

        Assert.assertEquals(7.7F, numeric.toFloat("7.7"), 0.0F);
        Assert.assertEquals(7.7F, numeric.toFloat("7.7", 1), 0.0F);

        Assert.assertEquals(1234.5678F, numeric.toFloat("1,234.5678"), 0.0F);
        Assert.assertEquals(1234.5678F, numeric.toFloat("1,234.5678", 1), 0.0F);

        Assert.assertEquals(1234.0F, numeric.toFloat("1,234"), 0.0F);
        Assert.assertEquals(1234.0F, numeric.toFloat("1,234", 1), 0.0F);
    }

    @Test
    public void toDouble() {
        Assert.assertEquals(0.0D, numeric.toDouble(null), 0.0D);
        Assert.assertEquals(0.0D, numeric.toDouble(""), 0.0D);
        Assert.assertEquals(-1.0D, numeric.toDouble(null, -1.0D), 0.0D);
        Assert.assertEquals(-1.0D, numeric.toDouble("", -1.0D), 0.0D);

        Object object = new Object();
        Assert.assertEquals(0.0D, numeric.toDouble(object), 0.0D);
        Assert.assertEquals(0.0D, numeric.toDouble("1a"), 0.0D);
        Assert.assertEquals(-1.0D, numeric.toDouble(object, -1.0D), 0.0D);
        Assert.assertEquals(-1.0D, numeric.toDouble("1a", -1.0D), 0.0D);

        Integer n1 = 10;
        Assert.assertEquals(10.0D, numeric.toDouble(n1), 0.0D);
        Assert.assertEquals(10.0D, numeric.toDouble(n1, 1), 0.0D);

        int n2 = 20;
        Assert.assertEquals(20.0D, numeric.toDouble(n2), 0.0D);
        Assert.assertEquals(20.0D, numeric.toDouble(n2, 1), 0.0D);

        Long l1 = 10L;
        Assert.assertEquals(10.0D, numeric.toDouble(l1), 0.0D);
        Assert.assertEquals(10.0D, numeric.toDouble(l1, 1), 0.0D);

        long l2 = 20L;
        Assert.assertEquals(20.0D, numeric.toDouble(l2), 0.0D);
        Assert.assertEquals(20.0D, numeric.toDouble(l2, 1), 0.0D);

        Float f1 = 2.4F;
        Assert.assertEquals(2.4D, numeric.toDouble(f1), 0.01D);
        Assert.assertEquals(2.4D, numeric.toDouble(f1, 1), 0.01D);

        float f2 = 5.5F;
        Assert.assertEquals(5.5D, numeric.toDouble(f2), 0.0D);
        Assert.assertEquals(5.5D, numeric.toDouble(f2, 1), 0.0D);

        Double d1 = 2.2D;
        Assert.assertEquals(2.2D, numeric.toDouble(d1), 0.0D);
        Assert.assertEquals(2.2D, numeric.toDouble(d1, 1), 0.0D);

        double d2 = 5.5D;
        Assert.assertEquals(5.5D, numeric.toDouble(d2), 0.0D);
        Assert.assertEquals(5.5D, numeric.toDouble(d2, 1), 0.0D);

        Assert.assertEquals(7.7D, numeric.toDouble("7.7"), 0.0D);
        Assert.assertEquals(7.7D, numeric.toDouble("7.7", 1), 0.0D);

        Assert.assertEquals(1234.5678D, numeric.toDouble("1,234.5678"), 0.0D);
        Assert.assertEquals(1234.5678D, numeric.toDouble("1,234.5678", 1), 0.0D);

        Assert.assertEquals(1234.0D, numeric.toDouble("1,234"), 0.0D);
        Assert.assertEquals(1234.0D, numeric.toDouble("1,234", 1), 0.0D);
    }

    @Test
    public void toIntsString() {
        Assert.assertArrayEquals(new int[0], numeric.toInts((String) null));
        Assert.assertArrayEquals(new int[0], numeric.toInts(""));
        Assert.assertArrayEquals(new int[]{0, 1, 2, 3}, numeric.toInts("0,1,2,3"));
        Assert.assertArrayEquals(new int[]{0, 1, 2, 3}, numeric.toInts("0,1,2,3,"));
        Assert.assertArrayEquals(new int[]{0, 1, 2, 3, 0}, numeric.toInts("0,1,2,3,a"));
        Assert.assertArrayEquals(new int[]{0, 1, 0, 3, 0}, numeric.toInts("0,1,a,3,b"));
    }

    @Test
    public void toIntsArray() {
        Assert.assertArrayEquals(new int[0], numeric.toInts((String[]) null));
        Assert.assertArrayEquals(new int[0], numeric.toInts(new String[0]));
        Assert.assertArrayEquals(new int[]{0, 1, 2, 3}, numeric.toInts(new String[]{"0", "1", "2", "3"}));
        Assert.assertArrayEquals(new int[]{0, 1, 2, 3, 0}, numeric.toInts(new String[]{"0", "1", "2", "3", "a"}));
        Assert.assertArrayEquals(new int[]{0, 1, 0, 3, 0}, numeric.toInts(new String[]{"0", "1", "b", "3", "a"}));
    }
}
