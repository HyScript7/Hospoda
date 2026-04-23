package io.github.hyscript7.hospoda;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Sklad {
    private final ConcurrentMap<Predmet, Double> sklad;

    public Sklad() {
        this.sklad = new ConcurrentHashMap<>();
    }

    public synchronized boolean ziskej(Map<Predmet, Double> pozadavky) {
        for (Map.Entry<Predmet, Double> entry : pozadavky.entrySet()) {
            if (sklad.getOrDefault(entry.getKey(), 0D) < entry.getValue()) {
                return false;
            }
        }
        for (Map.Entry<Predmet, Double> entry : pozadavky.entrySet()) {
            sklad.merge(entry.getKey(), -entry.getValue(), Double::sum);
        }
        return true;
    }

    public synchronized void uloz(Predmet predmet, double mnozstvi) {
        sklad.merge(predmet, mnozstvi, Double::sum);
    }

    public synchronized double getMnozstvi(Predmet predmet) {
        return sklad.getOrDefault(predmet, 0D);
    }
}
