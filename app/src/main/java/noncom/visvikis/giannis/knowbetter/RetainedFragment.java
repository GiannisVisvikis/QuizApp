package noncom.visvikis.giannis.knowbetter;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;


/**
 *  Will hold on to expensive data through orientation changes and will produce the sounds
 */



public class RetainedFragment extends Fragment
{

    private MediaPlayer mp;
    private  boolean isReleased;
    private List<QuizQuestion> quizQuestions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }


    public void setQuizQuestions(List<QuizQuestion> quizQuestions)
    {
        this.quizQuestions = quizQuestions;
    }

    public List<QuizQuestion> getQuizQuestions()
    {
        return quizQuestions;
    }


    public void playSound(int soundId)
    {

        stopSound();

        mp = MediaPlayer.create(getActivity().getApplicationContext(), soundId);
        mp.start();

        //in case of play sound file from assets
//        try {
//            AssetFileDescriptor afd = getActivity().getAssets().openFd(filePath);
//            //Log.e("AFD_IS_NULL", (afd == null) + "");
//            FileDescriptor fd = afd.getFileDescriptor();
//
//            mp.setDataSource(fd, afd.getStartOffset(), afd.getLength());
//            mp.prepare();
//        }
//        catch (IOException io){
//            Toast.makeText(getActivity(), "Sound not found", Toast.LENGTH_SHORT).show();
//        }

        isReleased = false;
        //set a listener to free resource on completion
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
                isReleased = true;
                mp = null;
            }
        });

        mp.start();

    }


    private void stopSound(){
        //if not released then it is still playing (gets released inside the completion listener)
        if(mp != null && !isReleased)
            mp.stop();
        mp = null;
    }


}
