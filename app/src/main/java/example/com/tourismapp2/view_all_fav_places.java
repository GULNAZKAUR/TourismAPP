package example.com.tourismapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import example.com.tourismapp2.classpack.places_details;
import example.com.tourismapp2.classpack.user_added_fav_places_details;

import static android.content.Context.MODE_PRIVATE;

public class view_all_fav_places extends Fragment {

    ArrayList<user_added_fav_places_details> temp_places_id_array_list = new ArrayList<>();//contains all places ids which user added into favourities

    ArrayList<places_details> arrayList = new ArrayList<>();
    DatabaseReference fetch_saved_places_mainref,fetch_places ;
    RecyclerView rcv_managecategory_showcategory;
    MyRecyclerAdapter_Places myad_view = new MyRecyclerAdapter_Places();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v =  inflater.inflate(R.layout.fragment_view_all_fav_places, container, false);



       return  v;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreference=getActivity().getSharedPreferences("mypref",MODE_PRIVATE);
        String email = sharedPreference.getString("email","");

        getActivity().findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        getActivity().findViewById(R.id.logout6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreference=getActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreference.edit();
                long beforeTime=sharedPreference.getLong("time",0);
                long currenttime=new Date().getTime();
                long timeDiff=currenttime-beforeTime;
                SimpleDateFormat format=new SimpleDateFormat("hh-mm");
                String value= format.format(timeDiff);
                System.out.println(value);

                boolean task1,task2;
                task1=sharedPreference.getBoolean("TASK1",false);
                task2=sharedPreference.getBoolean("TASK2",false);
                boolean flag=false;
                if(task1)
                {
                    if(!task2)
                    {
                        Toast.makeText(getActivity(), "No Place added in PLanner", Toast.LENGTH_SHORT).show();


                    }
                    else
                    {
                        flag=true;
                    }

                }
                else {
                    Toast.makeText(getActivity(), "No Place addded to favourites", Toast.LENGTH_SHORT).show();

                }


                if(flag) {
                    editor.clear();
                    editor.apply();
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), Login_Signup.class);
                    startActivity(intent);
                }


            }
        });
//

        rcv_managecategory_showcategory=getActivity().findViewById(R.id.view_all_saved);



        rcv_managecategory_showcategory.setAdapter(myad_view);

        //Specifying Layout Manager to RecyclerView is Compulsary for Proper Rendering

        LinearLayoutManager simpleverticallayout= new LinearLayoutManager(getContext());
        rcv_managecategory_showcategory.setLayoutManager(simpleverticallayout);
        //
        
        fetch_saved_places_mainref  = FirebaseDatabase.getInstance().getReference("User_Added_Fav_Places");
        fetch_places  = FirebaseDatabase.getInstance().getReference("Places");

        fetch_saved_places_mainref.orderByChild("user_email_id").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                temp_places_id_array_list.clear();
                Log.d("Hello-msg",snapshot.toString());

                if(snapshot.exists()){ // adding the User's all fav places into arraylist "temp_places_id_list"
                    for (DataSnapshot sin : snapshot.getChildren()){
                        user_added_fav_places_details obj2 = sin.getValue(user_added_fav_places_details.class);
                        temp_places_id_array_list.add(obj2);
                    }
                    arrayList.clear();
                    fetch_places_details();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void fetch_places_details(){

        for (int i = 0 ; i < temp_places_id_array_list.size() ; i++){
            //running the for loop till the length of array list containing user's fav places.

            String place_id = temp_places_id_array_list.get(i).getPlaces_id(); // obtaining the ID of fav place from array list.
            fetch_places.child(place_id).addListenerForSingleValueEvent(new ValueEventListener() { //Matching the ID from arraylist with the corr. place ID in Place details tre.
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("Hello-msg",snapshot.toString());
                    if(snapshot.exists()){
                        places_details obj3 = snapshot.getValue(places_details.class);
                        arrayList.add(obj3); //adding the place's details into the arraylist from object created above.

                        //notify change
//                        Toast.makeText(getActivity(), ""+arrayList.size(), Toast.LENGTH_SHORT).show();
                        myad_view.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }


    class MyRecyclerAdapter_Places extends RecyclerView.Adapter<MyRecyclerAdapter_Places.MyViewHolder>
    {

        // Define ur own View Holder (Refers to Single Row)
        class MyViewHolder extends RecyclerView.ViewHolder
        {
            CardView singlecardview;

            // We have Changed View (which represent single row) to CardView in whole code

            public MyViewHolder(View itemView) {

                super(itemView);
                singlecardview = (itemView.findViewById(R.id.card));

            }

        }


        // Inflate ur Single Row / CardView from XML here
        @Override
        public MyRecyclerAdapter_Places.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater  = LayoutInflater.from(parent.getContext());

            View viewthatcontainscardview = inflater.inflate(R.layout.view_fav_places_row_design,parent,false);
//            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.cardview_category));
            return new MyRecyclerAdapter_Places.MyViewHolder(viewthatcontainscardview);
        }


        @Override
        public void onBindViewHolder(MyRecyclerAdapter_Places.MyViewHolder holder, final int position) {

            CardView localcardview = holder.singlecardview;
            ImageView imv101;
            TextView tv_place_name,tv_catdesc;
            imv101=(ImageView)(holder.itemView.findViewById(R.id.imvcardview_catphoto));
            tv_place_name=(TextView)(holder.itemView.findViewById(R.id.tvcardview_catname));
            TextView rating=holder.itemView.findViewById(R.id.rating2);
            rating.setText(arrayList.get(position).getRating());
          LinearLayout button_plan_destination=(holder.itemView.findViewById(R.id.button_plan_destination));
          LinearLayout button_remove_fav=(holder.itemView.findViewById(R.id.button_remove_fav));

//
            Toast.makeText(getActivity(), ""+arrayList.get(position).getPlace_name(), Toast.LENGTH_SHORT).show();
            places_details places_details_obj=arrayList.get(position);


            tv_place_name.setText(places_details_obj.getPlace_name());

            Picasso.get().load(places_details_obj.getImages()).resize(500,500).centerInside().into(imv101);

            button_plan_destination.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(getContext().getApplicationContext(),Calendar.class);
                    in.putExtra("place_id",places_details_obj.getPush_key()); // Sends that selected place's key to Manage reviews page
                    startActivity(in);
                }
            });

            button_remove_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fetch_saved_places_mainref.child(temp_places_id_array_list.get(position).getFav_places_id()).removeValue();

                }
            });

        }
        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
}