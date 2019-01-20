package io.graversen.rust.rcon.logging;

import java.io.PrintStream;

@FunctionalInterface
public interface ISimpleLogger extends ILogger
{
    @Override
    void info(String message, Object... args);

    @Override
    default void warning(String message, Object... args)
    {
        info(message, args);
    }

    @Override
    default void error(String message, Object... args)
    {
        info(message, args);
    }

    @Override
    default PrintStream out()
    {
        return null;
    }

    @Override
    default PrintStream error()
    {
        return null;
    }
}
