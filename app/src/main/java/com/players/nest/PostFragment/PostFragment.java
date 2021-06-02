package com.players.nest.PostFragment;

import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.GlideImageLoader;
import com.players.nest.HelperClasses.MediaFilesScanner;
import com.players.nest.HelperClasses.Permissions;
import com.players.nest.ModelClasses.ImagesVideosAndroid10;
import com.players.nest.R;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.players.nest.HelperClasses.Constants.SELECTED_FILE_URL;

public class PostFragment extends Fragment implements RecyclerViewAdapt.clickListener {

    private static final String TAG = "POST_FRAGMENT";

    Toolbar toolbar;
    CardView playIcon;
    ImageView imageView;
    ImageView cameraIcon;
    VideoView videoView;
    Spinner spinnerFolder;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    AppBarLayout appBarLayout;
    LinearLayout noPhotosToDisplay;
    ConstraintLayout mainVideoLayout;
    CoordinatorLayout coordinatorLayout;
    RecyclerViewAdapt recyclerViewAdapt;

    TextView cameraTxt;

    String selectedFile;
    private boolean isFolder;
    List<String> filePaths = new ArrayList<>();
    List<String> fileDirectories = new ArrayList<>();
    List<String> directoriesNames = new ArrayList<>();

    // For Android versions above 10
    boolean isAndroid10;
    ImagesVideosAndroid10 currentFolder;
    List<String> filePaths10 = new ArrayList<>();
    ImagesVideosAndroid10.FilePaths selectedFilePath;
    ArrayList<ImagesVideosAndroid10> directoriesList10 = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);


        //Reference Created
        toolbar = view.findViewById(R.id.toolbar2);
        playIcon = view.findViewById(R.id.cardView32);
        cameraTxt = view.findViewById(R.id.cameraTxt);
        spinnerFolder = view.findViewById(R.id.spinner);
        videoView = view.findViewById(R.id.videoPlayer);
        cameraIcon = view.findViewById(R.id.imageView4);
        progressBar = view.findViewById(R.id.progressBar14);
        appBarLayout = view.findViewById(R.id.appBarLayout);
        imageView = view.findViewById(R.id.cropperImageView);
        recyclerView = view.findViewById(R.id.recycler_view14);
        mainVideoLayout = view.findViewById(R.id.constraintLayout21);
        noPhotosToDisplay = view.findViewById(R.id.linearLayout29);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);


        /**
         * Checking The Android Version -- Because in Android 10 (Q) and above we can't access
         * External storage just by taking the READ_EXTERNAL_STORAGE Permission**/
        isAndroid10 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;


        //setting up Toolbar
        toolbar.inflateMenu(R.menu.post_fragment_menu);
        toolbar.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.post) {
                if (isAndroid10)
                    postFile();
                else {
                    if (selectedFile != null) {
                        /* When its a Video File */
                        if (MediaFilesScanner.isVideoFile(selectedFile))
                            openPostOrTrimmerActivity(selectedFile);
                        else
                            cropImage(Uri.fromFile(new File(selectedFile)));
                    } else
                        Snackbar.make(view.findViewById(android.R.id.content), "Nothing Selected.", Snackbar.LENGTH_SHORT).show();
                }
            }
            return true;
        });
        toolbar.setNavigationOnClickListener(view1 -> requireActivity().onBackPressed());
        cameraIcon.setOnClickListener(v -> openCamera());
        cameraTxt.setOnClickListener(v -> openCamera());


        //Requesting permissions from the user
        requestPermissionsFromUser();

        return view;
    }


    /**
     * For Android Version greater than 10(Q)
     */
    private void postFile() {

        if (selectedFilePath != null) {
            if (MediaFilesScanner.isVideoFile(selectedFilePath.getAbsPath()))
                openPostOrTrimmerActivity(selectedFilePath.getContentUri());
            else
                cropImage(Uri.parse(selectedFilePath.getContentUri()));
        } else
            Toast.makeText(getContext(), "Something went wrong. Please try again later.",
                    Toast.LENGTH_SHORT).show();

    }


    public void openPostOrTrimmerActivity(String selectedFile) {

        if (videoView.getDuration() < 45000) {
            Intent intent = new Intent(getContext(), PostActivity.class);
            intent.putExtra(SELECTED_FILE_URL, selectedFile);
            intent.putExtra(Constants.POST_TYPE, Constants.VIDEO_POST_TYPE);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(getContext(), "Video is greater than 45 seconds. Please crop the video.",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Intent intent = new Intent(getActivity(), VideoTrimmer.class);
            intent.putExtra(Constants.VIDEO_FILE_PATH, selectedFile);
            startActivity(intent);
        }
    }


    public void openCamera() {
        if (Permissions.isPermissionGrantedByApp(getContext())) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.CAMERA_REQUEST_CODE);
        } else
            requestPermissions(Permissions.PERMISSIONS, Permissions.PERMISSION_REQUEST_CODE);
    }


    private void setAdapter(List<String> paths) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), getNoOfCols(120),
                GridLayoutManager.VERTICAL, false);
        recyclerViewAdapt = new RecyclerViewAdapt(getContext(), paths, this);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapt);
    }


    void requestPermissionsFromUser() {

        if (Permissions.isPermissionGrantedByApp(getContext())) {
            getFileDirectories(Environment.getExternalStorageDirectory().getAbsolutePath());
            setSpinner();
        } else {
            requestPermissions(Permissions.PERMISSIONS, Permissions.PERMISSION_REQUEST_CODE);
        }
    }


    private void cropImage(Uri imageUri) {

        UCrop uCrop = UCrop.of(Objects.requireNonNull(imageUri), Uri.fromFile(new File(requireContext()
                .getCacheDir(), Objects.requireNonNull(imageUri.getLastPathSegment()))));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(500, 500);

        //Changing the UI of the Cropping Activity
        UCrop.Options options = new UCrop.Options();
        options.setStatusBarColor(requireActivity().getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(requireActivity().getResources().getColor(R.color.colorPrimary));
        options.setToolbarWidgetColor(getResources().getColor(android.R.color.white));
        options.setFreeStyleCropEnabled(true);
        options.setHideBottomControls(false);

        uCrop.withOptions(options);
        uCrop.start(Objects.requireNonNull(getActivity()), PostFragment.this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (data != null) {
            if (requestCode == UCrop.REQUEST_CROP) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra(SELECTED_FILE_URL, String.valueOf(UCrop.getOutput(data)));
                intent.putExtra(Constants.POST_TYPE, Constants.IMAGE_POST_TYPE);
                startActivity(intent);
            } else if (requestCode == Constants.CAMERA_REQUEST_CODE) {
                if (data.getExtras() != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bytes);
                    String image = MediaStore.Images.Media.insertImage(Objects.requireNonNull(getContext()).getContentResolver(), bitmap, "Title", null);
                    Log.d(TAG, "onActivityResult: " + image);
                    cropImage(Uri.parse(image));
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == Permissions.PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean flag = true;
                for (int permission : grantResults) {
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    //Get Directories and Setup spinner --> Starting new Thread.
                    getFileDirectories(Environment.getExternalStorageDirectory().getAbsolutePath());
                    setSpinner();
                } else {
                    Toast.makeText(getContext(), "Please Grant Permissions", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                }
            } else {
                Toast.makeText(getContext(), "Please Grant Permissions", Toast.LENGTH_LONG).show();
                requireActivity().onBackPressed();
            }
        }
    }


    private void setSpinner() {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.spinner_layout, directoriesNames);
        spinnerFolder.setAdapter(arrayAdapter);

        spinnerFolder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {

                if (isAndroid10) {
                    if (directoriesList10.size() != 0) {
                        String selectedDirectory = spinnerFolder.getSelectedItem().toString().toLowerCase();
                        for (ImagesVideosAndroid10 ob : directoriesList10) {
                            if (ob.getFolderName().toLowerCase().equals(selectedDirectory)) {
                                getFilesFromSelectedFolder(ob);
                                break;
                            }
                        }
                    } else
                        noFilesToDisplay();

                } else {
                    getSelectedDirectoryFiles(fileDirectories.get(position));
                    setAdapter(filePaths);
                    setTopWidget(filePaths.get(0));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private void getFilesFromSelectedFolder(ImagesVideosAndroid10 ob) {

        currentFolder = ob;
        filePaths10.clear();
        ArrayList<ImagesVideosAndroid10.FilePaths> obFiles = ob.getFiles();

        for (ImagesVideosAndroid10.FilePaths i : obFiles)
            filePaths10.add(i.getContentUri());

        setAdapter(filePaths10);
        setTopWidget(ob.getFiles().get(0));
    }


    private void setTopWidget(ImagesVideosAndroid10.FilePaths filePath) {

        if (MediaFilesScanner.isVideoFile(filePath.getAbsPath()))
            playVideo(filePath.getContentUri());
        else
            setTopImage(filePath.getContentUri());

        selectedFilePath = filePath;
    }


    private void setTopWidget(String url) {

        if (MediaFilesScanner.isVideoFile(url))
            playVideo(url);
        else
            setTopImage(url);
        selectedFile = url;
    }


    private void setTopImage(String imageUrl) {
        GlideImageLoader.loadImageWithProgress(getContext(), imageUrl, imageView, progressBar);
        videoView.stopPlayback();
        videoView.setVisibility(View.GONE);
        playIcon.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
    }


    /**
     * RecyclerView Item Clicked
     **/
    @Override
    public void itemClicked(int position) {

        if (isAndroid10) {
            if (currentFolder != null)
                setTopWidget(currentFolder.getFiles().get(position));
            else
                Toast.makeText(getContext(), "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
        } else
            setTopWidget(filePaths.get(position));


        appBarLayout.setExpanded(true, true);
        appBarLayout.setLiftOnScroll(true);
    }


    private void playVideo(String videoUrl) {
        videoView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        videoView.setVideoPath(videoUrl);
        playIcon.setVisibility(View.INVISIBLE);
        videoView.start();

        mainVideoLayout.setOnClickListener(v -> {
            if (!videoView.isPlaying()) {
                videoView.start();
                playIcon.setVisibility(View.INVISIBLE);
            } else {
                videoView.pause();
                playIcon.setVisibility(View.VISIBLE);
            }

        });

        videoView.setOnCompletionListener(mp -> videoView.start());
    }


    public int getNoOfCols(float colWidth) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidth / colWidth + 0.5);
    }


    public void getSelectedDirectoryFiles(String selectedDirectory) {

        filePaths.clear();
        File directory = new File(selectedDirectory);
        // get all the files from a directory
        File[] fList = directory.listFiles();
        assert fList != null;
        for (File file : fList) {
            if (file.isFile() && MediaFilesScanner.isFilePhotoOrVideo(file.getName())) {
                filePaths.add(0, file.getAbsolutePath());
            }
        }
    }


    public void getFileDirectories(String rootDirectoryPath) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ImagesVideosPath();
        else {
            /* For Android Version less than 10(Q) */
            File rootDirectory = new File(rootDirectoryPath);
            File[] files = rootDirectory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && MediaFilesScanner.isFilePhotoOrVideo(file.getName())) {
                        String parentDirectory = file.getParent();
                        if (!fileDirectories.contains(parentDirectory)) {
                            assert parentDirectory != null;
                            if (parentDirectory.contains("Camera")) {
                                fileDirectories.add(0, parentDirectory);
                                directoriesNames.add(0, parentDirectory.substring((parentDirectory.lastIndexOf("/") + 1)));
                            } else {
                                fileDirectories.add(parentDirectory);
                                directoriesNames.add(parentDirectory.substring((parentDirectory.lastIndexOf("/") + 1)));
                            }
                        }
                    } else if (file.isDirectory() && !(file.getName().startsWith(".")) && !(file.getName().startsWith("Android"))) {
                        getFileDirectories(file.getAbsolutePath());
                    }
                }
            } else
                noFilesToDisplay();
        }
    }


    public void noFilesToDisplay() {
        coordinatorLayout.setVisibility(View.GONE);
        noPhotosToDisplay.setVisibility(View.VISIBLE);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void ImagesVideosPath() {
        directoriesList10.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_id, column_index_data, column_index_folder_name;

        String contentUri, absoluteFilePath;
        uri = MediaStore.Files.getContentUri("external");

        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = Objects.requireNonNull(getContext()).getContentResolver()
                .query(uri, projection, null, null, orderBy + " DESC");

        if (cursor != null) {
            column_id = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            directoriesList10.clear();
            directoriesNames.clear();
            while (cursor.moveToNext()) {

                long imageId = cursor.getLong(column_id);
                absoluteFilePath = cursor.getString(column_index_data);
                contentUri = String.valueOf(ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"),
                        imageId));

                for (int i = 0; i < directoriesList10.size(); i++) {
                    if (directoriesList10.get(i).getFolderName().equals(cursor.getString(column_index_folder_name))) {
                        isFolder = true;
                        int_position = i;
                        break;
                    } else {
                        isFolder = false;
                    }
                }

                ArrayList<ImagesVideosAndroid10.FilePaths> al_path = new ArrayList<>();
                if (isFolder && directoriesList10.size() != 0) {
                    al_path.addAll(directoriesList10.get(int_position).getFiles());
                    al_path.add(new ImagesVideosAndroid10.FilePaths(absoluteFilePath, contentUri));
                    directoriesList10.get(int_position).setFiles(al_path);

                } else {
                    al_path.add(new ImagesVideosAndroid10.FilePaths(absoluteFilePath, contentUri));
                    ImagesVideosAndroid10 obj_model = new ImagesVideosAndroid10(cursor.getString(column_index_folder_name),
                            al_path);
                    directoriesList10.add(obj_model);
                    directoriesNames.add(cursor.getString(column_index_folder_name));
                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
            playIcon.setVisibility(View.VISIBLE);
        }
    }
}
