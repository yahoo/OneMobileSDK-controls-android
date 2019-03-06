/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aol.mobile.sdk.controls.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.aol.mobile.sdk.controls.utils.PlaybackSpeedChooserAdapter.Item.Type.SPEED;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.findView;

public class PlaybackSpeedChooserAdapter extends BaseAdapter {
    @NonNull
    private final List<Item> items = new ArrayList<>();

    public PlaybackSpeedChooserAdapter(@NonNull Context context) {
        Resources resources = context.getResources();
        int headerColor = resources.getColor(R.color.dialog_header_color);
        int trackColor = resources.getColor(R.color.dialog_title_color);
        Drawable selectedIcon = resources.getDrawable(R.drawable.ic_item_selected);
        Drawable closeIcon = resources.getDrawable(R.drawable.ic_dialog_close);

        items.add(new Item(headerColor, resources.getString(R.string.playback_speed_title)));

        items.add(new Item(trackColor, 0.25f, "0.25", selectedIcon));
        items.add(new Item(trackColor, 0.5f, "0.5", selectedIcon));
        items.add(new Item(trackColor, 0.75f, "0.75", selectedIcon));
        items.add(new Item(trackColor, 1f, resources.getString(R.string.normal_playback_speed), selectedIcon));
        items.add(new Item(trackColor, 1.25f, "1.25", selectedIcon));
        items.add(new Item(trackColor, 1.5f, "1.5", selectedIcon));
        items.add(new Item(trackColor, 1.75f, "1.75", selectedIcon));
        items.add(new Item(trackColor, 2f, "2", selectedIcon));

        items.add(new Item(trackColor, resources.getString(R.string.dialog_close_title), closeIcon));

        notifyDataSetInvalidated();
    }

    public void select(int index) {
        Item selectedItem = items.get(index);

        if (selectedItem.type == SPEED) {
            for (Item item : items) {
                if (item.type == SPEED) {
                    item.isSelected = item == selectedItem;
                }
            }
        }

        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.track_item_view, null);
            TextView titleView = findView(convertView, R.id.track_title);
            ImageView imageView = findView(convertView, R.id.track_check_image);
            View divider = findView(convertView, R.id.track_divider);
            convertView.setTag(new ViewHolder(titleView, imageView, divider));
        }

        ViewHolder views = (ViewHolder) convertView.getTag();
        Item item = getItem(position);

        views.image.setVisibility(item.isSelected ? VISIBLE : INVISIBLE);
        views.image.setImageDrawable(item.imageDrawable);
        views.title.setText(item.text);
        views.title.setTextColor(item.color);
        views.divider.setVisibility(item.hadDivider ? VISIBLE : GONE);

        return convertView;
    }

    private static class ViewHolder {
        @NonNull
        final TextView title;
        @NonNull
        final ImageView image;
        @NonNull
        final View divider;

        private ViewHolder(@NonNull TextView title, @NonNull ImageView image, @NonNull View divider) {
            this.title = title;
            this.image = image;
            this.divider = divider;
        }
    }

    public static class Item {
        @NonNull
        public final Type type;
        public final float value;
        final int color;
        String text;
        @Nullable
        final Drawable imageDrawable;
        final boolean hadDivider;
        boolean isSelected;

        Item(int color, @Nullable String text) {
            this.type = Type.TITLE;
            this.value = -1;
            this.color = color;
            this.text = text;
            this.imageDrawable = null;
            this.hadDivider = false;
            this.isSelected = false;
        }

        Item(int color, @Nullable String text, @Nullable Drawable drawable) {
            this.type = Type.CLOSE;
            this.value = -1;
            this.color = color;
            this.text = text;
            this.imageDrawable = drawable;
            this.hadDivider = true;
            this.isSelected = true;
        }

        Item(int color, float playbackSpeed, @NonNull String text, @NonNull Drawable selectedIcon) {
            this.type = Type.SPEED;
            this.value = playbackSpeed;
            this.color = color;
            this.text = text;
            this.imageDrawable = selectedIcon;
            this.hadDivider = false;
            this.isSelected = playbackSpeed == 1;
        }

        public enum Type {SPEED, TITLE, CLOSE}
    }


}
