package GROUPS_ADAPTER;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.daniel.liquid_democracy.FirebaseHelper;

import INITIATIVES_ADAPTER.DelegatesListActivity;
import MAIN_CLASSES.Group;
import okhttp3.internal.Util;

import com.example.daniel.liquid_democracy.Main_issue_creator;
import com.example.daniel.liquid_democracy.R;
import com.example.daniel.liquid_democracy.SearchActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.storage.UploadTask;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.provider.MediaStore.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsActivity extends AppCompatActivity {
    StorageReference storageRef, imagesRef;
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    Button crtGroup;
    TextInputEditText nameTxt;
    ImageView groupImg;
    Button saveBtn;
    private Toolbar mToolbar;
    private final int PICK_IMAGE_REQUEST = 234;
    private final int PICK_MEMBERS_REQUEST = 1004;
    DatabaseReference db;
    Boolean saved;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final int MY_REQUEST = 1002;

    private Query searchedRef;
    private Uri filePathUri;
    Bitmap bitmap;
    FirebaseAuth mAuth;
    String currentGroupID, currentUserID;
    HashMap<String, Boolean> members;
    Group g = new Group();
    int row_index = -1; //default no row chosen

    Intent intent;
    private int code;
    private MaterialSearchView searchView;
    private Button invitemembers;
    private EditText groupdescription;
    private List<Group> groups, queriedgroups;
    private String name;
    private String grpdescription;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postslist);

        members = new HashMap();
        mAuth = FirebaseAuth.getInstance();

        storageRef = FirebaseStorage.getInstance().getReference();
        imagesRef = storageRef.child("images/groups");
        //StorageReference filepath = imagesRef.child()

        /*mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("GROUPS");*/

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("GROUPS");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        searchView = findViewById(R.id.search_view);
        mRecyclerView = findViewById(R.id.recyclergroups);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TODO read groups images
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Groups");

        crtGroup = findViewById(R.id.create_group);

        intent = getIntent();
        Bundle b = intent.getExtras();

        if (b.get("userID")!= null) {
            code = (int) b.get("request_Code");
            Log.d("checker code", String.valueOf(code));
            currentUserID = (String) b.get("userID"); //mAuth.getCurrentUser().getUid();

            if (Util.equal(code, "1001")) { //Groups accessed from issue creator
                crtGroup.setVisibility(View.INVISIBLE);
                crtGroup.setEnabled(false);
            } else { //Groups accessed from main layout
                crtGroup.setVisibility(View.VISIBLE);
                crtGroup.setEnabled(true);
                if (Util.equal(currentUserID, null)) {
                    currentUserID = "fodte";
                }

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            Log.d("checkerreadstorage", " permission");
        }
        FirebaseRecyclerOptions<Group> options =
                new FirebaseRecyclerOptions.Builder<Group>()
                        .setQuery(mRef, Group.class)
                        .build();

        FirebaseRecyclerAdapter<Group, ViewHolder> fra = new FirebaseRecyclerAdapter<Group, ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Group model) {

                Log.d("checker", "entrou");
                holder.setDetails(getApplicationContext(), model.getName(), model.getImage());
                groups = new ArrayList<>();

                //add models (that have appeared on screen) to listarray
                groups.add(model);

                //Select only one item from groups by clicking (for issue creation)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        row_index = position;

                        if (row_index == position) {

                            if (Util.equal(code, 1001)) { //Groups accessed from issue creator
                                Intent intent = new Intent();
                                ///Intent intent = new Intent(GroupsActivity.this,Main_issue_creator.class);
                                //url for group image
                                intent.putExtra("url", groups.get(row_index).getImage());

                                intent.putExtra("id", getRef(row_index).getKey());
                                setResult(RESULT_OK, intent);

                                //notifyDataSetChanged();
                                finish();
                            } else {

                                Intent intent = new Intent(GroupsActivity.this, SingleGroupActivity.class);
                                Log.d("checker row index", String.valueOf(row_index));

                                intent.putExtra("groupID", getRef(row_index).getKey());
                                intent.putExtra("userID", currentUserID);

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivityForResult(intent, MY_REQUEST);
                            }
                        }
                    }

                    /*@Override
                    public void onClick(View view, int position) {
                        row_index=position;
                        Log.d("checkernewrow", String.valueOf(row_index));
                        // save selected model position to Common
                        Common.currentItem = groups.get(row_index);
                    }*/

                });
                holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.group_row, parent, false);

                return new ViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);

        /*searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Log.d("checker", " search view shown");

            }

            @Override
            public void onSearchViewClosed() {
                Log.d("checker", " search view closed");


            }
        });*/

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("checker", " query text changed");

                if (newText != null && !newText.isEmpty()) {

                    //List<String> lstFound = new ArrayList<String>();
                    List<String> searched_groups = new ArrayList<>();

                    for (Group element : groups) {
                        if (element.getName().contains(newText)) {
                            Log.d("checker text written", element.getName());
                            searched_groups.add(element.getName());
                        }
                    }

                    searchedRef = mRef.orderByChild("name").startAt(newText).endAt(newText + "\uf8ff");

                    //orderByKey();

                    FirebaseRecyclerOptions<Group> options =
                            new FirebaseRecyclerOptions.Builder<Group>()
                                    .setQuery(searchedRef, Group.class)
                                    .build();

                    FirebaseRecyclerAdapter<Group, ViewHolder> fra = new FirebaseRecyclerAdapter<Group, ViewHolder>(
                            options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Group model) {

                            holder.setDetails(getApplicationContext(), model.getName(), model.getImage());
                            queriedgroups = new ArrayList<>();

                            //add models (that have appeared on screen) to listarray
                            queriedgroups.add(model);

                            //Select only one item from groups by clicking (for issue creation)
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    row_index = position;

                                    if (row_index == position) {

                                        if (Util.equal(code, "1001")) {
                                            Intent intent = new Intent();
                                            ///Intent intent = new Intent(GroupsActivity.this,Main_issue_creator.class);
                                            //url for group image
                                            intent.putExtra("url", queriedgroups.get(row_index).getImage());

                                            intent.putExtra("id", getRef(row_index).getKey());
                                            setResult(RESULT_OK, intent);

                                            //notifyDataSetChanged();
                                            finish();
                                        } else {
                                            //TODO if member, else
                                            Intent intent = new Intent(GroupsActivity.this, SingleGroupActivity.class);
                                            Log.d("checker row index", String.valueOf(row_index));

                                            intent.putExtra("groupID", getRef(row_index).getKey());
                                            intent.putExtra("userID", currentUserID);


                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivityForResult(intent, MY_REQUEST);
                                        }
                                    }
                                }
                            });
                            holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));
                        }

                        @NonNull
                        @Override
                        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.group_row, parent, false);

                            return new ViewHolder(view);
                        }
                    };
                    fra.startListening();
                    mRecyclerView.setAdapter(fra);

                } else {
                    Log.d("checker", " else");

                    FirebaseRecyclerOptions<Group> options =
                            new FirebaseRecyclerOptions.Builder<Group>()
                                    .setQuery(mRef, Group.class)
                                    .build();

                    FirebaseRecyclerAdapter<Group, ViewHolder> fra = new FirebaseRecyclerAdapter<Group, ViewHolder>(
                            options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Group model) {

                            holder.setDetails(getApplicationContext(), model.getName(), model.getImage());
                            groups = new ArrayList<>();

                            //add models (that have appeared on screen) to listarray
                            groups.add(model);

                            //Select only one item from groups by clicking (for issue creation)
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    row_index = position;

                                    if (row_index == position) {
                                        if (Util.equal(code, "1001")) { //Groups accessed from issue creator
                                            Intent intent = new Intent();
                                            ///Intent intent = new Intent(GroupsActivity.this,Main_issue_creator.class);
                                            //url for group image
                                            intent.putExtra("url", groups.get(row_index).getImage());

                                            intent.putExtra("id", getRef(row_index).getKey());
                                            setResult(RESULT_OK, intent);

                                            //notifyDataSetChanged();
                                            finish();
                                        } else {

                                            //TODO if member, else
                                            Intent intent = new Intent(GroupsActivity.this, SingleGroupActivity.class);

                                            Log.d("checker row index", String.valueOf(row_index));
                                            intent.putExtra("groupID", getRef(row_index).getKey());
                                            intent.putExtra("userID", currentUserID);

                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivityForResult(intent, MY_REQUEST);
                                        }
                                    }
                                }

                    /*@Override
                    public void onClick(View view, int position) {
                        row_index=position;
                        Log.d("checkernewrow", String.valueOf(row_index));
                        // save selected model position to Common
                        Common.currentItem = groups.get(row_index);
                    }*/

                            });
                            holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));
                        }

                        @NonNull
                        @Override
                        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.group_row, parent, false);

                            return new ViewHolder(view);
                        }
                    };
                    fra.startListening();
                    mRecyclerView.setAdapter(fra);
                }
                return true;
            }
        });

        crtGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNewGroup();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;

    }


    private void displayNewGroup() {
        final Dialog d = new Dialog(this);
        d.setTitle("Save to Firebase");
        d.setContentView(R.layout.input_dialog);
        //helper = new FirebaseHelper(mRef);

        saveBtn = d.findViewById(R.id.saveBtn);
        nameTxt = d.findViewById(R.id.nameTxt);
        groupImg = d.findViewById(R.id.groupimage);
        invitemembers = d.findViewById(R.id.invitemembers);
        groupdescription = d.findViewById(R.id.group_description);

        invitemembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupsActivity.this, MembersActivity.class);
                startActivityForResult(intent, PICK_MEMBERS_REQUEST);
            }
        });

        groupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO verify if description is being saved in groups firebase
                //TODO invite proposed members and wait for their confirmal to have them as new members in the groups

                if (members != null && name != null) {

                    Log.d("checker", "will save");
                    ///upload to firebase database
                    if (saveGroups(g)) {

                        //look for recently added group name
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.d("checker correct group?", currentGroupID);

                                //upload image to storage
                                if (filePathUri != null && currentGroupID != null) {
                                    final ProgressDialog pd = new ProgressDialog(GroupsActivity.this);
                                    pd.setTitle("uploading.....");
                                    pd.show();

                                    StorageReference filepath = imagesRef.child(currentGroupID + ".jpg");
                                    filepath.putFile(filePathUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                //Repoe backgrounds originais
                                                nameTxt.setText("");
                                                groupImg.setImageResource(R.drawable.ic_add_circle_black_24dp);

                                                pd.dismiss();
                                                Toast.makeText(GroupsActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                                d.dismiss();
                                            } else {
                                                String message = task.getException().toString();
                                                Toast.makeText(GroupsActivity.this, message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("checkernew", "nothing");
                            }
                        });

                        Log.d("checker group ID after storage", currentGroupID + ".jpg");

                        //TODO NetworkRequest: no auth token for request
                        //get path from new uploaded image (in storage) and save it in database
                        imagesRef.child(currentGroupID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                Log.d("checkernew6", uri.toString());
                                Uri downloadUri = uri;
                                String generatedFilePath = downloadUri.toString();
                                mRef.child(currentGroupID).child("image").setValue(generatedFilePath);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Toast.makeText(GroupsActivity.this, "Couldn't upload image successfully", Toast.LENGTH_SHORT).show();

                            }
                        });
                        //adapter = new ListAdapter(PostsListActivity.this ,R.layout.activity_listitems,helper.retrieve());
                        // lv.setAdapter(adapter);
                    }
                }
            }
        });

        d.show();
    }

    //select image from gallery to represent new group created
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    public Boolean saveGroups(Group groupsActivity)
    {
        if (groupsActivity==null){
            saved=false;
        }else{
            try{
                currentGroupID = mRef.push().getKey();
                Log.d("checker", currentGroupID);

                mRef.child(currentGroupID).setValue(groupsActivity);
                saved=true;
                Log.d("checker after set value of groups", saved.toString());

            }catch (DatabaseException e){
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePathUri = data.getData();

            try {
                //visualization of new groups image in UI
                bitmap = Images.Media.getBitmap(getContentResolver(), filePathUri);
                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                groupImg.setBackground(ob);
                invitemembers.setEnabled(true);
                invitemembers.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_MEMBERS_REQUEST && resultCode == RESULT_OK && data.hasExtra("membersList")) {

            //Bundle wrapper = getIntent().getBundleExtra("membersList");
            members = (HashMap<String, Boolean>) data.getSerializableExtra("membersList");
            saveBtn.setVisibility(View.VISIBLE);
            saveBtn.setEnabled(true);

            //define group
            name = nameTxt.getText().toString();
            grpdescription = groupdescription.getText().toString();

            //Temporary nomenclature
            String image = "temp";

            g.setImage(image);
            g.setName(name);
            g.setDescription(grpdescription);

            //add current user that started group to the group of members
            //Log.d("checker groups", members.toString());
            members.put(currentUserID,true);
            Log.d("checker groups after user", members.toString());
            g.setMembers(members);
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
            groupImg.setBackground(ob);

            //get uri
            final FileOutputStream fos;
            try {
                fos = openFileOutput("my_new_image.jpg", Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            } catch (FileNotFoundException e) {
                Log.d("checkernew8", "fodeu");
                e.printStackTrace();
            }
            //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            String path = Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "Title", null);

            Log.d("checkernew5", "vistop" + path);

            filePathUri = Uri.parse(path);

            //enable user to save new group
            saveBtn.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your
                    Toast.makeText(GroupsActivity.this, "GET_ACCOUNTS ALLOWED!!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GroupsActivity.this, "GET_ACCOUNTS Denied",
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
