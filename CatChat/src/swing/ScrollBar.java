package swing;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JScrollBar;

public class ScrollBar extends JScrollBar {

    public ScrollBar() {
        setUI(new ModernScrollBarUI());
        setPreferredSize(new Dimension(10, 5));
        setBackground(new Color(244, 244, 244));
        setUnitIncrement(20);
    }
}
