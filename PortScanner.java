import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PortScanner {
    
    private static volatile boolean stopScanning = false;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=================================");
        System.out.println("      Simple Port Scanner");
        System.out.println("=================================\n");
        
        System.out.print("Enter target host (e.g., localhost, 192.168.1.1): ");
        String host = scanner.nextLine().trim();
        
        System.out.print("Enter start port (1-65535): ");
        int startPort = scanner.nextInt();
        
        System.out.print("Enter end port (1-65535): ");
        int endPort = scanner.nextInt();
        
        System.out.print("Enter timeout in ms (recommended: 200): ");
        int timeout = scanner.nextInt();
        
        System.out.println("\n=================================");
        System.out.println("Scanning " + host + " from port " + startPort + " to " + endPort);
        System.out.println("Type 'stop' at any time to quit scanning");
        System.out.println("=================================\n");
        
        Thread stopThread = new Thread(() -> {
            Scanner stopScanner = new Scanner(System.in);
            while (!stopScanning) {
                if (stopScanner.hasNextLine()) {
                    String input = stopScanner.nextLine().trim().toLowerCase();
                    if (input.equals("stop")) {
                        stopScanning = true;
                        System.out.println("\n[!] Stopping scan...");
                        break;
                    }
                }
            }
        });
        stopThread.setDaemon(true);
        stopThread.start();
        
        long startTime = System.currentTimeMillis();
        List<Integer> openPorts = scanPorts(host, startPort, endPort, timeout);
        long endTime = System.currentTimeMillis();
        
        System.out.println("\n=================================");
        System.out.println(stopScanning ? "Scan stopped by user!" : "Scan complete!");
        System.out.println("Time taken: " + (endTime - startTime) / 1000.0 + " seconds");
        System.out.println("Found " + openPorts.size() + " open port(s)");
        System.out.println("=================================");
        
        scanner.close();
    }
    
    public static List<Integer> scanPorts(String host, int startPort, int endPort, int timeout) {
        List<Integer> openPorts = new ArrayList<>();
        int totalPorts = endPort - startPort + 1;
        int checked = 0;
        
        for (int port = startPort; port <= endPort; port++) {
            if (stopScanning) {
                break;
            }
            
            if (isPortOpen(host, port, timeout)) {
                String service = getCommonService(port);
                System.out.println("[+] Port " + port + " is OPEN" + 
                                   (service != null ? " (" + service + ")" : ""));
                openPorts.add(port);
            }
            
            checked++;
            if (checked % 100 == 0 || checked == totalPorts) {
                int progress = (checked * 100) / totalPorts;
                System.out.print("\rProgress: " + progress + "% [" + checked + "/" + totalPorts + "]");
            }
        }
        
        System.out.println();
        return openPorts;
    }
    
    public static boolean isPortOpen(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String getCommonService(int port) {
        switch (port) {
            case 21: return "FTP";
            case 22: return "SSH";
            case 23: return "Telnet";
            case 25: return "SMTP";
            case 53: return "DNS";
            case 80: return "HTTP";
            case 110: return "POP3";
            case 143: return "IMAP";
            case 443: return "HTTPS";
            case 445: return "SMB";
            case 3306: return "MySQL";
            case 3389: return "RDP";
            case 5432: return "PostgreSQL";
            case 8080: return "HTTP-Alt";
            case 8443: return "HTTPS-Alt";
            default: return null;
        }
    }
}