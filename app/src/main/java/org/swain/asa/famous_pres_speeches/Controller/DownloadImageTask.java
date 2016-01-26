package org.swain.asa.famous_pres_speeches.Controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Famous US Speeches Android Application
 * Copyright (C) 2015  Asa F. Swain
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This class downloads and image from a website URL and converts it to a black & white image
 * storing the image in the ImageView bitmapImage variable
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bitmapImage;

    // constructor
    public DownloadImageTask(ImageView bitmapsImage) {
        this.bitmapImage = bitmapsImage;
    }


    /**
     * This method overrides the doInBackground method from AsyncTask
     * @param urls - an array containing a single HTTP URL to load
     * @return Bitmap image generated from URL
     */
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap newImage = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            newImage = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return newImage;
    }

    /**
     * This method is called automatically when doInBackground finishes
     * and it load the image into the class bitmapImage bitmap and changes the image to be black and white
     * @param result - the Bitmap images generated from the URL
     */
    protected void onPostExecute(Bitmap result) {
        // this code creates a filter to display the image in black & white
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

        bitmapImage.setImageBitmap(result);
        bitmapImage.setColorFilter(filter);
    }
}
