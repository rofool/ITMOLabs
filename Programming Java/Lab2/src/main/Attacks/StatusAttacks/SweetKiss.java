package main.Attacks.StatusAttacks;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

public class SweetKiss extends StatusMove {
    public SweetKiss(double pow, double acc) {
        super(Type.FAIRY, pow, acc);
    }
    @Override
    protected void applyOppEffects(Pokemon p) {
        super.applyOppEffects(p);
        Effect.confuse(p);
    }
    @Override
    protected String describe() {
        return "Uses SweetKiss";
    }
}

