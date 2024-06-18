/**
 * @file IntPtr.java
 * @class IntPtr
 * To have an int reference.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 */

package com.polyplugins.AutoLoot;

// To have an int reference.
public class IntPtr {
    public IntPtr(int val) { this.val = val; }
    public int get() { return this.val; }
    public void set(int val) { this.val = val; }
    private int val;
}
