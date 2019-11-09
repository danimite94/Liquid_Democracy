package com.example.daniel.liquid_democracy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import MAIN_CLASSES.Delegation;
import MAIN_CLASSES.Group;
import MAIN_CLASSES.Initiative;
import MAIN_CLASSES.Issue;
import MAIN_CLASSES.User;

public class FirebaseHelper {

    DatabaseReference db;
    Boolean saved;
    ArrayList<Group> groups = new ArrayList<>();
    ArrayList<Delegation> delegations = new ArrayList<>();
    ArrayList<Initiative> initiatives = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();
    ArrayList<Issue> issues = new ArrayList<>();

    public FirebaseHelper(DatabaseReference db){
        this.db=db;
    }

    public Boolean saveGroups(Group groupsActivity)
    {

        if (groupsActivity==null){
            saved=false;
        }else{
            try{
                db.push().setValue(groupsActivity);
                saved=true;
            }catch (DatabaseException e){
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }
    public Boolean saveIssues(Issue issue)
    {

        if (issue==null){
            saved=false;
        }else{
            try{
                db.push().setValue(issue);
                saved=true;
            }catch (DatabaseException e){
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }
    public Boolean saveUser(User user)
    {

        if (user==null){
            saved=false;
        }else{
            try{
                db.push().setValue(user);
                saved=true;
            }catch (DatabaseException e){
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }
    public Boolean savDelegation(Delegation deleg)
    {

        if (deleg==null){
            saved=false;
        }else{
            try{
                db.push().setValue(deleg);
                saved=true;
            }catch (DatabaseException e){
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }
    public Boolean saveInit(Initiative init)
    {

        if (init==null){
            saved=false;
        }else{
            try{
                db.push().setValue(init);
                saved=true;
            }catch (DatabaseException e){
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }


    private void fetchGroupsData(DataSnapshot dataSnapshot){
        groups.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            Group group = (Group) ds.getValue(Group.class);

            //group.setImage(ds.child("image").getValue().toString());
//            group.setName(ds.child("name").getValue().toString());

            groups.add(group);

        }
    }
    private void fetchDelegData(DataSnapshot dataSnapshot){
        delegations.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            Delegation delegation = (Delegation) ds.getValue(Delegation.class);

            //group.setImage(ds.child("image").getValue().toString());
//            group.setName(ds.child("name").getValue().toString());

            delegations.add(delegation);

        }
    }
    private void fetchInitData(DataSnapshot dataSnapshot){
        initiatives.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            Initiative init = (Initiative) ds.getValue(Initiative.class);

            //group.setImage(ds.child("image").getValue().toString());
//            group.setName(ds.child("name").getValue().toString());

            initiatives.add(init);

        }
    }
    private void fetchIssueData(DataSnapshot dataSnapshot){
        issues.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            Issue issue = (Issue) ds.getValue(Issue.class);

            //group.setImage(ds.child("image").getValue().toString());
//            group.setName(ds.child("name").getValue().toString());

            issues.add(issue);

        }
    }
    private void fetchUserData(DataSnapshot dataSnapshot){
        users.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            User user = (User) ds.getValue(User.class);

            //group.setImage(ds.child("image").getValue().toString());
//            group.setName(ds.child("name").getValue().toString());

            users.add(user);

        }
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        return imageEncoded;
    }

    public ArrayList<Group> retrieve() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchGroupsData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return groups;
    }
}
