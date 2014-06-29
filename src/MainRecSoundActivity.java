package plug;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import static com.progx73.mysoundapplication.sound.FFT.*;


public class MainRecSoundActivity extends Activity {

    int echant = 44000;
    short audiobuffer[] = new short[4096];

    double dData[] = new double[1024];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      


                alert("In Activity...let"'s go !');


                Log.d("info","lauching recording !");

                int i= AudioRecord.getMinBufferSize(echant,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                AudioRecord a= new AudioRecord(MediaRecorder.AudioSource.MIC,echant,
                        AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,i);

                a.startRecording();

                int bufferSize=AudioRecord.getMinBufferSize(echant,AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT); //get the buffer size to use with this audio record
                Log.d("info","Buffer minimum : "+bufferSize);


                Date d = new Date();
                long depart = d.getTime();
                boolean rec= true;
                Log.d("info","thread recording !");
                while(rec)
                {
                    rec = ((new Date().getTime()-depart)>5000);
                    int taille = a.read(audiobuffer,0,audiobuffer.length);
                    Log.d("info","t:"+taille);

                }
                a.release();
                Log.d("info","thread release recording !");

                /*********************************
                Calcul de la frequence du signal
                **********************************/
                /*Log.d("info", "******************** METHODE ZERO CROSSING ********************");


               int   numCrossing=0; //initialize your number of zero crossings to 0
                int p;
                for (p=0;p<audiobuffer.length/4;p+=4) {
                    if (audiobuffer[p]>0 && audiobuffer[p+1]<=0) numCrossing++;
                    if (audiobuffer[p]<0 && audiobuffer[p+1]>=0) numCrossing++;
                    if (audiobuffer[p+1]>0 && audiobuffer[p+2]<=0) numCrossing++;
                    if (audiobuffer[p+1]<0 && audiobuffer[p+2]>=0) numCrossing++;
                    if (audiobuffer[p+2]>0 && audiobuffer[p+3]<=0) numCrossing++;
                    if (audiobuffer[p+2]<0 && audiobuffer[p+3]>=0) numCrossing++;
                    if (audiobuffer[p+3]>0 && audiobuffer[p+4]<=0) numCrossing++;
                    if (audiobuffer[p+3]<0 && audiobuffer[p+4]>=0) numCrossing++;
                }//for p

                for (p=(audiobuffer.length/4)*4;p<audiobuffer.length-1;p++) {
                    if (audiobuffer[p]>0 && audiobuffer[p+1]<=0) numCrossing++;
                    if (audiobuffer[p]<0 && audiobuffer[p+1]>=0) numCrossing++;
                }



                int frequency=(echant/audiobuffer.length)*(numCrossing/2);
                Log.d("info","Frequence claculée : "+frequency);

*/


                Log.d("info", "******************** METHODE FFT ********************");

                byteToDouble();
                findFrequency();
/*

                //initialise tab de complexes.
                Complex[] complexData = new Complex[audiobuffer.length];

                //affecte elements, convertion audiobuffer vers tabComplexe.
                for (int ffti = 0; ffti < complexData.length; ffti++) {
                    complexData[ffti] = new Complex(audiobuffer[ffti], 0);

                }
                //application fft
                Complex[] fftResult = FFT.fft(complexData);

                */
/** Calcul du peak **//*

                double res[] = new double[fftResult.length];
                double max = 0.0;
                for(int ffti=0;ffti<fftResult.length;ffti++)
                {
                    res[ffti] = Math.sqrt(fftResult[ffti].re()*fftResult[ffti].re())+(fftResult[ffti].im()*fftResult[ffti].im());
                    if( res[ffti]>max)max =  res[ffti];

                }
                Log.d("info","FFT : "+max);
*/


            }
        });

        /***********************
         * Conversion Byte to double
         */

    }
    public void byteToDouble(){
      /*  ByteBuffer buf= ByteBuffer.wrap(audiobuffer);
        buf.order(ByteOrder.BIG_ENDIAN);
        int i=0;
        while(buf.remaining()>1){
            Short s = buf.getShort();
            dData[ 2 * i ] = s.doubleValue(); // 32768.0; //real
            dData[ 2 * i + 1] = 0.0;    // imag
            ++i;
        }*/
        for(int i=0;i<dData.length;i++)
        {
            dData[i] = new Double(audiobuffer[i]);
            Log.d("info","conversion vers double : "+i+" "+dData[i]);
        }
    }
    public void findFrequency(){

        double frequency;

        //DoubleFFT_1D fft= new DoubleFFT_1D(audioFrames);
/* edu/emory/mathcs/jtransforms/fft/DoubleFFT_1D.java */

        Complex[] complexData = new Complex[dData.length];
        //affecte elements, convertion audiobuffer vers tabComplexe.
        for (int ffti = 0; ffti < complexData.length; ffti++) {
            complexData[ffti] = new Complex(dData[ffti], 0);

        }
        Complex[] tabComplex =FFT.fft(complexData);
      //  fft.complexForward(dData); // do the magic so we can find peak
        double tabSolve[] = new double[dData.length];
        for(int i = 0; i < tabComplex.length; i++){
            //re[i] = dData[i*2];
            //im[i] = dData[(i*2)+1];
            //mag[i] = Math.sqrt((re[i] * re[i]) + (im[i]*im[i]));
            tabSolve[i] = Math.sqrt((tabComplex[i].re() * tabComplex[i].re()) + (tabComplex[i].im() * tabComplex[i].im()));
            Log.d("info",i+";"+ tabSolve[i]);
        }

        double peak = -1.0;
        int peakIn=-1;
        for(int i = 0; i < dData.length; i++){
            if(peak < tabSolve[i]){
                peakIn=i;
                peak= tabSolve[i];
            }
        }
        frequency = (echant * (double)peakIn) / (double)dData.length;
       // System.out.print("Peak: "+peakIn+", Frequency: "+frequency+"\n");
        Log.d("info","Peak: "+peakIn+", Frequency: "+frequency+"\n");
        alert("Peak: "+peakIn+", Frequency: "+frequency+"\n");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_rec_sound, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
