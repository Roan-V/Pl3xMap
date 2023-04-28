/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.command;

import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a command sender.
 */
public abstract class Sender implements ForwardingAudience.Single {
    private final Object sender;

    public <@NonNull T> Sender(@NonNull T sender) {
        this.sender = sender;
    }

    @SuppressWarnings("unchecked")
    public <@NonNull T> @NonNull T getSender() {
        return (T) this.sender;
    }

    public abstract boolean hasPermission(@NonNull String permission);

    public void sendMessage(@NonNull String message) {
        if (!Lang.strip(message).isBlank()) {
            sendMessage(Pl3xMap.api().adventure().console(), true, Lang.parse(message));
        }
    }

    public void sendMessage(@NonNull String message, @NonNull TagResolver.@NonNull Single... placeholders) {
        if (!Lang.strip(message).isBlank()) {
            sendMessage(Pl3xMap.api().adventure().console(), true, Lang.parse(message, placeholders));
        }
    }

    public void sendMessage(Audience audience, boolean prefix, @NonNull ComponentLike message) {
        audience.sendMessage(prefix ? Lang.parse(Lang.PREFIX_COMMAND).append(message) : message);
    }

    @Override
    public abstract boolean equals(@Nullable Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract @NonNull String toString();

    public interface Player<T> {
        @NonNull T getPlayer();

        @NonNull UUID getUUID();

        @Nullable World getWorld();
    }
}
