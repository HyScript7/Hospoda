package io.github.hyscript7.hospoda.stav;

public class StromChovani<T> {
    private Stav<T> aktualniStav;

    public StromChovani() {
        this.aktualniStav = null;
    }

    public void zmenStav(Stav<T> novyStav) {
        if (aktualniStav != null) {
            aktualniStav.priOdchodu(novyStav);
        }
        novyStav.priVstupu(aktualniStav);
        this.aktualniStav = novyStav;
    }

    public void run() {
        if (aktualniStav != null) {
            aktualniStav.run();
        }
    }
}
