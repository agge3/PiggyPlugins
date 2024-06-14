/**
 * @file AutoQuesterConfig.java
 * @class AutoQuesterConfig
 * Config - AutoQuest for 10 quest points! 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Special thanks to EthanApi and PiggyPlugins for API, inspiration, and a 
 * source of code at times.
 */

package com.agge.AutoQuester;

import net.runelite.client.config.*;

@ConfigGroup("AutoQuesterConfig")
public interface AutoQuesterConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle()
    {
        return Keybind.NOT_SET;
    }

    @ConfigSection(
            name = "AutoQuester Configuration",
            description = "Select quests and other useful things",
            position = 1,
            closedByDefault = false
    )
    String autoQuesterConfig = "autoQuesterConfig";

    @ConfigItem(
            keyName = "testInstructions",
            name = "Test",
            description = "For testing your own quest instructions!",
            position = 1,
            section = autoQuesterConfig
    )
    default boolean testInstructions() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "xMarksTheSpot",
            name = "X Marks the Spot",
            description = "",
            position = 2,
            section = autoQuesterConfig
    )
    default boolean xMarksTheSpot() 
    {
        return false;
    }

    @ConfigSection(
            name = "Debug",
            description = "Options to debug/fix the plugin",
            position = 10,
            closedByDefault = false
    )
    String autoQuesterDebug = "autoQuesterDebug";
    
    @ConfigItem(
            keyName = "skipInstruction",
            name = "Skip the current instruction",
            description = "Toggle on, then off -- will skip the instruction",
            position = 11,
            section = autoQuesterDebug
    )
    default boolean skipInstruction() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "saveInstructions",
            name = "Save registered instructions",
            description = "Don't hard reset, save the current instruction index",
            position = 12,
            section = autoQuesterDebug
    )
    default boolean saveInstructions() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "skipNInstructions",
            name = "Skip n instructions",
            description = "Immediately set to 0 again, or will keep skiping n!",
            position = 13,
            section = autoQuesterDebug
    )
    default int skipNInstructions() 
    {
        return 0;
    }
}
