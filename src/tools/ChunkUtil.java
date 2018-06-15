package tools;

import java.io.IOException;

/**
 * 黄俊杰
 * 2018/6/15
 */
public class ChunkUtil {
    ChunkUtil() {
    }

    public static final void readCheckType(IntReader reader, int expectedType) throws IOException {
        int type = reader.readInt();
        if (type != expectedType) {
            throw new IOException("Expected chunk of type 0x" + Integer.toHexString(expectedType) + ", read 0x" + Integer.toHexString(type) + ".");
        }
    }
}
