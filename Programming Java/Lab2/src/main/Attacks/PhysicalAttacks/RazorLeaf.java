package main.Attacks.PhysicalAttacks;

import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class RazorLeaf extends PhysicalMove {
    public RazorLeaf(double pow, double acc) {
        super(Type.GRASS, pow, acc);
    }

    @Override
    protected double calcCriticalHit(Pokemon att, Pokemon def) {
        return 1.0 / 8.0;
    }

    @Override
    protected String describe() {
        return "Uses Razor Leaf";
    }
}