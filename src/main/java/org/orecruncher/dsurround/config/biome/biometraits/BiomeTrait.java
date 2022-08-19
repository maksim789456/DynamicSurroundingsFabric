package org.orecruncher.dsurround.config.biome.biometraits;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.TagKey;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum BiomeTrait {
    // Generic case of WTF
    UNKNOWN("UNKNOWN"),
    // Special internal traits for synthetic biomes
    FAKE("FAKE"),
    INSIDE("inside"),
    VILLAGE("VILLAGE"),
    PLAYER("PLAYER"),
    SPACE("SPACE"),
    CLOUDS("CLOUDS"),
    UNDER_RIVER("UNDER_RIVER"),
    UNDER_WATER("UNDER_WATER"),
    UNDER_OCEAN("UNDER_OCEAN"),
    // Biome categories as traits
    NONE("none"),
    TAIGA("taiga", ConventionalBiomeTags.TAIGA),
    EXTREME_HILLS("extreme_hills", ConventionalBiomeTags.EXTREME_HILLS),
    JUNGLE("jungle", ConventionalBiomeTags.JUNGLE),
    MESA("mesa", ConventionalBiomeTags.MESA),
    PLAINS("plains", ConventionalBiomeTags.PLAINS),
    SAVANNA("savanna", ConventionalBiomeTags.SAVANNA),
    ICY("icy", ConventionalBiomeTags.ICY),
    THEEND("the_end", ConventionalBiomeTags.IN_THE_END),
    BEACH("beach", ConventionalBiomeTags.BEACH),
    FOREST("forest", ConventionalBiomeTags.FOREST),
    OCEAN("ocean", ConventionalBiomeTags.OCEAN),
    DESERT("desert", ConventionalBiomeTags.DESERT),
    RIVER("river", ConventionalBiomeTags.RIVER),
    SWAMP("swamp", ConventionalBiomeTags.SWAMP),
    MUSHROOM("mushroom", ConventionalBiomeTags.MUSHROOM),
    NETHER("nether", ConventionalBiomeTags.IN_NETHER),
    UNDERGROUND("underground", ConventionalBiomeTags.UNDERGROUND),
    /* Extended categories */
    WATER("WATER"),
    WET("WET"),
    DRY("DRY"),
    HOT("HOT"),
    COLD("COLD"),
    SPARSE("SPARSE"),
    DENSE("DENSE"),
    CONIFEROUS("CONIFEROUS"),
    SPOOKY("SPOOKY"),
    DEAD("DEAD"),
    MAGICAL("MAGICAL"),
    PLATEAU("PLATEAU"),
    MOUNTAIN("MOUNTAIN"),
    HILLS("HILLS"),
    SANDY("SANDY"),
    SNOWY("SNOWY"),
    WASTELAND("WASTELAND"),
    VOID("VOID"),
    OVERWORLD("OVERWORLD"),
    DEEP("DEEP");

    private static final Map<String, BiomeTrait> mapper = new HashMap<>();
    public static final Map<TagKey<Biome>, BiomeTrait> tagMapper = new HashMap<>();

    static {
        register(BiomeTrait.NONE);  // stone_shore why?
        register(BiomeTrait.TAIGA);
        register(BiomeTrait.EXTREME_HILLS);
        register(BiomeTrait.JUNGLE);
        register(BiomeTrait.MESA);
        register(BiomeTrait.PLAINS);
        register(BiomeTrait.SAVANNA);
        register(BiomeTrait.ICY);
        register(BiomeTrait.THEEND);
        register(BiomeTrait.BEACH);
        register(BiomeTrait.FOREST);
        register(BiomeTrait.OCEAN);
        register(BiomeTrait.DESERT);
        register(BiomeTrait.RIVER);
        register(BiomeTrait.SWAMP);
        register(BiomeTrait.MUSHROOM);
        register(BiomeTrait.NETHER);
        register(BiomeTrait.UNDERGROUND);
        register(BiomeTrait.WATER);
        register(BiomeTrait.WET);
        register(BiomeTrait.DRY);
        register(BiomeTrait.HOT);
        register(BiomeTrait.COLD);
        register(BiomeTrait.SPARSE);
        register(BiomeTrait.DENSE);
        register(BiomeTrait.CONIFEROUS);
        register(BiomeTrait.SPOOKY);
        register(BiomeTrait.DEAD);
        register(BiomeTrait.MAGICAL);
        register(BiomeTrait.PLATEAU);
        register(BiomeTrait.MOUNTAIN);
        register(BiomeTrait.HILLS);
        register(BiomeTrait.SANDY);
        register(BiomeTrait.SNOWY);
        register(BiomeTrait.WASTELAND);
        register(BiomeTrait.VOID);
        register(BiomeTrait.OVERWORLD);
        register(BiomeTrait.DEEP);
    }

    private final String name;
    private TagKey<Biome> tag;

    @SafeVarargs
    BiomeTrait(String name, TagKey<Biome>... tags) {
        this.name = name.toUpperCase();
        var first = Arrays.stream(tags).findFirst();
        first.ifPresent(biomeTagKey -> this.tag = biomeTagKey);
    }

//    public static BiomeTrait of(Biome.Category category) {
//        var result = mapper.get(category.getName().toUpperCase());
//        return result == null ? UNKNOWN : result;
//    }

    public static BiomeTrait of(String name) {
        var result = mapper.get(name.toUpperCase());
        return result == null ? UNKNOWN : result;
    }

    private static void register(BiomeTrait trait) {
        mapper.put(trait.name, trait);
        var tag = trait.getTag();
        tag.ifPresent(biomeTagKey -> tagMapper.put(biomeTagKey, trait));
    }

    public String getName() {
        return this.name;
    }

    public Optional<TagKey<Biome>> getTag() {
        return Optional.ofNullable(tag);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
