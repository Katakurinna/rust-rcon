package io.graversen.rust.rcon.objects.util;

import java.util.Objects;

public enum Animals
{
    BEAR,
    BOAR,
    CHICKEN,
    STAG,
    HORSE,
    WOLF,
    UNKNOWN;

    public static Animals parse(String string)
    {
        if (Objects.isNull(string))
        {
            return UNKNOWN;
        }

        try
        {
            return Animals.valueOf(string.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            return UNKNOWN;
        }
    }
}
