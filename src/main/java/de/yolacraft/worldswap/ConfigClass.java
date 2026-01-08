package de.yolacraft.worldswap;

import com.google.gson.JsonObject;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunOption;
import me.contaria.speedrunapi.config.api.annotations.Config;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@Config(init = Config.InitPoint.PRELAUNCH)
public class ConfigClass implements SpeedrunConfig {
    @Config.Category("worldswap")
    @Config.Numbers.Whole.Bounds(min = 1, max = 10)
    public int minutes = 5;

    @Config.Category("worldswap")
    public boolean isIGT = true;

    @Override
    public String modID() {
        return "worldswap";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public @Nullable SpeedrunOption<?> parseField(Field field, SpeedrunConfig config, String... idPrefix) {
        return SpeedrunConfig.super.parseField(field, config, idPrefix);
    }

    @Override
    public void onSave(JsonObject jsonObject){
        RunState.interval = minutes * 20 * 60;
        RunState.isIGT = isIGT;
    }
}
