package main.Attacks.PhysicalAttacks;

import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Stat;
import ru.ifmo.se.pokemon.Type;

public class DoubleEdge extends PhysicalMove {
    public DoubleEdge(double pow, double acc) {
        super(Type.NORMAL, pow, acc);
    }

    @Override
    protected void applySelfDamage(Pokemon p, double damage) {
        p.setMod(Stat.HP, (int) Math.round(damage / 3.0));
    }

    @Override
    protected String describe() {
        return "Uses DoubleEdge";
    }
}

