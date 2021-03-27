# RPi4 Kubernetes Cluster 
Running dockerized backend services on Arm64 RPi4 hardware.

![architecture](docs/architecture.svg)

### 1. Hardware list
| Node Type           | Device                        |
|---------------------|-------------------------------|
| K8s Controller Node | Raspberry Pi 4 (4G or 8G RAM) |
| K8s Worker Nodes    | Raspberry Pi 4 (8G RAM)       |

### 2. Prepare all nodes
* Install [64bit Raspberry Pi OS Lite](https://downloads.raspberrypi.org/raspios_lite_arm64/) for Arm64.
* Update the system
  ```
  sudo apt update
  sudo apt upgrade
  sudo apt install vim docker.io 
  ``` 
* Set hostname, edit files:
  ```
  /etc/hostname
  /etc/hosts
  ```
* Expand file system to full SD capacity and reboot.
  ```
  sudo raspi-config
  ```
* Disable swap
  ```
  sudo dphys-swapfile swapoff
  sudo dphys-swapfile uninstall
  sudo systemctl disable dphys-swapfile
  ```

### 3. Install k8s nodes  
* Use official Kubernetes [kubeadm install](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/) guide.
