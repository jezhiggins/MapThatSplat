package net.beaner.mapthatsplat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;

public class SplatContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<MenuItem> ITEMS = new ArrayList<MenuItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, MenuItem> ITEM_MAP = new HashMap<String, MenuItem>();

    static {
        // Add 3 sample items.
        addItem(new MenuItem("map", "Roadkill Map", new RoadkillFragment()));
        addItem(new MenuItem("2", "Map a Splat", new SplatDetailFragment("Map a Splat")));
        addItem(new MenuItem("3", "Top 10 Splats", new SplatDetailFragment("Top 10 Splats")));
        addItem(new MenuItem("4", "Browse All Splats", new SplatDetailFragment("Browse")));
    }

    private static void addItem(MenuItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class MenuItem {
        public final String id;
        public final String content;
        public final Fragment fragment;

        public MenuItem(String id, String content, Fragment frag) {
            this.id = id;
            this.content = content;
            this.fragment = frag;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
