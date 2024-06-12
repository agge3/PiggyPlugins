/**
 * @file Action.java
 * @class Action
 * Wrapper for different boolean Action(s) to be performed. 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Special thanks to EthanApi and PiggyPlugins for API, inspiration, and a 
 * source of code at times.
 */

package com.agge.AutoQuester;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.EthanApiPlugin.Collections.query.TileItemQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.PlayerUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import static net.runelite.api.TileItem.OWNERSHIP_SELF;
import static net.runelite.api.TileItem.OWNERSHIP_GROUP;
import com.agge.AutoQuester.AutoQuesterConfig;
import com.agge.AutoQuester.AutoQuesterOverlay;
import com.agge.AutoQuester.AutoQuesterTileOverlay;
import com.agge.AutoQuester.Util;
import com.agge.AutoQuester.IntPtr;
import net.runelite.api.widgets.Widget;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.agge.AutoQuester.Pathing;
import com.example.InteractionApi.NPCInteraction;
import com.example.PacketUtils.WidgetInfoExtended;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Action {
    public Action(int max)  
    {
        log.info("Constructing Action!");
        this.max = max;
        this.timeout = 0;
    }

    public boolean continueDialogue() {
         log.info("Entering cont dialog");
         Optional<Widget> mainContinueOpt = Widgets.search().withTextContains(
            "Click here to continue").first();
         if (mainContinueOpt.isPresent()) {
             MousePackets.queueClickPacket();
             WidgetPackets.queueResumePause(mainContinueOpt.get().getId(), -1);
             timeout = 0;
             return true;
         }

         // These have yet to be needed.
         //Optional<Widget> continue1Opt = Widgets.search().withId(12648448).hiddenState(false).first();
         //if (continue1Opt.isPresent()) {
         //    log.info("continue 1");
         //    MousePackets.queueClickPacket();
         //    WidgetPackets.queueResumePause(continue1Opt.get().getId(), 1);
         //    timeout = 0;
         //    return true;
         //}
         //Optional<Widget> continue2Opt = Widgets.search().withId(41484288).hiddenState(false).first();
         //if (continue2Opt.isPresent()) {
         //    log.info("continue 2");
         //    MousePackets.queueClickPacket();
         //    WidgetPackets.queueResumePause(continue2Opt.get().getId(), 1);
         //    timeout = 0;
         //    return true;
         //}

         if (timeout > 1) {
             timeout = 0;
             return true;
         }
         timeout++;
         return false;
    }

    public boolean selectDialogue(String str, int choice) {
         log.info("Selecting dialogue");
         Optional<Widget> d = Widgets.search()
                                     .withTextContains(str)
                                     .first();
         if (d.isPresent()) {
             MousePackets.queueClickPacket();
             WidgetPackets.queueResumePause(d.get().getId(), choice);
             timeout = 0;
             return true;
         }

         // Don't want to timeout here.
         //if (timeout > MAX_TIMEOUT) {
         //    timeout = 0;
         //    return true;
         //}
         //timeout++;
        
         return false;
    }

    public boolean interactWith(String name, String action) {
         log.info("Interacting with");
         if (NPCInteraction.interact(name, action)) {
             timeout = 0;
             return true;
         }

         //if (timeout > max) {
         //    timeout = 0;
         //    return true;
         //}
         //timeout++;
        
         return false;
    }

    public int getTimeout()
    {
        return this.timeout;
    }
    
    private int max;
    private int timeout;
}
