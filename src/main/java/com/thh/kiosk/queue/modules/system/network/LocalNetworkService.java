package com.thh.kiosk.queue.modules.system.network;

import com.thh.kiosk.queue.infrastructure.hardware.NetworkScanner;

import org.springframework.stereotype.Service;

import java.net.InetAddress;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalNetworkService {

    private static final String LOCAL_IP = "127.0.0.1";

    private final NetworkScanner networkScanner;

    public String getIp() {
        InetAddress address = networkScanner.scanActiveLanAddress();

        if (address != null) {
            return address.getHostAddress();
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return LOCAL_IP;
        }
    }
}