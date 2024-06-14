/**
 * @file AutoQuesterOverlay.java
 * @class AutoQuesterOverlay
 * Overlay - AutoQuest for 10 quest points! 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Special thanks to EthanApi and PiggyPlugins for API, inspiration, and a 
 * source of code at times.
 */

package com.agge.AutoQuester;


import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.google.common.base.Strings;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.lang.model.type.ArrayType;
import javax.sound.sampled.Line;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

public class AutoQuesterOverlay extends Overlay {

    private final PanelComponent panelComponent = new PanelComponent();
    private final PanelComponent slPanel = new PanelComponent();
    private final Client client;
    private final AutoQuesterPlugin plugin;

    @Inject
    private AutoQuesterOverlay(Client client, AutoQuesterPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setDragTargetable(true);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        slPanel.getChildren().clear();

        LineComponent name = buildLine(
            "Current instruction: ", plugin.getInstructionName());
        //LineComponent timeout = buildLine(
        //    "Timeout: ", String.valueOf(plugin.timeout));
        //LineComponent idleTicks = buildLine(
        //    "Idle Ticks: ", String.valueOf(plugin.idleTicks));
        //LineComponent lootQ = buildLine(
        //    "Loot Q: ", String.valueOf(plugin.lootQueue.size()));

        panelComponent.getChildren().addAll(
            Arrays.asList(name));
        if (client.getLocalPlayer().getInteracting() != null) {
            Actor intr = plugin.player.getInteracting();
        }

        return panelComponent.render(graphics);
    }

    /**
     * Builds a line component with the given left and right text
     *
     * @param left
     * @param right
     * @return Returns a built line component with White left text and Yellow right text
     */
    private LineComponent buildLine(String left, String right) {
        return LineComponent.builder()
                .left(left)
                .right(right)
                .leftColor(Color.WHITE)
                .rightColor(Color.YELLOW)
                .build();
    }
}
