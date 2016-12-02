/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.s;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.common.FilterCreatureCard;
import mage.game.Game;
import mage.game.permanent.token.Token;
import mage.players.Player;
import mage.target.TargetCard;

/**
 *
 * @author LevelX2
 */
public class StitcherGeralf extends CardImpl {

    public StitcherGeralf(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{3}{U}{U}");
        this.supertype.add("Legendary");
        this.subtype.add("Human");
        this.subtype.add("Wizard");

        this.power = new MageInt(3);
        this.toughness = new MageInt(4);

        // {2}{U}, {tap}: Each player puts the top three cards of his or her library into his or her graveyard. Exile up to two creature cards put into graveyards this way. Create an X/X blue Zombie creature token, where X is the total power of the cards exiled this way.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new StitcherGeralfEffect(), new ManaCostsImpl("{2}{U}"));
        ability.addCost(new TapSourceCost());
        this.addAbility(ability);
    }

    public StitcherGeralf(final StitcherGeralf card) {
        super(card);
    }

    @Override
    public StitcherGeralf copy() {
        return new StitcherGeralf(this);
    }
}

class StitcherGeralfEffect extends OneShotEffect {

    public StitcherGeralfEffect() {
        super(Outcome.PutCreatureInPlay);
        this.staticText = "Each player puts the top three cards of his or her library into his or her graveyard. Exile up to two creature cards put into graveyards this way. Create an X/X blue Zombie creature token, where X is the total power of the cards exiled this way";
    }

    public StitcherGeralfEffect(final StitcherGeralfEffect effect) {
        super(effect);
    }

    @Override
    public StitcherGeralfEffect copy() {
        return new StitcherGeralfEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Cards cards = new CardsImpl();
            for (UUID playerId: game.getState().getPlayersInRange(controller.getId(), game)) {
                Player player = game.getPlayer(playerId);
                if (player != null) {
                    cards.addAll(player.getLibrary().getTopCards(game, 3));
                }
            }
            controller.moveCards(cards, Zone.GRAVEYARD, source, game);
            TargetCard target = new TargetCard(0,2,Zone.GRAVEYARD, new FilterCreatureCard("creature cards to exile"));
            controller.chooseTarget(outcome, cards, target, source, game);
            int power = 0;
            for (UUID cardId: target.getTargets()) {
                Card card = game.getCard(cardId);
                if (card != null) {
                    power += card.getPower().getValue();
                    controller.moveCardToExileWithInfo(card, null, "", source.getSourceId(), game, Zone.GRAVEYARD, true);
                }
            }
            return new CreateTokenEffect(new StitcherGeralfZombieToken(power)).apply(game, source);
        }
        return false;
    }
}

class StitcherGeralfZombieToken extends Token {

    StitcherGeralfZombieToken(int xValue) {
        super("Zombie", "X/X blue Zombie creature token");
        setOriginalExpansionSetCode("C14");
        setTokenType(1);
        cardType.add(CardType.CREATURE);
        color.setBlue(true);
        subtype.add("Zombie");
        power = new MageInt(xValue);
        toughness = new MageInt(xValue);
    }
}
