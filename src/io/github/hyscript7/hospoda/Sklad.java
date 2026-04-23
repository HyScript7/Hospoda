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
        for (Map.Entry<Predmet, Double> entry : sklad.entrySet()) {
            if (sklad.get(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        for (Map.Entry<Predmet, Double> entry : sklad.entrySet()) {
            sklad.put(entry.getKey(), sklad.get(entry.getKey()) - entry.getValue());
        }
        return true;
    }

    public synchronized void uloz(Predmet predmet, double amount) {
        sklad.put(predmet, sklad.get(predmet) + amount);
    }

    public synchronized double getMnozstvy(Predmet predmet) {
        return sklad.get(predmet);
    }
}
