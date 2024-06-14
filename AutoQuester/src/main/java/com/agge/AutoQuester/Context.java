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

import com.agge.AutoQuester.Pathing;
import com.agge.AutoQuester.Instructions;
import com.agge.AutoQuester.Action;

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
