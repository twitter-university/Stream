/* $Id: $
   Copyright 2012, G. Blake Meike

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.marakana.android.parser;

import java.io.IOException;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.marakana.android.parser.AtomFeedParser.PostHandler;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
abstract class Element {
    protected final Map<String, Element> parseMap;

    public Element(Map<String, Element> parseMap) { this.parseMap = parseMap; }

    @SuppressWarnings("unused")
    public void start(AtomFeedParser root, XmlPullParser parser, PostHandler hdlr)
        throws XmlPullParserException, IOException
    {
        parser.next();
    }

    @SuppressWarnings("unused")
    public void text(AtomFeedParser root, String text, PostHandler hdlr)
        throws XmlPullParserException, IOException
    {
        // do nothing
    }

    @SuppressWarnings("unused")
    public void end(AtomFeedParser root, PostHandler hdlr)
        throws XmlPullParserException, IOException
    {
        // do nothing
    }
}