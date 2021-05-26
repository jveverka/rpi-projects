package one.microproject.devicecontroller.dto;

import java.io.InputStream;

public record DataStream(InputStream is, String fileName, String mimeType) {
}
