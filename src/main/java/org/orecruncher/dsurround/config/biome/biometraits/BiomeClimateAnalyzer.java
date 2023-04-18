package org.orecruncher.dsurround.config.biome.biometraits;

import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BiomeClimateAnalyzer implements IBiomeTraitAnalyzer {

    @Override
    public Collection<BiomeTrait> evaluate(Identifier id, Biome biome) {
        List<BiomeTrait> results = new ArrayList<>();

        var path = id.getPath();

        // If it's The End there isn't much going on.
        if (path.contains("the_end") || path.contains("end_")) {
//        if (category == Biome.Category.THEEND) {
            results.add(BiomeTrait.VOID);
            return results;
        }

        var biomeTemp = biome.getTemperature();

        if(biomeTemp<0.15F)
            results.add(BiomeTrait.SNOWY);

        // Nether is always hot
        if (path.contains("nether") || path.contains("soul_sand_valley") || path.contains("basalt_deltas") || path.contains("warped_forest") || path.contains("crimson_forest"))
//        if (category == Biome.Category.NETHER)
            results.add(BiomeTrait.HOT);
        else if (biomeTemp < 0.15F)
            results.add(BiomeTrait.COLD);
        else if (biomeTemp > 1F)
            results.add(BiomeTrait.HOT);
        
        String [] wetBiomes = {"jungle","swamp","snowy_slopes","frozen_peaks","mangrove_swamp","mushroom_fields"};
        String [] dryBiomes = {"desert","savanna","badlands","nether","soul_sand_valley","warped_forest","crimson_forest","basalt_deltas"};

        if (Arrays.stream(wetBiomes).anyMatch(path::contains))
            results.add(BiomeTrait.WET);
        else if (Arrays.stream(dryBiomes).anyMatch(path::contains))
            results.add(BiomeTrait.DRY);

        return results;
    }
}
