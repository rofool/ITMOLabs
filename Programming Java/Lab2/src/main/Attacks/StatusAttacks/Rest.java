package main.Attacks.StatusAttacks;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

public class Rest extends StatusMove {
    public Rest(double pow, double acc) {
        super(Type.PSYCHIC, pow, acc);
    }
    @Override
    protected void applySelfEffects(Pokemon p) {
        super.applySelfEffects(p);
        Effect.sleep(p);
    }
    @Override
    protected String describe() {
        return "Uses rest and restores hp";
    }
}

