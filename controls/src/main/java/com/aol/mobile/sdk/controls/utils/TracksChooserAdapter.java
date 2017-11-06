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
import com.aol.mobile.sdk.controls.viewmodel.TrackOptionVM;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.aol.mobile.sdk.controls.utils.TracksChooserAdapter.Item.Type.AUDIO;
import static com.aol.mobile.sdk.controls.utils.TracksChooserAdapter.Item.Type.CC;
import static com.aol.mobile.sdk.controls.utils.ViewUtils.findView;

public class TracksChooserAdapter extends BaseAdapter {
    @NonNull
    private final List<Item> items = new ArrayList<>();
    @NonNull
    private Drawable selectedIcon;
    @NonNull
    private Drawable closeIcon;

    public TracksChooserAdapter(@NonNull Drawable selectedIcon, @NonNull Drawable closeIcon) {
        this.selectedIcon = selectedIcon;
        this.closeIcon = closeIcon;
    }

    @SuppressWarnings("deprecation")
    public void updateData(@NonNull Context context, @NonNull LinkedList<TrackOptionVM> audioTracks,
                           @NonNull LinkedList<TrackOptionVM> ccTracks) {
        Resources resources = context.getResources();
        int headerColor = resources.getColor(R.color.tracks_header_color);
        int trackColor = resources.getColor(R.color.track_title_color);

        items.clear();
        if (!audioTracks.isEmpty()) {
            items.add(new Item(headerColor, resources.getString(R.string.audio_tracks_title)));
            for (TrackOptionVM audioTrack : audioTracks) {
                items.add(new Item(trackColor, audioTrack, selectedIcon, AUDIO, audioTracks.indexOf(audioTrack)));
            }
        }

        if (!ccTracks.isEmpty()) {
            items.add(new Item(headerColor, resources.getString(R.string.text_tracks_title)));
            for (TrackOptionVM ccTrack : ccTracks) {
                items.add(new Item(trackColor, ccTrack, selectedIcon, CC, ccTracks.indexOf(ccTrack)));
            }
        }

        items.add(new Item(trackColor, resources.getString(R.string.track_close_title), closeIcon));

        notifyDataSetInvalidated();
    }

    public void select(int index) {
        Item selectedItem = items.get(index);

        if (selectedItem.type == CC || selectedItem.type == AUDIO) {
            for (Item item : items) {
                if (item.type == selectedItem.type) {
                    item.imageVisibility = item == selectedItem ? VISIBLE : INVISIBLE;
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
    public boolean isEnabled(int position) {
        return getItem(position).imageVisibility != GONE;
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

        views.image.setVisibility(item.imageVisibility);
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
        public final int index;
        int color;
        int imageVisibility;
        @Nullable
        Drawable imageDrawable;
        @Nullable
        String text;
        boolean hadDivider;

        Item(int color, @Nullable String text) {
            this.color = color;
            this.imageVisibility = GONE;
            this.text = text;
            this.type = Type.TITLE;
            this.index = -1;
        }

        Item(int color, @Nullable String text, @Nullable Drawable drawable) {
            this.color = color;
            this.imageVisibility = VISIBLE;
            this.text = text;
            this.imageDrawable = drawable;
            this.hadDivider = true;
            this.type = Type.CLOSE;
            this.index = -1;
        }

        Item(int color, @NonNull TrackOptionVM track, @NonNull Drawable selectedIcon, @NonNull Type type, int index) {
            this.color = color;
            this.imageVisibility = track.isSelected ? VISIBLE : INVISIBLE;
            this.imageDrawable = selectedIcon;
            this.text = track.title;
            this.type = type;
            this.index = index;
        }

        public enum Type {AUDIO, CC, TITLE, CLOSE}
    }
}
