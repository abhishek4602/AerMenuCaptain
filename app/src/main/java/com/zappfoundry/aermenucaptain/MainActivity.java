package com.zappfoundry.aermenucaptain;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RecyclerTouchListener.RecyclerItemTouchHelperListener ,
        AdapterView.OnItemSelectedListener, IAttachOrderItems {
    public static int currentTable=1; Order m;   private NotificationCompat.Builder notification_builder;  private NotificationManagerCompat notification_manager;
    List<Order> orderList=new ArrayList<Order>() ;TakeAwayObject t ; private NotificationManager mNotificationManager;  public static Map<String, Integer> tableNotifCount=new HashMap<>() ; private int id = 1;
    IAttachOrderItems   iAttachOrderItems;List<TakeAwayObject> tList=new ArrayList<TakeAwayObject>(SplashandLoaderActivity.tableCount) ;
    List<String> gridStringList=new ArrayList<String>() ;  TableItemRecyclerView tableItemRecyclerView;
    RecyclerView recyclerViewOrderlist,recyclerViewTableSelect;
    Context context; String[] spinnerAdapter;

    OrderRecyclerViewHandler OrderItems;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FirebaseApp.initializeApp();
        context = this;
        iAttachOrderItems = this;


        for (int spinnerLoaderCount = 1; spinnerLoaderCount <= SplashandLoaderActivity.tableCount; spinnerLoaderCount++) {

            gridStringList.add("table " + String.valueOf(spinnerLoaderCount));
            tableNotifCount.put(String.valueOf(spinnerLoaderCount),1);
        }
        recyclerViewTableSelect = findViewById(R.id.recyclerViewTableSelect);
        recyclerViewTableSelect.setHasFixedSize(true);
        tableItemRecyclerView = new TableItemRecyclerView(gridStringList, this, iAttachOrderItems);
        // .setLayoutManager( new GridLayoutManager(this, GridLayoutManager.HORIZONTAL,false));
        recyclerViewTableSelect.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerViewTableSelect.setAdapter(tableItemRecyclerView);
        tableItemRecyclerView.notifyDataSetChanged();   Button buttonConfirm=findViewById(R.id.buttonConfirm);
TextView tvLiveOrders =findViewById(R.id.tvLiveOrders);
        tvLiveOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        ImageView imageViewLogoImage=findViewById(R.id.imageViewCafeLogo);
        Picasso.get().load((SplashandLoaderActivity.imageURL)).into(imageViewLogoImage);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTable(currentTable);
            }
        });

        final boolean[] isFirstTime = {true};
        for(int j=0;j<=SplashandLoaderActivity.tableCount;j++)
        {
            final DocumentReference docRefOrders = db.collection("CNC").document("orders").collection("tables").document("table"+j);
            final int finalJ = j;

            recyclerViewOrderlist=findViewById(R.id.recyclerViewOrderList);
            recyclerViewOrderlist.setHasFixedSize(true);
            OrderItems=new OrderRecyclerViewHandler(orderList,this,iAttachOrderItems);
            recyclerViewOrderlist.setLayoutManager( new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            recyclerViewOrderlist.setAdapter(OrderItems);


            docRefOrders.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }
                    ArrayList<Order> tempList=new ArrayList<>();
                    if (snapshot != null && snapshot.exists()) {
                        {
                            if(!isFirstTime[0]){

                                for(int k=0;k<orderList.size();k++){


                                    m= orderList.get(k);

                                    if(m.isFromDB()==true)
                                    {
                                        if(!tempList.contains(m)){
                                            tempList.add(m);}
                                    }

                                }
                                orderList.clear();
                                orderList.addAll(tempList);
                                OrderItems.notifyDataSetChanged();
                                //orderList.clear();
                            }
                            if(finalJ==SplashandLoaderActivity.tableCount){
                                isFirstTime[0] =false;

                                ReloadMenu("table 1");
                                OrderItems.notifyDataSetChanged();
                            }




                            t=new TakeAwayObject();
                            try{ t.setOrderID((String) snapshot.get("orderID"));}catch (Exception EorderID){ t.setOrderID("");}
                            try{   t.setTotalAmount(Integer.parseInt(String.valueOf(snapshot.get("totalAmount"))));}catch (Exception EorderID){ t.setTotalAmount(0);}
                            try{   t.setUserEmail((String) snapshot.get("userEmail"));}catch (Exception EorderID){ t.setUserEmail("");}
                            try{  t.setTimeStamp((String) snapshot.get("timeStamp"));}catch (Exception EorderID){ t.setTimeStamp("");}

                            tList.add(t);
                            Log.e("TlistSize", String.valueOf(tList.size()));
                            List<Map<String, Object>> veg = (List<Map<String, Object>>) snapshot.get("orders");
                            if (veg == null) {
                                Log.d("Order Data", "Order " + snapshot.getData());
                            } else {
                                ArrayList<Map> arrayList = new ArrayList<>();
                                if(finalJ==SplashandLoaderActivity.tableCount)
                                {

                                /*    OrderItems=new OrderRecyclerViewHandler(orderList,context,iAttachOrderItems);
                                    recyclerViewOrderlist.setAdapter(OrderItems);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ReloadMenu("table 1");
                                            OrderItems.notifyDataSetChanged();

                                        }
                                    }, 3000);
*/
                                }
                                for (int i = 0; i < veg.size(); i++)
                                {
                                    Map<String, Object> m = veg.get(i);
                                    //      Log.e("VegGet", String.valueOf(veg.get(i)));
                                    final Order item = new Order();
                                    item.setItemName(m.get("itemName").toString());
                                    Log.e("ItemNameO",m.get("itemName").toString());
                                    Log.e("ItemTableO",m.get("tableNumber").toString());
                                    item.setItemID( 100*(Integer.parseInt( m.get("tableNumber").toString()))+i);
                                    item.setItemCount(Integer.parseInt(m.get("itemCount").toString()));
                                    try{item.setDelivered((Boolean) m.get("delivered"));}catch (Exception mm){}

                                    item.setItemTotal(Integer.parseInt(m.get("itemTotal").toString()));
                                    item.setItemPrice(Integer.parseInt(m.get("itemPrice").toString()));
                                    item.setTableNumber(Integer.parseInt(m.get("tableNumber").toString()));
                                    Log.e(String.valueOf( item.getTableNumber()),item.getItemName()) ;

                                    if( isFirstTime[0]==false)
                                    {


                                        //}catch (Exception oo){}

                                    }
                                    int flag=1;
                                    item.setFromDB(Boolean.valueOf( m.get("fromDB").toString()));
                                    int orderedTable= Integer.parseInt(String.valueOf(snapshot.get("tableNumber")));
                                    for(Order o:orderList){

                                        if (o.getItemID()==item.getItemID()){flag=0;}

                                    }

                                    if(flag==1)
                                    {
                                        if( isFirstTime[0]==false)
                                        {
                                            if(!item.isFromDB())
                                            {
                                                 if(!item.isDelivered())
                                                 {
                                                     soundNotification(item.getTableNumber());

                                                     int count= tableNotifCount.get(String.valueOf( item.getTableNumber()));
                                                     count+=1;
                                                     tableNotifCount.put(String.valueOf( item.getTableNumber()),count);
                                                     showNotification(item.getTableNumber(),count);
                                                 }
                                            }
                                        }
                                    orderList.add(item);
                                    item.setFromDB(true);
                                    }

                                  /*  if(!orderList.contains(item.getItemID()))
                                    {orderList.add(item);
                                        Log.e("orderlistsize", String.valueOf( item.getTableNumber())); Log.e("orderlistsize", String.valueOf( item.getItemName())); Log.e("orderlistsize", String.valueOf( item.getItemID()));}
*/

                                }
                                if( isFirstTime[0] ==false){
                                    ReloadMenu("table "+currentTable);
                                    OrderItems.notifyDataSetChanged();

                                }}

                        }
                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                }
            });
            if(!isFirstTime[0]){break;}
        }


        OrderItems.notifyDataSetChanged();
        ItemTouchHelper.SimpleCallback  itemTouchHelperCallback = new RecyclerTouchListener(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewOrderlist);

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof OrderRecyclerViewHandler.RecyclerViewHolder) {
            // get the removed item name to display it in snack bar
            String name = orderList.get(viewHolder.getAdapterPosition()).getItemName();

            // backup of removed item for undo purpose
            final Order deletedItem = orderList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view

            OrderItems.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
          /*  Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();*/
        }
    }

    void refreshSummary(){
        OrderRecyclerViewHandler orderSummaryHandler=new OrderRecyclerViewHandler(orderList,context,iAttachOrderItems);
        recyclerViewOrderlist.setAdapter(orderSummaryHandler);
        orderSummaryHandler.notifyDataSetChanged();
       /* for (Order o : orderList)
        {
           *//* if (o.getItemTotal() != 0)
            {
                Total += (o.getItemTotal());
            }*//*
        }
        TextView textviewTotal = findViewById(R.id.textView18);
        textviewTotal.setText("₹" + Integer.toString(Total));*/
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(getApplicationContext(),spinnerAdapter[i] , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void ReloadMenu(String tableNumber) {
        TextView tvTableNumber=findViewById(R.id.tvTableNumberHeader);
        TextView itemTotal=findViewById(R.id.tvTotalAmount);
        final DocumentReference docRefOrders = db.collection("CNC").document("orders").collection("tables").document("table"+tableNumber);


        String substr=tableNumber.substring(6);
        TakeAwayObject tk=  tList.get((Integer.parseInt(substr))-1);
        for(TakeAwayObject p:tList){
            //   Log.e("PList", String.valueOf(p.Orders));
        }
        itemTotal.setText("Bill Total : Rs "+String.valueOf(tk.getTotalAmount()) );
        tvTableNumber.setText(tableNumber);
        ArrayList<Order> tempList=new ArrayList<>();
        for (Order m : orderList)
        {

            Log.e("itemName",m.getItemName());
            Log.e("itemTable",String.valueOf(m.getTableNumber()));
            if( (String.valueOf( m.getTableNumber())).trim().equals(substr.toString().trim()))
            {

                Log.e("PList", String.valueOf(m.getTableNumber()));
                tempList.add(m) ;   }
        }

        currentTable=Integer.parseInt(substr) ;
        for(Order l:tempList)
        {
            Log.e("LLL",l.getItemName());
        }


        RefreshMenuData(tempList);
        updateTotal(currentTable);
    }

    @Override
    public void delivered(long itemID) {
        final DocumentReference docRefOrders = db.collection("CNC").document("orders").collection("tables").document("table"+currentTable) ;
        for(Order r:orderList){if (r.getItemID()==itemID){Log.e("before",String.valueOf(r.getItemName())) ;r.setDelivered(true);r.setFromDB(false);}}



        List<Order> templist=new ArrayList<Order>() ;

        for(Order r:orderList){if (r.getTableNumber()==currentTable){Log.e("before",String.valueOf(r.isDelivered())) ;}}
        /*     orderList.get (itemID).setDelivered(true); orderList.get (itemID).setFromDB(false); */
        for(Order r:orderList){if (r.getTableNumber()==currentTable){Log.e("before",String.valueOf(r.isDelivered())) ;}}
        for(Order r:orderList){if (r.getTableNumber()==currentTable){templist.add(r);}}
        //  docRefOrders.update("orders", FieldValue.arrayRemove(o));

       /* Map<String, Object> availableProducts = new HashMap<>();
        Map<String, Object> zeroMap = new HashMap<>();
        Map<String, Object> product = new HashMap<>();
        product.put("delivered", false);

        zeroMap.put(String.valueOf(o.getItemID()), o);
        availableProducts.put("orders", zeroMap);

        ArrayList<Map> orders = new ArrayList<>(); orders.add(zeroMap);*/
        TakeAwayObject tSend= tList.get(currentTable);
        Log.e("MList", String.valueOf(templist));
        tSend.Orders= (ArrayList<Order>) templist;
        docRefOrders.set(tSend, SetOptions.merge());

    }

    void RefreshMenuData(List<Order> refreshList )
    {
        OrderItems = new OrderRecyclerViewHandler(refreshList,context,iAttachOrderItems) ;
        recyclerViewOrderlist.setAdapter(OrderItems);
        OrderItems.notifyDataSetChanged();


    }
    void clearTable(int tableNumber)
    {
        List<Order> templist=new ArrayList<Order>() ;
        final DocumentReference pO = db.collection("CNC").document("pastorders")  ;
        for(Order r:orderList){if (r.getTableNumber()==tableNumber){templist.add(r);}}
        TakeAwayObject tO=tList.get(tableNumber);

        tO.Orders= (ArrayList<Order>) templist;
        /*tO.setUserEmail();*/
        pO.update("details",FieldValue.arrayUnion(tO));


        TakeAwayObject tSend= new TakeAwayObject();
        final DocumentReference docRefOrders = db.collection("CNC").document("orders").collection("tables").document("table"+tableNumber) ;
        docRefOrders.set(tSend, SetOptions.merge());
        TextView itemTotal=findViewById(R.id.tvTotalAmount);   itemTotal.setText("Total Amount : Rs "+String.valueOf(0) );
        List<Order> templist2=new ArrayList<Order>() ;
        for(int k=0;k<orderList.size();k++){


            m= orderList.get(k);

            if(m.getTableNumber()!=tableNumber)
            {
                if(!templist2.contains(m)){
                    templist2.add(m);}
            }

        }
        orderList.clear();
        orderList.addAll(templist2);

        ReloadMenu("table "+tableNumber);
    }
    void showNotification(int currentTable,int count)
    {
        tableItemRecyclerView.showNotification(currentTable,count);
    }
    void updateTotal(int currentTable)
    {
        TextView itemTotal=findViewById(R.id.tvTotalAmount);
        int total=0;
        for(Order r:orderList){if (r.getTableNumber()==currentTable){total+=r.getItemTotal();}}
        itemTotal.setText("Total Amount : Rs "+String.valueOf(total) );
    }
    void soundNotification(int tableNumber)
    {
       /* NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.menucardicon)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");


        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setOnlyAlertOnce(true);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        builder.setLights(Color.BLUE, 500, 500);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        builder.setVibrate(pattern);
        builder.setStyle(new NotificationCompat.InboxStyle());
// Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());*/

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
        Intent ii = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();

        bigText.setBigContentTitle("New Order Received on Table "+tableNumber);
        bigText.setSummaryText("");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
       // mBuilder.setOnlyAlertOnce(true);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.aermenu);
        mBuilder.setContentTitle("AerMenu has received new orders");
        mBuilder.setContentText("Click here to open now");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        mBuilder.setVibrate(pattern);
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(id, mBuilder.build());
        id++;
    }
}