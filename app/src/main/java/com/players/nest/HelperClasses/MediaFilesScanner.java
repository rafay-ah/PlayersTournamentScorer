package com.players.nest.HelperClasses;

public class MediaFilesScanner {

    public static boolean isFilePhotoOrVideo(String fileName) {
        return fileName.endsWith(".jpg") || fileName.endsWith(".png") ||
                fileName.endsWith(".gif") || fileName.endsWith(".mp4") ||
                fileName.endsWith(".3gp") || fileName.endsWith(".mkv") ||
                fileName.endsWith("bmp");
    }


    public static boolean isVideoFile(String fileName) {
        return fileName.endsWith(".mp4") || fileName.endsWith(".3gp") || fileName.endsWith(".mkv");
    }
}
