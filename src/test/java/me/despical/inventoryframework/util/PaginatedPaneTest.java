package me.despical.inventoryframework.util;

import me.despical.inventoryframework.pane.StaticPane;
import org.junit.jupiter.api.Test;

import me.despical.inventoryframework.pane.PaginatedPane;
import me.despical.inventoryframework.pane.Pane;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class PaginatedPaneTest {

    @Test
    void testGetPanesNonExistentPage() {
        PaginatedPane pane = new PaginatedPane(0, 0);

        //noinspection ResultOfMethodCallIgnored
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
