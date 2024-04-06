package org.example;

import org.example.core.BattleService;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        new Server().run(new BattleService());
    }
}