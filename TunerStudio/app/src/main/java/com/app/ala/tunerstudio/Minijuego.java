package com.app.ala.tunerstudio;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class Minijuego extends Fragment {


    List<String> notes = new LinkedList(Arrays.asList("E6", "A5", "D4", "G3", "B2", "E1"));
    List<String> notesCopy = notes;
    //List<int> notesSelected = (1, 1, 1);
    Integer[] timeStamp = {0, 1840, 3600, 5240, 7000, 8800};
    MediaPlayer mediaPlayer;
    Integer selectedTimeStamp;

    Handler mHandler;

    public Minijuego() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_minijuego, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mediaPlayer = MediaPlayer.create(getView().getContext(), R.raw.c_sonido);
        mediaPlayer.setOnSeekCompleteListener(timeStampDetector);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                createButtons();
            }
        });



    }

    public void createButtons(){
        for(int i=0;i<3;i++){
            createButton(i);
        }

        notes = notesCopy;

        Random rand = new Random();
        int randomNum = rand.nextInt(3);

    }

    void createButton(int pos){
        Random rand = new Random();
        int randomNum = rand.nextInt(notes.size());

        //notesSelected[pos] = randomNum;

        if (pos == 0){
            if(randomNum+1 != notes.size()){
                selectedTimeStamp = timeStamp[randomNum+1] - timeStamp[randomNum];
            }
            else{
                selectedTimeStamp = mediaPlayer.getDuration() - timeStamp[randomNum];
            }
            mediaPlayer.seekTo(timeStamp[randomNum]);
        }
        notes.remove(randomNum);
    }

    private MediaPlayer.OnSeekCompleteListener timeStampDetector = new MediaPlayer.OnSeekCompleteListener (){
        public void onSeekComplete (MediaPlayer mp) {
            mediaPlayer.start();
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }, selectedTimeStamp);
        }
    };

}
