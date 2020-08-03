package com.pier.snom.init;

import com.pier.snom.SoulnomiconMain;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModSounds
{

    public static final SoundEvent BOOK_FLIP = new ModSoundEvent("book_flip");
    public static final SoundEvent BOOK_CLOSE = new ModSoundEvent("book_close");
    public static final SoundEvent CONTROL_SMASH = new ModSoundEvent("control_smash");
    public static final SoundEvent CONTROLLING = new ModSoundEvent("control_loop");
    public static final SoundEvent CLAIRVOYANCE_START = new ModSoundEvent("start_searching");

    public static void register(IForgeRegistry<SoundEvent> registry)
    {
        registry.register(BOOK_FLIP);
        registry.register(BOOK_CLOSE);
        registry.register(CLAIRVOYANCE_START);
        registry.register(CONTROLLING);
        registry.register(CONTROL_SMASH);
    }


    private static class ModSoundEvent extends SoundEvent
    {
        private ModSoundEvent(String name)
        {
            super(new ResourceLocation(SoulnomiconMain.ID, name));
            this.setRegistryName(SoulnomiconMain.ID, name);
        }
    }
}
