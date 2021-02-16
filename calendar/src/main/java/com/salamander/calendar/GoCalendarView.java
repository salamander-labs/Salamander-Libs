package com.salamander.calendar;

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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.salamander.calendar.fonts.Fonts;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * The roboto calendar view
 *
 * @author Marco Hernaiz Cao
 */
public class GoCalendarView extends LinearLayout {

    // ************************************************************************************************************************************************************************
    // * Attributes
    // ************************************************************************************************************************************************************************

    public static final int RED_CIRCLE = R.drawable.calendar_red_circle;
    public static final int GREEN_CIRCLE = R.drawable.calendar_green_circle;
    public static final int BLUE_CIRCLE = R.drawable.calendar_blue_circle;
    // View
    private Context context;
    private TextView monthTitle, yearTitle;
    private TextView leftButton;
    private TextView rightButton;
    private View view;

    // Class
    private GoCalendarListener goCalendarListener;
    private Calendar currentCalendar;
    private Locale locale;

    // Style
    private int monthTitleColor;
    private int monthTitleFont;
    private int dayOfWeekColor;
    private int dayOfWeekFont;
    private int dayOfMonthColor;
    private int dayOfMonthFont;

    //bny
    private ViewGroup prevDaySelected;
    private int currentMonthIndex = 0, prevMonthIndex = 0;

    // ************************************************************************************************************************************************************************
    // * Initialization methods
    // ************************************************************************************************************************************************************************

    public GoCalendarView(Context context) {
        super(context);
        this.context = context;
        onCreateView();
    }

