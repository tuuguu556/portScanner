import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PortScanner {
    
    private static volatile boolean stopScanning = false;
    private static volatile int portsScanned = 0;
    private static volatile int openPortsFound = 0;
    private static volatile long scanStartTime = 0;
    
    // Top 20 most common ports
    private static final int[] TOP_20_PORTS = {
        21, 22, 23, 25, 53, 80, 110, 111, 135, 139,
        143, 443, 445, 993, 995, 1723, 3306, 3389, 5900, 8080
    };
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            clearScreen();
            showMenu();
            
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    quickScan(scanner);
                    break;
                case "2":
                    customScan(scanner);
                    break;
                case "3":
                    fullScan(scanner);
                    break;
                case "4":
                    commonPortsScan(scanner);
                    break;
                case "5":
                    running = false;
                    System.out.println("\nThank you for using Port Scanner. Goodbye!");
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please try again.");
                    waitForEnter(scanner);
            }
        }
        
        scanner.close();
    }
    
    private static void showMenu() {
        System.out.println("========================================");
        System.out.println("      SIMPLE PORT SCANNER v1.0");
        System.out.println("========================================");
        System.out.println();
        System.out.println("  [1] Quick Scan (Top 20 ports)");
        System.out.println("  [2] Custom Scan (Choose your range)");
        System.out.println("  [3] Full Scan (All 65535 ports)");
        System.out.println("  [4] Common Ports (1-1024)");
        System.out.println("  [5] Exit");
        System.out.println();
        System.out.println("========================================");
    }
    
    private static void quickScan(Scanner scanner) {
        clearScreen();
        System.out.println("========================================");
        System.out.println("      QUICK SCAN MODE");
        System.out.println("========================================");
        
        System.out.print("Enter target host (e.g. localhost, 192.168.1.1): ");
        String host = scanner.nextLine().trim();
        
        System.out.println("\nScanning top 20 most common ports...");
        System.out.println("Type 'stats' for statistics\n");
        
        portsScanned = 0;
        openPortsFound = 0;
        scanStartTime = System.currentTimeMillis();
        startStopThread();
        
        long startTime = System.currentTimeMillis();
        scanSpecificPorts(host, TOP_20_PORTS, 200);
        long endTime = System.currentTimeMillis();
        
        showScanSummary(startTime, endTime);
        waitForEnter(scanner);
    }
    
    private static void customScan(Scanner scanner) {
        clearScreen();
        System.out.println("========================================");
        System.out.println("      CUSTOM SCAN MODE");
        System.out.println("========================================");
        
        System.out.print("Enter target host (e.g. localhost, 192.168.1.1): ");
        String host = scanner.nextLine().trim();
        
        System.out.print("Enter start port (1-65535): ");
        int startPort = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Enter end port (1-65535): ");
        int endPort = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Enter timeout in ms (recommended: 200): ");
        int timeout = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.println("\nStarting scan...");
        System.out.println("Type 'stop' to quit | Type 'stats' for statistics\n");
        
        stopScanning = false;
        portsScanned = 0;
        openPortsFound = 0;
        scanStartTime = System.currentTimeMillis();
        startStopThread();
        
        long startTime = System.currentTimeMillis();
        scanPorts(host, startPort, endPort, timeout);
        long endTime = System.currentTimeMillis();
        
        showScanSummary(startTime, endTime);
        waitForEnter(scanner);
    }
    
    private static void fullScan(Scanner scanner) {
        clearScreen();
        System.out.println("========================================");
        System.out.println("      FULL SCAN MODE");
        System.out.println("========================================");
        
        System.out.print("Enter target host (e.g. localhost, 192.168.1.1): ");
        String host = scanner.nextLine().trim();
        
        System.out.println("\n[!] WARNING: Full scan will take a long time!");
        System.out.print("Continue? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("\n[!] Scan cancelled.");
            waitForEnter(scanner);
            return;
        }
        
        System.out.println("\nScanning all 65535 ports...");
        System.out.println("Type 'stop' to quit | Type 'stats' for statistics\n");
        
        stopScanning = false;
        portsScanned = 0;
        openPortsFound = 0;
        scanStartTime = System.currentTimeMillis();
        startStopThread();
        
        long startTime = System.currentTimeMillis();
        scanPorts(host, 1, 65535, 200);
        long endTime = System.currentTimeMillis();
        
        showScanSummary(startTime, endTime);
        waitForEnter(scanner);
    }
    
    private static void commonPortsScan(Scanner scanner) {
        clearScreen();
        System.out.println("========================================");
        System.out.println("      COMMON PORTS MODE");
        System.out.println("========================================");
        
        System.out.print("Enter target host (e.g. localhost, 192.168.1.1): ");
        String host = scanner.nextLine().trim();
        
        System.out.println("\nScanning ports 1-1024...");
        System.out.println("Type 'stop' to quit | Type 'stats' for statistics\n");
        
        stopScanning = false;
        portsScanned = 0;
        openPortsFound = 0;
        scanStartTime = System.currentTimeMillis();
        startStopThread();
        
        long startTime = System.currentTimeMillis();
        scanPorts(host, 1, 1024, 200);
        long endTime = System.currentTimeMillis();
        
        showScanSummary(startTime, endTime);
        waitForEnter(scanner);
    }
    
    private static void startStopThread() {
        Thread stopThread = new Thread(() -> {
            Scanner stopScanner = new Scanner(System.in);
            while (!stopScanning) {
                if (stopScanner.hasNextLine()) {
                    String input = stopScanner.nextLine().trim().toLowerCase();
                    if (input.equals("stop")) {
                        stopScanning = true;
                        System.out.println("\n[!] Stopping scan...");
                        break;
                    } else if (input.equals("stats")) {
                        showLiveStats();
                    }
                }
            }
        });
        stopThread.setDaemon(true);
        stopThread.start();
    }
    
    private static void showLiveStats() {
        long currentTime = System.currentTimeMillis();
        long elapsed = (currentTime - scanStartTime) / 1000;
        double portsPerSecond = elapsed > 0 ? (double) portsScanned / elapsed : 0;
        
        System.out.println("\n========== LIVE STATISTICS ==========");
        System.out.println("  Ports Scanned:    " + portsScanned);
        System.out.println("  Open Ports Found: " + openPortsFound);
        System.out.println("  Time Elapsed:     " + elapsed + " seconds");
        System.out.println("  Scan Rate:        " + String.format("%.2f", portsPerSecond) + " ports/sec");
        System.out.println("=====================================\n");
    }
    
    private static void scanSpecificPorts(String host, int[] ports, int timeout) {
        int openCount = 0;
        
        for (int i = 0; i < ports.length; i++) {
            int port = ports[i];
            
            if (isPortOpen(host, port, timeout)) {
                String service = getCommonService(port);
                System.out.println("[+] Port " + port + " is open" + 
                                   (service != null ? " (" + service + ")" : ""));
                openCount++;
                openPortsFound++;
            }
            
            portsScanned++;
            int progress = ((i + 1) * 100) / ports.length;
            System.out.print("\rProgress: " + progress + "% [" + (i + 1) + "/" + ports.length + "]");
        }   
        System.out.println("\n");
    }
    
    private static void scanPorts(String host, int startPort, int endPort, int timeout) {
        int totalPorts = endPort - startPort + 1;
        int checked = 0;
        int openCount = 0;
        
        for (int port = startPort; port <= endPort; port++) {
            if (stopScanning) {
                break;
            }
            
            if (isPortOpen(host, port, timeout)) {
                String service = getCommonService(port);
                System.out.println("[+] Port " + port + " is OPEN" + 
                                   (service != null ? " (" + service + ")" : ""));
                openCount++;
                openPortsFound++;
            }
            
            checked++;
            portsScanned++;
            
            if (checked % 100 == 0 || checked == totalPorts) {
                int progress = (checked * 100) / totalPorts;
                System.out.print("\rProgress: " + progress + "% [" + checked + "/" + totalPorts + "]");
            }
        }
        
        System.out.println("\n");
    }
    
    private static void showScanSummary(long startTime, long endTime) {
        System.out.println("========================================");
        System.out.println(stopScanning ? "Scan stopped by user!" : "Scan complete!");
        System.out.println("Time taken: " + (endTime - startTime) / 1000.0 + " seconds");
        System.out.println("Total ports scanned: " + portsScanned);
        System.out.println("Open ports found: " + openPortsFound);
        System.out.println("========================================");
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
            case 111: return "RPCbind";
            case 135: return "MS-RPC";
            case 139: return "NetBIOS";
            case 143: return "IMAP";
            case 443: return "HTTPS";
            case 445: return "SMB";
            case 993: return "IMAPS";
            case 995: return "POP3S";
            case 1723: return "PPTP";
            case 3306: return "MySQL";
            case 3389: return "RDP";
            case 5432: return "PostgreSQL";
            case 5900: return "VNC";
            case 8080: return "HTTP-Alt";
            case 8443: return "HTTPS-Alt";
            default: return null;
        }
    }
    
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    private static void waitForEnter(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}