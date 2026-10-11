# Smart Library Management System (SLMS) - Deployment Guide
**Semester Project Deployment Reference (Pure HTML5 + CSS3 + Java SE 17 + JDBC + SQL/PL-SQL)**

This guide provides step-by-step instructions for deploying the Smart Library Management System across four common deployment targets:
1. **Free Cloud Hosting (Render / Railway / Fly.io)** - Instant HTTPS URL for college viva/demo.
2. **Campus Wi-Fi / Local Area Network (LAN)** - Accessible from other student laptops & phones.
3. **Standalone Executable JAR (`smart-library.jar`)** - Single-command portable deployment.
4. **Dedicated Linux Cloud Server (AWS EC2 / DigitalOcean / Oracle Free Tier)** - 24/7 Production VPS with Systemd.

---

## Architecture & Deployment Prerequisites

- **Runtimes:** Java SE 17 or higher (OpenJDK, Temurin, or Oracle JDK).
- **Database:** Zero-configuration embedded pure-Java H2 engine (default, ACID-compliant file database `library_db.mv.db`), or optional external MySQL 8+.
- **Web Server:** Pure Java built-in `com.sun.net.httpserver.HttpServer` with dynamic `$PORT` environment variable support.
- **Dependencies:** Pre-bundled in `lib/` (`h2.jar`, `mysql-connector-j.jar`).

---

## Option 1: 1-Click Free Cloud Deployment (Render.com)

[Render.com](https://render.com) offers free web hosting with automatic Docker builds and custom `https://...` URLs.

### Step-by-Step Instructions:
1. **Push your project to GitHub / GitLab:**
   ```bash
   git init
   git add .
   git commit -m "Initial commit for Smart Library Management System"
   git remote add origin https://github.com/<your-username>/smart-library.git
   git branch -M main
   git push -u origin main
   ```
2. **Log into [Render Dashboard](https://dashboard.render.com/)**:
   - Click **New +** &rarr; **Web Service**.
   - Select your GitHub repository (`smart-library`).
3. **Configure Service Details:**
   - **Name:** `smart-library` (or custom name)
   - **Region:** Any (e.g. Frankfurt, Singapore, Ohio)
   - **Runtime:** **Docker** (Render will automatically detect the provided `Dockerfile`)
   - **Instance Type:** **Free**
4. **Deploy:**
   - Click **Create Web Service**.
   - Render automatically compiles the Java sources using the multi-stage `Dockerfile`, exposes port `$PORT`, initializes the schema, and assigns a live public URL:
     ```
     https://smart-library-xxxx.onrender.com
     ```
5. **Access:**
   - Open the URL on any laptop or phone.

---

## Option 2: College Wi-Fi / Local Network (LAN) Demo

To let your project guide, professors, or team members access the application directly from their own laptops or mobile devices connected to the same college Wi-Fi or lab network:

### Step 1: Find your Laptop's Local IP Address
- **On Windows (PowerShell or CMD):**
  ```powershell
  ipconfig
  ```
  Look for **IPv4 Address** under `Wireless LAN adapter Wi-Fi` or `Ethernet` (for example: `192.168.1.45` or `10.0.x.x`).

### Step 2: Allow Port 8080 Through Windows Firewall
Run PowerShell as Administrator and execute:
```powershell
New-NetFirewallRule -DisplayName "Smart Library System (8080)" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
```
*(Or search "Windows Defender Firewall with Advanced Security" &rarr; Inbound Rules &rarr; New Rule &rarr; Port &rarr; 8080 &rarr; Allow).*

### Step 3: Start the Application
Double-click `run.bat` or run:
```powershell
.\run.bat
```

### Step 4: Access From Any Device on the Same Wi-Fi
On your teammate's or professor's phone or laptop, open any browser and enter:
```
http://192.168.1.45:8080
```
*(Replace `192.168.1.45` with your actual IPv4 address).*

---

## Option 3: Standalone Executable JAR Deployment

You can package the entire application into a single executable JAR file:

### Step 1: Build the JAR Package
- **On Windows:** Double-click `package.bat` (or run in terminal):
  ```cmd
  package.bat
  ```
- **On Linux / macOS:**
  ```bash
  chmod +x package.sh
  ./package.sh
  ```
This generates `smart-library.jar` in your root folder.

### Step 2: Run the Executable JAR
```bash
java -jar smart-library.jar
```
Or specify a custom port:
```bash
java -jar smart-library.jar 9000
```

### Distributing to Another Computer
To copy the project to a college lab computer or pen drive, you only need:
1. `smart-library.jar`
2. `lib/` directory
3. `web/` directory
4. `sql/` directory

---

## Option 4: Linux Cloud VPS (Ubuntu / Debian / AWS EC2 / DigitalOcean)

For a persistent 24/7 production server with automatic restart:

### Step 1: Install Java 17 on Linux Server
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk git
```

### Step 2: Clone & Build Application
```bash
cd /opt
sudo git clone https://github.com/<your-username>/smart-library.git
cd smart-library
sudo chmod +x build.sh run.sh package.sh
sudo ./build.sh
```

### Step 3: Create Systemd Daemon Service
Create a service file:
```bash
sudo nano /etc/systemd/system/smart-library.service
```
Paste the following configuration:
```ini
[Unit]
Description=Smart Library Management System
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/smart-library
ExecStart=/usr/bin/java -cp "bin:lib/h2.jar:lib/mysql-connector-j.jar" Main 8080
Restart=always
RestartSec=10
Environment=PORT=8080

[Install]
WantedBy=multi-user.target
```

### Step 4: Enable and Start the Service
```bash
sudo systemctl daemon-reload
sudo systemctl enable smart-library
sudo systemctl start smart-library
sudo systemctl status smart-library
```

The system will now run 24/7 and automatically reboot if the server restarts.

---

## Evaluation Login Credentials

Once deployed, use any of the pre-seeded evaluation accounts:

| Portal | Demo Email | Demo Password | Responsibilities |
|---|---|---|---|
| **Administrator** | `admin@library.edu` | `admin123` | System management, audit logs, analytical SQL reports |
| **Librarian** | `librarian@library.edu` | `librarian123` | Book issue/return desk, fine collection, copy management |
| **Student** | `aarav.patel@student.edu` | `student123` | Self-borrow catalog, active loans, waitlist reservations |
