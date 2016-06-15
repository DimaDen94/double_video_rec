package com.up_site.twocam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dmitry on 10.06.2016.
 */
public class MergeVideo extends AsyncTask<String, Integer, String> {
    int count;
    File path;
    String filename;

    @Override
    protected void onPreExecute() {


    }


    @Override
    protected String doInBackground(String... params) {
        try {
            String paths[] = new String[count];
            Movie[] inMovies = new Movie[count];
            for (int i = 0; i < count; i++) {
                paths[i] = path + filename + String.valueOf(i + 1) + ".mp4";
                inMovies[i] = MovieCreator.build((DataSource) new FileInputStream(
                        paths[i]).getChannel());
            }
            List<Track> videoTracks = new LinkedList<Track>();
            List<Track> audioTracks = new LinkedList<Track>();
            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            BasicContainer out = (BasicContainer) new DefaultMp4Builder()
                    .build(result);

            @SuppressWarnings("resource")
            FileChannel fc = new RandomAccessFile(String.format(Environment
                    .getExternalStorageDirectory() + "/wishbyvideo.mp4"),
                    "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String mFileName = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        mFileName += "/wishbyvideo.mp4";

        return mFileName;
    }

    @Override
    protected void onPostExecute(String value) {
        super.onPostExecute(value);

    }
}
