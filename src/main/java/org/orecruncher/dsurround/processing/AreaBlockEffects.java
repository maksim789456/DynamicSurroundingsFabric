package org.orecruncher.dsurround.processing;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.orecruncher.dsurround.Client;
import org.orecruncher.dsurround.eventing.ClientEventHooks;
import org.orecruncher.dsurround.lib.math.TimerEMA;
import org.orecruncher.dsurround.lib.scanner.ClientPlayerContext;
import org.orecruncher.dsurround.processing.misc.BlockEffectManager;
import org.orecruncher.dsurround.processing.scanner.AlwaysOnBlockEffectScanner;
import org.orecruncher.dsurround.processing.scanner.RandomBlockEffectScanner;

import java.util.Collection;

public class AreaBlockEffects extends ClientHandler {

    protected ClientPlayerContext locus;
    protected BlockEffectManager blockEffects;
    protected RandomBlockEffectScanner nearEffects;
    protected RandomBlockEffectScanner farEffects;
    protected AlwaysOnBlockEffectScanner alwaysOn;

    private boolean isConnected = false;

    AreaBlockEffects() {
        super("Area Block Effects");

        ClientEventHooks.BLOCK_UPDATE.register(this::blockUpdates);
    }

    @Override
    public void process(final PlayerEntity player) {
        if (player == null) return;
        if (!isConnected) return;

        // reduces crash events, but eventually can lead to no sound events appeared
        // #Investigate
        // TODO: #ImplementLogging #GatherLogsAfterPatch #GatherIssuesAfterPatch
        if (this.blockEffects != null) {
            this.blockEffects.tick(player);
        }
        if (this.nearEffects != null) {
            this.nearEffects.tick();
        }
        if (this.farEffects != null) {
            this.farEffects.tick();
        }
        if (this.alwaysOn != null) {
            this.alwaysOn.tick();
        }
    }

    @Override
    public void onConnect() {
        this.locus = new ClientPlayerContext();
        this.blockEffects = new BlockEffectManager();
        this.alwaysOn = new AlwaysOnBlockEffectScanner(this.locus, this.blockEffects, Client.Config.blockEffects.blockEffectRange);
        this.nearEffects = new RandomBlockEffectScanner(this.locus, this.blockEffects, RandomBlockEffectScanner.NEAR_RANGE);
        this.farEffects = new RandomBlockEffectScanner(this.locus, this.blockEffects, RandomBlockEffectScanner.FAR_RANGE);

        this.isConnected = true;
    }

    @Override
    public void onDisconnect() {
        this.locus = null;
        this.blockEffects = null;
        this.alwaysOn = null;
        this.nearEffects = null;
        this.farEffects = null;

        this.isConnected = false;
    }

    private void blockUpdates(Collection<BlockPos> blockPos) {
        if (this.alwaysOn != null) {
            blockPos.forEach(this.alwaysOn::onBlockUpdate);
        }
    }

    @Override
    protected void gatherDiagnostics(Collection<String> left, Collection<String> right, Collection<TimerEMA> timers) {
        if (this.blockEffects == null || left == null) return;
        
        left.add(Formatting.LIGHT_PURPLE + String.format("Total Effects: %d", this.blockEffects.count()));
    }
}
