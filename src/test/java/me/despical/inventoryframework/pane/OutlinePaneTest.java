package me.despical.inventoryframework.pane;

import me.despical.inventoryframework.pane.util.Mask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OutlinePaneTest {

    @Test
    void testApplyMaskInvalidDimensions() {
        assertThrows(IllegalArgumentException.class, () ->
            new OutlinePane(3, 7).applyMask(new Mask("0", "1")));
    }
}
