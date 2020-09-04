package com.github.despical.inventoryframework.font;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.despical.inventoryframework.font.util.Font;
import com.github.despical.inventoryframework.util.CSVUtil;
import com.github.despical.inventoryframework.util.SkullUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A font for characters with a space as default character. Only one instance of this class should ever exist and should
 * be used everywhere.
 *
 * @author Despical
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public class CSVFont extends Font {

    /**
     * A map with all the items and their dedicated characters
     */
    private final Map<Character, ItemStack> characterMappings;

    /**
     * The default character to use when a requested character cannot be found
     */
    private final char defaultCharacter;

    /**
     * Creates a new default font
     *
     * @param defaultCharacter the default character to use when a requested character cannot be found
     * @param filePath the relative file path to the csv file containing the character mappings
     * @since 1.0.1
     */
    public CSVFont(char defaultCharacter, String filePath) {
        this.defaultCharacter = defaultCharacter;

        try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
            characterMappings = CSVUtil.readAll(inputStream).stream()
                    .collect(Collectors.toMap(v -> v[0].charAt(0), v -> SkullUtil.getSkull(v[1])));
        } catch (IOException e) {
            throw new RuntimeException("Error loading CSV-based font: " + filePath, e);
        }
    }

    @NotNull
    @Contract(pure = true)
    @Override
    public ItemStack getDefaultItem() {
        return characterMappings.get(defaultCharacter);
    }

    @Nullable
    @Contract(pure = true)
    @Override
    public ItemStack toItem(char character) {
        return characterMappings.get(character);
    }
}