/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * 
 * This file is part of org.appwork.remoteapi.test
 * 
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package org.appwork.remoteapi.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.appwork.remoteapi.RemoteAPIInterface;
import org.appwork.remoteapi.RemoteAPIRequest;
import org.appwork.remoteapi.RemoteAPIResponse;

/**
 * @author thomas
 * 
 */
public interface TestApiInterface extends RemoteAPIInterface {
    public void iAmGod(final RemoteAPIRequest request, final RemoteAPIResponse response) throws UnsupportedEncodingException, IOException;

    public String merge(String s1, String s2, int i, boolean b);

    public int sum(long a, Byte b);

    public boolean toggle(boolean b);
}
