package net.beaner.mapthatsplat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        addItem(new MenuItem("1", "Roadkill Map"));
        addItem(new MenuItem("2", "Map a Splat"));
        addItem(new MenuItem("3", "Top 10 Splats"));
        addItem(new MenuItem("4", "Browse All Splats"));
    }

    private static void addItem(MenuItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class MenuItem {
        public String id;
        public String content;

        public MenuItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
