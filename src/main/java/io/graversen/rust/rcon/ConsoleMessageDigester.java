package io.graversen.rust.rcon;

import io.graversen.rust.rcon.events.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ConsoleMessageDigester
{
    private final Pattern squareBracketInsideMatcher = Pattern.compile("\\[(.*?)\\]");
    private final Pattern squareBracketOutsideMatcher = Pattern.compile("\\](.*?)\\[");

    public Optional<ConsoleDigests> digest(String consoleInput)
    {
        return Arrays.stream(ConsoleDigests.values())
                .filter(x -> x.deepMatches(consoleInput))
                .findFirst();
    }

    public ChatMessageEvent digestChatMessageEvent(String consoleInput)
    {
        // [CHAT] Pope of the Nope[468295/76561197979952036] : ssss
        validateEvent(consoleInput, ConsoleDigests.CHAT);

        final String[] chatMessageParts = consoleInput.split("\\s:\\s", 2);

        final String leftHandString = chatMessageParts[0];
        final String chatMessage = chatMessageParts[1].trim();

        final Matcher matcherSteamId = squareBracketInsideMatcher.matcher(leftHandString);

        List<String> matchingStrings = new ArrayList<>();
        while (matcherSteamId.find())
        {
            matchingStrings.add(matcherSteamId.group(1));
        }

        final String steamId64 = matchingStrings.get(matchingStrings.size() - 1).split("/")[1];

        final Matcher matcherPlayerName = squareBracketOutsideMatcher.matcher(leftHandString);

        String playerName = "N/A";
        if (matcherPlayerName.find())
        {
            playerName = matcherPlayerName.group(1).trim();
        }

        return new ChatMessageEvent(playerName, steamId64, chatMessage);
    }

    public PlayerConnectedEvent digestPlayerConnectedEvent(String consoleInput)
    {
        // 82.102.20.179:52298/76561197979952036/Pope of the Nope joined [windows/76561197979952036]
        // 79.193.40.58:55162/76561198845816557/m_7o7 joined [linux/76561198845816557]
        // 92.171.193.37:52278/76561198063601715/Orion (EN/FR/ES) joined [windows/76561198063601715]
        return null;
    }

    public PlayerDeathEvent digestPlayerDeathEvent(String consoleInput)
    {
        // Jiji[3831334/76561198189196679] was killed by [4267870/8141844]
        // TheLyss Gamer[4147282/76561198154440141] was killed by boar (Boar)
        // TheLyss Gamer[4147282/76561198154440141] was killed by Doctor Delete[168316/76561197979952036]
        return null;
    }

    public PlayerKillEvent digestPlayerKillEvent(String consoleInput)
    {
        // [4267870/8141844] was killed by Jiji[3831334/76561198189196679]
        // UnknOwn  csgolive.com[9460039/76561198104761939] was killed by Doctor Delete[9126388/76561197979952036]
        // -_-[9468122/76561198165597845] was killed by Doctor Delete[9126388/76561197979952036]
        return null;
    }

    public PlayerDisconnectedEvent digestPlayerDisconnectedEvent(String consoleInput)
    {
        // 82.102.20.179:61714/76561197979952036/Pope of the Nope disconnecting: closing
        validateEvent(consoleInput, ConsoleDigests.PLAYER_DISCONNECTED);

        final String[] splitInput = consoleInput.split("/");
        final String[] splitInputLastElement = splitInput[2].split("disconnecting:");

        final String ipAddress = splitInput[0];
        final String steamId64 = splitInput[1];
        final String playerName = splitInputLastElement[0].trim();
        final String reason = splitInputLastElement[1].trim();

        return new PlayerDisconnectedEvent(ipAddress, steamId64, playerName, reason);
    }

    public PlayerSpawnedEvent digestPlayerSpawnedEvent(String consoleInput)
    {
        // DarkDouchebag[1014803/76561198046357656] has entered the game
        validateEvent(consoleInput, ConsoleDigests.PLAYER_SPAWNED);

        final Matcher matcherSteamId = squareBracketInsideMatcher.matcher(consoleInput);

        List<String> matchingStrings = new ArrayList<>();
        while (matcherSteamId.find())
        {
            matchingStrings.add(matcherSteamId.group(1));
        }

        final String steamId64 = matchingStrings.get(matchingStrings.size() - 1).split("/")[1];
        final String playerName = consoleInput.split("\\[")[0].trim();

        return new PlayerSpawnedEvent(playerName, steamId64);
    }

    public WorldEvent digestWorldEvent(String consoleInput)
    {
        // [event] assets/prefabs/npc/cargo plane/cargo_plane.prefab
        // [event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab
        // [event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab

        if (consoleInput.equalsIgnoreCase("[event] assets/prefabs/npc/cargo plane/cargo_plane.prefab"))
        {
            return new WorldEvent(WorldEvent.EventTypes.CARGO_PLANE);
        }
        else if (consoleInput.equalsIgnoreCase("[event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab"))
        {
            return new WorldEvent(WorldEvent.EventTypes.CH47_SCIENTISTS);
        }
        else if (consoleInput.equalsIgnoreCase("[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab"))
        {
            return new WorldEvent(WorldEvent.EventTypes.PATROL_HELICOPTER);
        }
        else
        {
            throw new UnsupportedOperationException("Unsupported World Event");
        }
    }

    public void validateEvent(String consoleInput, ConsoleDigests consoleDigest)
    {
        digest(consoleInput)
                .filter(c -> c.equals(consoleDigest))
                .orElseThrow(() -> new IllegalArgumentException("Invalid event type for console input"));
    }
}
