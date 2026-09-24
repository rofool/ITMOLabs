package main.Attacks.StatusAttacks;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.SpecialMove;
import ru.ifmo.se.pokemon.Type;

public class GrassWhistle extends SpecialMove {
    public GrassWhistle(double pow, double acc) {
        super(Type.GRASS, pow, acc);
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        super.applyOppEffects(p);
        Effect.sleep(p);
    }

    @Override
    protected String describe() {
        return "Uses Grass Whistle";
    }
}
