package me.despical.inventoryframework.util;

import me.despical.inventoryframework.exception.XMLLoadException;
import me.despical.inventoryframework.exception.XMLReflectionException;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
/**
 * 
 * An utility class for xml files
 * 
 * @author Despical
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public class XMLUtil {

    /**
     * Loads an event consumer from the given instance and element
     *
     * @param instance the object instance
     * @param element the element
     * @param name the name of the attribute
     * @param eventType event type of class
     * @param <T> any object that is event
     * @return the consumer to be called on click
     */
    @Nullable
    @Contract(pure = true)
    public static <T extends Event> Consumer<T> loadOnEventAttribute(@NotNull Object instance, @NotNull Element element,
                                                                     @NotNull Class<T> eventType, @NotNull String name) {
        String attribute = element.getAttribute(name);
        for (Method method : instance.getClass().getMethods()) {
            if (!method.getName().equals(attribute))
                continue;

            int parameterCount = method.getParameterCount();
            boolean eventParameter;
            if (parameterCount == 0) {
                eventParameter = false;
            } else if (parameterCount == 1 &&
                    eventType.isAssignableFrom(method.getParameterTypes()[0])) {
                eventParameter = true;
            } else {
                continue;
            }

            return event -> {
                try {
                    method.setAccessible(true);
                    if (eventParameter) {
                        method.invoke(instance, event);
                    } else {
                        method.invoke(instance);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new XMLReflectionException(e);
                }
            };
        }

        return null;
    }

    /**
     * Sets a field from the given instance and element to the specified value
     *
     * @param instance the class instance the field is located in
     * @param element the element from which the field is specified
     * @param value the field's new value
     */
    public static void loadFieldAttribute(@NotNull Object instance, @NotNull Element element, @Nullable Object value) {
        try {
            Field field = instance.getClass().getField(element.getAttribute("field"));

            field.setAccessible(true);
            field.set(instance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new XMLLoadException(e);
        }
    }
}