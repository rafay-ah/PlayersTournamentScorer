package com.players.nest.VideoTrimmer.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {


    private static final String TAG = "TAGS";

    /**
     * Used for Android Q+
     *
     * @param uri Original Video Uri
     * @return The path of the copied video from the internal storage
     */
    public static String copyFileToInternalStorage(Context context, Uri uri) {

        Cursor returnCursor = context.getContentResolver().query(uri, new String[]{
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        }, null, null, null);


        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File output;
        File dir = new File(context.getFilesDir() + "/" + "Videos");
        if (!dir.exists()) {
            dir.mkdir();
        }
        output = new File(context.getFilesDir() + "/" + "Videos" + "/" + name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }

        return output.getPath();
    }


    public static boolean deleteCopiedFile(File file) {

        if (file != null) {
            return file.delete();
        }
        return false;
    }
}
