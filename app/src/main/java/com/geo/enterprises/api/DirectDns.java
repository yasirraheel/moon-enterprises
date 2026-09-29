package com.geo.enterprises.api;

import androidx.annotation.NonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Dns;

/**
 * Custom DNS resolver that maps moonenterprises.net directly to the origin server IP
 * to bypass any CDN edge challenges (such as Hostinger CDN JS challenges/bot filters)
 * while falling back to system DNS.
 */
public class DirectDns implements Dns {

    private static final String TARGET_HOST = "moonenterprises.net";
    private static final byte[] ORIGIN_IP_BYTES = new byte[]{(byte) 109, (byte) 106, (byte) 254, (byte) 187};

    @NonNull
    @Override
    public List<InetAddress> lookup(@NonNull String hostname) throws UnknownHostException {
        if (TARGET_HOST.equalsIgnoreCase(hostname) || ("www." + TARGET_HOST).equalsIgnoreCase(hostname)) {
            List<InetAddress> result = new ArrayList<>();
            try {
                // Priority 1: Direct origin server IP (109.106.254.187)
                result.add(InetAddress.getByAddress(hostname, ORIGIN_IP_BYTES));
            } catch (Exception e) {
                android.util.Log.e("DirectDns", "Failed to create direct origin IP address: " + e.getMessage());
            }

            // Fallback: System DNS
            try {
                List<InetAddress> systemAddresses = Dns.SYSTEM.lookup(hostname);
                for (InetAddress addr : systemAddresses) {
                    if (!result.contains(addr)) {
                        result.add(addr);
                    }
                }
            } catch (Exception ignored) {
            }

            if (!result.isEmpty()) {
                return result;
            }
        }
        return Dns.SYSTEM.lookup(hostname);
    }
}
