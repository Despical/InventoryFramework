package com.github.despical.inventoryframework.exception;

import org.jetbrains.annotations.NotNull;

import com.github.despical.inventoryframework.Gui;

/**
 * An exception indicating that something went wrong while trying to load a {@link Gui} from an XML file.
 *
 * @author Despical
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public class XMLLoadException extends RuntimeException {

    /**
     * Constructs the exception with a given message
     *
     * @param message the message to show
     * @since 1.0.1
     */
    public XMLLoadException(@NotNull String message) {
        super(message);
    }

    /**
     * Constructs the exception with a given cause
     *
     * @param cause the cause of this exception
     * @since 1.0.1
     */
    public XMLLoadException(@NotNull Throwable cause) {
        super(cause);
    }

    /**
     * Constructs the exception with a given message and cause
     *
     * @param message the message to show
     * @param cause the cause of this exception
     */
    public XMLLoadException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}