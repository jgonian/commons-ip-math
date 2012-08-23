package net.ripe.commons.ip.speed;

import java.util.NavigableSet;

import net.ripe.commons.ip.Range;
import net.ripe.commons.ip.Rangeable;

public interface SpeedTestInterface<C extends Rangeable<C, R>, R extends Range<C, R>> {

    public void add(R range);
    
    public NavigableSet<R> getSet();
}
