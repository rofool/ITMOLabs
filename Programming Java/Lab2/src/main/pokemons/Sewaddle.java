package main.pokemons;

import main.Attacks.PhysicalAttacks.*;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Sewaddle extends Pokemon {
    public Sewaddle(String name, int level) {
        super(name, level);
        super.setType(Type.BUG);
        super.setType(Type.GRASS);
        super.setStats(45, 53, 70, 40, 60, 42);
        Tackle tackle = new Tackle(40,100);
        RazorLeaf razorleaf = new RazorLeaf(55,95);
        super.setMove(tackle,razorleaf);
    }
}
