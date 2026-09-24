package main.pokemons;

import main.Attacks.PhysicalAttacks.*;
import main.Attacks.StatusAttacks.GrassWhistle;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Swadloon extends Pokemon {
    public Swadloon(String name, int level) {
        super(name, level);
        super.setType(Type.BUG);
        super.setType(Type.GRASS);
        super.setStats(55, 63, 90, 50, 80, 42);
        Tackle tackle = new Tackle(40,100);
        RazorLeaf razorleaf = new RazorLeaf(55,95);
        GrassWhistle grasswhistle = new GrassWhistle(0,55);
        super.setMove(tackle,razorleaf, grasswhistle);
    }
}
