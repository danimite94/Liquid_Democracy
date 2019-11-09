package com.example.daniel.liquid_democracy;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class Liquid extends Application {

    public static final String channel1ID = "LD1";
    public static final String channel21ID = "LD21";
    public static final String channel22ID = "LD22";
    public static final String channel23ID = "LD23";
    public static final String channel24ID = "LD24";
    public static final String channel25ID = "LD25";
    public static final String channel3ID = "LD3";
    public static final String channel4ID = "LD4";
    public static final String channel5ID = "LD5";
    public static final String channel6ID = "LD6";
    public static final String channel7ID = "LD7";
    public static final String channel8ID = "LD8";
    public static final String channel9ID = "LD9";
    public static final String channel10ID = "LD10";
    public static final String channel11ID = "LD11";
    public static final String channel12ID = "LD12";

    @Override
    public void onCreate() {
        super.onCreate();

        //channel VOTATION
        CharSequence name = "VOTATION_END";
        String description = "All the decisions have been made and a victorious initiative has been found, the entire group is notified. winner is congratulated";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description,channel1ID,name,importance);

        //channel VOTATION_21
        CharSequence name21 = "DISCUSSION_STARTS";
        String description21 = "When any discussion phase starts, (all)supporters are notified";
        int importance21 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description21,channel21ID,name21,importance21);

        //channel VOTATION_22
        CharSequence name22 = "DISCUSSION_STOPS";
        String description22 = "When any discussion phase stops, (all)supporters are notified";
        int importance22 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description22,channel22ID,name22,importance22);

        //channel VOTATION_23
        CharSequence name23 = "VERIFICATION_STARTS";
        String description23 = "When any verification phase starts, supporters are notified";
        int importance23 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description23,channel23ID,name23,importance23);

        //channel VOTATION_24
        CharSequence name24 = "VERIFICATION_STOPS";
        String description24 = "When any verification phase starts, supporters are notified";
        int importance24 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description24,channel24ID,name24,importance24);

        //channel VOTATION_25
        CharSequence name25 = "VOTATION_STARTS";
        String description25 = "When any votation phase starts, supporters are notified";
        int importance25 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description25,channel25ID,name25,importance25);

        //channel SUGGESTION
        CharSequence name3 = "SUGGESTION_CREATE";
        String description3 = "When a suggestion is created, owner of init is notified";
        int importance3 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description3,channel3ID,name3,importance3);

        //channel ISSUE_1
        CharSequence name4 = "ISSUE_CREATE";
        String description4 = "When an issue is created, elements of the group are notified";
        int importance4 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description4,channel4ID,name4,importance4);

        //channel INITS_1
        CharSequence name5 = "INITIATIVE_PARTICIPANTS";
        String description5 = "When an initiative is supported or apposed, owner is notified";
        int importance5 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description5,channel5ID,name5,importance5);

        //channel INITS_2
        CharSequence name6 = "INITIATIVE_DELETED";
        String description6 = "When an initiative is deleted, all the elements of the group OR supporters of the issue are notified";
        int importance6 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description6,channel6ID,name6,importance6);

        //channel INITS_3
        CharSequence name7 = "INITIATIVE_EDITED";
        String description7 = "When an initiative is edited, all the elements of the group OR supporters of the issue are notified";
        int importance7 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description7,channel7ID,name7,importance7);

        //channel INITS_4
        CharSequence name8 = "INITIATIVE_ADDED";
        String description8 = "When an initiative is added, all the elements of the group OR supporters of the issue are notified";
        int importance8 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description8,channel8ID,name8,importance8);

        //channel GROUPS_1
        CharSequence name9 = "GROUP_ACCEPTED";
        String description9 = "When a user is accepted in a group, everyone in the group gets notified.";
        int importance9 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description9,channel9ID,name9,importance9);

        //channel GROUPS_2
        CharSequence name10 = "GROUP_JOIN";
        String description10 = "When a user tries to join a group, the owner is notified to make a decision";
        //versao 2.0: everyone gets notified to make a decision
        int importance10 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description10,channel10ID,name10,importance10);

        //channel GROUPS_3
        CharSequence name11 = "GROUP_INVITED";
        String description11 = "When a user is invited for a new group, that user is notified";
        int importance11 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description11,channel11ID,name11,importance11);

        //channel GROUPS_4
        CharSequence name12 = "GROUP_CREATED";
        String description12 = "When a new group is created, all users are notified";
        int importance12 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description12,channel12ID,name12,importance12);

        //channel GROUPS_5
        /*CharSequence name13 = "GROUP_EXPELLED";
        String description13 = "When a user is expelled from group";
        //versao 2.0: all participants need to agree
        int importance13 = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannels(description13,channel13ID,name13,importance13);*/

    }

    private void createNotificationChannels(String description, String channel_id, CharSequence name, int importance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("checker notification"," channel created");

            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);
            //Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //channel.setSound(defaultSound,);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

}
