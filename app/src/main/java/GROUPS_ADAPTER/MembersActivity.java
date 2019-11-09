package GROUPS_ADAPTER;

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
import java.util.HashMap;

import INITIATIVES_ADAPTER.ViewHolder;
import MAIN_CLASSES.User;

public class MembersActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private Button save_members;
    private Button back_button;
    DatabaseReference mRefMembers;
    FirebaseDatabase mFirebaseDatabase;
    HashMap<String, Boolean> members;
    private ArrayList<String> userskey;
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegateslist);

        members = new HashMap<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mRefMembers = mFirebaseDatabase.getReference("Users");
        mRecyclerView = findViewById(R.id.recyclerdelegates);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save_members = findViewById(R.id.select_members);
        save_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save selected members to database
                Log.d("checker members before saving",members.toString());
                sendDataToPrevPg(members);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            Log.d("checkerreadstorage", " permission");
        }
        //Query personsQuery = mRef.orderByKey();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(mRefMembers, User.class)
                        .build();

        FirebaseRecyclerAdapter<User, ViewHolder> fra = new FirebaseRecyclerAdapter<User, INITIATIVES_ADAPTER.ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final INITIATIVES_ADAPTER.ViewHolder holder, final int position, @NonNull final User model) {

                holder.setUser(getApplicationContext(), model.getEmail()); //, model.getImage());
                holder.mView.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);
                holder.userID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        model.setSelected(!model.isSelected());
                        holder.mView.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);
                        Log.d("checker arrays", members.toString());

                        if (!members.containsKey(getRef(position).getKey())) {
                            Log.d("checker","added");
                            members.put(getRef(position).getKey(), true);
                        } else if (members.containsKey(getRef(position).getKey())) {
                            Log.d("checker","removed");
                            members.remove(getRef(position).getKey());
                        }

                        if (members.isEmpty()){
                            save_members.setVisibility(View.INVISIBLE);
                            save_members.setEnabled(false);
                        }else{
                            save_members.setVisibility(View.VISIBLE);
                            save_members.setEnabled(true);
                        }
                    }
                });
                //TODO set only users that belong to the group

            }

            @NonNull
            @Override
            public INITIATIVES_ADAPTER.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_row, parent, false);

                return new INITIATIVES_ADAPTER.ViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);

    }

    private void sendDataToPrevPg(HashMap<String, Boolean> hm) {
        // Send to previous activity page

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("membersList", hm);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your
                    Toast.makeText(MembersActivity.this, "GET_ACCOUNTS ALLOWED!!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MembersActivity.this, "GET_ACCOUNTS Denied",
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
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
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



