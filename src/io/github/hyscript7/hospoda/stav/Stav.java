package io.github.hyscript7.hospoda.stav;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Stav<T> {
    private static record Prechod<T>(Predicate<T> podminka, Stav<T> naslednyStav) {}

    private final T herec;
    private final StromChovani<T> strom;
    private final List<Prechod<T>> prechody;

    public Stav(T herec, StromChovani<T> strom) {
        this.herec = herec;
        this.strom = strom;
        this.prechody = new ArrayList<>();
    }

    protected T getHerec() {
        return herec;
    }

    protected StromChovani<T> getStrom() {
        return strom;
    }

    public void pridejPrechod(Predicate<T> podminka, Stav<T> novyStav) {
        prechody.add(new Prechod<>(podminka, novyStav));
    }

    public void run() {
        chovani();
        zkontrolujPrechody();
    }

    public void zkontrolujPrechody() {
        for (Prechod<T> entry : prechody) {
            if (entry.podminka.test(herec)) {
                strom.zmenStav(entry.naslednyStav);
                return;
            }
        }
    }

    public void chovani() {};

    public void priVstupu(Stav<T> predchoziStav) {}
    public void priOdchodu(Stav<T> novyStav) {}
}
