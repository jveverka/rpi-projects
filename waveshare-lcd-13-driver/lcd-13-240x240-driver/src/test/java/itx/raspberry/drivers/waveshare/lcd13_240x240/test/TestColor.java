package itx.raspberry.drivers.waveshare.lcd13_240x240.test;

import itx.raspberry.drivers.waveshare.lcd13_240x240.NativeDriverLowLevel;
import itx.raspberry.drivers.waveshare.lcd13_240x240.api.Color;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestColor {

    @DataProvider(name = "colorData")
    public static Object[][] primeNumbers() {
        return new Object[][] {
                { 0x00, 0x00, 0x00, Integer.valueOf(NativeDriverLowLevel.BLACK)},
                { 0x1F, 0x3F, 0x1F, Integer.valueOf(NativeDriverLowLevel.WHITE)},
                { 0x1F, 0x00, 0x1F, Integer.valueOf(NativeDriverLowLevel.MAGENTA)},
                { 0x00, 0x00, 0x1F, Integer.valueOf(NativeDriverLowLevel.BLUE)},
                { 0x00, 0x3F, 0x1F, Integer.valueOf(NativeDriverLowLevel.GBLUE)},
                { 0x1F, 0x3F, 0x00, Integer.valueOf(NativeDriverLowLevel.GRED)},
                { 0x1F, 0x00, 0x00, Integer.valueOf(NativeDriverLowLevel.RED)},
                { 0x00, 0x3F, 0x00, Integer.valueOf(NativeDriverLowLevel.GREEN)},
                { 0xFF, 0xFF, 0xFF, null },
                { 0x00, 0x00, 0xFF, null },
                { 0x00, 0xFF, 0x00, null },
                { 0xFF, 0x00, 0x00, null },
        };
    }

    @Test(dataProvider = "colorData")
    public void testColor(int r, int g, int b, Integer expectedResult) {
        try {
            Color color = new Color(r, g, b);
            Assert.assertNotNull(color);
            Assert.assertEquals(color.getColor(), expectedResult.intValue());
        } catch (UnsupportedOperationException e) {
            if (expectedResult != null) {
                Assert.fail();
            }
        }
    }

}
