package com.adobe.dx.aep.poc.uis.core.entities;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.Serializable;

public class Pair<K, V> implements Serializable {
    private K key;
    private V value;

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public Pair(@NamedArg("key") K var1, @NamedArg("value") V var2) {
        this.key = var1;
        this.value = var2;
    }

    public String toString() {
        return this.key + "=" + this.value;
    }

    public int hashCode() {
        byte var1 = 7;
        int var2 = 31 * var1 + (this.key != null ? this.key.hashCode() : 0);
        var2 = 31 * var2 + (this.value != null ? this.value.hashCode() : 0);
        return var2;
    }

    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof Pair)) {
            return false;
        } else {
            Pair var2 = (Pair)var1;
            if (this.key != null) {
                if (!this.key.equals(var2.key)) {
                    return false;
                }
            } else if (var2.key != null) {
                return false;
            }

            if (this.value != null) {
                if (!this.value.equals(var2.value)) {
                    return false;
                }
            } else if (var2.value != null) {
                return false;
            }

            return true;
        }
    }
}
