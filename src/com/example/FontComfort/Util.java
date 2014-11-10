package com.example.FontComfort;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.WeakHashMap;

public class Util {

    // Remenber to initialize AssetManager;
    public static AssetManager M_ASSET_MANAGER = null;

    public final static Typeface DEFAULT_TYPEFACE = Typeface.MONOSPACE;

    public static WeakHashMap<String, Typeface> mTypeFaces = new WeakHashMap<String, Typeface>();

    public static void init(AssetManager am) {
        M_ASSET_MANAGER = am;
    }

    public static Typeface getTypeface(String str) {
        if (str.equals("DEFAULT"))
            return Typeface.create(Typeface.DEFAULT, 0);
        else if (str.toUpperCase(Locale.US).equals("DEFAULT_BOLD"))
            return Typeface.create(Typeface.DEFAULT_BOLD, 0);
        else if (str.toUpperCase(Locale.US).equals("MONOSPACE"))
            return Typeface.create(Typeface.MONOSPACE, 0);
        else if (str.toUpperCase(Locale.US).equals("SANS_SERIF"))
            return Typeface.create(Typeface.SANS_SERIF, 0);
        else if (str.equals("SERIF"))
            return Typeface.create(Typeface.SERIF, 0);

        Typeface ty = mTypeFaces.get(str);
        if (ty != null)
            return ty;

        File file = new File(str);
        Log.d("TypeFace", "File Load Path : " + str);

        if (file.isFile()) {
            ty = Typeface.createFromFile(file);
        } else {
            if (M_ASSET_MANAGER == null) {
                Log.e("TypeFace", "Can not get AssetManager Status");
                return null;
            }
            try {
                ty = Typeface.createFromAsset(M_ASSET_MANAGER, str);
            } catch (Exception ex) {
                ex.printStackTrace();
                return Typeface.create(DEFAULT_TYPEFACE, 0);
            }
        }
        mTypeFaces.put(str, ty);
        return ty;

    }
}
