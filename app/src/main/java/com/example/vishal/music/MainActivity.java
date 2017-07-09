package com.example.vishal.music;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static com.example.vishal.music.R.layout.activity_main;

public class MainActivity extends ActionBarActivity {

    ListView lv;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        lv = (ListView) findViewById(R.id.lvPlaylist);
        final ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());
        Collections.sort(mySongs, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2)
            {

                return  file1.getName().compareTo(file2.getName());
            }
        });
        items = new String[ mySongs.size() ];
        for(int i=0;i<mySongs.size();i++){
            //toast(mySongs.get(i).getName().toString());
            items[i]= mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
        //ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(),R.layout.song_layout,R.id.textView,items);
        //lv.setAdapter(adp);
        CustomAdapter adapter = new CustomAdapter(this,items);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(),Player.class).putExtra("pos",position).putExtra("songlist",mySongs));
            }
        });
    }

    public ArrayList<File> findSongs(File root){
        ArrayList<File> al = new ArrayList<File>();
        File[] files = root.listFiles();
        for(File singleFile : files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                al.addAll(findSongs(singleFile));
            }
            else {
                if(singleFile.getName().endsWith(".mp3")||singleFile.getName().endsWith(".wav")){
                    al.add(singleFile);
                }
            }
        }
        return  al;
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    class CustomAdapter extends ArrayAdapter<String>
    {
        Context context;
        String[] title;


        CustomAdapter(Context c, String[] title)
        {

            super(c, R.layout.song_layout,title);
            this.context = c;
            this.title=title;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = vi.inflate(R.layout.song_layout, parent, false);
            TextView titlee = (TextView) row.findViewById(R.id.textView);
            int pos = position+1;
            titlee.setText(+pos + ". " + title[position]);
            pos++;
            return row;
        }

    }
}
