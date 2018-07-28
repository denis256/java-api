/*
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

/**
 * Test case for {@link WalletsIn}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 */
public final class WalletsInTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void iteratesWallets() {
        MatcherAssert.assertThat(
            new WalletsIn(Paths.get("src/test/resources/walletsIn")),
            // @checkstyle MagicNumber (1 line)
            new IsIterableWithSize<>(new IsEqual<>(5))
        );
    }

    @Test
    public void createsWalletInWallets() throws IOException {
        final Wallets wallets = new WalletsIn(this.folder.newFolder().toPath());
        wallets.create();
        MatcherAssert.assertThat(
            "Can't create wallet in wallets",
            wallets,
            new IsIterableWithSize<>(new IsEqual<>(1))
        );
    }

    @Test
    public void createsWalletInFolder() throws IOException {
        final Path path = this.folder.newFolder().toPath();
        new WalletsIn(path).create();
        MatcherAssert.assertThat(
            "Can't create wallet in folder",
            new WalletsIn(path),
            new IsIterableWithSize<>(new IsEqual<>(1))
        );
    }

    // @todo #12:30min Ensure creating a new wallet in a path where a
    //  wallet with the same id throws an exception and does not
    //  overwrite the existing one. This could be done using by
    //  using a Random object with a similar seed in WalletsIn
    //  and creating a wallet twice for example. When it is done
    //  enable the test.
    @Test
    @Ignore("see todo above")
    public void doesNotOverwriteExistingWallet() throws IOException {
        final Path path = this.folder.newFolder().toPath();
        new WalletsIn(path).create();
        this.thrown.expect(IOException.class);
        this.thrown.expectMessage("duplicate");
        new WalletsIn(path).create();
    }
}
