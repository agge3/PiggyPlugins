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
    default Keybind toggle() {
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
            keyName = "xMarksTheSpot",
            name = "X Marks the Spot",
            description = "",
            position = 2,
            section = autoQuesterConfig
    )
    default boolean xMarksTheSpot() {
        return false;
    }
}
