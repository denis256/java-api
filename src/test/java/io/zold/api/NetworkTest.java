/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.zold.api;

import java.util.ArrayList;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Repeated;
import org.cactoos.text.RandomText;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Network}.
 *
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
 * @since 0.1
 * @todo #5:30min Implement Remote interface. Remote Interface must be
 *  implemented because Network depends on Remote behavior. Network.pull
 *  needs to search all remotes for some wallet id and merge all found
 *  wallets; Network.push must add a wallet to a remote based in remote.
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 */
public final class NetworkTest {

    /**
     * Network can push wallet to right remote.
     * Tests if the {@link Network} pulls the {@link Wallet} to the remote
     * selected according rules defined in {@link Network}: {@link Remote}
     * with higher score and ({@link Remote#score()} > 16. Throws a
     * {@link RuntimeException} if {@link Network} tries to push the wallet
     * to the wrong node.
     */
    @Test
    public void pullWalletToRightRemote()  {
        final Remote highremote = Mockito.mock(Remote.class);
        final Score highscore = Mockito.mock(Score.class);
        Mockito.when(highscore.suffixes()).thenReturn(
            new Repeated<>(20, new RandomText())
        );
        Mockito.when(highremote.score()).thenReturn(highscore);
        final Remote mediumremote = Mockito.mock(Remote.class);
        final Score mediumscore = Mockito.mock(Score.class);
        Mockito.when(mediumscore.suffixes()).thenReturn(
            new Repeated<>(10, new RandomText())
        );
        Mockito.when(mediumremote.score()).thenReturn(mediumscore);
        Mockito.doThrow(new RuntimeException()).when(mediumremote).add(
            Mockito.any(Wallet.class)
        );
        final Remote lowremote = Mockito.mock(Remote.class);
        final Score lowscore = Mockito.mock(Score.class);
        Mockito.when(lowscore.suffixes()).thenReturn(
            new Repeated<>(5, new RandomText())
        );
        Mockito.when(lowremote.score()).thenReturn(lowscore);
        Mockito.doThrow(new RuntimeException()).when(lowremote).add(
            Mockito.any(Wallet.class)
        );
        final Wallet wallet = Mockito.mock(Wallet.class);
        new Network.Simple(
            new IterableOf<Remote>(
                highremote, mediumremote, lowremote
            )
        ).push(wallet);
    }

    /**
     * Network cannot push wallet when there is no remote available.
     * Tests if the {@link Network} don't push the {@link Wallet} to the remote
     * when it has an ({@link Remote#score()} < 16. Throws a
     * {@link RuntimeException} if {@link Network} tries to push the wallet
     * to the remote.
     */
    @Test
    public void pushWalletWhenAnyRemoteIsAvailable() {
        final Remote remote = Mockito.mock(Remote.class);
        final Score score = Mockito.mock(Score.class);
        Mockito.when(score.suffixes()).thenReturn(
            new Repeated<>(15, new RandomText())
        );
        Mockito.when(remote.score()).thenReturn(score);
        Mockito.doThrow(new RuntimeException()).when(remote).add(
            Mockito.any(Wallet.class)
        );
        final Wallet wallet = Mockito.mock(Wallet.class);
        new Network.Simple(
            new IterableOf<Remote>(remote)
        ).push(wallet);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void pullNotYetSupported() {
        new Network.Simple(new ArrayList<>(1)).pull(1L);
    }

}
