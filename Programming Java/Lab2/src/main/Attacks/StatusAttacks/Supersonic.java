package main.Attacks.StatusAttacks;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

public class Supersonic extends StatusMove {
    public Supersonic(double pow, double acc) {
        super(Type.NORMAL, pow, acc);
    }
    @Override
    protected void applyOppEffects(Pokemon p) {
        super.applyOppEffects(p);
        Effect.confuse(p);

    }
    @Override
    protected String describe() {
        return "Uses Supersonic";
    }
}

