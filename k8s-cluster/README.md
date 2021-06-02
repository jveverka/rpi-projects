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
* Edit ``/boot/cmdline.txt`` Add this text at the end of the line, but don't create any new lines:
  ```
  cgroup_enable=cpuset cgroup_memory=1 cgroup_enable=memory
  ```
* Make sure that IPv4 and IPv6 packet forwarding is allowed in kernel. Edit ``/etc/sysctl.conf``
  ```
  net.ipv4.ip_forward=1
  net.ipv6.conf.all.forwarding=1
  net.bridge.bridge-nf-call-ip6tables = 1
  net.bridge.bridge-nf-call-iptables = 1
  ```
  Apply configuration:
  ```
  sudo sysctl --system 
  ```
* Use official Kubernetes [kubeadm install](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/) guide.
  Install [kubeadm, kubelet and kubectl](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/#installing-kubeadm-kubelet-and-kubectl).

### 3. Setup k8s controller node  
* Init controller node:
  ```
  sudo kubeadm init --pod-network-cidr=10.244.0.0/16 --apiserver-advertise-address=<IP-ADDRESS-OF-CONTROLLER> 
  ```
* Configure kubectl for local user.
  ```
  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config
  ```
* Deploy a pod network to the cluster using Flannel. Note that ``cidr=10.244.0.0/16`` is hardcoded in ``kube-flannel.yml`` !
  ```
  kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml;  
  ```
* Print join command to be used on worker nodes.  
  ```
  sudo kubeadm token create --print-join-command
  ```
  
### 4. Setup k8s worker node(s)  
* Join any number of worker nodes by running the following on each as root on each node:
  ```
  sudo kubeadm join <IP-ADDRESS-OF-CONTROLLER>:6443 --token <TOKEN> \
    --discovery-token-ca-cert-hash sha256:<CERT-HASH> 
  ```

### 5. Setup Nginx Ingress Controller (Optional)
* Install nginx-ingress controller
  ```
  kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v0.45.0/deploy/static/provider/baremetal/deploy.yaml
  ```

### 6. Check k8s cluster setup 
* On k8s controller node:
  ```
  kubectl config view
  kubectl get nodes
  kubectl get all -A
  kubectl describe nodes
  kubectl get pods --all-namespaces
  kubectl logs -f <pod-name> -n <name-space>
  kubectl get namespace
  kubectl get --raw='/readyz?verbose'
  kubectl describe ingress
  ```
  
### 7. k8s dashboard (Optional)
* Command below installs [web ui](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/) for k8s cluster.
  ```
  kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.2.0/aio/deploy/recommended.yaml
  ```
* Create sample user & get access token as described [here](https://github.com/kubernetes/dashboard/blob/master/docs/user/access-control/creating-sample-user.md)
  or use [kubernetes-dashboard-access.yml](kubernetes-dashboard-access.yml)
  ```
  kubectl apply -f kubernetes-dashboard-access.yml
  kubectl -n kubernetes-dashboard get secret $(kubectl -n kubernetes-dashboard get sa/admin-user -o jsonpath="{.secrets[0].name}") -o go-template="{{.data.token | base64decode}}"
  ```
* Create ssh tunnel to k8s master node and activate the proxy
  ```
  ssh -L localhost:8001:127.0.0.1:8001 <user>@<master_public_IP>  
  kubectl proxy
  ```
* Access the UI
  ```
  http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/#/login
  ```
* Undeploy kubernetes dashboard
  ```
  kubectl delete -f kubernetes-dashboard-access.yml
  kubectl delete -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.2.0/aio/deploy/recommended.yaml  
  ```