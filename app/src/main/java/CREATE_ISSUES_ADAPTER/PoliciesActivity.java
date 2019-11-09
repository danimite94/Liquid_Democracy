package CREATE_ISSUES_ADAPTER;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.daniel.liquid_democracy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import MAIN_CLASSES.Policy;
import okhttp3.internal.Util;

public class PoliciesActivity extends AppCompatActivity {

    private Button create_policy;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    RecyclerView mRecyclerView;
    DatabaseReference mRefPolicy;
    FirebaseDatabase mFirebaseDatabase;
    ArrayList<Policy> policies;

    private Intent intent;
    private int row_index,code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegateslist);

        //TODO generalize delegateslist activity xml
        create_policy = findViewById(R.id.select_members);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefPolicy = mFirebaseDatabase.getReference("Policies");

        mRecyclerView = findViewById(R.id.recyclerdelegates);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        intent = getIntent();
        Bundle b = intent.getExtras();

        if (b.get("request_Code") != null) { //debug purposes
            code = (int) b.get("request_Code"); //mAuth.getCurrentUser().getUid();
        } else {
            code = 1005;
        }

        //TODO recycler view com policies, onde é possivel escolher uma cell (como no groupsactivity)
        create_policy.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
        create_policy.setEnabled(true);
        create_policy.setVisibility(View.VISIBLE);
        create_policy.setWidth(77);
        create_policy.setHeight(77);
        create_policy.setText(null);

        create_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PoliciesActivity.this, CreatePolicyActivity.class);
                startActivityForResult(intent, 1006);

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            Log.d("checkerreadstorage", " permission");
        }
        FirebaseRecyclerOptions<Policy> options =
                new FirebaseRecyclerOptions.Builder<Policy>()
                        .setQuery(mRefPolicy, Policy.class)
                        .build();

        FirebaseRecyclerAdapter<Policy, ViewHolder> fra = new FirebaseRecyclerAdapter<Policy, ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Policy model) {

                Log.d("checker", model.getName());
                holder.setPolicy(getApplicationContext(), model.getName(), model.getAdmission_time(), model.getDiscussion_time(), model.getQuorum_admission(), model.getQuorum_verification());
                policies = new ArrayList<>();

                //add models (that have appeared on screen) to listarray
                policies.add(model);

                //Select only one item from groups by clicking (for issue creation)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        row_index = position;

                        if (row_index == position && Util.equal(code,1005)) {

                            Intent intent = new Intent();
                            Log.d("checker row index", String.valueOf(row_index));

                            intent.putExtra("id", getRef(row_index).getKey());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                });
                holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.policy_row, parent, false);

                return new ViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your
                    Toast.makeText(PoliciesActivity.this, "GET_ACCOUNTS ALLOWED!!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PoliciesActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }


    //check permissions to access storage
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }


}
