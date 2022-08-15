package org.orecruncher.dsurround.config.biome.biometraits;

import net.minecraft.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.orecruncher.dsurround.lib.GameUtils;

import java.util.*;

public class BiomeCategoryAnalyzer implements IBiomeTraitAnalyzer {
    @Override
    public Collection<BiomeTrait> evaluate(Identifier id, Biome biome) {
        List<BiomeTrait> results = new ArrayList<>();

        //Biome selector (in biomes.jsom) checks biome id in traits (like minecraft:ocean)
        //We start use ConventionalBiomeTags from Fabric to fix this
        var biomes = GameUtils.getWorld().getRegistryManager().get(Registry.BIOME_KEY);

        //Search tags for current biome, sort only Fabric tags, checks to contain in BiomeTrait, and covert
        var entry = biomes.getEntry(biomes.getRawId(biome));
        if (entry.isPresent()) {
            List<BiomeTrait> biomeTraits = entry.get()
                    .streamTags()
                    .filter(d -> d.id().toString().startsWith("c:")) //only fabric tags
                    .filter(BiomeTrait.tagMapper::containsKey)
                    .map(BiomeTrait.tagMapper::get)
                    .toList();
            results.addAll(biomeTraits);
        }

        var path = id.getPath();
        if (results.contains(BiomeTrait.OCEAN) || results.contains(BiomeTrait.RIVER))
            results.add(BiomeTrait.WATER);

        if (results.contains(BiomeTrait.BEACH) || results.contains(BiomeTrait.DESERT) || path.contains("badlands"))
            results.add(BiomeTrait.SANDY);

        if (!results.contains(BiomeTrait.NETHER) && !results.contains(BiomeTrait.THEEND))
            results.add(BiomeTrait.OVERWORLD);

        if (results.contains(BiomeTrait.TAIGA))
            results.add(BiomeTrait.CONIFEROUS);

        //Arctic Wind in snow biomes
        if (path.contains("snowy_slopes") || path.contains("jagged_peaks") || path.contains("grove"))
        {
            if (!results.contains(BiomeTrait.COLD))
                results.add(BiomeTrait.COLD);

            if (!results.contains(BiomeTrait.ICY))
                results.add(BiomeTrait.ICY);
        }

        if (path.contains("meadow"))
            results.add(BiomeTrait.PLAINS);

        if (path.contains("stony_peaks"))
            results.add(BiomeTrait.HILLS);

        return results;
    }
}
