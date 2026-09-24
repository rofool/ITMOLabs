package main.Attacks.PhysicalAttacks;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class FireFang extends PhysicalMove {
    public FireFang(double pow, double acc) {
        super(Type.FIRE, pow, acc);

    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        super.applyOppEffects(p);
        if (Math.random() < 0.1) {
            Effect.flinch(p);
        }
        if (Math.random() < 0.1) {
            Effect.burn(p);
        }
    }

    @Override
    protected String describe() {
        return "Uses Fire Fang";
    }
}

