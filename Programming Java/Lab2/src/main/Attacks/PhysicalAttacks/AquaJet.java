package main.Attacks.PhysicalAttacks;

import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Type;

public class AquaJet extends PhysicalMove {
    public AquaJet(double pow, double acc) {
        super(Type.WATER, pow, acc);
        this.priority = 1;
    }


    @Override
    protected String describe() {
        return "Uses Aqua Jet";
    }
}

