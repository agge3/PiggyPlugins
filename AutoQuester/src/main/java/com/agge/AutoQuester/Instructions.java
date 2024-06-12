/**
 * @file Instructions.java
 * @class Instructions
 * Provides functionality to register, execute, and clear boolean instructions. 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 */

package com.agge.AutoQuester;

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
import com.agge.AutoQuester.IntPtr;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.Vector;

@Slf4j
public class Instructions {
    public Instructions()
    {
        log.info("Constructing Instructions!");
        this.instructions = new Vector<BooleanSupplier>();
        this.currIdx = 0;
    }

    /**
     * Executes current instruction.
     * @return TRUE if instruction executed, FALSE if still executing
     */
    public boolean execute()
    {
        if (currIdx < instructions.size() && 
            instructions.get(currIdx).getAsBoolean()) {
            instructions.remove(currIdx);
            // Do not increment currIdx since the current index is now pointing 
            // to the next instruction.
            return true;
        }
        return false;
    }

    /** 
     * Executes all registered instructions.
     * @return TRUE if all instructions executed, FALSE if still executing
     */
    public boolean executeInstructions()
    {
        // Execute the current instruction.
        boolean executed = execute();
        // Check if there are more instructions left after executing the current 
        // one.
        return executed && currIdx >= instructions.size();
    }

    /**
     * Register instructions into this instance's instruction vector.
     * @param BooleanSupplier instruction
     * A functional boolean instruction.
     * TRUE if instruction has executed, FALSE if still executing
     * @param Optional<Integer> n
     * Add n instructions. RECOMMENDED: Seed Random.
     */
    public void register(BooleanSupplier instruction, Optional<Integer> n) 
    {
        if (!n.isPresent()) {
            this.instructions.add(instruction);
        } else {
            for (int i = 0; i < n.get(); i++) {
                this.instructions.add(instruction);
            }
        }
    }

    /**
     * Returns the size of the instruction list.
     * @return The size of the instruction list
     */
    public int getSize() 
    {
        return instructions.size();
    }

    /**
     * Returns the current index of the executing instruction.
     * @return The current index
     */
    public int getCurrIdx() 
    {
        return currIdx;
    }

    /**
     * Clears all instructions.
     */
    public void clear() 
    {
        instructions.clear();
        currIdx = 0; // Reset the current index
    }

    private Vector<BooleanSupplier> instructions;
    private int currIdx;
}
