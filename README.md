# LMNResolver

## Description
LMNResolver is a Java-based utility designed to discover and resolve hostnames within local networks. Utilizing the host discovery protocols like LLMNR (Link-Local Multicast Name Resolution), mDNS (Multicast DNS) and NBNS (NetBIOS Name Service), this tool provides an efficient approach to identify devices and their hostnames in the local networks.

## Features
- **mDNS Resolving**: Enables hostname resolution for devices using the Multicast DNS protocol, which works upon UDP port number 5353, and uses the 224.0.0.251 multicast ip address.  Commonly used in many home and corporate networks, mDNS is particularly prevalent in devices like printers, smart TVs, and IoT devices.

- **LLMNR Resolving**: Link-local Multicast Name Resolution (LLMNR) is also used for hostname resolution, which works upon UDP port number 5355, and uses 224.0.0.252 multicast ip address. It is included in Windows devices and is also implemented by systemd-resolved on Linux.

- **NBNS Resolving**: Efficiently uncovers hostnames for devices leveraging the NetBIOS Name Service protocol. This is typically found in older Windows systems and some SMB devices. It operates upon UDP port number 137, and it is widely used upon windows networks.

- **Flexible Input Options**: Supports two primary forms of network range inputs:
    - Network Part (e.g., 192.168.1.) followed by the IP range (e.g., 1-254)
    - CIDR Notations (e.g., 192.168.1.0/24)
    
## Installation & Usage

#### 1. Clone the Repository:

```
$ git clone https://github.com/88oo/LMNResolver.git
$ cd LMNResolver/
```

#### 2. Compile the Source Code:

```
$ javac Main.java
```

#### 3. Execute the Tool:

```
$ java Main
```

#### 4. Specify Network Range:

Post-launch, you'll be guided to input network range details:

- Begin with the network part, such as 192.168.1.. Subsequently, determine the IP range within this network, like 1-254.
- For a different approach, input using the CIDR notation: 192.168.1.0/24.

After providing the network range, the tool will scour the defined range and resolve hostnames through the selected protocols.

## Contributing
Contributions are more than welcome! Should you have bug fixes or improvements in mind:

1. Fork the repository.
2. Generate a new feature branch.
3. Implement your changes.
4. Forward a pull request.

For any substantial modifications, initiate an issue first so the proposed adjustments can be discussed.
