package com.sanket.fundraw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sanket.fundraw.Classes.DrawingView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawingView drawview1;
    private ImageButton currPaint,drawBtn,eraseBtn,newBtn,saveBtn,logoutBtn;
    private float smallBrush, mediumBrush, largeBrush;
    FirebaseFirestore db1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawview1 = (DrawingView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);

        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        drawBtn = findViewById(R.id.draw_btn);
        eraseBtn = findViewById(R.id.erase_btn);
        newBtn = findViewById(R.id.new_btn);
        saveBtn = findViewById(R.id.save_btn);
        logoutBtn = findViewById(R.id.logout_btn);

        db1 = FirebaseFirestore.getInstance();

        drawview1.setBrushSize(mediumBrush);

        drawBtn.setOnClickListener(this);
        eraseBtn.setOnClickListener(this);
        newBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    public void paintClicked(View view){
        drawview1.setErase(false);
        drawview1.setBrushSize(drawview1.getLastBrushSize());
        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawview1.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.draw_btn){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.choose_brush);
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawview1.setBrushSize(smallBrush);
                    drawview1.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawview1.setBrushSize(mediumBrush);
                    drawview1.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawview1.setBrushSize(largeBrush);
                    drawview1.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        }
        else if(view.getId()==R.id.erase_btn){

            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.choose_brush);
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawview1.setErase(true);
                    drawview1.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawview1.setErase(true);
                    drawview1.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawview1.setErase(true);
                    drawview1.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        }
        else if(view.getId()==R.id.new_btn){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawview1.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        else if(view.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawview1.setDrawingCacheEnabled(true);

                    SharedPreferences sharedPreferences1 = getSharedPreferences("UserDetails",Context.MODE_PRIVATE);
                    String user = sharedPreferences1.getString("mobile","0");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    String key=user+currentDateandTime;
                    Map<String, Object> map1 = new HashMap<>();
                    Bitmap bitmap = drawview1.getDrawingCache();
                    String stringbitm = String.valueOf(bitmap);
                    map1.put(key,stringbitm);

                    db1.collection(user).add(map1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this, "Saved to Cloud", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "There was some problem with the server", Toast.LENGTH_SHORT).show();
                        }
                    });

                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), drawview1.getDrawingCache(),
                            "contact+time.png", "FunDraw By Sanket Kudale");



                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawview1.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
        else if (view.getId()==R.id.logout_btn){
            AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
            exitDialog.setTitle("Logout");
            exitDialog.setMessage("Are you sure you want to logout?");
            exitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    SharedPreferences sp1 = getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sp1.edit();
                    editor1.putString("logged","0");
                    editor1.apply();
                    editor1.commit();

                    SharedPreferences sp2 = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sp2.edit();
                    editor2.putString("mobile","0");
                    editor2.apply();
                    editor2.commit();

                    startActivity(new Intent(MainActivity.this,Welcome.class));
                }
            });
            exitDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            exitDialog.show();
        }
    }

}