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

package bisq.core.dao.node.lite;

import bisq.core.dao.blockchain.vo.BsqBlock;
import bisq.core.dao.node.NodeExecutor;

import bisq.common.UserThread;
import bisq.common.handlers.ResultHandler;

import javax.inject.Inject;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import java.util.List;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

/**
 * Processes tasks in custom thread. Results are mapped back to user thread so client don't need to deal with threading.
 * We use a SingleThreadExecutor to guarantee that the parser is only running from one thread at a time to avoid
 * risks with concurrent write to the BsqBlockChain.
 */
@Slf4j
public class LiteNodeExecutor {

    private final LiteNodeParser liteNodeParser;
    private final ListeningExecutorService executor;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("WeakerAccess")
    @Inject
    public LiteNodeExecutor(LiteNodeParser liteNodeParser, NodeExecutor nodeExecutor) {
        this.liteNodeParser = liteNodeParser;
        executor = nodeExecutor.get();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Package private
    ///////////////////////////////////////////////////////////////////////////////////////////

    void parseBlocks(List<BsqBlock> bsqBlockList,
                     Consumer<BsqBlock> newBlockHandler,
                     ResultHandler resultHandler,
                     Consumer<Throwable> errorHandler) {
        ListenableFuture<Void> future = executor.submit(() -> {
            long startTs = System.currentTimeMillis();
            liteNodeParser.parseBsqBlocks(bsqBlockList,
                    newBsqBlock -> UserThread.execute(() -> newBlockHandler.accept(newBsqBlock)));
            log.info("parseBlocks took {} ms for {} blocks", System.currentTimeMillis() - startTs, bsqBlockList.size());
            return null;
        });

        Futures.addCallback(future, new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void ignore) {
                UserThread.execute(() -> UserThread.execute(resultHandler::handleResult));
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                UserThread.execute(() -> errorHandler.accept(throwable));
            }
        });
    }

    void parseBlock(BsqBlock bsqBlock,
                    Consumer<BsqBlock> newBlockHandler,
                    Consumer<Throwable> errorHandler) {
        ListenableFuture<BsqBlock> future = executor.submit(() -> {
            long startTs = System.currentTimeMillis();
            liteNodeParser.parseBsqBlock(bsqBlock);
            log.info("parseBlocks took {} ms", System.currentTimeMillis() - startTs);
            return bsqBlock;
        });

        Futures.addCallback(future, new FutureCallback<BsqBlock>() {
            @Override
            public void onSuccess(BsqBlock bsqBlock) {
                UserThread.execute(() -> UserThread.execute(() -> newBlockHandler.accept(bsqBlock)));
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                UserThread.execute(() -> errorHandler.accept(throwable));
            }
        });
    }
}
