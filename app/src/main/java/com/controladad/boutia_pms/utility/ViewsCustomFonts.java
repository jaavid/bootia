package com.controladad.boutia_pms.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;


public class ViewsCustomFonts {
    static ViewsCustomFonts viewsCustomFonts ;
    HashMap<String,String> fontMap = new HashMap<>();
    public static ViewsCustomFonts getInstance()    {
        if(viewsCustomFonts==null)
            viewsCustomFonts= new ViewsCustomFonts();
        return viewsCustomFonts;
    }
    public void addFont(String alias, String fontName){
        fontMap.put(alias,fontName);
    }
    public Typeface getFont(String alias, Context context)
    {
        String fontFilename = fontMap.get(alias);
        if (fontFilename == null) {
            Log.e("", "Font not available with name " + alias);
            return null;
        }
        else
        {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontFilename);
            return typeface;
        }
    }
}
