package tools;

import java.io.File;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 黄俊杰
 * 2018/6/15
 * 获取apk文件的 [0]:版本号;[1]包名 [2]是版本code
 */
public class AnalysisApkUtils {
    /**
     * @param apkUrl 要解析的apk的文件地址
     * @return String[]数组 [0]:版本号;[1]包名 [2]是版本code
     */
    public static String[] analysisApk(String apkUrl) {
        return analysisApk(new File(apkUrl));
    }

    /**
     * @param apkUrl 要解析的apk文件
     * @return String[]数组 [0]:版本号;[1]包名 [2]是版本code
     */
    public static String[] analysisApk(File apkUrl) {
        String[] st = new String[3];
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(apkUrl);
            Enumeration enumeration = zipFile.entries();
            ZipEntry zipEntry = null;
            while (enumeration.hasMoreElements()) {
                zipEntry = (ZipEntry) enumeration.nextElement();
                if (zipEntry.isDirectory()) {

                } else {
                    if ("AndroidManifest.xml".equals(zipEntry.getName())) {
                        try {
                            AXmlResourceParser parser = new AXmlResourceParser();
                            parser.open(zipFile.getInputStream(zipEntry));
                            while (true) {
                                int type = parser.next();
                                if (type == XmlPullParser.END_DOCUMENT) {
                                    break;
                                }
                                switch (type) {
                                    case XmlPullParser.START_TAG: {
                                        for (int i = 0; i != parser.getAttributeCount(); ++i) {
                                            if ("versionName".equals(parser.getAttributeName(i))) {
                                                st[0] = getAttributeValue(parser, i);
                                            } else if ("package".equals(parser.getAttributeName(i))) {
                                                st[1] = getAttributeValue(parser, i);
                                            } else if ("versionCode".equals(parser.getAttributeName(i))) {
                                                st[2] = getAttributeValue(parser, i);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return st;
    }

    /**
     * 获取指定节点的值
     *
     * @param parser 节点
     * @param index  下标
     * @return
     */
    private static String getAttributeValue(AXmlResourceParser parser, int index) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);
        if (type == TypedValue.TYPE_STRING) {
            return parser.getAttributeValue(index);
        }
        if (type == TypedValue.TYPE_ATTRIBUTE) {
            return String.format("?%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_REFERENCE) {
            return String.format("@%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_FLOAT) {
            return String.valueOf(Float.intBitsToFloat(data));
        }
        if (type == TypedValue.TYPE_INT_HEX) {
            return String.format("0x%08X", data);
        }
        if (type == TypedValue.TYPE_INT_BOOLEAN) {
            return data != 0 ? "true" : "false";
        }
        if (type == TypedValue.TYPE_DIMENSION) {
            return Float.toString(complexToFloat(data)) +
                    DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type == TypedValue.TYPE_FRACTION) {
            return Float.toString(complexToFloat(data)) +
                    FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type >= TypedValue.TYPE_FIRST_COLOR_INT && type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return String.format("#%08X", data);
        }
        if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
            return String.valueOf(data);
        }
        return String.format("<0x%X, type 0x%02X>", data, type);
    }

    private static String getPackage(int id) {
        if (id >>> 24 == 1) {
            return "android:";
        }
        return "";
    }

    public static float complexToFloat(int complex) {
        return (float) (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
    }

    private static final float RADIX_MULTS[] = {
            0.00390625F, 3.051758E-005F, 1.192093E-007F, 4.656613E-010F
    };
    private static final String DIMENSION_UNITS[] = {
            "px", "dip", "sp", "pt", "in", "mm", "", ""
    };
    private static final String FRACTION_UNITS[] = {
            "%", "%p", "", "", "", "", "", ""
    };
}
