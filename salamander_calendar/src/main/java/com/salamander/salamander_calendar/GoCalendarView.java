package com.salamander.salamander_calendar;

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
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.salamander.salamander_calendar.fonts.Fonts;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
    //bny
    public static ViewGroup today, prevSelected;
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

    // ************************************************************************************************************************************************************************
    // * Initialization methods
    // ************************************************************************************************************************************************************************
    private OnClickListener onDayOfMonthClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // Extract day selected
            ViewGroup dayOfMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfMonthContainer.getTag();
            tagId = tagId.substring(19, tagId.length());
            TextView dayOfMonthText = (TextView) view.findViewWithTag("dayOfMonthText" + tagId);

            // Fire event
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentCalendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayOfMonthText.getText().toString()));

            if (goCalendarListener == null) {
                throw new IllegalStateException("You must assing a valid GoCalendarListener first!");
            } else {
                goCalendarListener.onDateSelected(calendar.getTime());

            }
        }
    };

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

        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goCalendarListener == null) {
                    throw new IllegalStateException("You must assing a valid GoCalendarListener first!");
                }
                goCalendarListener.onLeftButtonClick();
            }
        });

        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goCalendarListener == null) {
                    throw new IllegalStateException("You must assing a valid GoCalendarListener first!");
                }
                goCalendarListener.onRightButtonClick();
            }
        });
    }

    // ************************************************************************************************************************************************************************
    // * Private auxiliary methods
    // ************************************************************************************************************************************************************************

    private void initializeComponentBehavior() {
        // Initialize calendar for current month
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);
        initializeCalendar(currentCalendar);
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

        String dateText = new DateFormatSymbols(locale).getMonths()[currentCalendar.get(Calendar.MONTH)];
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
        String[] weekDaysArray = new DateFormatSymbols(locale).getShortWeekdays();
        for (int i = 1; i < weekDaysArray.length; i++) {
            dayOfWeek = view.findViewWithTag("dayOfWeek" + getWeekIndex(i, currentCalendar));
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
        ImageView dayOfMonthImage;
        ViewGroup dayOfMonthContainer;

        for (int i = 1; i < 43; i++) {

            dayOfMonthContainer = view.findViewWithTag("dayOfMonthContainer" + i);
            dayOfMonthText = view.findViewWithTag("dayOfMonthText" + i);
            dayOfMonthImage = view.findViewWithTag("dayOfMonthImage" + i);
            dayOfMonthJmlOrder = view.findViewWithTag("dayOfMonthJmlOrder" + i);

            dayOfMonthText.setVisibility(View.INVISIBLE);
            dayOfMonthImage.setVisibility(View.INVISIBLE);
            //layoutDayOfMonthImage.setVisibility(View.INVISIBLE);
            dayOfMonthJmlOrder.setVisibility(View.INVISIBLE);

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
        Calendar auxCalendar = Calendar.getInstance(locale);
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
        prevSelected.setBackgroundResource(R.drawable.kotak);
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

    // ************************************************************************************************************************************************************************
    // * Event handler methods
    // ************************************************************************************************************************************************************************

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
    // * Public calendar methods
    // ************************************************************************************************************************************************************************

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
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);
        currentCalendar.setTime(currentCalendar.getTime());
        TextView dayOfMonth = getDayOfMonthText(currentCalendar);
        Typeface robotoTypeface = Fonts.obtaintTypefaceFromString(context, getResources().getString(R.string.currentDayOfMonthFont));

        ViewGroup dayOfMonthContainer = getDayOfMonthContainer(currentCalendar);
        today = dayOfMonthContainer;
        //dayOfMonthContainer.setBackgroundResource(R.drawable.calendar_blue_fill);
        dayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.jml_kegiatan));
        dayOfMonth.setText(Html.fromHtml("<b>" + dayOfMonth.getText().toString() + "</b>"));
    }

    public void markDayAsSelectedDay(Date currentDate) {

        // Clear previous marks
        if (prevSelected != null)
            clearDayOfMonthContainerBackground();

        Locale locale = context.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);
        currentCalendar.setTime(currentDate);
        ViewGroup dayOfMonthContainer = getDayOfMonthContainer(currentCalendar);
        //if (dayOfMonthContainer != today) {
        dayOfMonthContainer.setBackgroundResource(R.drawable.calendar_bg_day_selected);
        //}
        prevSelected = dayOfMonthContainer;
    }

    public void markDayWithStyle(int style, Date currentDate, int count) {
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);
        currentCalendar.setTime(currentDate);
        ImageView dayOfMonthImage = getDayOfMonthImage(currentCalendar);
        TextView dayOfMonthText = getDayOfMonthText(currentCalendar);
        TextView dayOfMonthCount = getDayOfMonthJmlOrder(currentCalendar);

        // Draw day with style
        dayOfMonthImage.setVisibility(View.VISIBLE);
        dayOfMonthCount.setVisibility(View.VISIBLE);
        dayOfMonthCount.setText(String.valueOf(count));
        dayOfMonthImage.setImageDrawable(null);
        dayOfMonthImage.setBackgroundResource(style);
        //dayOfMonthText.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/treb_bold.ttf"));
    }

    public void markHoliday(Date currentDate) {
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);
        currentCalendar.setTime(currentDate);
        TextView dayOfMonthText = getDayOfMonthText(currentCalendar);

        dayOfMonthText.setTextColor(ContextCompat.getColor(context, R.color.holiday));

    }

    public void markMultipleDays(ArrayList<String> dates) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        HashMap<String, Integer> maps = new HashMap<String, Integer>();
        for (String temp : dates) {
            Integer count = maps.get(temp);
            maps.put(temp, (count == null) ? 1 : count + 1);
        }
        for (HashMap.Entry<String, Integer> entry : maps.entrySet()) {
            try {
                String date = entry.getKey();
                int count = entry.getValue();
                Date d = format.parse(date);
                markDayWithStyle(BLUE_CIRCLE, d, count);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void markHolidays(ArrayList<String> dates) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        HashMap<String, Integer> maps = new HashMap<>();
        for (String temp : dates) {
            Integer count = maps.get(temp);
            maps.put(temp, (count == null) ? 1 : count + 1);
        }
        for (HashMap.Entry<String, Integer> entry : maps.entrySet()) {
            try {
                markHoliday(format.parse(entry.getKey()));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface GoCalendarListener {

        public void onDateSelected(Date date);

        public void onRightButtonClick();

        public void onLeftButtonClick();
    }
}
