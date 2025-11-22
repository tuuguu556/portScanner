# Simple Port Scanner

A lightweight, command-line port scanner written in Java. Scan network ports to identify open services on local or remote hosts.

## Features

- Customizable port ranges (1-65535)
- Adjustable timeout settings
- Real-time progress tracking
- Ability to stop scans mid-execution
- Automatic service identification for common ports
- Cross-platform (Windows, Linux, macOS)

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Command-line interface (Terminal, CMD, PowerShell)

## Installation

1. Download the source code:
   - Save `PortScanner.java` to your computer

2. Compile the program:
   javac PortScanner.java

3. Run the program  
   java PortScanner

## Usage

When you run the program, you'll be prompted to enter:

1. Target host - The hostname or IP address to scan
   - Examples: `localhost`, `192.168.1.1`, `example.com`

2. Start port - The beginning of the port range (1-65535)
   - Example: `1`

3. End port - The end of the port range (1-65535)
   - Example: `1024`

4. Timeout - Connection timeout in milliseconds
   - Recommended: `200` (lower = faster but less reliable)

### Example Session

=================================
      Simple Port Scanner
=================================

Enter target host (e.g., localhost, 192.168.1.1): localhost
Enter start port (1-65535): 1
Enter end port (1-65535): 1000
Enter timeout in ms (recommended: 200): 200

=================================
Scanning localhost from port 1 to 1000
Type 'stop' at any time to quit scanning
=================================

[+] Port 22 is OPEN (SSH)
[+] Port 80 is OPEN (HTTP)
[+] Port 443 is OPEN (HTTPS)
Progress: 100% [1000/1000]

=================================
Scan complete!
Time taken: 12.5 seconds
Found 3 open port(s)
=================================
## Stopping a Scan

To stop a scan while it's running:
-type `stop` and enter

## Common Services Detected

The scanner automatically identifies these common services:

21 - FTP
22 - SSH 
23 - Telnet 
25 - SMTP 
53 - DNS 
80 - HTTP 
110 - POP3 
143 - IMAP 
443 - HTTPS 
445 - SMB 
3306 - MySQL 
3389 - RDP 
5432 - PostgreSQL 
8080 - HTTP-Alt 
8443 - HTTPS-Alt 

## Creating an Executable

### Option 1: JAR File (Recommended)

# Compile
javac PortScanner.java

# Create JAR
jar cfe PortScanner.jar PortScanner PortScanner.class

# Run
java -jar PortScanner.jar

### Option 2: Native Executable (Java 14+)

# Create JAR first (see above)

# Windows EXE
jpackage --input . --name PortScanner --main-jar PortScanner.jar --main-class PortScanner --type exe

# Linux package
jpackage --input . --name PortScanner --main-jar PortScanner.jar --main-class PortScanner --type deb

## Important Notes

**Legal Disclaimer**

- Only scan hosts you own or have explicit permission to scan
- Unauthorized port scanning may be illegal in your jurisdiction
- Some networks may detect and block port scanning attempts
- Use this tool responsibly and ethically

## Troubleshooting

Problem: "Connection timed out" for all ports
- Try increasing the timeout value
- Check if the target host is reachable (`ping` the host first)
- Ensure no firewall is blocking your connection

Problem: Scan is too slow
- Reduce the timeout value (try 100ms)
- Scan smaller port ranges
- Some hosts intentionally slow down responses to port scans

Problem: No ports showing as open on localhost
- Ensure services are actually running on your machine
- Try scanning common ports: 80, 443, 22, 3306

## Contributing

Suggestions and improvements are welcome

## Author

Created only as a educational tool for learning network protocols and Java socket programming.