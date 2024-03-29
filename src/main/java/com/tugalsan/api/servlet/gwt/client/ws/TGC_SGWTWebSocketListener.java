/**
 Copyright 2013 Stephen Samuel

 Licensed under the Apache License,Version2.0(the"License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,software
 distributed under the License is distributed on an"AS IS"BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.tugalsan.api.servlet.gwt.client.ws;

/**
 * @author Stephen K Samuel 14 Sep 2012 08:58:10
 */
public interface TGC_SGWTWebSocketListener {

    void onClose(TGC_SGWTWebSocketCloseEvent event);

    void onMessage(String msg);

    void onOpen();
}
