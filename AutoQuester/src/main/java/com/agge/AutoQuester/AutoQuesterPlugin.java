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
import com.agge.AutoQuester.Context;
import com.agge.AutoQuester.Registry;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.ShopInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.WidgetInfoExtended;
import net.runelite.api.Client;
import net.runelite.client.RuneLite;

import java.util.*;
import java.util.concurrent.Executors;
import java.awt.event.KeyEvent;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> AutoQuester</html>",
        description = "AutoQuest for 10 quest points!",
        enabledByDefault = false,
        tags = {"agge", "piggy", "plugin"}
)

@Slf4j
public class AutoQuesterPlugin extends Plugin {
    // static Client and ClientThread instances for the global namespace.
    @Inject
    public static Client client;
    @Inject
    public static ClientThread clientThread;

    @Inject
    public PlayerUtil playerUtil;
    @Inject
    public AutoQuesterHelper acHelper;

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
    private Util util;

    @Provides
    private AutoQuesterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoQuesterConfig.class);
    }

    // Create null reference pointers for needed utilities.
    private Pathing pathing = null;
    private Instructions _instructions = null;
    private Action action = null;
    public Player player = null;
    private Context ctx = null;
    private Registry registry = null;

    // k, v for needed locations.
    private WorldPoint shopkeeper = new WorldPoint(3212, 3246, 0);
    private WorldPoint veos = new WorldPoint(3228, 3242, 0);
 
    // Public instance variables.
    public static final int MAX_TIMEOUT = 2;
    public static WorldPoint GOAL = null;
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
        pathing = new Pathing();
        _instructions = new Instructions();
        action = new Action(2);

        log.debug("Pathing: " + pathing);
        log.debug("Instructions: " + _instructions);
        log.debug("Action: " + action);

        if (pathing == null) {
            log.error("Pathing is not initialized properly");
            throw new IllegalStateException("Pathing is not initialized properly");
        }
        if (_instructions == null) {
            log.error("Instructions is not initialized properly");
            throw new IllegalStateException("Instructions is not initialized properly");
        }
        if (action == null) {
            log.error("Action is not initialized properly");
            throw new IllegalStateException("Action is not initialized properly");
        }

        // Package in Context for the Instructions Registry.
        Context ctx = new Context(pathing, _instructions, action);
        Registry registry = new Registry(ctx);

        getInstanceVariables();
        
        //registry.sheepShearer();
        registry.cooksAssistant();
    }

    @Override  
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);

        resetEverything();
    }

    /**
     * Get the current instruction's name.
     * @return String, the current instruction's name or no instructions
     * @note Don't allow direct access to _instructions, return a new 
     * String object.
     */
    public String getInstructionName()
    {
        if (_instructions.getSize() == 0)
            return "No instructions!";
        return _instructions.getName();
    }

    public void resetEverything() {
        // Release resources for everything and hope garbage collector claims 
        // them.
        pathing = null;
        action = null;
        player = null;
        // Instructions should be cleared. @see class Instructions
        _instructions.clear();
        _instructions = null;
        // SAFE to release Context and Registry.
        ctx = null;
        registry = null;
    }

    private void getInstanceVariables()
    {   
        try {
            client = RuneLite.getInjector().getInstance(Client.class);
            player = client.getLocalPlayer();
        } catch (NullPointerException e) {
            log.info("Error: Unable to get instance variables");
        }
    }

    // Wrapper to return boolean for player.getAnimation()
    //private void isAnimating() {
    //    if (player.getAnimation() == -1)
    //        return true;
    //    return false;
    //}
   
    @Subscribe
    private void onGameTick(GameTick event) {
        log.info("Idle ticks: " + action.getTicks());
        log.info("Curr idx: " + _instructions.getIdx());
        log.info("Size: " + _instructions.getSize());
        log.info("Curr WorldPoint: " + player.getWorldLocation()); 
        _instructions.executeInstructions();
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

    // A better solution would be to use Widget packets, but if it's only SPACE...
    private boolean pressSpace() 
    {
        KeyEvent keyPress = new KeyEvent(client.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
        client.getCanvas().dispatchEvent(keyPress);
        KeyEvent keyRelease = new KeyEvent(client.getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
        client.getCanvas().dispatchEvent(keyRelease);
        KeyEvent keyTyped = new KeyEvent(client.getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
        client.getCanvas().dispatchEvent(keyTyped);
        return true;
    }
    //private boolean pressSpace() 
    //{
    //    Executors.newSingleThreadExecutor().submit(pressKey());
    //    return true;
    //}
}
