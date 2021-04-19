package javagames.example.ch1;

import java.awt.*;

public class DisplayModeWrapper {
    DisplayMode dm;

    public DisplayModeWrapper(DisplayMode dm) {
        this.dm = dm;
    }

    @Override
    public String toString() {
        return " " + dm.getWidth() + "X" + dm.getHeight();
    }

    @Override
    public boolean equals(Object obj) {
        DisplayModeWrapper other = (DisplayModeWrapper) obj;
        if (dm.getWidth() != other.dm.getWidth() || dm.getHeight() != other.dm.getHeight())
            return false;
        return true;
    }
}
