/*
 Copyright © 2014, Hager Controls S.A.S
 
 All rights are reserved. Reproduction or transmission in whole or in part, in
 any form or by any means, electronic, mechanical or otherwise, is prohibited
 without the prior written consent of the copyright owner.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES INCLUDING,
 BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.

 Filename: AlljoynBusListener.java
 */

package com.hager.alljoyn.sample.client;

import org.alljoyn.bus.AboutListener;
import org.alljoyn.bus.AboutObjectDescription;
import org.alljoyn.bus.AnnotationBusException;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Variant;
import org.allseen.LSF.LampDetails;
import org.allseen.LSF.LampParameters;
import org.allseen.LSF.LampService;
import org.allseen.LSF.LampState;

import java.util.Map;

/**
 * Connect to alljoyn bus and listen to network devices.
 * 
 * @author t.cazabat
 * 
 */
public class AlljoynBusListener implements AboutListener {

    private static final short CONTACT_PORT = 42;
    private static final String BUS_NAME = "com.hager.alljoyn.hdm.client";
    BusAttachment bus;
    LampStateSignalHandler lampHandler;


    public void initializeBus() {
        bus = new BusAttachment(BUS_NAME, BusAttachment.RemoteMessage.Receive);

        Status status = bus.connect();
        if (status != Status.OK) {
            return;
        }

        System.out.println("BusAttachment.connect successful on " + System.getProperty("org.alljoyn.bus.address"));

        bus.registerAboutListener(this);

        status = bus.whoImplements(new String[] { "org.allseen.LSF.LampState" });
        if (status != Status.OK) {
            return;
        }
    }

    public void closeBus() {
        if (bus != null && bus.isConnected()) {
            bus.cancelWhoImplements(null);
            bus.unregisterAboutListener(this);
            bus.unregisterSignalHandlers(lampHandler);
            bus.disconnect();
        }
    }

    @Override
    public void announced(String busName, int version, short port, AboutObjectDescription[] objectDescriptions,
            Map<String, Variant> aboutData) {
        String servicePath = null;

        System.out.println("Announced BusName:     " + busName);
        System.out.println("Announced Version:     " + version);
        System.out.println("Announced SessionPort: " + port);
        System.out.println("Announced ObjectDescription: ");

        if (objectDescriptions != null) {
            for (AboutObjectDescription o : objectDescriptions) {
                System.out.println("\t" + o.path);
                servicePath = o.path;
                for (String s : o.interfaces) {
                    System.out.println("\t\t" + s);
                }
            }
        }

        System.out.println("Contents of Announced AboutData:");
        try {
            for (Map.Entry<String, Variant> entry : aboutData.entrySet()) {
                System.out.println("\tField: " + entry.getKey() + " = ");

                if (entry.getKey().equals("AppId")) {
                    byte[] appId = entry.getValue().getObject(byte[].class);
                    for (byte b : appId) {
                        System.out.println(String.format("%02X", b));
                    }
                } else if (entry.getKey().equals("SupportedLanguages")) {
                    String[] supportedLanguages = entry.getValue().getObject(String[].class);
                    for (String s : supportedLanguages) {
                        System.out.println(s + " ");
                    }
                } else {
                    System.out.print(entry.getValue().getObject(String.class));
                }
                System.out.println("\n");
            }
        } catch (AnnotationBusException e1) {
            e1.printStackTrace();
        } catch (BusException e1) {
            e1.printStackTrace();
        }
        
        SessionOpts sessionOpts = new SessionOpts();
        sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
        sessionOpts.isMultipoint = false;
        sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
        sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

        Mutable.IntegerValue sessionId = new Mutable.IntegerValue();

        bus.enableConcurrentCallbacks();

        Status status = bus.joinSession(busName, port, sessionId, sessionOpts, new SessionListener());
        if (status != Status.OK) {
            return;
        }
        System.out.println(String.format("BusAttachement.joinSession successful sessionId = %d", sessionId.value));

        ProxyBusObject proxyObj = bus.getProxyBusObject(busName, servicePath, sessionId.value, new Class<?>[] { LampState.class,
                LampDetails.class, LampParameters.class, LampService.class });

        LampState lampState = proxyObj.getInterface(LampState.class);

        if (lampState != null) {
            try {
                System.out.println("Lamp state version : " + lampState.getVersionState());
            } catch (BusException e) {
                e.printStackTrace();
            }
        }

        LampParameters lampParameters = proxyObj.getInterface(LampParameters.class);

        if (lampParameters != null) {
            try {
                System.out.println("Lamp parameters version : " + lampParameters.getVersionParameter());
            } catch (BusException e) {
                e.printStackTrace();
            }
        }

        LampDetails lampDetails = proxyObj.getInterface(LampDetails.class);

        if (lampDetails != null) {
            try {
                System.out.println("Lamp details version : " + lampDetails.getVersionDetails());
            } catch (BusException e) {
                e.printStackTrace();
            }
        }

        LampService lampService = proxyObj.getInterface(LampService.class);

        if (lampService != null) {
            try {
                System.out.println("Lamp service version : " + lampService.getVersionService());
            } catch (BusException e) {
                e.printStackTrace();
            }
        }

        status = this.bus.addMatch("sessionless='t'");
        if (status != Status.OK) {
            return;
        }

        lampHandler = new LampStateSignalHandler();

        status = this.bus.registerSignalHandlers(lampHandler);
        if (status != Status.OK) {
            return;
        }

    }
}
