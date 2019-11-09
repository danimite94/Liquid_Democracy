package SUGGESTIONS_ADAPTER;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.liquid_democracy.Main_issue_creator;
import com.example.daniel.liquid_democracy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.Adapter;
import MAIN_CLASSES.Initiative;
import MAIN_CLASSES.Issue;
import MAIN_CLASSES.Item;
import MAIN_CLASSES.Suggestion;

public class Main_suggestion_creator extends Fragment {

    //implements ItemClickListener {
    private static final int MY_REQUEST = 1001;
    RecyclerView.LayoutManager layoutManager;
    FirebaseAuth mAuth;
    private DatabaseReference mRef, mRefSug, mRefInit,mRefActionInit;
    private String currentUserID, initID;
    public Button saveSuggestion;
    private EditText description;

    private Suggestion suggestion;

    public Main_suggestion_creator() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mainsuggestioncreator, container, false);

        saveSuggestion = view.findViewById(R.id.save_suggestion);
        description = view.findViewById(R.id.suggestion_description);
        currentUserID = getArguments().getString("user");
        initID = getArguments().getString("init");
        suggestion = new Suggestion();

        Log.d("checker init", initID);
        //mAuth.getCurrentUser().getUid();

        //Database
        mRef = FirebaseDatabase.getInstance().getReference();
        mRefSug = FirebaseDatabase.getInstance().getReference("Suggestions");
        mRefInit = FirebaseDatabase.getInstance().getReference("Initiatives");
        mRefActionInit = FirebaseDatabase.getInstance().getReference().child("Action").child("Initiatives");

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        saveSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("checker12", description.getText().toString());

                if (description != null) {

                    saveSug(currentUserID,description.getText().toString(),initID);
                } else {
                    Toast.makeText(getActivity(), "Empty description", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;

    }


    private void saveSug(final String currentUserID, String description, final String initID) {
        //define properties to be saved in variables
        //mAuth = FirebaseAuth.getInstance();

        //set owner and description
        Log.d("checker1", description);
        suggestion.setText(description);
        suggestion.setOwner(currentUserID);

        //push of new suggestion relative to that initiative
        final String key = mRefSug.push().getKey();

        //save Issue properties
        mRefSug.child(key).setValue(suggestion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        mRefInit.child(initID).child("Suggestions").child(key).setValue(true);

                        //saves suggestionID instead of userID because 1 user might have multiple suggestions fro that initiative
                        //meaning: it is not unique (like userID is for voting)
                        mRefActionInit.child(initID).child(key).setValue("suggest");

                        Toast.makeText(getActivity(), "Success to upload data", Toast.LENGTH_SHORT).show();

                        //back to initiative activity
                        getActivity().onBackPressed();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(getActivity(), "Failed to upload data", Toast.LENGTH_SHORT).show();
                    }
                });

        //TODO create eliminate button in the suggestion

    }

}


