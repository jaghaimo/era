package era;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.util.Misc;

import org.apache.log4j.Level;

import lombok.extern.log4j.Log4j;

@Log4j
public class EraListener extends BaseCampaignEventListener {

    // At most increases rep by 0.01 when faction hates the one you've beaten
    // (beaten reputation at adjusted faction is -1.0). Negative sign to give
    // a positive increase to the player.
    public static float ADJUSTMENT_SCALE = -100f;

    // Never adjust these factions.
    public static String BLACKLIST[] = { "derelict", "knights_of_ludd", "lions_guard", "mercenary", "neutral", "omega",
            "poor", "remnants", "scavengers", "sleeper" };

    public EraListener() {
        super(false);
        Global.getSector().addTransientListener(this);
        // Change to DEBUG for verbose logging
        log.setLevel(Level.INFO);
    }

    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {
        if (!result.didPlayerWin()) {
            log.debug("Skipping since player did not win");
            return;
        }
        FactionAPI playerFaction = Global.getSector().getPlayerFaction();
        FactionAPI commissionedFaction = Misc.getCommissionFaction();
        FactionAPI beatenFaction = result.getLoserResult().getFleet().getFaction();
        log.debug("Trying to adjust rep based on beating " + beatenFaction.getId());
        adjustReputation(playerFaction, beatenFaction, new FactionAPI[] { commissionedFaction, playerFaction });
    }

    private void adjustReputation(FactionAPI playerFaction, FactionAPI beatenFaction, FactionAPI skipFactions[]) {
        for (FactionAPI consideredFaction : Global.getSector().getAllFactions()) {
            if (!canAdjust(consideredFaction, beatenFaction, skipFactions)) {
                continue;
            }
            adjust(playerFaction, beatenFaction, consideredFaction);
        }
    }

    private void adjust(FactionAPI playerFaction, FactionAPI beatenFaction, FactionAPI consideredFaction) {
        float delta = getAdjustmentDelta(consideredFaction, beatenFaction);
        if (delta < 0) {
            // Already checked for non-hostile, so this should always be positive.
            log.warn("Skipping adjustment due to too low change " + consideredFaction.getId());
            return;
        }
        log.info("Adjusting faction " + consideredFaction.getId() + " rep by " + String.valueOf(delta));
        playerFaction.adjustRelationship(consideredFaction.getId(), delta);
    }

    private boolean canAdjust(FactionAPI faction, FactionAPI beatenFaction, FactionAPI skipFactions[]) {
        if (isBlacklisted(faction)) {
            log.debug("Skipping blacklisted faction " + faction.getId());
            return false;
        }
        if (isSkippable(faction, skipFactions)) {
            log.debug("Skipping faction " + faction.getId());
            return false;
        }
        if (!faction.isHostileTo(beatenFaction)) {
            log.debug("Skipping non-hostile faction " + faction.getId());
            return false;
        }
        return true;
    }

    private float getAdjustmentDelta(FactionAPI consideredFaction, FactionAPI beatenFaction) {
        return consideredFaction.getRelationship(beatenFaction.getId()) / ADJUSTMENT_SCALE;
    }

    private boolean isBlacklisted(FactionAPI faction) {
        for (String blacklistedFactionId : BLACKLIST) {
            if (blacklistedFactionId.equals(faction.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean isSkippable(FactionAPI faction, FactionAPI skipFactions[]) {
        for (FactionAPI skippableFaction : skipFactions) {
            if (skippableFaction == null) {
                continue;
            }
            if (faction.equals(skippableFaction)) {
                return true;
            }
        }
        return false;
    }
}
