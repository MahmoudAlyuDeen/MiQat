package afterapps.meeqat.helpers;

import afterapps.meeqat.datamodel.PrayersResponse;
import afterapps.meeqat.datamodel.ReverseGeoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
 * Created by Mahmoud.AlyuDeen on 7/24/2016.
 */
public class RetrofitClients {

    public interface ReverseGeoClient {
        String reverseGeoEndPoint = "maps/api/timezone/json";

        @GET(reverseGeoEndPoint)
        Call<ReverseGeoResponse> getReverseGeo(
                @Query("key") String apiKey,
                @Query("timestamp") String timestamp,
                @Query("location") String coordinates
        );
    }

    public interface PrayerTimesClient {
        String prayerTimesEndPoint = "calendar";

        @GET(prayerTimesEndPoint)
        Call<PrayersResponse> getPrayerTimes(
                @Query("latitude") String latitude,
                @Query("longitude") String longitude,
                @Query("timezonestring") String timezoneID,
                @Query("method") int method
        );
    }


}
