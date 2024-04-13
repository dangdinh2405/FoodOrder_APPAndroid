package com.example.w10.placeholder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<String, PlaceholderItem>();


    static {
        List<PlaceholderItem> ITEMS = createPlaceholderItems();
        // Add some sample items.
        for (PlaceholderItem item : ITEMS) {
            addItem(item);
        }
    }
    private static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static List<PlaceholderItem> createPlaceholderItems() {
        return Arrays.asList(
                new PlaceholderItem("1", "Cơm chiên Dương Châu", false),
                new PlaceholderItem("2", "Cơm gà xối mỡ", false),
                new PlaceholderItem("3", "Phở bò tẩm bổ", false),
                new PlaceholderItem("4", "Mì xào giòn", false)
        );
    }

    public static class PlaceholderItem {
        public final String id;
        public final String content;
        public final boolean checked;

        public PlaceholderItem(String id, String content, boolean checked) {
            this.id = id;
            this.content = content;
            this.checked = checked;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}