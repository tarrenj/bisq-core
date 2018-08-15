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

import bisq.common.proto.persistable.PersistablePayload;

import io.bisq.generated.protobuffer.PB;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.annotation.Nullable;

/**
 * Contains immutable BSQ specific data (TxOutputType) and is used to get
 * stored in the list of immutable Transactions (Tx) in a block.
 * TempTxOutput get converted to immutable TxOutput after tx parsing is completed.
 * Gets persisted.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TxOutput extends BaseTxOutput implements PersistablePayload {
    public static TxOutput fromTempOutput(TempTxOutput tempTxOutput) {
        return new TxOutput(tempTxOutput.getIndex(),
                tempTxOutput.getValue(),
                tempTxOutput.getTxId(),
                tempTxOutput.getPubKeyScript(),
                tempTxOutput.getAddress(),
                tempTxOutput.getOpReturnData(),
                tempTxOutput.getBlockHeight(),
                tempTxOutput.getTxOutputType());
    }

    private final TxOutputType txOutputType;

    public TxOutput(int index,
                    long value,
                    String txId,
                    @Nullable PubKeyScript pubKeyScript,
                    @Nullable String address,
                    @Nullable byte[] opReturnData,
                    int blockHeight,
                    TxOutputType txOutputType) {
        super(index,
                value,
                txId,
                pubKeyScript,
                address,
                opReturnData,
                blockHeight);

        this.txOutputType = txOutputType;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public PB.BaseTxOutput toProtoMessage() {
        PB.TxOutput.Builder builder = PB.TxOutput.newBuilder()
                .setTxOutputType(txOutputType.toProtoMessage());
        return getRawTxOutputBuilder().setTxOutput(builder).build();
    }

    public static TxOutput fromProto(PB.BaseTxOutput proto) {
        return new TxOutput(proto.getIndex(),
                proto.getValue(),
                proto.getTxId(),
                proto.hasPubKeyScript() ? PubKeyScript.fromProto(proto.getPubKeyScript()) : null,
                proto.getAddress().isEmpty() ? null : proto.getAddress(),
                proto.getOpReturnData().isEmpty() ? null : proto.getOpReturnData().toByteArray(),
                proto.getBlockHeight(),
                TxOutputType.fromProto(proto.getTxOutput().getTxOutputType()));
    }


    @Override
    public String toString() {
        return "TxOutput{" +
                "\n     txOutputType=" + txOutputType +
                "\n} " + super.toString();
    }
}
