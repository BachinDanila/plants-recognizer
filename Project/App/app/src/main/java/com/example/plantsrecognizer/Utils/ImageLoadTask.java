package com.example.plantsrecognizer.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private final String url;

    @SuppressLint("StaticFieldLeak")
    private final ImageView imageView;

    @SuppressLint("StaticFieldLeak")
    private final Context context;

    public ImageLoadTask(String url, ImageView imageView, Context context) {
        this.url = url;
        this.imageView = imageView;
        this.context = context;
    }

    private void saveAsBitmap(Bitmap bitmap, String filename) {
        try {
            FileOutputStream out;
            out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            // ставить 85 бесполезно, PNG - это формат сжатия без потерь
            out.close();
        } catch (Exception ignored) {
        }
    }

    private String getFilename(char[] url) {
        StringBuilder string = new StringBuilder();
        for (int i = url.length - 1; i >= 0; i--) {
            if (url[i] == '/') {
                break;
            }
            string.append(url[i]);
        }
        return string.toString();
    }

    private Bitmap getImageFromUrl(String url) throws IOException {
        URL urlConnection = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlConnection
                .openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        return BitmapFactory.decodeStream(input);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        String filename = getFilename(url.toCharArray());
        try {
            //Read image from file
            InputStream input = context.openFileInput(filename);
            return BitmapFactory.decodeStream(input);
        } catch (FileNotFoundException e) {
            //Read image from url link
            try {
                Bitmap bitmap = getImageFromUrl(url);
                bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
                saveAsBitmap(bitmap, filename);
                return bitmap;
            } catch (Exception base_e) {
                base_e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
    }

}