    public GoCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (isInEditMode()) {
            return;
        }
        getAttributes(context, attrs);
        onCreateView();
    }

    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GoCalendarView, 0, 0);
        monthTitleColor = typedArray.getColor(R.styleable.GoCalendarView_monthTitleColor, ContextCompat.getColor(context, R.color.monthTitleColor));
        monthTitleFont = typedArray.getInt(R.styleable.GoCalendarView_monthTitleFont, R.string.monthTitleFont);
        dayOfWeekColor = typedArray.getColor(R.styleable.GoCalendarView_dayOfWeekColor, ContextCompat.getColor(context, R.color.dayOfWeekColor));
        dayOfWeekFont = typedArray.getInt(R.styleable.GoCalendarView_dayOfWeekFont, R.string.dayOfWeekFont);
        dayOfMonthColor = typedArray.getColor(R.styleable.GoCalendarView_dayOfMonthColor, ContextCompat.getColor(context, R.color.dayOfMonthColor));
        dayOfMonthFont = typedArray.getInt(R.styleable.GoCalendarView_dayOfMonthFont, R.string.dayOfMonthFont);
        typedArray.recycle();
    }

    public View onCreateView() {

        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflate.inflate(R.layout.go_calendar_picker_layout, this, true);

        findViewsById(view);

        initializeEventListeners();

        initializeComponentBehavior();

        return view;
    }

    private void findViewsById(View view) {
        leftButton = view.findViewById(R.id.leftButton);
        rightButton = view.findViewById(R.id.rightButton);
        monthTitle = view.findViewWithTag("monthTitle");
        yearTitle = view.findViewWithTag("yearTitle");
    }

    private void initializeEventListeners() {

        leftButton.setOnClickListener(v -> {
            if (goCalendarListener == null) {
                throw new IllegalStateException("You must assing a valid GoCalendarListener first!");
            }
            currentMonthIndex--;
            goCalendarListener.onLeftButtonClick();
        });

        rightButton.setOnClickListener(v -> {
            if (goCalendarListener == null) {
                throw new IllegalStateException("You must assing a valid GoCalendarListener first!");
            }
            currentMonthIndex++;
            goCalendarListener.onRightButtonClick();
        });
    }

    // ************************************************************************************************************************************************************************
    // * Private auxiliary methods
    // ************************************************************************************************************************************************************************

    private void initializeComponentBehavior() {
        // Initialize calendar for current month
        Calendar currentCalendar = Calendar.getInstance(getLocale());
        initializeCalendar(currentCalendar);
    }

    private Locale getLocale() {
        if (locale == null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                locale = context.getResources().getConfiguration().getLocales().getFirstMatch(new String[]{"us", "en", "id"});
            else locale = context.getResources().getConfiguration().locale;
        return locale;
    }

    @SuppressLint("DefaultLocale")
    private void initializeTitleLayout() {
        // Apply styles
        String font = getResources().getString(monthTitleFont);
        Typeface robotoTypeface = Fonts.obtaintTypefaceFromString(context, font);
        int color = monthTitleColor;
        monthTitle.setTypeface(robotoTypeface);
        yearTitle.setTypeface(robotoTypeface);
        monthTitle.setTextColor(color);
        yearTitle.setTextColor(color);

        String dateText = new DateFormatSymbols(getLocale()).getMonths()[currentCalendar.get(Calendar.MONTH)];
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length());
        monthTitle.setText(dateText);
        yearTitle.setText(String.valueOf(currentCalendar.get(Calendar.YEAR)));
    }

    @SuppressLint("DefaultLocale")
    private void initializeWeekDaysLayout() {

        // Apply styles
        String font = getResources().getString(dayOfWeekFont);
        Typeface robotoTypeface = Fonts.obtaintTypefaceFromString(context, font);
        int color = dayOfWeekColor;

        TextView dayOfWeek;
        String dayOfTheWeekString;
        String[] weekDaysArray = new DateFormatSymbols(getLocale()).getShortWeekdays();
        for (int i = 1; i < weekDaysArray.length; i++) {
            dayOfWeek = (TextView) view.findViewWithTag("dayOfWeek" + getWeekIndex(i, currentCalendar));
            dayOfTheWeekString = weekDaysArray[i];

            // Check it for languages with only one week day lenght
            if (dayOfTheWeekString.length() > 1) {
                dayOfTheWeekString = dayOfTheWeekString.substring(0, 1).toUpperCase() + dayOfTheWeekString.subSequence(1, 2);
            }

            dayOfWeek.setText(dayOfTheWeekString);

            // Apply styles
            dayOfWeek.setTypeface(robotoTypeface);
            dayOfWeek.setTextColor(color);
        }
    }

    private void initializeDaysOfMonthLayout() {

        // Apply styles
        String font = getResources().getString(dayOfMonthFont);
        Typeface robotoTypeface = Fonts.obtaintTypefaceFromString(context, font);
        int color = dayOfMonthColor;
        TextView dayOfMonthText, dayOfMonthJmlOrder;
        ImageView imgUnReadOrder;
        ViewGroup dayOfMonthContainer;

        for (int i = 1; i < 43; i++) {

            dayOfMonthContainer = view.findViewWithTag("dayOfMonthContainer" + i);
            dayOfMonthText = view.findViewWithTag("dayOfMonthText" + i);
            //dayOfMonthImage = view.findViewWithTag("dayOfMonthImage" + i);
            dayOfMonthJmlOrder = view.findViewWithTag("dayOfMonthJmlOrder" + i);
            imgUnReadOrder = view.findViewWithTag("imgUnReadOrder" + i);

            imgUnReadOrder.setVisibility(View.GONE);
            dayOfMonthJmlOrder.setVisibility(View.GONE);
            dayOfMonthText.setVisibility(View.INVISIBLE);
            //dayOfMonthImage.setVisibility(View.INVISIBLE);

            // Apply styles
            dayOfMonthText.setTypeface(robotoTypeface);
            if (mod(i - 1, 7) == 0 || mod(i, 7) == 0)
                dayOfMonthText.setTextColor(ContextCompat.getColor(context, R.color.holiday));
            else
                dayOfMonthText.setTextColor(color);
            dayOfMonthText.setBackgroundResource(android.R.color.transparent);

            dayOfMonthContainer.setBackgroundResource(R.drawable.kotak);
            dayOfMonthContainer.setOnClickListener(null);
        }
    }

    private int mod(int x, int y) {
        int result = x % y;
        return result < 0 ? result + y : result;
    }

    private void setDaysInCalendar() {
        Calendar auxCalendar = Calendar.getInstance(getLocale());
        auxCalendar.setTime(currentCalendar.getTime());
        auxCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = auxCalendar.get(Calendar.DAY_OF_WEEK);
        TextView dayOfMonthText;
        ViewGroup dayOfMonthContainer;

        // Calculate dayOfMonthIndex
        int dayOfMonthIndex = getWeekIndex(firstDayOfMonth, auxCalendar);
        int maxDate = auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i < 43; i++) {
            if (i < dayOfMonthIndex || i >= maxDate + dayOfMonthIndex) {
                dayOfMonthContainer = view.findViewWithTag("dayOfMonthContainer" + i);
                dayOfMonthContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.dateSeparatorColor));
            }
        }
        for (int i = 1; i <= maxDate; i++, dayOfMonthIndex++) {
            dayOfMonthContainer = view.findViewWithTag("dayOfMonthContainer" + dayOfMonthIndex);
            dayOfMonthText = view.findViewWithTag("dayOfMonthText" + dayOfMonthIndex);
            if (dayOfMonthText == null) {
                break;
            }
            dayOfMonthContainer.setOnClickListener(onDayOfMonthClickListener);
            dayOfMonthText.setVisibility(View.VISIBLE);
            dayOfMonthText.setText(String.valueOf(i));
        }

        // If the last week row has no visible days, hide it or show it in case
        ViewGroup weekRow = view.findViewWithTag("weekRow6");
        dayOfMonthText = view.findViewWithTag("dayOfMonthText36");
        if (dayOfMonthText.getVisibility() == INVISIBLE) {
            weekRow.setVisibility(GONE);
        } else {
            weekRow.setVisibility(VISIBLE);
        }
    }

    private void clearDayOfMonthContainerBackground() {
        prevDaySelected.setBackgroundResource(R.drawable.kotak);
    }

    private ViewGroup getDayOfMonthContainer(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        ViewGroup dayOfMonthContainer = view.findViewWithTag("dayOfMonthContainer" + (currentDay + monthOffset));
        return dayOfMonthContainer;
    }

    private TextView getDayOfMonthText(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        TextView dayOfMonth = view.findViewWithTag("dayOfMonthText" + (currentDay + monthOffset));
        return dayOfMonth;
    }

    private ImageView getDayOfMonthImage(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        ImageView dayOfMonth = view.findViewWithTag("dayOfMonthImage" + (currentDay + monthOffset));
        return dayOfMonth;
    }

    private ImageView getImgUnReadOrder(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        ImageView imgUnReadOrder = view.findViewWithTag("imgUnReadOrder" + (currentDay + monthOffset));
        return imgUnReadOrder;
    }

    private TextView getDayOfMonthJmlOrder(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        TextView dayOfMonth = view.findViewWithTag("dayOfMonthJmlOrder" + (currentDay + monthOffset));
        return dayOfMonth;
    }

    private int getMonthOffset(Calendar currentCalendar) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayWeekPosition = calendar.getFirstDayOfWeek();
        int dayPosition = calendar.get(Calendar.DAY_OF_WEEK);

        if (firstDayWeekPosition == 1) {
            return dayPosition - 1;
        } else {

            if (dayPosition == 1) {
                return 6;
            } else {
                return dayPosition - 2;
            }
        }
    }

    private int getWeekIndex(int weekIndex, Calendar currentCalendar) {
        int firstDayWeekPosition = currentCalendar.getFirstDayOfWeek();

        if (firstDayWeekPosition == 1) {
            return weekIndex;
        } else {

            if (weekIndex == 1) {
                return 7;
            } else {
                return weekIndex - 1;
            }
        }
    }

    // ************************************************************************************************************************************************************************
    // * Event handler methods
    // ************************************************************************************************************************************************************************

    private OnClickListener onDayOfMonthClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // Extract day selected
            ViewGroup dayOfMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfMonthContainer.getTag();
            tagId = tagId.substring(19);
            TextView dayOfMonthText = view.findViewWithTag("dayOfMonthText" + tagId);

            // Fire event
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentCalendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfMonthText.getText().toString()));

            if (goCalendarListener == null) {
                throw new IllegalStateException("You must assing a valid GoCalendarListener first!");
            } else {
                goCalendarListener.onDateSelected(calendar.getTimeInMillis());

            }
        }
    };

    // ************************************************************************************************************************************************************************
    // * Public calendar methods
    // ************************************************************************************************************************************************************************

    public interface GoCalendarListener {

        public void onDateSelected(long date);

        public void onRightButtonClick();

        public void onLeftButtonClick();
    }

    public void setGoCalendarListener(GoCalendarListener goCalendarListener) {
        this.goCalendarListener = goCalendarListener;
    }

    @SuppressLint("DefaultLocale")
    public void initializeCalendar(Calendar currentCalendar) {

        this.currentCalendar = currentCalendar;
        locale = new Locale("id");

        // Set date title
        initializeTitleLayout();

        // Set weeks days titles
        initializeWeekDaysLayout();

        // Initialize days of the month
        initializeDaysOfMonthLayout();

        // Set days in calendar
        setDaysInCalendar();
    }

    public void markDayAsCurrentDay() {
        Calendar currentCalendar = Calendar.getInstance(getLocale());
        currentCalendar.setTime(currentCalendar.getTime());
        TextView dayOfMonth = getDayOfMonthText(currentCalendar);
        Typeface robotoTypeface = Fonts.obtaintTypefaceFromString(context, getResources().getString(R.string.currentDayOfMonthFont));

        ViewGroup dayOfMonthContainer = getDayOfMonthContainer(currentCalendar);
        //today = dayOfMonthContainer;
        //dayOfMonthContainer.setBackgroundResource(R.drawable.calendar_blue_fill);
        dayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.jml_kegiatan));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            dayOfMonth.setText(Html.fromHtml("<b>" + dayOfMonth.getText().toString() + "</b>"));
        else
            dayOfMonth.setText(Html.fromHtml("<b>" + dayOfMonth.getText().toString() + "</b>", Html.FROM_HTML_MODE_LEGACY));
    }
    public void markDayAsSelectedDay(Date currentDate) {
        markDayAsSelectedDay(currentDate.getTime());
    }

    public void markDayAsSelectedDay(long currentDateMillis) {

        // Clear previous marks
        Calendar.getInstance().setTimeInMillis(currentDateMillis);
        if ((prevMonthIndex == currentMonthIndex) && (prevDaySelected != null))
            clearDayOfMonthContainerBackground();

        Calendar currentCalendar = Calendar.getInstance(getLocale());
        currentCalendar.setTimeInMillis(currentDateMillis);
        ViewGroup dayOfMonthContainer = getDayOfMonthContainer(currentCalendar);
        //if (dayOfMonthContainer != today) {
        dayOfMonthContainer.setBackgroundResource(R.drawable.calendar_bg_day_selected);
        //}
        prevMonthIndex = currentMonthIndex;
        prevDaySelected = dayOfMonthContainer;
    }

    public void markDayWithStyle(int style, long currentDate, int count) {
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);
        currentCalendar.setTimeInMillis(currentDate);
        //ImageView dayOfMonthImage = getDayOfMonthImage(currentCalendar);
        TextView dayOfMonthText = getDayOfMonthText(currentCalendar);
        TextView dayOfMonthCount = getDayOfMonthJmlOrder(currentCalendar);

        // Draw day with style
        //dayOfMonthImage.setVisibility(View.VISIBLE);
        dayOfMonthCount.setVisibility(View.VISIBLE);
        dayOfMonthCount.setText(String.valueOf(count));
        //LinearLayout linearLayout = getLLDayOfMonthJmlOrder(currentCalendar);
        if (count == 0)
            dayOfMonthCount.setVisibility(GONE);
        else dayOfMonthCount.setVisibility(VISIBLE);

        //dayOfMonthImage.setImageDrawable(null);
        //dayOfMonthImage.setBackgroundResource(style);
        //dayOfMonthText.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/treb_bold.ttf"));
    }

    public void markHoliday(long currentDate) {
        Calendar currentCalendar = Calendar.getInstance(getLocale());
        currentCalendar.setTimeInMillis(currentDate);
        TextView dayOfMonthText = getDayOfMonthText(currentCalendar);

        dayOfMonthText.setTextColor(ContextCompat.getColor(context, R.color.holiday));

    }

    public void markMultipleDays(ArrayList<String> dates) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        HashMap<String, Integer> maps = new HashMap<>();
        for (String temp : dates) {
            Integer count = maps.get(temp);
            maps.put(temp, (count == null) ? 1 : count + 1);
        }
        for (HashMap.Entry<String, Integer> entry : maps.entrySet()) {
            try {
                String date = entry.getKey();
                int count = entry.getValue();
                Date d = format.parse(date);
                assert d != null;
                markDayWithStyle(BLUE_CIRCLE, d.getTime(), count);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void markUnread(ArrayList<String> dates) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        HashMap<String, Integer> maps = new HashMap<>();
        for (String temp : dates) {
            Integer count = maps.get(temp);
            maps.put(temp, (count == null) ? 1 : count + 1);
        }
        for (HashMap.Entry<String, Integer> entry : maps.entrySet()) {
            try {
                String date = entry.getKey();
                int count = entry.getValue();
                if (count > 0)
                    markUnreadWithStyle(format.parse(date));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void markUnreadWithStyle(Date currentDate) {
        Calendar currentCalendar = Calendar.getInstance(getLocale());
        currentCalendar.setTime(currentDate);
        ImageView imgUnReadOrder = getImgUnReadOrder(currentCalendar);

        imgUnReadOrder.setVisibility(VISIBLE);
    }

    public void markHolidays(ArrayList<String> dates) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        HashMap<String, Integer> maps = new HashMap<>();
        for (String temp : dates) {
            Integer count = maps.get(temp);
            maps.put(temp, (count == null) ? 1 : count + 1);
        }
        for (HashMap.Entry<String, Integer> entry : maps.entrySet()) {
            try {
                markHoliday(Objects.requireNonNull(format.parse(entry.getKey())).getTime());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
