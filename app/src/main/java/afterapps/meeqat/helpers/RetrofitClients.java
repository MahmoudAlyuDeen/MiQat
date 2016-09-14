package afterapps.meeqat.helpers;

import afterapps.meeqat.datamodel.ReverseGeoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
 * Created by Mahmoud.AlyuDeen on 7/24/2016.
 */
public class RetrofitClients {

    public interface ReverseGeoClient {
        String reverseGeoEndPoint = "https://maps.googleapis.com/maps/api/timezone/json";

        @GET(reverseGeoEndPoint)
        Call<ReverseGeoResponse> getReverseGeo(
                @Query("api_key") String apiKey,
                @Query("timestamp") double timestamp,
                @Query("coordinates") String coordinates
        );
    }

}
