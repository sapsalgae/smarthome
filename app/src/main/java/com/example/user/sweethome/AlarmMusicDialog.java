package com.example.user.sweethome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by USER on 2018-01-21.
 */

public class AlarmMusicDialog extends Activity {
    private Context context;
    private String musicFile;
    private String musicList[];
    private final String MEDIA_PATH = new String("/내 디바이스/musicalbum");
    private AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder;

    public AlarmMusicDialog(Context context) {
        this.context = context;
        alertDialogBuilder = new AlertDialog.Builder(context);

        getSongList();
        showMusicList();
    }

    // 음원 리스트 가져오기
    public void getSongList(){
        int numMusic; int i = 0;
        File musicFiles = new File(MEDIA_PATH);

        try {
            numMusic = musicFiles.listFiles(new Mp3Filter()).length;
            musicList = new String[numMusic];

            if (numMusic > 0) {
                for (File file : musicFiles.listFiles(new Mp3Filter())) {
                    musicList[i++] = file.getName();
                }

                Log.d("music: ", musicList[i]);
            }

            Log.d("here?", "dfd");
        }

        catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    // 선택 목록 상자
    public void showMusicList() {
        alertDialogBuilder.setTitle("알람음");

        alertDialogBuilder.setSingleChoiceItems(musicList, -1, new DialogInterface.OnClickListener(){
            @Override
             public void onClick(DialogInterface dialog, int id) {
                musicFile = musicList[id].toString();
                dialog.dismiss();
            }
        });

        // dialog 생성
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String getMusicFile() {
        return musicFile;
    }

    public AlertDialog.Builder getAlertDialogBuilder() {
        return this.alertDialogBuilder;
    }

    public CharSequence[] getMusicList() {
        return musicList;
    }

    @Override
    public void onBackPressed() {
        alertDialog.dismiss();
    }

    class Mp3Filter implements FilenameFilter{
        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3"));
        }
    }
}