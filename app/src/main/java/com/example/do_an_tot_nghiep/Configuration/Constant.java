package com.example.do_an_tot_nghiep.Configuration;

/**
 * @author Phong-Kaster
 * @since 18-11-2022
 * this class contains all constant variable in this application
 */
public class Constant {

    /**
     * @since 17-11-2022
     */
    public static String UPLOAD_URI()
    {
        // PHẢI CÓ TÊN THƯ MỤC DỰ ÁN Ở ĐÂY
        return "http://192.168.1.57:8080/assets/uploads/";
    }


    /**
     * @since 18-11-2022
     * Use this APP_PATH if testing device is a real hardware device
     */
    public static String APP_PATH()
    {
        // PHẢI CÓ TÊN THƯ MỤC DỰ ÁN Ở ĐÂY
        return "http://192.168.1.57:8080/";
    }

    /**
     * @since 18-11-2022
     * Use this APP_PATH if testing device is the Android emulator
     */
    public static String APP_PATH_EMULATOR()
    {
        return "http://192.168.1.57:8080/";
    }

    /**
     * @since 18-11-2022
     * application name
     */
    public static String APP_NAME()
    {
        return "Umbrella Health";
    }

    public static String accessToken;
    public static void setAccessToken(String accessToken)
    {
        Constant.accessToken = accessToken;
    }
    public static String getAccessToken()
    {
        return Constant.accessToken;
    }

    /**
     * @since 30-11-2022
     * umbrella video
     */
    public static String VIDEO_PATH()
    {
        return "https://www.youtube.com/watch?v=W4JA4dbscis&ab_channel=UmbrellaCorporation4";
    }

    /**
     * @since 22-12-2022
     */
    public static String OPEN_WEATHER_MAP_API_KEY()
    {
        return "4167c6d6038647807b56abd84b7d6626";
    }

    public static  String OPEN_WEATHER_MAP_API_KEY_2()
    {
        return "fc07e74110a9bcbc166c7887e51ec2db";
    }

    public static String OPEN_WEATHER_MAP_PATH()
    {
        return "https://api.openweathermap.org/data/2.5/weather/";
    }
}
