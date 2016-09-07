package com.sapienter.jbilling.server.util;

import com.sapienter.jbilling.server.user.EntityBL;
import org.codehaus.groovy.grails.web.util.WebUtils;

public class ColorConverter {

//    public static final String BASE_GREEN = "#8ec549";
//    public static final Integer[] BASE_COLOR = new Integer[] {0Xc5, 0Xc4, 0X11};
    public static Integer[] BASE_COLOR;

    public static final Integer[] DIFF_BASE = new Integer[] {0x0, 0x0, 0x0};
    public static final Integer[] DIFF_CCE4AD = new Integer[] {-0x3e, -0x1f, -0x64};
    public static final Integer[] DIFF_A7CEAA = new Integer[] {-0x19, -0x09, -0x61};
    public static final Integer[] DIFF_8DC449 = new Integer[] {0x01, 0x01, 0x00};
    public static final Integer[] DIFF_8BC53D = new Integer[] {0x03, 0x00, 0x0C};
    public static final Integer[] DIFF_7BAA3F = new Integer[] {0x13, 0x1b, 0x0A};
    public static final Integer[] DIFF_3C9343 = new Integer[] {0x52, 0x32, 0x06};
    public static final Integer[] DIFF_3C9242 = new Integer[] {0x52, 0x33, 0x07};
    public static final Integer[] DIFF_4B8C3C = new Integer[] {0x43, 0x39, 0x0d};
    public static final Integer[] DIFF_347F3A = new Integer[] {0x5a, 0x46, 0x0f};
    public static final Integer[] DIFF_008000 = new Integer[] {0x8e, 0x45, 0x49};
    public static final Integer[] DIFF_37672C = new Integer[] {0x57, 0x5e, 0x1d};
    public static final Integer[] DIFF_35642B = new Integer[] {0x02, 0x61, 0x1e};


    public static String convert(Integer[] diffHex) {
        return "#" + toHexString(BASE_COLOR[0] - diffHex[0]) + toHexString(BASE_COLOR[1] - diffHex[1]) + toHexString(BASE_COLOR[2] - diffHex[2]);
    }

    private static String toHexString(Integer hex) {
        String hexString = hex < 0 ? "00" : hex > 255 ? "ff" : Integer.toHexString(hex);

        return hexString.length() == 1 ? "0" + hexString : hexString;
    }

    public static void setBaseColor() {
        Integer uiColor = new EntityBL((Integer) WebUtils.retrieveGrailsWebRequest().getSession().getAttribute("company_id")).getEntity().getUiColor();

        // Defaults to green
        if (uiColor == null) {
            uiColor = 9356617;
        }

        // Set new base color
        BASE_COLOR = new Integer[] {
            (uiColor >> 16) & 0xFF,
            (uiColor >> 8) & 0xFF,
            (uiColor >> 0) & 0xFF
        };
    }
}