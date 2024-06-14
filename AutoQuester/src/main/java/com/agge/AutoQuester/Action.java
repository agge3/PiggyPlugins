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
import com.example.InteractionApi.ShopInteraction;
import com.example.PacketUtils.WidgetInfoExtended;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Action {
    public Action(int max)  
    {
        log.info("Constructing Action!");
        this._max = max;
        this._ticks = 0;
    }

    public boolean continueDialogue() {
         log.info("Entering cont dialog");
         Optional<Widget> mainContinueOpt = Widgets.search().withTextContains(
            "Click here to continue").first();
         if (mainContinueOpt.isPresent()) {
             MousePackets.queueClickPacket();
             WidgetPackets.queueResumePause(mainContinueOpt.get().getId(), -1);
             _ticks = 0;
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

         if (_ticks > 1) {
             _ticks = 0;
             return true;
         }
         _ticks++;
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
             _ticks = 0;
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

    public boolean interactNPC(String name, String action) 
    {
         log.info("Interacting with");
         if (NPCInteraction.interact(name, action)) {
             _ticks = 0;
             return true;
         }

         //if (timeout > max) {
         //    timeout = 0;
         //    return true;
         //}
         //timeout++;
        
         return false;
    }

    //public boolean interactMenu()
    //{
    //    Widget menu = plugin.getClient().getWidget(
    //    MousePackets.queueClickPacket();
    //    //interactWidget not implemented yet
    //    WidgetPackets.queueWidgetAction(plugin.getClient().getWidget(
    //        config.item().getWidgetInfo().getPackedId()), "Smith", "Smith set");

    public boolean buyN(String name, int n)
    {
        log.info("Interacting with");
        if (n == 1) {
            return ShopInteraction.buyOne(name);
        }
        return false;
    }
    
    // A better solution would be to use Widget packets, but if it's only SPACE...
    public boolean pressSpace() 
    {
        try {
            KeyEvent keyPress = new KeyEvent(AutoQuesterPlugin.client.getCanvas(), 
                    KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, 
                    KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
            AutoQuesterPlugin.client.getCanvas().dispatchEvent(keyPress);
            KeyEvent keyRelease = new KeyEvent(AutoQuesterPlugin.client.getCanvas(), 
                KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 
                KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
            AutoQuesterPlugin.client.getCanvas().dispatchEvent(keyRelease);
            KeyEvent keyTyped = new KeyEvent(AutoQuesterPlugin.client.getCanvas(), 
                KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, 
                KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
            AutoQuesterPlugin.client.getCanvas().dispatchEvent(keyTyped);
            return true;
        } catch (IllegalArgumentException e) {
            // Ignore the exception, SPACE executes fine.
            return true;
        }
    }

    public boolean interactTileItem(String name, int actionNo)
    {
        log.info("Trying to interact with: " + name + " " + actionNo);
        AtomicBoolean found = new AtomicBoolean(false);
        TileItems.search()
                 .withName(name)
                 .withinDistance(10) // Assumed to be close.
                 .first().ifPresent(item -> { 
            MousePackets.queueClickPacket();
            TileItemPackets.queueTileItemAction(
                actionNo, item.getTileItem().getId(),
                item.getLocation().getX(), item.getLocation().getY(), false);
            log.info("Interacted with: " + name);
            found.set(true); });
        return found.get();
    }

    /**
     * Block next instruction.
     * @param int ticks
     * How many ticks to block for.
     * @return TRUE when done blocking
     */
    public boolean block(int ticks)
    {
        log.info("Blocking next action!");
        this._ticks++;
        log.info("Ticks: " + this._ticks);
        if (this._ticks > ticks) {
            this._ticks = 0;
            return true;
        }
        return false;
    }   

    public void setMax(int max)
    {
        this._max = max;
    }

    public int getMax()
    {
        return this._max;
    }

    public int getTicks()
    {
        return this._ticks;
    }

    public boolean timeout(int n)
    {
        return this._ticks > n;
    }
    
    private int _max;
    private int _ticks;
}
