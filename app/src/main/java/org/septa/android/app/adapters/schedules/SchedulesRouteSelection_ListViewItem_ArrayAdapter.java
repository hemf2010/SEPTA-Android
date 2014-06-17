/*
 * SchedulesTransportType_ListViewItem_ArrayAdapter.java
 * Last modified on 05-05-2014 16:51-0400 by brianhmayo
 *
 * Copyright (c) 2014 SEPTA.  All rights reserved.
 */

package org.septa.android.app.adapters.schedules;

import android.content.Context;
import android.graphics.Color;
import android.nfc.Tag;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import org.septa.android.app.R;
import org.septa.android.app.managers.SchedulesFavoritesAndRecentlyViewedStore;
import org.septa.android.app.models.ObjectFactory;
import org.septa.android.app.models.SchedulesFavoriteModel;
import org.septa.android.app.models.SchedulesRecentlyViewedModel;
import org.septa.android.app.models.RouteTypes;
import org.septa.android.app.models.SchedulesRouteModel;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SchedulesRouteSelection_ListViewItem_ArrayAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer  {
    public static final String TAG = SchedulesRouteSelection_ListViewItem_ArrayAdapter.class.getName();
    private final Context mContext;
    private LayoutInflater mInflater;

    private ArrayList<SchedulesFavoriteModel> favorites = new ArrayList<SchedulesFavoriteModel>();
    private ArrayList<SchedulesRecentlyViewedModel> recentlyViewed = new ArrayList<SchedulesRecentlyViewedModel>();
    protected ArrayList<SchedulesRouteModel> routes = new ArrayList<SchedulesRouteModel>();

    private SchedulesFavoritesAndRecentlyViewedStore schedulesFavoritesAndRecentlyViewedStore;

    String[] resourceEndNames;
    String leftImageStartName;
    String rightImageBackgroundName;

    RouteTypes routeType = null;

    private String[] sectionTitles = new String[]{ "Favorites", "Recently Viewed", "Routes"};

    public SchedulesRouteSelection_ListViewItem_ArrayAdapter(Context context, RouteTypes routeType) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        this.routeType = routeType;

        schedulesFavoritesAndRecentlyViewedStore = ObjectFactory.getInstance().getSchedulesFavoritesAndRecentlyViewedStore(context);
        reloadFavoriteAndRecentlyViewedLists();

        resourceEndNames = context.getResources().getStringArray(R.array.schedulesfragment_listview_bothimage_endnames);
        leftImageStartName = context.getString(R.string.schedulesfragment_listview_leftimage_startname);
        rightImageBackgroundName = context.getString(R.string.schedulesfragment_listview_rightimage_startname);
    }

    public void reloadFavoriteAndRecentlyViewedLists() {
        recentlyViewed = schedulesFavoritesAndRecentlyViewedStore.getRecentlyViewedList(routeType.name());
        favorites = schedulesFavoritesAndRecentlyViewedStore.getFavoriteList(routeType.name());
    }

    public void setSchedulesRouteModel(ArrayList<SchedulesRouteModel> routes) {
        this.routes = routes;

        notifyDataSetChanged();
    }

    protected Object[] getItems() {
        ArrayList<Object> items = new ArrayList<Object>(getCount());

//        Log.d(TAG, "count of favorites is "+favorites.size());
//        Log.d(TAG, "count of recently viewed is "+recentlyViewed.size());
//        Log.d(TAG, "count of routes is "+routes.size());

        items.addAll(favorites); items.addAll(recentlyViewed); items.addAll(routes);

        return items.toArray();
    }

    public boolean isFavorite(int position) {

        return getItems()[position] instanceof SchedulesFavoriteModel;
    }

    public boolean isRecentlyViewed(int position) {

        return getItems()[position] instanceof SchedulesRecentlyViewedModel;
    }

    @Override
    public int getCount() {
        return favorites.size()+recentlyViewed.size()+routes.size();
    }

    @Override
    public Object getItem(int position) {

        return getItems()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int adjustedPosition(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = null;

        if (isFavorite(adjustedPosition(position))) {     // favorite position rows
            rowView = mInflater.inflate(R.layout.schedules_routeselection_favoriteandrecentlyviewed_listview_item, parent, false);

            TextView routeIdTextView = (TextView)rowView.findViewById(R.id.schedules_routeselection_favoriterecentlyviewed_routeid_textview);
            TextView startStopNameTextView = (TextView)rowView.findViewById(R.id.schedules_routeselection_favoriterecentlyviewed_start_textview);
            TextView endStopNameTextView = (TextView)rowView.findViewById(R.id.schedules_routeselection_favoriterecentlyviewed_end_textview);

            SchedulesFavoriteModel favoriteModel = (SchedulesFavoriteModel)getItems()[adjustedPosition(position)];

            routeIdTextView.setText(favoriteModel.getRouteCode());
            startStopNameTextView.setText(favoriteModel.getRouteStartName());
            endStopNameTextView.setText(favoriteModel.getRouteEndName());

            // to create a larger space in the ListView, each row has a transparent view built in
            // if we are not the last row in the logical section, make it gone, else visible
            View transparentView = (View)rowView.findViewById(R.id.schedules_routeselection_favoriteandrecentlyviewed_transparentview);
            if (adjustedPosition(position) == favorites.size()) {
                transparentView.setVisibility(View.VISIBLE);
            } else {
                transparentView.setVisibility(View.GONE);
            }
        } else {                        // recently viewed position rows
            if (isRecentlyViewed(adjustedPosition(position))) {
                rowView = mInflater.inflate(R.layout.schedules_routeselection_favoriteandrecentlyviewed_listview_item, parent, false);

                TextView routeIdTextView = (TextView)rowView.findViewById(R.id.schedules_routeselection_favoriterecentlyviewed_routeid_textview);
                TextView startStopNameTextView = (TextView)rowView.findViewById(R.id.schedules_routeselection_favoriterecentlyviewed_start_textview);
                TextView endStopNameTextView = (TextView)rowView.findViewById(R.id.schedules_routeselection_favoriterecentlyviewed_end_textview);

                SchedulesRecentlyViewedModel recentlyViewedModel = (SchedulesRecentlyViewedModel)getItems()[adjustedPosition(position)];

                routeIdTextView.setText(recentlyViewedModel.getRouteCode());
                startStopNameTextView.setText(recentlyViewedModel.getRouteStartName());
                endStopNameTextView.setText(recentlyViewedModel.getRouteEndName());

                // to create a larger space in the ListView, each row has a transparent view built in
                // if we are not the last row in the logical section, make it gone, else visible
                View transparentView = (View)rowView.findViewById(R.id.schedules_routeselection_favoriteandrecentlyviewed_transparentview);
                if ((adjustedPosition(position)+1) == (favorites.size()+recentlyViewed.size())) {
                    transparentView.setVisibility(View.VISIBLE);
                } else {
                    transparentView.setVisibility(View.GONE);
                }
            } else{
                String[] routeTypeLabels = mContext.getResources().getStringArray(R.array.schedulesfragment_listview_bothimage_endnames);

                SchedulesRouteModel rtm = (SchedulesRouteModel)getItem(position);

                rowView = mInflater.inflate(R.layout.schedules_routeselection_routes_listview_item, parent, false);
                ImageView leftIconImageView = (ImageView)rowView.findViewById(R.id.schedules_routeselect_item_leftImageView);
                ImageView rightBackgroundImageView = (ImageView)rowView.findViewById(R.id.schedules_routeselection_item_rightImageBackgroundview);
                TextView routeIdTextView = (TextView)rowView.findViewById(R.id.schedules_routeselection_item_routeid);
                TextView routeLongNameTextView = (TextView)rowView.findViewById(R.id.schedules_routeselection_item_routelongname);

                int id = mContext.getResources().getIdentifier(leftImageStartName + routeTypeLabels[routeType.ordinal()] + "_small", "drawable", mContext.getPackageName());
                leftIconImageView.setImageResource(id);

                id = mContext.getResources().getIdentifier(rightImageBackgroundName + routeTypeLabels[routeType.ordinal()], "drawable", mContext.getPackageName());
                rightBackgroundImageView.setImageResource(id);

                SchedulesRouteModel route = (SchedulesRouteModel)getItems()[adjustedPosition(position)];

                routeIdTextView.setText(route.getRouteCode());

                switch (route.getRouteCode().length()) {
                    case 6: {
                        routeIdTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        break;
                    }
                    case 5: {
                        routeIdTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        break;
                    }
                    case 4: {
                        routeIdTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        break;
                    }
                    default: {
                    }
                }

                routeLongNameTextView.setText(route.getRouteLongName());
            }
        }

        return rowView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.schedules_routeselection_headerview, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.schedules_routeselection_sectionheader_textview);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        if (favorites.size()>0 && position<favorites.size()) {
            holder.text.setText(sectionTitles[0]);
            holder.text.setBackgroundColor(Color.parseColor("#990DA44A"));
        } else {
            if (recentlyViewed.size()>0 && (position-favorites.size())<recentlyViewed.size()) {
                holder.text.setText(sectionTitles[1]);
                holder.text.setBackgroundColor(Color.parseColor("#990DA44A"));
            } else {
                holder.text.setText(sectionTitles[2]);

                // get the color from the looking array given the ordinal position of the route type
                String color = mContext.getResources().getStringArray(R.array.schedules_routeselection_routesheader_colors)[routeType.ordinal()];
                holder.text.setBackgroundColor(Color.parseColor(color));
            }
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        if (isFavorite(position)) {

            return 0;
        } else {
            if (isRecentlyViewed(position)) {

                return 1;
            }
        }

        return 2;
    }

    @Override
    public int getPositionForSection(int section) {
        switch (section) {
            case 0: {

                return 0;
            }
            case 1: {

                return favorites.size();
            }
            case 2: {

                return favorites.size()+recentlyViewed.size();
            }
            default: {

                return 0;
            }
        }
    }

    @Override
    public int getSectionForPosition(int position) {
        if (isFavorite(position)) {
            return 0;
        } else {
            if (isRecentlyViewed(position)) {
                return 1;
            }
        }

        return 2;
    }

    @Override
    public Object[] getSections() {

        return sectionTitles;
    }

    class HeaderViewHolder {
        TextView text;
    }
}