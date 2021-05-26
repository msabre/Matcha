package application.services.json.strategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class BaseStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}

