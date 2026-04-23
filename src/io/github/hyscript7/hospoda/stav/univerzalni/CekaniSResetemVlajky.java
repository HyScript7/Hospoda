package io.github.hyscript7.hospoda.stav.univerzalni;

import io.github.hyscript7.hospoda.stav.StromChovani;

import java.time.Duration;

public class CekaniSResetemVlajky<T extends Thread> extends Cekani<T> {
    private final IVlajkaUspesnosti kontejner;

    public CekaniSResetemVlajky(T herec, StromChovani<T> strom, Duration delka, IVlajkaUspesnosti kontejnerVlajky) {
        super(herec, strom, delka);
        this.kontejner = kontejnerVlajky;
    }

    @Override
    public void chovani() {
        super.chovani();
        kontejner.nastavUspesnostPosledniAkce(true);
    }
}
