package com.github.bcTornado608.papermcportal.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang3.SerializationUtils;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class WorldDataType implements PersistentDataType<byte[], WorldWrapper> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<WorldWrapper> getComplexType() {
        return WorldWrapper.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull WorldWrapper complex, @NotNull PersistentDataAdapterContext context) {
        return SerializationUtils.serialize(complex);
    }

    @Override
    public @NotNull WorldWrapper fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        try{
            InputStream is = new ByteArrayInputStream(primitive);
            ObjectInputStream o = new ObjectInputStream(is);
            return (WorldWrapper) o.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    
}
