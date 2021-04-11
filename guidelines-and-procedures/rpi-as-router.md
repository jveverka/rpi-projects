# RPi as router
This how-to describes how to setup raspberry pi as router and NAT, DHCP and firewall.
Wifi adapter is used as WAN interface, Ethernet adapter is used as LAN interface for local network.
[Rasbian lite](https://www.raspberrypi.org/downloads/raspbian/) was used for this setup, version 2019-04-08 (Stretch). 

## wlan0 configuration
This interface is configured in automatic dhcp mode.

## eth0 configuration
This interface has static configuration. Internal network is 192.168.40.0/24 
This interface is default gateway for this network.

## Routing, networking and DHCP setup

### 1. Enable ipv4 packet forwarding
in ``/etc/sysctl.conf`` setup variable __net.ipv4.ip_forward__ to 1
```
net.ipv4.ip_forward = 1
```

### 2. Setup network interfaces
edit ``/etc/network/interfaces``, so it looks like this:
```
# interfaces(5) file used by ifup(8) and ifdown(8)
# Please note that this file is written to be used with dhcpcd
# For static IP, consult /etc/dhcpcd.conf and 'man dhcpcd.conf'
# Include files from /etc/network/interfaces.d:
source-directory /etc/network/interfaces.d

auto eth0
iface eth0 inet static
    address 192.168.40.1
    netmask 255.255.255.0

auto wlan0
allow-hotplug wlan0
iface wlan0 inet dhcp
wpa-conf /etc/wpa_supplicant/wpa_supplicant.conf
iface default inet dhcp
```

### 3. Setup WIFI connection credentials
Add wifi network setup into ``/etc/wpa_supplicant/wpa_supplicant.conf``
```
network={
   ssid="network-name"
   psk="supersercretpassword"
}

```

### 4. Setup DHCP server on eth0
Install dhcp server ``sudo apt-get install isc-dhcp-server ``
Then create and edit a new file ``/etc/dhcp/dhcpd.conf`` containing:
```
option domain-name "example.org";
option domain-name-servers 8.8.8.8, 8.8.4.4;
default-lease-time 600;
max-lease-time 7200;
ddns-update-style none;

subnet 192.168.40.0 netmask 255.255.255.0 {
       range 192.168.40.10 192.168.40.250;
       option routers 192.168.40.1;
}
```
Also edit the file ``/etc/default/isc-dhcp-server`` to add the line:
```
INTERFACESv4="eth0"
INTERFACESv6=""
```
To control the DHCP server, use:
```
sudo systemctl restart isc-dhcp-server
sudo systemctl status isc-dhcp-server
```


### 5. Setup NAT and firewall

