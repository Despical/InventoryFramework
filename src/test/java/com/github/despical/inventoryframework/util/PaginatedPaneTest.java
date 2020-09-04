package com.github.despical.inventoryframework.util;

import org.junit.jupiter.api.Test;

import com.github.despical.inventoryframework.pane.PaginatedPane;
import com.github.despical.inventoryframework.pane.Pane;
import com.github.despical.inventoryframework.pane.StaticPane;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class PaginatedPaneTest {

    @Test
    void testGetPanesNonExistentPage() {
        PaginatedPane pane = new PaginatedPane(0, 0);

        assertThrows(IllegalArgumentException.class, () -> pane.getPanes(0));
    }

    @Test
    void testGetPanesCollectionContents() {
        PaginatedPane paginatedPane = new PaginatedPane(0, 0);

        StaticPane pane0 = new StaticPane(0, 0);
        StaticPane pane1 = new StaticPane(0, 0);
        StaticPane pane2 = new StaticPane(0, 0);

        paginatedPane.addPane(0, pane0);
        paginatedPane.addPane(0, pane1);
        paginatedPane.addPane(0, pane2);

        Collection<Pane> panes = paginatedPane.getPanes(0);
        assertTrue(panes.contains(pane0));
        assertTrue(panes.contains(pane1));
        assertTrue(panes.contains(pane2));
    }

    @Test
    void testGetPanesCollectionSize() {
        PaginatedPane paginatedPane = new PaginatedPane(0, 0);

        StaticPane pane0 = new StaticPane(0, 0);
        StaticPane pane1 = new StaticPane(0, 0);
        StaticPane pane2 = new StaticPane(0, 0);

        paginatedPane.addPane(0, pane0);
        paginatedPane.addPane(0, pane1);
        paginatedPane.addPane(0, pane2);

        assertEquals(3, paginatedPane.getPanes(0).size());
    }
}