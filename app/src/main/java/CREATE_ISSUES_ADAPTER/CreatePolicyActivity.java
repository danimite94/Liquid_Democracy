package CREATE_ISSUES_ADAPTER;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.daniel.liquid_democracy.R;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import MAIN_CLASSES.Policy;
import okhttp3.internal.Util;

public class CreatePolicyActivity extends AppCompatActivity {

    private NumberPicker np_quorum_admission, np_quorum_verification, np_admission_time, np_discussion_time;
    private int admission_time;
    private int discussion_time;
    private int verification_time;
    private int voting_time;

    private int quorum_admission;
    private int quorum_verification;

    private EditText policyname;
    private Button saveBtn;
    DatabaseReference mRefPolicy;
    FirebaseDatabase mFirebaseDatabase;

    private Boolean saved;
    private Policy p;
    private String currentPolicyID, policy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpolicy);


        p= new Policy();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefPolicy = mFirebaseDatabase.getReference("Policies");

        np_quorum_admission = findViewById(R.id.quorum_admission);
        np_quorum_verification = findViewById(R.id.quorum_verification);
        np_admission_time = findViewById(R.id.admission_time);
        np_discussion_time = findViewById(R.id.discussion_time);

        //set values to number pickers
        np_quorum_admission.setMaxValue(100);
        np_quorum_admission.setMinValue(0);
        np_quorum_verification.setMaxValue(100);
        np_quorum_verification.setMinValue(0);
        np_admission_time.setMinValue(0);
        np_admission_time.setMaxValue(100);
        np_discussion_time.setMinValue(0);
        np_discussion_time.setMaxValue(100);

        //set wrap selector wheel
        np_quorum_admission.setWrapSelectorWheel(true);
        np_quorum_verification.setWrapSelectorWheel(true);
        np_admission_time.setWrapSelectorWheel(true);
        np_discussion_time.setWrapSelectorWheel(true);

        //set listeners
        np_quorum_admission.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // do your other stuff depends on the new value
                quorum_admission = newVal;
            }
        });

        np_quorum_verification.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // do your other stuff depends on the new value
                quorum_verification = newVal;
            }
        });

        np_admission_time.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // do your other stuff depends on the new value
                admission_time = newVal;
            }
        });

        np_discussion_time.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // do your other stuff depends on the new value
                discussion_time = newVal;
            }
        });

        policyname = findViewById(R.id.nameTxt);
        policyname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                policy = s.toString();
            }
        });

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("checker policy conditions", quorum_admission + "" + quorum_verification  + "" + discussion_time + "" +admission_time + "" +  policy );
                if (quorum_admission != 0 && quorum_verification != 0 && admission_time != 0 && discussion_time != 0 && !Util.equal(policy,null)) {

                    voting_time = 1;
                    verification_time = 1;

                    p.setAdmission_time(admission_time);
                    p.setDiscussion_time(discussion_time);
                    p.setQuorum_admission(quorum_admission);
                    p.setQuorum_verification(quorum_verification);
                    p.setVerification_time(verification_time);
                    p.setVoting_time(voting_time);
                    p.setName(policy);

                    if (savePolicies(p)) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    Toast.makeText(CreatePolicyActivity.this, "Policy Parameters cannot be null",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public Boolean savePolicies(Policy policy)
    {
        if (policy==null){
            saved=false;
        }else{
            try{
                currentPolicyID = mRefPolicy.push().getKey();
                Log.d("checker", currentPolicyID);

                mRefPolicy.child(currentPolicyID).setValue(policy);
                saved=true;
                Log.d("checker after set value of groups", saved.toString());

            }catch (DatabaseException e){
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }

}
