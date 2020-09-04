package com.github.despical.inventoryframework.pane;

import org.junit.jupiter.api.Test;

import com.github.despical.inventoryframework.pane.OutlinePane;
import com.github.despical.inventoryframework.pane.util.Mask;

import static org.junit.jupiter.api.Assertions.*;

public class OutlinePaneTest {

    @Test
    void testApplyMaskInvalidDimensions() {
        assertThrows(IllegalArgumentException.class, () ->
            new OutlinePane(3, 7).applyMask(new Mask("0", "1")));
    }
}
