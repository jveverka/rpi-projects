package itx.raspberry.drivers.waveshare.lcd13_240x240.api;

public enum FontSize {

    FONT_8(8),
    FONT_12(12),
    FONT_16(16),
    FONT_20(20),
    FONT_24(24);

    private int size;

    FontSize(int size) {
       this.size = size;
    }

    public int getSize() {
        return size;
    }

}
