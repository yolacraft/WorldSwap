package de.yolacraft.worldswap;

import net.minecraft.client.network.ServerInfo;

import java.util.HashMap;
import java.util.Map;

public class RunState {
    public static int playtime;
    public static boolean done;
    public static boolean timerRunning = true;
    public static String displayText = "00:00";
    public static String displayText2 = "00:00";
    public static int interval = 5 * 60 * 20;
    public static int rem = 1;
    public static PlayerBackup playerBackup;
    public static boolean continueNXT = false;
    public static boolean restore = false;
    public static boolean isIGT = false;
    public static boolean coop = false;
    public static String lastServer;
    public static int lastPort;
    public static int coopport = 25565;
    public static boolean fixlan = false;
    public static Map<String, PlayerBackup> coopUserBackup = new HashMap<>();
    public static boolean AtumMode = false;
    public static String color1 = "#54FCFC";
    public static String color2 = "#FCFC54";

    public static void reCalcTimer(int time) {
        playtime = time;
        displayText2 = formatTicks(playtime);

        rem = playtime % interval;
        displayText = formatTicks(interval-rem+19);
    }

    public static String formatTicks(long ticks) {
        long totalSeconds = ticks / 20;

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static void main(String[] args) {
        long ticks1 = 6000;
        long ticks2 = 75000;

        System.out.println(formatTicks(ticks1));
        System.out.println(formatTicks(ticks2));
    }
}
