package com.thh.kiosk.queue.infrastructure.hardware;

import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.system.log.ServiceLogTag;

import org.springframework.stereotype.Component;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ServiceLogTag(LogTag.NETWORK)
public class NetworkScanner {

    public InetAddress scanActiveLanAddress() {
        String targetIpString = null;

        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            InetAddress activeAddress = socket.getLocalAddress();

            if (activeAddress != null && !activeAddress.isAnyLocalAddress() && !activeAddress.isLoopbackAddress()) {
                targetIpString = activeAddress.getHostAddress();
                log.info("{} OS Routing Table suggests primary IP: {}", LogTag.NETWORK, targetIpString);
            }
        } catch (Exception e) {
            log.warn("{} OS Routing trick failed: {}", LogTag.NETWORK, e.getMessage());
        }

        InetAddress bestAddress = null;
        int highestScore = -1;

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();

                if (ni.isLoopback() || !ni.isUp() || ni.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        if (addr.getHostAddress().equals(targetIpString)) {
                            return addr;
                        }

                        int score = getScore(ni);

                        if (score > highestScore) {
                            highestScore = score;
                            bestAddress = addr;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error when read Network Interfaces: {}", e.getMessage());
        }

        if (bestAddress != null) {
            log.info("Final Selected IP for System: {}", bestAddress.getHostAddress());
        } else {
            log.warn("No valid LAN IP found. Fallback triggered.");
        }
        return bestAddress;
    }

    private static int getScore(NetworkInterface ni) {
        int score = 0;
        String displayName = ni.getDisplayName().toLowerCase();

        if (displayName.contains("wlan")) {
            score += 100;
        } else if (displayName.contains("ethernet") || displayName.contains("pcie") ||
                displayName.contains("gigabit") || displayName.contains("realtek") ||
                displayName.contains("intel")) {
            score += 50;
        } else {
            score += 10;
        }
        return score;
    }
}
