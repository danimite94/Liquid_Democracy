package Firebase_Cloud_Messaging_ADAPTER;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA2e1D-UE:APA91bHr_73QkKyEi_OYXtgkL4VQoz-jAHzlfHRgJ_txEAi37po74ehmr2PGUnRg9h144eceEOAwKg_JzqC6sfekHE7AOXzgwud1VswgjqPYtCtMt_7kUwadfyuIVgwmq8fB38ADRFgX"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
