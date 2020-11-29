package com.zappfoundry.aermenucaptain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class SplashandLoaderActivity extends AppCompatActivity  {
    public static String x="A"; Context context;  public static String restName="A";public static String imageURL="A";
    private FirebaseAuth mAuth;public static String userName=""; public static String userEmail="";
    public static int categoryCount=0;
    public static int tableCount=0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GoogleSignInClient mgoogleSignInClient;
    public static String y;
    private int shortAnimationDuration;
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        final ImageView img = findViewById(R.id.imageView5);
        final TextView AermenuName = findViewById(R.id.textView13);
        img.setAlpha(0f);
        img.setVisibility(View.VISIBLE);
        AermenuName.setAlpha(0f);
        AermenuName.setVisibility(View.VISIBLE);
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_longAnimTime);
        // Animate the content view to 100% opacity, and clear any animation
        // l//istener set on the view.
        img.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);
        AermenuName.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                img.animate()
                        .alpha(0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(null);
                AermenuName.animate()
                        .alpha(0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(null);
            }
        }, 2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashandLoaderActivity.this, MainActivity.class);
                startActivity(intent);


            }
        }, 3000);
        final DocumentReference docRefDesserts = db.collection("CNC").document("menu") ;
        docRefDesserts.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@com.google.firebase.database.annotations.Nullable DocumentSnapshot snapshot,
                                @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    {

                        //Picasso.get().load((snapshot.get("logoImage").toString())).into(logoRest);
                       // restName=(snapshot.get("restaurantName").toString());
                        imageURL=(snapshot.get("logoImage").toString());
                        //x1.setText((snapshot.get("restaurantName").toString()));
                        tableCount=Integer.parseInt(String.valueOf(snapshot.get("tableCount")));
                        Log.e("categoryCount", String.valueOf(tableCount));
                    /*    List<Map<String, Object>> veg = (List<Map<String, Object>>)  snapshot.get("veg");
                        for(int i=0;i<veg.size();i++){
                            Map<String, Object> m=veg.get(i);
                            Log.e("menuItem",m.toString());
                            MenuItem item = new MenuItem();

                        }*/


                    }
                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });
        //    Picasso.get().load(menuItemListDrinks.get(rvHolder.getAdapterPosition()).getImageURL().toString()).into(itemImage);
    }

}
