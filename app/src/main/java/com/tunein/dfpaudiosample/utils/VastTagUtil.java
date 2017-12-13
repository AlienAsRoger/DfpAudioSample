package com.tunein.dfpaudiosample.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Helper class to facilitate VST tag urls creation and filling with parameters
 *
 * @see <a href="https://support.google.com/dfp_premium/answer/1068325?hl=en">Video Tags parameters</a>
 */
public class VastTagUtil {

    private static final String BASE_VAST_URL = "https://pubads.g.doubleclick.net/gampad/ads?";
    private static final String UTF_8 = "UTF-8";

    private static String buildRequiredParameters(String adUnitId, String sizes) {
        return "iu=" + adUnitId
                + "&correlator=" + System.currentTimeMillis()
                + "&env=vp"
                + "&impl=s"
                + "&url=tunein.player"
                + "&gdfp_req=1"
                + "&output=vast"
                + "&unviewed_position_start=1"
                + "&ciu_szs=300x250"
                + "&sz=" + sizes;
    }

    static String buildCustomParameters(String parameters) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(parameters, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "&cust_params=" + encoded;
    }

    public static String createVastUrlFromUnitId(String adUnitId, String customParameters, String screenSizes) {
        return BASE_VAST_URL + buildRequiredParameters(adUnitId, screenSizes) + buildCustomParameters(customParameters);
    }
}
