package main.Attacks.SpecialAttacks;

import ru.ifmo.se.pokemon.*;

public class EnergyBall extends SpecialMove {
    public EnergyBall(double pow, double acc) {
        super(Type.GRASS, pow, acc);
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        super.applyOppEffects(p);
        if (Math.random() < 0.1) {
            p.setMod(Stat.SPECIAL_DEFENSE,-1);
        }
    }

    @Override
    protected String describe() {
        return "Uses Grass Whistle";
    }
}
