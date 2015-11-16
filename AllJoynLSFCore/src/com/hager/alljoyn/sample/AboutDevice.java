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

 Filename: AboutDevice.java
 */

package com.hager.alljoyn.sample;

import org.alljoyn.bus.AboutDataListener;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.Version;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Object used to announce a device on the Alljoyn network.
 * 
 * @author t.cazabat
 * 
 */
public class AboutDevice implements AboutDataListener {
    private byte[] appId;
    
    private static final String DEVICE_ID = "LAMP-ID";
    private static final String DEVICE_MODEL = "LAMP-MODEL";
    private static final String DEVICE_NAME = "LAMP-NAME";
    private static final String DEVICE_URL = "http://sampleurl.fr";
    private static final String DEVICE_MANUFACTURER = "LAMP-MANUF";
    private static final String DEVICE_DESCRIPTION = "A sample light used for tests.";
    private static final String APP_NAME = "AllJoyn Lamp Sample";

    public AboutDevice() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        this.appId = bb.array();
    }

    @Override
    public Map<String, Variant> getAboutData(String language) throws ErrorReplyBusException {

        Map<String, Variant> aboutData = new HashMap<String, Variant>();

        aboutData.put("AppId", new Variant(appId));
        aboutData.put("DefaultLanguage", new Variant(new String("en")));
        aboutData.put("DeviceId", new Variant(DEVICE_ID));
        aboutData.put("ModelNumber", new Variant(new String(DEVICE_MODEL)));
        aboutData.put("SupportedLanguages", new Variant(new String[] { "en" }));
        aboutData.put("DateOfManufacture", new Variant(new String("2015-10-15")));
        aboutData.put("SoftwareVersion", new Variant(new String("1.0")));
        aboutData.put("AJSoftwareVersion", new Variant(Version.get()));
        aboutData.put("HardwareVersion", new Variant(new String("0.1alpha")));
        aboutData.put("SupportUrl", new Variant(DEVICE_URL));

        aboutData.put("DeviceName", new Variant(DEVICE_NAME));
        aboutData.put("AppName", new Variant(APP_NAME));
        aboutData.put("Manufacturer", new Variant(DEVICE_MANUFACTURER));
        aboutData.put("Description", new Variant(DEVICE_DESCRIPTION));
 

        return aboutData;
    }

    @Override
    public Map<String, Variant> getAnnouncedAboutData() throws ErrorReplyBusException {
        Map<String, Variant> aboutData = new HashMap<String, Variant>();

        aboutData.put("AppId", new Variant(appId));
        aboutData.put("DefaultLanguage", new Variant(new String("en")));
        aboutData.put("DeviceName", new Variant(DEVICE_NAME));
        aboutData.put("DeviceId", new Variant(DEVICE_ID));
        aboutData.put("AppName", new Variant(APP_NAME));
        aboutData.put("Manufacturer", new Variant(DEVICE_MANUFACTURER));
        aboutData.put("ModelNumber", new Variant(DEVICE_MODEL));

        return aboutData;
    }
}
