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

package bisq.core.dao.state.blockchain;

import io.bisq.generated.protobuffer.PB;

import lombok.Data;

import javax.annotation.concurrent.Immutable;

/**
 * The base class for RawBlock and Block containing the common immutable bitcoin
 * blockchain specific data.
 */
@Immutable
@Data
public abstract class BaseBlock {
    protected final int height;
    protected final long time; // in ms
    protected final String hash;
    protected final String previousBlockHash;

    BaseBlock(int height, long time, String hash, String previousBlockHash) {
        this.height = height;
        this.time = time;
        this.hash = hash;
        this.previousBlockHash = previousBlockHash;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    PB.BaseBlock.Builder getBaseBlockBuilder() {
        return PB.BaseBlock.newBuilder()
                .setHeight(height)
                .setTime(time)
                .setHash(hash)
                .setPreviousBlockHash(previousBlockHash);
    }

    @Override
    public String toString() {
        return "BaseBlock{" +
                "\n     height=" + height +
                ",\n     time=" + time +
                ",\n     hash='" + hash + '\'' +
                ",\n     previousBlockHash='" + previousBlockHash + '\'' +
                "\n}";
    }
}
