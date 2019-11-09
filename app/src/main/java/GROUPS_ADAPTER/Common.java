package GROUPS_ADAPTER;

import Firebase_Cloud_Messaging_ADAPTER.APIService;
import Firebase_Cloud_Messaging_ADAPTER.Client;
import MAIN_CLASSES.Group;
import retrofit2.Retrofit;

public class Common {

    public static String currentToken = "";

    public static String baseUrl = "https://fcm.googleapis.com/";

    public static APIService getFCMClient(){
        return Client.getClient(baseUrl).create(APIService.class);
    }

}
