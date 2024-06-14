/**
 * @file Registry.java
 * @class Registry
 * Context struct for passing around context.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-16
 *
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
import com.agge.AutoQuester.Instructions;
import com.agge.AutoQuester.Action;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.ShopInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.WidgetInfoExtended;
import net.runelite.api.Client;

import java.util.*;
import java.util.concurrent.Executors;
import java.awt.event.KeyEvent;

public class Context {
    // Instance context.
    public Pathing pathing;
    //public Random random;
    public Instructions instructions;
    public Action action;

    public Context(Pathing pathing, Instructions instructions, 
            Action action)
    {
        this.pathing = pathing;
        //this.random = random;
        this.instructions = instructions;
        this.action = action;
    }
}
