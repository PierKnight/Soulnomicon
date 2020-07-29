package com.pier.snom.capability.abilities;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum EnumAbility
{
    NONE("none"),
    SEPARATION("separation"),
    CONTROL("control"),
    CLAIRVOYANCE("clairvoyance");


    private final String name;

    EnumAbility(String name)
    {
        this.name = name;
    }

    @OnlyIn(Dist.CLIENT)
    public String getLocalizedName()
    {
        return I18n.format("ability." + name);
    }
}
