package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.RconMessageTypes;
import io.graversen.rust.rcon.events.types.game.WorldEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class TestParseWorldEvent extends BaseDefaultParserTest
{
    @Test
    void test_validation()
    {
        final String[] eventMessages = {
                "[event] assets/prefabs/npc/cargo plane/cargo_plane.prefab",
                "[event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab",
                "[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab"
        };

        Arrays.stream(eventMessages).forEach(s -> defaultConsoleParser.validateEvent(s, RconMessageTypes.WORLD_EVENT));
    }

    @Test
    void test_unknown()
    {
        final String eventString = "[event] wubba lubba dub dub";
        assertEquals(WorldEvent.EventTypes.UNKNOWN, defaultConsoleParser.parseWorldEvent(eventString).getEventType());
    }

    @Test
    void test_cargoPlane()
    {
        final String eventString = "[event] assets/prefabs/npc/cargo plane/cargo_plane.prefab";
        assertEquals(WorldEvent.EventTypes.CARGO_PLANE, defaultConsoleParser.parseWorldEvent(eventString).getEventType());
    }

    @Test
    void test_ch47()
    {
        final String eventString = "[event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab";
        assertEquals(WorldEvent.EventTypes.CH47_SCIENTISTS, defaultConsoleParser.parseWorldEvent(eventString).getEventType());
    }

    @Test
    void test_patrolHelicopter()
    {
        final String eventString = "[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab";
        assertEquals(WorldEvent.EventTypes.PATROL_HELICOPTER, defaultConsoleParser.parseWorldEvent(eventString).getEventType());
    }
}
