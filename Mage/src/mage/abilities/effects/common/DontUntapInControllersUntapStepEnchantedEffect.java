package mage.abilities.effects.common;

import mage.abilities.Ability;
import mage.abilities.effects.ContinuousRuleModifiyingEffectImpl;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.PhaseStep;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;

/**
 * @author nantuko
 */
public class DontUntapInControllersUntapStepEnchantedEffect extends ContinuousRuleModifiyingEffectImpl {

    public DontUntapInControllersUntapStepEnchantedEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Detriment, false, true);
        staticText = "Enchanted permanent doesn't untap during its controller's untap step";
    }

    public DontUntapInControllersUntapStepEnchantedEffect(final DontUntapInControllersUntapStepEnchantedEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public DontUntapInControllersUntapStepEnchantedEffect copy() {
        return new DontUntapInControllersUntapStepEnchantedEffect(this);
    }

    @Override
    public String getInfoMessage(Ability source, GameEvent event, Game game) {
        Permanent enchantment = game.getPermanent(source.getSourceId());
        if (enchantment != null && enchantment.getAttachedTo() != null) {
            Permanent enchanted = game.getPermanent(enchantment.getAttachedTo());
            if (enchanted != null) {
                return enchanted.getLogName() + " doesn't untap during its controller's untap step (" + enchantment.getLogName() + ")";
            }            
        }
        return null;
    }

    
    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (GameEvent.EventType.UNTAP.equals(event.getType()) && PhaseStep.UNTAP.equals(game.getTurn().getStepType())) {
            Permanent enchantment = game.getPermanent(source.getSourceId());
            if (enchantment != null && enchantment.getAttachedTo() != null && event.getTargetId().equals(enchantment.getAttachedTo())) {
                Permanent permanent = game.getPermanent(enchantment.getAttachedTo());
                if (permanent != null &&  permanent.getControllerId().equals(game.getActivePlayerId())) {
                    return true;
                }
            }
        }
        return false;
    }

}