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

package bisq.core.dao.governance.proposal;

import bisq.core.dao.state.BsqStateService;
import bisq.core.dao.state.governance.Param;

import bisq.common.crypto.Hash;

import org.bitcoinj.core.Coin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * Encapsulates consensus critical aspects.
 */
@Slf4j
public class ProposalConsensus {
    public Coin getFee(BsqStateService bsqStateService, int chainHeadHeight) {
        return Coin.valueOf(bsqStateService.getParamValue(Param.PROPOSAL_FEE, chainHeadHeight));
    }

    public byte[] getHashOfPayload(Proposal payload) {
        final byte[] bytes = payload.toProtoMessage().toByteArray();
        return Hash.getSha256Ripemd160hash(bytes);
    }

    public byte[] getOpReturnData(byte[] hashOfPayload, byte opReturnType, byte version) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(opReturnType);
            outputStream.write(version);
            outputStream.write(hashOfPayload);
            return outputStream.toByteArray();
        } catch (IOException e) {
            // Not expected to happen ever
            e.printStackTrace();
            log.error(e.toString());
            return new byte[0];
        }
    }
}
