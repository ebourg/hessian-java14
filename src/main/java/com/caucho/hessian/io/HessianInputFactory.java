/*
 * Copyright (c) 2001-2004 Caucho Technology, Inc.  All rights reserved.
 *
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Caucho Technology (http://www.caucho.com/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Hessian", "Resin", and "Caucho" must not be used to
 *    endorse or promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    info@caucho.com.
 *
 * 5. Products derived from this software may not be called "Resin"
 *    nor may "Resin" appear in their names without prior written
 *    permission of Caucho Technology.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL CAUCHO TECHNOLOGY OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Scott Ferguson
 */

package com.caucho.hessian.io;

import java.util.logging.*;
import java.io.*;

public class HessianInputFactory
{
    public static final Logger log
            = Logger.getLogger(HessianInputFactory.class.getName());

    private HessianFactory _factory = new HessianFactory();

    public void setSerializerFactory(SerializerFactory factory)
    {
        _factory.setSerializerFactory(factory);
    }

    public SerializerFactory getSerializerFactory()
    {
        return _factory.getSerializerFactory();
    }

    public HeaderType readHeader(InputStream is)
            throws IOException
    {
        int code = is.read();

        int major = is.read();
        int minor = is.read();

        switch (code)
        {
            case -1:
                throw new IOException("Unexpected end of file for Hessian message");

            case 'c':
                if (major >= 2)
                {
                    return HeaderType.CALL_1_REPLY_2;
                }
                else
                {
                    return HeaderType.CALL_1_REPLY_1;
                }
            case 'r':
                return HeaderType.REPLY_1;

            case 'H':
                return HeaderType.HESSIAN_2;

            default:
                throw new IOException((char) code + " 0x" + Integer.toHexString(code) + " is an unknown Hessian message code.");
        }
    }

    public AbstractHessianInput open(InputStream is)
            throws IOException
    {
        int code = is.read();

        int major = is.read();
        int minor = is.read();

        switch (code)
        {
            case 'c':
            case 'C':
            case 'r':
            case 'R':
                if (major >= 2)
                {
                    return _factory.createHessian2Input(is);
                }
                else
                {
                    return _factory.createHessianInput(is);
                }

            default:
                throw new IOException((char) code + " is an unknown Hessian message code.");
        }
    }

    public static class HeaderType
    {
        public static final HeaderType CALL_1_REPLY_1 = new HeaderType(false, false);
        public static final HeaderType CALL_1_REPLY_2 = new HeaderType(false, true);
        public static final HeaderType HESSIAN_2 = new HeaderType(true, true);
        public static final HeaderType REPLY_1 = new HeaderType(false, false);
        public static final HeaderType REPLY_2 = new HeaderType(false, true);
        
        private boolean call2;
        private boolean reply2;

        private HeaderType(boolean call2, boolean reply2)
        {
            this.call2 = call2;
            this.reply2 = reply2;
        }

        public boolean isCall1()
        {
            return !isCall2();
        }

        public boolean isCall2()
        {
            return call2;
        }

        public boolean isReply1()
        {
            return !isReply2();
        }

        public boolean isReply2()
        {
            return reply2;
        }
    }
}
