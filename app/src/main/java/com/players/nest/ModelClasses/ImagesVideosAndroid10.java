package com.players.nest.ModelClasses;

import java.util.ArrayList;

public class ImagesVideosAndroid10 {

    String folderName;
    ArrayList<FilePaths> files;

    public ImagesVideosAndroid10(String folderName, ArrayList<FilePaths> files) {
        this.folderName = folderName;
        this.files = files;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public ArrayList<FilePaths> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<FilePaths> files) {
        this.files = files;
    }


    public static class FilePaths {

        String absPath, contentUri;

        public FilePaths(String absPath, String contentUri) {
            this.absPath = absPath;
            this.contentUri = contentUri;
        }

        public String getAbsPath() {
            return absPath;
        }

        public String getContentUri() {
            return contentUri;
        }
    }
}
