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



    @Config.Category("coop")
    public boolean coop = false;

    @Config.Category("coop")
    @Config.Numbers.Whole.Bounds(min = 1024, max = 65565)
    @Config.Numbers.TextField
    public int port = 25565;

    @Config.Category("timer")
    @Config.Strings.MaxChars(7)
    public String color1 = "#54FCFC";
    @Config.Category("timer")
    @Config.Strings.MaxChars(7)
    public String color2 = "#FCFC54";

    @Config.Category("experimental")
    public boolean atum_compatability = false;

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
        RunState.coop = coop;
        RunState.coopport = port;
        RunState.AtumMode = atum_compatability;
        RunState.color1 = color1;
        RunState.color2 = color2;
    }
}
