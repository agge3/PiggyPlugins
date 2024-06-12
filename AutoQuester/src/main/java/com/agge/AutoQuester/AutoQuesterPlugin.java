/**
 * @file AutoQuesterPlugin.java
 * @class AutoQuesterPlugin
 * AutoQuest for 10 quest points! 
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
import com.agge.AutoQuester.Instructions;
import com.agge.AutoQuester.Action;
import com.example.InteractionApi.NPCInteraction;
import com.example.PacketUtils.WidgetInfoExtended;

import java.util.*;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> AutoQuester</html>",
        description = "AutoQuest for 10 quest points!",
        enabledByDefault = false,
        tags = {"agge", "piggy", "plugin"}
)

@Slf4j
public class AutoQuesterPlugin extends Plugin {
    @Inject
    public PlayerUtil playerUtil;
    @Inject
    public AutoQuesterHelper acHelper;

    @Inject
    private Client client;
    @Inject
    private AutoQuesterConfig config;
    @Inject
    private AutoQuesterOverlay overlay;
    @Inject
    private AutoQuesterTileOverlay tileOverlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public ItemManager itemManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private Util util;

    @Provides
    private AutoQuesterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoQuesterConfig.class);
    }

    // Create null reference pointers for needed utilities.
    private Pathing pathing = null;
    private Random random = null;
    private Instructions instructions = null;
    private Action action = null;
    private Player player = null;

    // k, v for needed locations.
    private WorldPoint shopkeeper = new WorldPoint(3212, 3246, 0);
    private WorldPoint veos = new WorldPoint(3228, 3242, 0);
 
    // Public instance variables.
    public static final int MAX_TIMEOUT = 2;
    public boolean started = false;
    public int timeout = 0;

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        overlayManager.add(tileOverlay);
        
        init();
    }

    private void init() {
        // Instantiate objects that need clean state.
        random = new Random();
        pathing = new Pathing();
        instructions = new Instructions();
        action = new Action(2);
        
        registerInstructions();
    }

    @Override  
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);

        resetEverything();
    }

    public void resetEverything() {
        // Release resources for everything and hope garbage collector claims 
        // them.
        random = null;
        pathing = null;
        action = null;
        player = null;
        // Instructions should be cleared. @see class Instructions
        instructions.clear();
        instructions = null;
    }

    // Wrapper to return boolean for player.getAnimation()
    //private void isAnimating() {
    //    if (player.getAnimation() == -1)
    //        return true;
    //    return false;
    //}

    // Register all the instructions, these will return TRUE when they should 
    // be removed. Then move on to the next instruction.
    private void registerInstructions() {
        // Random between 2 and 4 (inclusive).
        int shortCont = 2 + random.nextInt(3);
        // Random between 3 and 6 (inclusive).
        int medCont = 3 + random.nextInt(4);   
        // Random between 7 and 12 (inclusive).
        int longCont = 7 + random.nextInt(6);  

        instructions.register(() -> pathing.pathTo(veos),
                Optional.empty());

        instructions.register(() -> pathing.isPathing(),
                Optional.empty());

        // Full Veos dialogue.
        instructions.register(() -> action.interactWith("Veos", "Talk-to"),
                Optional.empty());

        instructions.register(() -> action.continueDialogue(), 
                Optional.of(shortCont));  

        instructions.register(() -> action.selectDialogue(
            "I'm looking for a quest.", 2),
                Optional.empty());

        instructions.register(() -> action.continueDialogue(), 
                Optional.of(longCont));

        instructions.register(() -> action.selectDialogue(
            "Yes.", 1), 
                Optional.empty());

        instructions.register(() -> action.continueDialogue(), 
                Optional.of(medCont));

        //instructions.register(() -> action.selectDialogue("Yes.", 1),
        //        Optional.empty());

        //instructions.register(() -> action.continueDialogue(), 
        //        Optional.of(shortCont));

        instructions.register(() -> action.selectDialogue(
            "Okay, thanks Veos.", 1), 
                Optional.empty()); 

        instructions.register(() -> action.continueDialogue(), 
                Optional.of(medCont));

        instructions.register(() -> pathing.pathTo(
            new WorldPoint(3190, 3273,0)),
                Optional.empty());
        
        instructions.register(() -> pathing.isPathing(),
                Optional.empty());
    }
   
    @Subscribe
    private void onGameTick(GameTick event) {
        log.info("Idle ticks: " + action.getTimeout());
        log.info("Curr idx: " + instructions.getCurrIdx());
        log.info("Size: " + instructions.getSize());
        instructions.executeInstructions();
        pathing.run();
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        if (!started) 
            return;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!started)
            return;
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (!started) 
            return;
        int bid = event.getVarbitId();
        int pid = event.getVarpId();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("AutoQuesterConfig"))
            return;
    }

    //@Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();
        if (state == GameState.HOPPING || state == GameState.LOGGED_IN)
            return;
        EthanApiPlugin.stopPlugin(this);
    }

    private void checkRunEnergy() {
        if (runIsOff() && playerUtil.runEnergy() >= 30) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
    }

    private boolean runIsOff() {
        return EthanApiPlugin.getClient().getVarpValue(173) == 0;
    }

    private final HotkeyListener toggle = new HotkeyListener(
        () -> config.toggle()) {
            @Override
            public void hotkeyPressed() {
            toggle();
            }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;
    }
}
