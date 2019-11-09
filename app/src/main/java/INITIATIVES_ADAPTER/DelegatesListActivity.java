package INITIATIVES_ADAPTER;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.daniel.liquid_democracy.FirebaseHelper;
import com.example.daniel.liquid_democracy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import MAIN_CLASSES.User;


public class DelegatesListActivity extends AppCompatActivity {

    FirebaseHelper helper;
    StorageReference storageRef, imagesRef;
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRefUsers_ofgroup;

    ImageView groupImg;
    private Toolbar mToolbar;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private Uri filePathUri;
    Bitmap bitmap;
    FirebaseAuth mAuth;
    User g = new User();
    int row_index = -1; //default no row chosen
    List<String> delegates = new ArrayList<>();
    Intent intent;
    String issueID,groupID;
    ArrayList<String> issuesname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegateslist);

        mAuth = FirebaseAuth.getInstance();
        //currentUserID = "fodte"; //mAuth.getCurrentUser().getUid();

        storageRef = FirebaseStorage.getInstance().getReference();
        //imagesRef = storageRef.child("images/groups");
        //StorageReference filepath = imagesRef.child()

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("DELEGATES");

        mRecyclerView = findViewById(R.id.recyclerdelegates);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        intent = getIntent();
        //initID = intent.getStringExtra("ID");
        Bundle b = intent.getExtras();

        groupID = (String) b.get("groupID");
        Log.d("checker group", groupID);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefUsers_ofgroup = mFirebaseDatabase.getReference().child("Groups").child(groupID).child("members");

        //TODO read user profile image and username (so far only has email)

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            Log.d("checkerreadstorage", " permission");
        }

        FirebaseRecyclerOptions<Boolean> options =
                new FirebaseRecyclerOptions.Builder<Boolean>()
                        .setQuery(mRefUsers_ofgroup, Boolean.class)
                        .build();

        FirebaseRecyclerAdapter<Boolean, INITIATIVES_ADAPTER.ViewHolder> fra = new FirebaseRecyclerAdapter<Boolean, INITIATIVES_ADAPTER.ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull INITIATIVES_ADAPTER.ViewHolder holder, final int position, @NonNull Boolean model) {

                String user_tobechecked = getRef(position).getKey() ;

                Log.d("checker boolean", user_tobechecked);
                holder.setUser(getApplicationContext(),user_tobechecked); //, model.getImage());

                delegates.add(user_tobechecked);

                //Select only one item from groups by clicking (for issue creation)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        row_index = position;

                        if (row_index == position) {
                            Intent intent = new Intent();
                            ///Intent intent = new Intent(GroupsActivity.this,Main_issue_creator.class);
                            //url for group image
                            intent.putExtra("delegateID", delegates.get(row_index).toString());
                            setResult(RESULT_OK, intent);

                            //notifyDataSetChanged();
                            finish();
                        }
                    }

                });
                holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your
                    Toast.makeText(DelegatesListActivity.this, "GET_ACCOUNTS ALLOWED!!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DelegatesListActivity.this, "GET_ACCOUNTS Denied",
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
