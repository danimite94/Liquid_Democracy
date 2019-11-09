package com.example.daniel.liquid_democracy;


import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import INITIATIVES_ADAPTER.AdmissionActivity;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdmissionActivityTest {


    public DatabaseReference mRefFollow;
    public AdmissionActivity testInstance;

    //runs first
    @Before
    public void setUp(){
        mRefFollow = mock(DatabaseReference.class);
        testInstance = new AdmissionActivity();
        testInstance.setDB(mRefFollow);
    }

    @Test
    public void updateDSOADatabase_child2(){

        //workaround
        DatabaseReference databaseReferenceMock1 = mock(DatabaseReference.class);

        when(mRefFollow.child(anyString())).thenReturn(databaseReferenceMock1);

        String issueID = "a";

        String userID = "b";
        testInstance.updateDSOADatabase(issueID,userID,"follow");
        verify(mRefFollow).child(eq(issueID));
    }

    //runs second
    @Test
    public void updateDSOADatabase_removevalue() {

        //1st create variables
        DatabaseReference databaseReferenceMock1 = mock(DatabaseReference.class, CALLS_REAL_METHODS);
        DatabaseReference databaseReferenceMock2 = mock(DatabaseReference.class);

        //2nd hypothesise with random values
        when(mRefFollow.child(anyString())).thenReturn(databaseReferenceMock1);
        when(databaseReferenceMock1.child(anyString())).thenReturn(databaseReferenceMock2);

        //3rd real test
        String issueID = "a";
        String userID = "b";
        ///call method
        testInstance.updateDSOADatabase(issueID,userID,"follow");
        ///verify results
        verify(mRefFollow).child(eq(issueID));
        verify(databaseReferenceMock2).removeValue();

    }
}