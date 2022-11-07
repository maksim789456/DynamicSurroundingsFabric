package org.orecruncher.dsurround.mixins.core;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.orecruncher.dsurround.sound.SoundVolumeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundSystem.class, priority = 1100)
public class MixinSoundSystemLow {

    @Inject(method = "getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F", at = @At("HEAD"), cancellable = true)
    private void dsurround_getAdjustedVolume(SoundInstance sound, CallbackInfoReturnable<Float> ci) {
        try {
            ci.setReturnValue(SoundVolumeEvaluator.getAdjustedVolume(sound));
        } catch (final Exception ignore) {}
    }
}
