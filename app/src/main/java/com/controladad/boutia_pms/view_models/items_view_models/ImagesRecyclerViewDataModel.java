package com.controladad.boutia_pms.view_models.items_view_models;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.BaseObservable;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.view.View;
import android.widget.Toast;

import com.controladad.boutia_pms.activities.MainActivity;
import com.controladad.boutia_pms.adapters.ImagesAdapter;
import com.controladad.boutia_pms.models.data_models.ProjectModel;
import com.controladad.boutia_pms.utility.BindHelpers;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

public class ImagesRecyclerViewDataModel extends BaseObservable implements GeneralDataModel {

    private MainActivity mainActivity;
    private List<ImagesItemsDataModel> imagesItemsDataModelList = new ArrayList<>();



    private MainActivity getActivity() {
        if(mainActivity == null)
            mainActivity = BoutiaApplication.INSTANCE.getMainActivity();
        return BoutiaApplication.INSTANCE.getMainActivity();
    }


    @Setter
    @Getter
    private String itemName;
    @Getter
    private boolean isItemFilled = true;

    @Getter
    private ImagesAdapter adapter = new ImagesAdapter();

    public ImagesRecyclerViewDataModel() {
        ProjectModel.ReviewModel reviewModel = BoutiaApplication.INSTANCE.getMainActivityVM().getReviewModel();
        for (String path:reviewModel.getImagePathList())
            imagesItemsDataModelList.add(new ImagesItemsDataModel(path));
        adapter.updateData(imagesItemsDataModelList);
    }

    @Override
    public int getKey() {
        return Constants.IMAGES_RECYCLER_VIEW_ITEM_KEY;
    }
    public View.OnClickListener onAddImageClicked(){
        return v -> {
            if(BoutiaApplication.INSTANCE.getMainActivityVM().getReviewModel().getElectricTowerNumber().getNumber()==null &&
                    !Objects.equals(BoutiaApplication.INSTANCE.getMainActivityVM().getReviewModel().getCheckupType().getType(),"تحویل و تحول")){
                Toast.makeText(getActivity(),"قبل از اضافه کردن عکس بارکد دکل را اسکن کنید",Toast.LENGTH_LONG).show();
            }
            else {
                getActivity().setImagesRecyclerViewDataModel(this);
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constants.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.MY_PERMISSIONS_REQUEST_CAMERA);

                } else {
                /*StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());*/
                    dispatchTakePictureIntent();
                }
            }

        };
    }

    public void saveImage(Intent data) {
        int width = 350;
        int height = 500;
        Bitmap bitmap = BindHelpers.decodeSampledBitmapFromPath(mCurrentPhotoPath,width,height);

        File file = new File(mCurrentPhotoPath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        imagesItemsDataModelList.add(new ImagesItemsDataModel(mCurrentPhotoPath));
        BoutiaApplication.INSTANCE.getMainActivityVM().getReviewModel().getImagePathList().add(mCurrentPhotoPath);

     /*   String mTowerNumber = null;
        if(BoutiaApplication.INSTANCE.getMainActivityVM().getReviewModel().getElectricTowerNumber().getNumber() != null)
            mTowerNumber = BoutiaApplication.INSTANCE.getMainActivityVM().getReviewModel().getElectricTowerNumber().getNumber();
        String finalMTowerNumber = mTowerNumber;

        Disposable disposable1 =  Flowable.fromCallable(() -> {
            BoutiaApplication.INSTANCE.getDb().getImageDao()
                    .insertImages(new ImageEntity(BoutiaApplication.INSTANCE.getMainActivityVM().getReviewModel().getMid(),
                            null,
                            mCurrentPhotoPath,
                            finalMTowerNumber,
                            false));
            return false;
        }).subscribeOn(Schedulers.io())
                .subscribe((s) -> {
        }, throwable -> {
                    Timber.d("error");
        });*/

        adapter.updateData(imagesItemsDataModelList);
        Timber.d("test");

    }
    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI;
                if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT))
                    photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.controladad.boutia_pms.fileprovider",
                            photoFile);
                else
                    photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                getActivity().startActivityForResult(takePictureIntent, Constants.ADD_IMAGE_REQUEST_CODE);
            }
        }
    }
}
