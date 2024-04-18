package com.controladad.boutia_pms.utility;

import android.app.Activity;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.activities.MainActivity;
import com.controladad.boutia_pms.adapters.anims.ExpandableItemIndicator;
import com.controladad.boutia_pms.view_models.MainActivityVM;
import com.squareup.picasso.Picasso;

import cn.refactor.library.SmoothCheckBox;


public class BindHelpers extends BaseObservable {


    @BindingAdapter(value = {"postImageUrl","without_place_holder","display_height_per_height"},requireAll = false)
    public static void loadImage(ImageView view, String postImageURL, boolean withoutPlaceHolder, int displayHeightPerHeight) {
        if(displayHeightPerHeight==0) displayHeightPerHeight = 10;
        int height = MainActivityVM.displayHeight/displayHeightPerHeight;
        String imageUrl="x";
        if(postImageURL!=null && postImageURL.length()>7) imageUrl = Constants.BOUTIA_IMAGES_FILES + postImageURL.substring(8);
        if(!withoutPlaceHolder)
            Picasso.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.user_profile_default)
                .resize(0,height)
                .into(view);
        else Picasso.with(view.getContext())
                .load(imageUrl)
                .resize(0,height)
                .into(view);
    }
    @BindingAdapter({"textviewfont"})
    public static void setFont(TextView textView, String fontName) {
        textView.setTypeface(ViewsCustomFonts.getInstance().getFont(fontName,textView.getContext()));
    }


    @BindingAdapter({"text_input_font"})
    public static void setFont(TextInputLayout textInputLayout, String fontName) {
        textInputLayout.setTypeface(ViewsCustomFonts.getInstance().getFont(fontName,textInputLayout.getContext()));
    }

    @BindingAdapter({"smooth_check_box"})
    public static void setOnCheckedChangeListener(SmoothCheckBox smoothCheckBox, SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListener) {
        smoothCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @BindingAdapter({"hideSoftKeyboard"})
    public static void hideKeyboard(TextView view, FragmentActivity mainActivity){
        view.setOnFocusChangeListener((v , b)->{if(!b)hideKeyboardInFocus(v, mainActivity);});
    }

    private static void hideKeyboardInFocus(View view, FragmentActivity mainActivity) {
        InputMethodManager inputMethodManager =(InputMethodManager)mainActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @BindingAdapter(value = {"adapter_horizontal","activity"},requireAll = false)
    public static void setHorizontalAdapter (RecyclerView recyclerView, RecyclerView.Adapter adapter, MainActivity mainActivity){
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL,true));
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter(value = {"adapter_vertical","activity","number_of_columns"},requireAll = false)
    public static void setVerticalAdapter (RecyclerView recyclerView, RecyclerView.Adapter adapter, MainActivity mainActivity, int numberOfColumns){
        if(numberOfColumns>1) recyclerView.setLayoutManager(new StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL));
        else recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter({"image_src"})
    public static void setImage(ImageView imageView, int imageResource){
        imageView.setImageResource(imageResource);
    }

    @BindingAdapter({"image_drawable"})
    public static void setImage(ImageView imageView, Drawable drawable){
        imageView.setImageDrawable(drawable);
    }

    @BindingAdapter({"image_path"})
    public static void setImage(ImageView imageView, String path){

        int width = MainActivityVM.displayWidth/10;
        int height = MainActivityVM.displayHeight/10;
        Bitmap bitmap = decodeSampledBitmapFromPath(path,width,height);
        try {
            imageView.setImageBitmap(bitmap);
        }catch (OutOfMemoryError e){
            width = width/2;
            height = height/2;
            Bitmap bitmap2 = decodeSampledBitmapFromPath(path,width,height);
            try {
                imageView.setImageBitmap(bitmap2);
            }catch (OutOfMemoryError e2){
                Toast.makeText(imageView.getContext(),"حافظه پر است",Toast.LENGTH_LONG).show();
            }
        }

        /*Picasso.with(imageView.getContext())
                .load(path)
                .resize(width,height)
                .into(imageView);
*/
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    @BindingAdapter(value = {"expanded_expandable","animate_expandable"}, requireAll = false)
    public static void animate(ExpandableItemIndicator indicator, boolean isExpanded, boolean animate){
        indicator.setExpandedState(isExpanded,animate);

    }
}
