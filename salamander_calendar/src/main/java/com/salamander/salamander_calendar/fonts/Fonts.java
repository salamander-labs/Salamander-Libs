/*
 * Copyright (C) 2014 Marco Hernaiz Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.salamander.salamander_calendar.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

/**
 * The manager of roboto typefaces.
 *
 * @author Marco Hernaiz Cao
 */
public class Fonts {

    /*
     * Available fonts
     */
    public final static int FELBRIDGE = 0;
    public final static int FELBRIDGE_BOLD = 1;
    public final static int VERDANA_ITALIC = 2;

    private final static Map<String, Integer> typeFacesMap;
    private final static SparseArray<Typeface> mTypefaces = new SparseArray<>(20);

    static {
        typeFacesMap = new HashMap<>();
        typeFacesMap.put("regular", 0);
        typeFacesMap.put("bold", 1);
        typeFacesMap.put("italic", 2);
    }

    /**
     * Obtain typeface.
     *
     * @param context       The Context the widget is running in, through which it can access the current theme, resources, etc.
     * @param typefaceValue The value of "typeface" attribute
     * @return specify {@link Typeface}
     * @throws IllegalArgumentException if unknown `typeface` attribute value.
     */
    public static Typeface obtaintTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface = mTypefaces.get(typefaceValue);
        if (typeface == null) {
            typeface = createTypeface(context, typefaceValue);
            mTypefaces.put(typefaceValue, typeface);
        }
        return typeface;
    }

    /**
     * Obtain typeface.
     *
     * @param context             The Context the widget is running in, through which it can access the current theme, resources, etc.
     * @param typefaceValueString The value of "typeface" attribute
     * @return specify {@link Typeface}
     * @throws IllegalArgumentException if unknown `typeface` attribute value.
     */
    public static Typeface obtaintTypefaceFromString(Context context, String typefaceValueString) throws IllegalArgumentException {
        int typefaceValue = typeFacesMap.get(typefaceValueString);
        return obtaintTypeface(context, typefaceValue);
    }

    /**
     * Create typeface from assets.
     *
     * @param context       The Context the widget is running in, through which it can
     *                      access the current theme, resources, etc.
     * @param typefaceValue The value of "typeface" attribute
     * @return Roboto {@link Typeface}
     * @throws IllegalArgumentException if unknown `typeface` attribute value.
     */
    private static Typeface createTypeface(Context context, int typefaceValue)
            throws IllegalArgumentException {

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Felbridge.ttf");
        try {
            switch (typefaceValue) {
                case FELBRIDGE:
                    typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Felbridge.ttf");
                    break;
                case FELBRIDGE_BOLD:
                    typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Felbridge_Bold.otf");
                    break;
                case VERDANA_ITALIC:
                    typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Verdana_Italic.ttf");
                    break;
                default:
                    typeface = Typeface.createFromAsset(context.getAssets(),
                            "fonts/Felbridge.ttf");
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("font", e.toString());
        }
        return typeface;
    }

}
