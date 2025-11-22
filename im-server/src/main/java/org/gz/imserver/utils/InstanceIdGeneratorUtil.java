package org.gz.imserver.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Random;
import java.util.UUID;

/**
 *  实例ID生成工具类：提供兜底的唯一实例ID生成策略
 */
public class InstanceIdGeneratorUtil {
    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();
    /**
     * 进程ID（JVM启动时获取一次，避免重复计算）
     */
    private static final String PID = getProcessId();
    /**
     * 机器MAC地址（全局唯一）
     */
    private static final String MAC_ADDRESS = getMacAddress();

    /**
     * 获取进程ID（PID）
     * @return 进程ID，获取失败则返回随机数
     */
    private static String getProcessId() {
        try {
            // JVM进程ID：通过运行时MXBean获取
            String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            return pid;
        } catch (Exception e) {
            // 获取失败则返回4位随机数
            return String.format("%04d", RANDOM.nextInt(9999));
        }
    }

    /**
     * 获取机器MAC地址（物理唯一）
     * @return MAC地址，获取失败则返回UUID的前8位
     */
    private static String getMacAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();
            if (hardwareAddress == null) {
                return UUID.randomUUID().toString().substring(0, 8);
            }
            // 拼接MAC地址（如00-15-5D-00-00-01）
            StringBuilder mac = new StringBuilder();
            for (byte b : hardwareAddress) {
                mac.append(String.format("%02x-", b));
            }
            return mac.deleteCharAt(mac.length() - 1).toString();
        } catch (Exception e) {
            // 获取失败则返回UUID前8位
            return UUID.randomUUID().toString().substring(0, 8);
        }
    }

    /**
     * 方案1：IP+端口+PID（推荐）
     * @param ip 实例IP
     * @param port 实例端口
     * @return 唯一实例ID
     */
    public static String generateByIpPortPid(String ip, int port) {
        return String.format("%s:%d:%s", ip, port, PID);
    }

    /**
     * 方案2：IP+端口+时间戳+随机数
     * @param ip 实例IP
     * @param port 实例端口
     * @return 唯一实例ID
     */
    public static String generateByIpPortTimestamp(String ip, int port) {
        long timestamp = System.currentTimeMillis();
        int random = RANDOM.nextInt(9999);
        return String.format("%s:%d:%d:%04d", ip, port, timestamp, random);
    }

    /**
     * 方案3：MAC地址+端口+随机数（极致唯一）
     * @param port 实例端口
     * @return 唯一实例ID
     */
    public static String generateByMacPortRandom(int port) {
        int random = RANDOM.nextInt(9999);
        return String.format("%s:%d:%04d", MAC_ADDRESS, port, random);
    }
}
