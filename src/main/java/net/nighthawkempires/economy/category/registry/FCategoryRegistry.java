package net.nighthawkempires.economy.category.registry;

import net.nighthawkempires.core.datasection.AbstractFileRegistry;
import net.nighthawkempires.economy.category.CategoryModel;

import java.util.Map;

public class FCategoryRegistry extends AbstractFileRegistry<CategoryModel> implements CategoryRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FCategoryRegistry() {
        super("empires/shop/categories", SAVE_PRETTY, -1);
    }

    @Override
    public Map<String, CategoryModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}
