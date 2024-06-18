/**
 * @file Util.java
 * @class Util
 * Utility tools (mainly tick utilities and tick reference interface).
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 */

package com.polyplugins.AutoLoot;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MovementPackets;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import com.polyplugins.AutoLoot.IntPtr;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Util {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AutoLootConfig config;

    public boolean hasWaited(IntPtr ticks) {
        if (ticks.get() > config.waitFor()) {
            ticks.set(0);
            this.flag = false;
        }
        return !this.flag;
    }

    public boolean isWaiting(IntPtr ticks) {
        if (this.flag) {
            // ticks++
            int tmp = ticks.get();
            tmp++;
            ticks.set(tmp);
            return true;
        }
        return false;
    }   

    public void shouldWait() {
        this.flag = true;
    }

    /*
     * Reset the state of Util.
     */
    public void reset() {
        this.flag = false;
    }

    private boolean flag = false;
}
