package com.app.ala.tunerstudio;

import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.app.Activity;
import android.content.Intent;
import android.widget.Button;



public class Menu_Publi extends ActionBarActivity {

    Stop stopRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__publi);
        getFragmentManager().beginTransaction().add(R.id.principal, new Afinador()).commit();

        setButtonHandlers();
        //stopRecording = (Stop) getAcivity();

        /*ActionBar actionBar = getActionBar();
        //Escondiendo la Action Bar
        actionBar.hide();*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu__publi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //   return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    //___________________BOTONES_____________________________________________

    private void setButtonHandlers() {
        ((ImageButton)findViewById(R.id.saltarAfinador)).setOnClickListener(btnClick);
        ((ImageButton)findViewById(R.id.saltarMetronomo)).setOnClickListener(btnClick);
        ((ImageButton)findViewById(R.id.saltarMinijuego)).setOnClickListener(btnClick);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            stopRecording.stopRecord();

            switch(v.getId()){
                case R.id.saltarAfinador:{
                    Log.i("AVISO", "Saltar afinador");
                    getFragmentManager().beginTransaction().replace(R.id.principal, new Afinador()).commit();
                    break;
                }
                /*case R.id.saltarMetronomo:{
                    Log.i("AVISO", "Saltar metronomo");
                    getFragmentManager().beginTransaction().replace(R.id.principal, new Metronomo()).commit();
                    break;
                }*/
                case R.id.saltarMinijuego:{
                    Log.i("AVISO", "Saltar minijuego");
                    getFragmentManager().beginTransaction().replace(R.id.principal, new Minijuego()).commit();
                    break;
                }
            }
        }
    };
    //___________FIN BOTONES_____________________________________________
}
