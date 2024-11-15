package model;

import java.util.Collection;
import java.util.Map;

public record listGameResponse(Map<String, Collection<GameData>> listGames) {
}
