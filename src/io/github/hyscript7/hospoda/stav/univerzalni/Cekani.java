package io.github.hyscript7.hospoda.stav.univerzalni;

import io.github.hyscript7.hospoda.stav.Stav;
import io.github.hyscript7.hospoda.stav.StromChovani;

import java.time.Duration;

public class Cekani<T extends Thread> extends Stav<T> {
    private final Duration delka;

    public Cekani(T herec, StromChovani<T> strom, Duration delka) {
        super(herec, strom);
        this.delka = delka;
    }

    @Override
    public void chovani() {
        try {
            Thread.sleep(delka);
        } catch (InterruptedException e) {
            // TODO: Log warning
            Thread.currentThread().interrupt();
        }
    }
}
