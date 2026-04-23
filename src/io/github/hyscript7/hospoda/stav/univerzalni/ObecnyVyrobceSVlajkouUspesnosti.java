package io.github.hyscript7.hospoda.stav.univerzalni;

import io.github.hyscript7.hospoda.Predmet;
import io.github.hyscript7.hospoda.Sklad;
import io.github.hyscript7.hospoda.stav.Stav;
import io.github.hyscript7.hospoda.stav.StromChovani;

import java.util.Map;

public class ObecnyVyrobceSVlajkouUspesnosti<T> extends Stav<T> {
    private final Predmet predmetKPriprave;
    private final Map<Predmet, Double> limity;
    private final Sklad sklad;
    private final IVlajkaUspesnosti kontejner;

    public ObecnyVyrobceSVlajkouUspesnosti(T herec, StromChovani<T> stromChovani, Predmet predmetKPriprave, Sklad sklad, Map<Predmet, Double> limity, IVlajkaUspesnosti kontejnerVlajky) {
        super(herec, stromChovani);
        this.predmetKPriprave = predmetKPriprave;
        this.limity = limity;
        this.sklad = sklad;
        this.kontejner = kontejnerVlajky;
    }

    @Override
    public void chovani() {
        if (sklad.getMnozstvi(predmetKPriprave) >= limity.getOrDefault(predmetKPriprave, Double.MAX_VALUE)) {
            kontejner.nastavUspesnostPosledniAkce(true);
            return;
        }
        if (sklad.ziskej(predmetKPriprave.getRecept())) {
            sklad.uloz(predmetKPriprave, predmetKPriprave.getCitatelVyroby());
            kontejner.nastavUspesnostPosledniAkce(true);
        } else {
            kontejner.nastavUspesnostPosledniAkce(false);
        }
    }
}
