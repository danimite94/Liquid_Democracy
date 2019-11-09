package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import GROUPS_ADAPTER.GroupsActivity;
import GROUPS_ADAPTER.SingleGroupActivity;
import INITIATIVES_ADAPTER.IssueActivity;
import MAIN_ADAPTER.FeedActivity;
import POLICIES_ADAPTER.ViewHolderPolicy;
import GROUPS_ADAPTER.ViewHolder;

import MAIN_CLASSES.Group;
import MAIN_CLASSES.Item;
import MAIN_CLASSES.Policy;
import okhttp3.internal.Util;

import com.example.daniel.liquid_democracy.Main_issue_creator;
import com.example.daniel.liquid_democracy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MY_REQUEST = 1001;
    private String userID;
    Calendar calendar = Calendar.getInstance();
    public static List<Item> items;
    Context context;
    public String url, name, currentDate;
    private Intent intent;
    private int minCharsIssue = 10;
    private String policy;
    Button nextBtn;
    RecyclerView.LayoutManager layoutManager;

    public Adapter(List<Item> items, Button nextBtn, RecyclerView.LayoutManager layoutManager) {
        this.items = items;
        this.nextBtn = nextBtn;
        this.layoutManager = layoutManager;
    }

    class ViewHolderItem0 extends RecyclerView.ViewHolder {

        EditText description;

        Button botao;

        public ViewHolderItem0(View itemView) {
            super(itemView);
            this.description = itemView.findViewById(R.id.inittext);
            botao = nextBtn;

            botao.setVisibility(View.INVISIBLE);
            botao.setEnabled(false);
            botao.setOnClickListener(v -> {
                botao.setVisibility(View.INVISIBLE);
                botao.setEnabled(false);
                layoutManager.scrollToPosition(1);

            });

            Log.d("checker char", "verify1");

            description.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    items.get(getAdapterPosition()).setDescription(s.toString());
                    Main_issue_creator.firstinit.setDescription(s.toString());
                    Log.d("checker char", Main_issue_creator.firstinit.getDescription().toString());
                    if (s.toString().length()>minCharsIssue){
                        botao.setVisibility(View.VISIBLE);
                        botao.setEnabled(true);

                    }else{
                        botao.setVisibility(View.INVISIBLE);
                        botao.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    class ViewHolderSummary extends RecyclerView.ViewHolder {

        TextView groupTxt;
        TextView policyTxt;
        TextView descriptionTxt;
        DatabaseReference mRefIssue, mRefInit;
        Calendar calendar;

        public ViewHolderSummary(View itemView) {
            super(itemView);
            groupTxt = itemView.findViewById(R.id.groupTxt);
            policyTxt = itemView.findViewById(R.id.policyTxt);
            descriptionTxt = itemView.findViewById(R.id.inittext);

            policyTxt.setText(Main_issue_creator.issuetobesaved.getPolicies());
            groupTxt.setText(Main_issue_creator.issuetobesaved.getGroups());
            descriptionTxt.setText(Main_issue_creator.firstinit.getDescription());

            nextBtn.setText("ADD");
            nextBtn.setVisibility(View.VISIBLE);
            nextBtn.setEnabled(true);
            Log.d("checker verify", "NAO PASSOU AQUI FODAX!");

            mRefIssue = FirebaseDatabase.getInstance().getReference("Issues");
            mRefInit = FirebaseDatabase.getInstance().getReference("Initiatives");

            nextBtn.setOnClickListener(v -> {

                //get current time
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH");
                // Output "Wed Sep 26 14:23:28 EST 2012"
                currentDate = format1.format(calendar.getTime());

                //define init
                Main_issue_creator.firstinit.setCurrentDate(currentDate);

                //define issue
                Main_issue_creator.issuetobesaved.setCurrentDate(currentDate);
                Main_issue_creator.issuetobesaved.setStatus("admission");

                //push of new initiative relative to that issue and corresponding issue
                final String key = mRefIssue.push().getKey();

                final HashMap<String, Boolean> initid = new HashMap<>();

                //save Issue properties
                mRefIssue.child(key).setValue(Main_issue_creator.issuetobesaved)
                        .addOnSuccessListener(aVoid -> {
                            // Write was successful!

                            //get key for next push
                            String keyinit = mRefIssue.child(key).child("Initiatives").push().getKey();
                            initid.put(keyinit, true);
                            //push new initiative
                            mRefIssue.child(key).child("Initiatives").setValue(initid);

                            //save Initiative properties
                            mRefInit.child(keyinit).setValue(Main_issue_creator.firstinit).addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getApplicationContext(), "Success to create issue", Toast.LENGTH_SHORT).show();

                                Intent returnIntent = new Intent();
                                ((Main_issue_creator) context).setResult(Activity.RESULT_OK,returnIntent);
                                ((Main_issue_creator) context).finish();

                            }).addOnFailureListener(e -> {
                                // Write failed
                                Toast.makeText(context, "Failed to create new issue", Toast.LENGTH_SHORT).show();
                            });

                        })
                        .addOnFailureListener(e -> {
                            // Write failed
                            Toast.makeText(context, "Failed to create new issue", Toast.LENGTH_SHORT).show();
                        });
            });
        }
    }

    class ViewHolderPolicies extends RecyclerView.ViewHolder {
        RecyclerView mRecyclerView;
        FirebaseDatabase mFirebaseDatabase;
        DatabaseReference mRef;
        int row_index = -1; //default no row chosen
        Button botao;

        public ViewHolderPolicies(View itemView) {
            super(itemView);

            mRecyclerView = itemView.findViewById(R.id.recyclerpolicies);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

            botao = nextBtn;
            botao.setVisibility(View.INVISIBLE);
            botao.setEnabled(false);


            //TODO read groups images
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mRef = mFirebaseDatabase.getReference("Policies");
            Log.d("checker char", "verify3");

            FirebaseRecyclerOptions<Policy> options =
                    new FirebaseRecyclerOptions.Builder<Policy>()
                            .setQuery(mRef, Policy.class)
                            .build();

            FirebaseRecyclerAdapter<Policy, ViewHolderPolicy> fra = new FirebaseRecyclerAdapter<Policy, ViewHolderPolicy>(
                    options) {
                @Override
                protected void onBindViewHolder(@NonNull ViewHolderPolicy holder, final int position, @NonNull Policy model) {

                    Log.d("checker", "entrou");
                    botao.setOnClickListener(v ->{
                        Main_issue_creator.issuetobesaved.setPolicies(policy);
                        if (policy.equals("Long-term")){

                        }else if( policy.equals("Mid-term") ){

                        }else if(policy.equals("Short-term")){

                        }
                        layoutManager.scrollToPosition(3);
                    });

                    holder.setDetails(context, model.getName(), model.getDescription(), model.getImage());

                    //Select only one item from groups by clicking (for issue creation)
                    holder.itemView.setOnClickListener(v -> {

                        if (row_index == position) {
                            botao.setVisibility(View.INVISIBLE);
                            botao.setEnabled(false);
                            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            row_index = -1;
                        }else{
                            policy = getRef(position).getKey();
                            Log.d("CHECKER policy", policy);
                            botao.setVisibility(View.VISIBLE);
                            botao.setEnabled(true);
                            holder.itemView.setBackgroundColor(Color.parseColor("#FFB266"));
                            row_index = position;
                        }
                    });
                }

                @NonNull
                @Override
                public ViewHolderPolicy onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.policy_row, parent, false);

                    return new ViewHolderPolicy(view);
                }
            };
            fra.startListening();
            mRecyclerView.setAdapter(fra);
        }
    }

    class ViewHolderGroup extends RecyclerView.ViewHolder {
        RecyclerView mRecyclerView;
        FirebaseDatabase mFirebaseDatabase;
        DatabaseReference mRef;
        int row_index = -1; //default no row chosen
        Button botao;
        String groups;
        public ViewHolderGroup(View itemView) {
            super(itemView);

            mRecyclerView = itemView.findViewById(R.id.recyclergroups);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            Log.d("checker char", "verify2");

            botao = nextBtn;
            botao.setVisibility(View.INVISIBLE);
            botao.setEnabled(false);

            Log.d("checker char", "verify2.1");

            //TODO read groups images
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mRef = mFirebaseDatabase.getReference("Groups");

            FirebaseRecyclerOptions<Group> options =
                    new FirebaseRecyclerOptions.Builder<Group>()
                            .setQuery(mRef, Group.class)
                            .build();

            FirebaseRecyclerAdapter<Group, ViewHolder> fra = new FirebaseRecyclerAdapter<Group, ViewHolder>(
                    options) {
                @Override
                protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Group model) {

                    Log.d("checker group name ", "aqui");
                    botao.setOnClickListener(v -> {
                        groups = name;
                        Main_issue_creator.issuetobesaved.setGroups(groups);
                        layoutManager.scrollToPosition(2);
                    });


                    holder.setDetails(context, model.getName(), model.getImage());

                    //Select only one item from groups by clicking (for issue creation)
                    holder.itemView.setOnClickListener(v -> {

                        if (row_index == position) {
                            botao.setVisibility(View.INVISIBLE);
                            botao.setEnabled(false);
                            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            row_index = -1;
                        }else{
                            name = getRef(position).getKey();
                            Log.d("checker name", name);
                            botao.setVisibility(View.VISIBLE);
                            botao.setEnabled(true);
                            holder.itemView.setBackgroundColor(Color.parseColor("#FFB266"));
                            row_index = position;
                        }
                    });
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));

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
    }

    @Override
    public int getItemViewType(int positioni) {
        int value = 4;
        if (positioni == 0) {
            value = 0;
        } else if (positioni == 1) {
            value = 1;
        } else if (positioni == 2) {
            value = 2;
        } else if (positioni == 3) {
            value = 3;
        }
        Log.d("checker position", String.valueOf(value));
        return value;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        switch (viewType) {
            case 0: {
                Log.d("CHECKER", "passou0");
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.item0_layout, parent, false);
                return new ViewHolderItem0(view);
            }

            case 1: {
                Log.d("CHECKER", "passou1");
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.item2_layout, parent, false);
                return new ViewHolderGroup(view);
            }
            case 2: {
                Log.d("CHECKER", "passou2");
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.item1_layout, parent, false);
                return new ViewHolderPolicies(view);
            }
            case 3: {
                Log.d("CHECKER", "passou3");
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.create_issue_summary, parent, false);
                return new ViewHolderSummary(view);
            }
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case 0: {
                Log.d("CHECKER transicao", "passou0");

                //ViewHolderItem0 viewHolder = (ViewHolderItem0) holder;
                //viewHolder.setIsRecyclable(false);
                //viewHolder.description.setText(Main_issue_creator.firstinit.getDescription());
                //viewHolder.description.setText(items.get(position).getDescription());

                //save properties for firebase
                //Log.d("checker item description", String.valueOf(items.get(position).getDescription()));
                //viewHolder.setIsRecyclable(false);
                break;

            }

            case 1: {
                Log.d("CHECKER transicao", "passou1");

                //ViewHolderGroup viewHolder0 = (ViewHolderGroup) holder;
                //viewHolder0.setIsRecyclable(false);
                /*nextBtn.setOnClickListener(v -> {
                    if (name != null) {

                        //Picasso.get().load(url).placeholder(R.drawable.loading).resize(150, 150).centerCrop().into(viewHolder2.addGroup);

                        HashMap<String, Boolean> groups = new HashMap<>();
                        groups.put(name, true);
                        Main_issue_creator.issuetobesaved.setGroups(groups);

                        Log.d("checker quorum", String.valueOf(Main_issue_creator.issuetobesaved.getGroups()));

                        layoutManager.scrollToPosition(2);
                    }
                });*/
                break;

            }

            case 2: {
                Log.d("CHECKER transicao", "passou2");
                //ViewHolderPolicies viewHolder1 = (ViewHolderPolicies) holder;
                //viewHolder1.setIsRecyclable(false);
                /*nextBtn.setOnClickListener(v -> {
                    Main_issue_creator.issuetobesaved.setPolicies(policy);
                    layoutManager.scrollToPosition(3);

                });*/
                //save properties for firebase
               /* Main_issue_creator.issuetobesaved.setAdmission_time(item.getAdmission());
                Main_issue_creator.issuetobesaved.setDiscussion_time(item.getDiscussion());
                Main_issue_creator.issuetobesaved.setVerification_time(item.getVerification());
                Main_issue_creator.issuetobesaved.setVoting_time(item.getVoting());*/
                //viewHolder1.setIsRecyclable(false);

                //Log.d("checker admission", String.valueOf(Main_issue_creator.issuetobesaved.getAdmission_time()));
                break;

            }

            case 3: {
                Log.d("CHECKER transicao", "passou3");

                //ViewHolderSummary viewHolder = (ViewHolderSummary) holder;
                //viewHolder.setIsRecyclable(false);
                    /*nextBtn.setOnClickListener(v -> {

                        DatabaseReference mRefIssue, mRefInit;

                        mRefIssue = FirebaseDatabase.getInstance().getReference("Issues");
                        mRefInit = FirebaseDatabase.getInstance().getReference("Initiatives");

                        //push of new initiative relative to that issue and corresponding issue
                        final String key = mRefIssue.push().getKey();

                        final HashMap<String, Boolean> initid = new HashMap<>();

                        //save Issue properties
                        mRefIssue.child(key).setValue(Main_issue_creator.issuetobesaved)
                                .addOnSuccessListener(aVoid -> {
                                    // Write was successful!
                                    Toast.makeText(getApplicationContext(), "Success to upload data", Toast.LENGTH_SHORT).show();

                                    //get key for next push
                                    String keyinit = mRefIssue.child(key).child("Initiatives").push().getKey();
                                    initid.put(keyinit, true);
                                    //push new initiative
                                    mRefIssue.child(key).child("Initiatives").setValue(initid);

                                    //save Initiative properties
                                    mRefInit.child(keyinit).setValue(Main_issue_creator.firstinit);

                                    Intent returnIntent = new Intent();
                                    ((Main_issue_creator) context).setResult(Activity.RESULT_OK,returnIntent);
                                    ((Main_issue_creator) context).finish();


                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Write failed
                                        Toast.makeText(context, "Failed to upload data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });*/
                break;

            }

        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ... Check for some data from the intent
        // .. lets toast again
        if (data != null) {
            url = data.getStringExtra("url");
            name = data.getStringExtra("id");
            Log.d("checker name", name);

        }

        if (url != null && name != null) {
            Toast.makeText(context, "Handled the result successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

        }


    }*/


}
