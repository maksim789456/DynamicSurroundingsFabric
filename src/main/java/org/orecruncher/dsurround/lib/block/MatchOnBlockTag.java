package org.orecruncher.dsurround.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class MatchOnBlockTag extends BlockStateMatcher {

    private final TagKey<Block> tagId;

    MatchOnBlockTag(Identifier tagId) {
        this.tagId = TagKey.of(RegistryKeys.BLOCK, tagId);
    }

    @Override
    public boolean isEmpty() {
        return Registries.BLOCK.containsId(tagId.id());
    }

    @Override
    public boolean match(BlockState state) {
        return state.isIn(this.tagId);
    }
}
