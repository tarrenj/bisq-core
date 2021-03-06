/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.dao.voting.voteresult;

import bisq.core.dao.governance.voteresult.VoteResultConsensus;

import lombok.extern.slf4j.Slf4j;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

@Slf4j
public class VoteResultConsensusTest {
    @Before
    public void setup() {
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetWeightedMeritAmount() {
        int currentChainHeight;
        int blocksPerYear = 50_000; // 144*365=51264;
        currentChainHeight = 1_000_000;

        assertEquals("fresh issuance", 100000, VoteResultConsensus.getWeightedMeritAmount(100_000, 1_000_000,
                currentChainHeight, blocksPerYear));
        assertEquals("0.5 year old issuance", 75000, VoteResultConsensus.getWeightedMeritAmount(100_000, 975_000,
                currentChainHeight, blocksPerYear));
        assertEquals("1 year old issuance", 50000, VoteResultConsensus.getWeightedMeritAmount(100_000, 950_000,
                currentChainHeight, blocksPerYear));
        assertEquals("1.5 year old issuance", 25000, VoteResultConsensus.getWeightedMeritAmount(100_000, 925_000,
                currentChainHeight, blocksPerYear));
        assertEquals("2 year old issuance", 0, VoteResultConsensus.getWeightedMeritAmount(100_000, 900_000,
                currentChainHeight, blocksPerYear));
        assertEquals("3 year old issuance", 0, VoteResultConsensus.getWeightedMeritAmount(100_000, 850_000,
                currentChainHeight, blocksPerYear));
    }

    @Test
    public void testInvalidChainHeight() {
        exception.expect(IllegalArgumentException.class);
        VoteResultConsensus.getWeightedMeritAmount(100_000, 2_000_000, 1_000_000, 1_000_000);
    }

    @Test
    public void testInvalidAmount() {
        exception.expect(IllegalArgumentException.class);
        VoteResultConsensus.getWeightedMeritAmount(-100_000, 1, 1_000_000, 1_000_000);
    }

    @Test
    public void testInvalidCurrentChainHeight() {
        exception.expect(IllegalArgumentException.class);
        VoteResultConsensus.getWeightedMeritAmount(100_000, 1, -1, 1_000_000);
    }

    @Test
    public void testInvalidBlockPerYear() {
        exception.expect(IllegalArgumentException.class);
        VoteResultConsensus.getWeightedMeritAmount(100_000, 1, 11, -1);
    }
}
