package com.app.ala.tunerstudio;


import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class Afinador extends Fragment implements Stop{

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    int[] bufferData;
    int mPeakPos;
    double[] absNormalizedSignal;
    final int mNumberOfFFTPoints = 1024;

    public Afinador() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


    bufferSize = AudioRecord.getMinBufferSize
                    (RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_afinador, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setButtonHandlers();
        enableButtons(false);

        //TextView textfrequency= (TextView) getView().findViewById(R.id.frecuencia);
        //textfrequency.bringToFront();
    }

    //___________________BOTONES_____________________________________________

    private void setButtonHandlers() {
        ((Button)getView().findViewById(R.id.btStart)).setOnClickListener(btnClick);
        ((Button)getView().findViewById(R.id.btStop)).setOnClickListener(btnClick);
    }


    private void enableButton(int id,boolean isEnable){
        ((Button)getView().findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.btStart,!isRecording);
        enableButton(R.id.btStop,isRecording);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btStart:{
                    Log.i("AVISO", "Start Recording");
                    enableButtons(true);
                    startRecording();
                    break;
                }
                case R.id.btStop:{
                    Log.i("AVISO", "Stop Recording");
                    enableButtons(false);
                    stopRecording();
                    //calculate();
                    break;
                }
            }
        }
    };
    //___________FIN BOTONES_____________________________________________

    //_______USANDO EL MICRO_______________________________________________
    public void startRecording(){               //recogerSonido
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

        recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            public void run() {
                writeAudioDataToFile();
            }
        });

        recordingThread.start();
    }

    void updateUI(){
        if(absNormalizedSignal != null){
            Log.i("FFT", ""+RECORDER_SAMPLERATE);
            int frequency = mPeakPos*(RECORDER_SAMPLERATE/mNumberOfFFTPoints)*2;

            TextView texto = (TextView) getView().findViewById(R.id.frecuencia);
            texto.setText("" + frequency);

            getView().findViewById(R.id.parriba).bringToFront();

            if (frequency > 125.81 && frequency < 135.81) {//C
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 133.59 && frequency < 143.59){//C# Db
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 141.83 && frequency < 151.83){//D
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 150.56 && frequency < 160.56){//D# Eb
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 159.81 && frequency < 169.81){//E
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 169.61 && frequency < 179.61){//F
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 180.00 && frequency < 190.00) {//F# Gb
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 191.00 && frequency < 201.00) {//G
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 202.65 && frequency < 212.65) {//G# Ab
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 215.00 && frequency < 225.00) {//A
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 228.08 && frequency < 228.08) {//A# Bb
                texto.setBackgroundColor(Color.GREEN);
            }
            else if (frequency > 241.94 && frequency < 251.94) {//B
                texto.setBackgroundColor(Color.GREEN);
            }
            else{
                texto.setBackgroundColor(Color.BLACK);
            }
        }
    }

    private void writeAudioDataToFile(){        //guardarSonido
        byte data[] = new byte[bufferSize];     //array tipo byte -> está creado para guardar el audio, el sonido
        String filename = getTempFilename();    //donde se va a guardar el sonido (ruta+nombre del archivo)
        FileOutputStream os = null;             //lo lleva a la ruta

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;

        if(null != os){
            while(isRecording){
                read = recorder.read(data, 0, bufferSize);
                if(read > 0){
                    absNormalizedSignal = calculateFFT(data); // --> HERE ^__^
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            updateUI();
                        }
                    });
                }



                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording(){ //dejarRecogerSonido
        if(null != recorder){
            isRecording = false;

            recorder.stop();
            recorder.release();

            recorder = null;
        }

        copyWaveFile(getTempFilename(), getFilename());
    }
    //____DEJANDO DE USAR EL MICRO________________________________________

    private void deleteTempFile() {                 //borrarTemporal
        File file = new File(getTempFilename());
        file.delete();
    }

    private String getTempFilename(){               //cogerTemporal
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private void copyWaveFile(String inFilename,String outFilename){ //copiarArchivoDeOnda
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            Log.i("AVISO", "File size: " + totalDataLen);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double[] calculateFFT(byte[] signal)
    {
        double mMaxFFTSample;

        double temp;
        Complex[] y;
        Complex[] complexSignal = new Complex[mNumberOfFFTPoints];
        double[] absSignal = new double[mNumberOfFFTPoints/2];

        for(int i = 0; i < mNumberOfFFTPoints-1; i++){
            temp = (double)((signal[2*i] & 0xFF) | (signal[2*i+1] << 8)) / 32768.0F;
            complexSignal[i] = new Complex(temp,0.0);
        }

        y = FFT.fft(complexSignal); // --> Here I use FFT class

        mMaxFFTSample = 0.0;
        mPeakPos = 0;
        for(int i = 0; i < (mNumberOfFFTPoints/2); i++)
        {
            absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
            if(absSignal[i] > mMaxFFTSample)
            {
                mMaxFFTSample = absSignal[i];
                mPeakPos = i;
            }
        }
        Log.i("Frecuencia", ""+mMaxFFTSample);
        return absSignal;
    }


    @Override
    public void stopRecord() {
        stopRecording();
    }
}
