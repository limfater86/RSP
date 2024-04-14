package org.example.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public enum HitType {
    Rock,
    Scissors,
    Paper;

    private static final HashMap<HitType, HitType> beatsMap = new HashMap<>(Map.of(
            Rock, Scissors,
            Scissors, Paper,
            Paper, Rock
    ));

    public boolean isBeat(HitType opponentHit) {
        HitType winHit = beatsMap.get(this);
        return winHit.equals(opponentHit);
    }
}
