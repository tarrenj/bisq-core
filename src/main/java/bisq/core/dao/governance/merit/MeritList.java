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

package bisq.core.dao.governance.merit;

import bisq.core.dao.governance.ConsensusCritical;

import bisq.common.proto.persistable.PersistableList;

import io.bisq.generated.protobuffer.PB;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;

// We don't persist that list but use it only for encoding the MeritList list
// to PB bytes in the blindVote.

// Not used as PersistableList
// TODO create diff. super class
@EqualsAndHashCode(callSuper = true)
public class MeritList extends PersistableList<Merit> implements ConsensusCritical {

    public MeritList(List<Merit> list) {
        super(list);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public PB.MeritList toProtoMessage() {
        return getBuilder().build();
    }

    private PB.MeritList.Builder getBuilder() {
        return PB.MeritList.newBuilder()
                .addAllMerit(getList().stream()
                        .map(Merit::toProtoMessage)
                        .collect(Collectors.toList()));
    }

    public static MeritList fromProto(PB.MeritList proto) {
        return new MeritList(new ArrayList<>(proto.getMeritList().stream()
                .map(Merit::fromProto)
                .collect(Collectors.toList())));
    }

    public static MeritList getMeritListFromBytes(byte[] bytes) throws InvalidProtocolBufferException {
        return MeritList.fromProto(PB.MeritList.parseFrom(bytes));
    }
}
