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

 Filename: LampStateSignalHandler.java
 */

package com.hager.alljoyn.sample.client;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusSignalHandler;

public class LampStateSignalHandler {
    @BusSignalHandler(iface = "org.allseen.LSF.LampState", signal = "LampStateChanged")
    public void lampStateChanged(String LampID) throws BusException {
        System.out.println("signal received: " + LampID);
    }
}
