package com.tekkimariani.cleanup.net;

import com.jcraft.jsch.*;

/* 
        String host = "192.168.203.8"; // Ziel-PC
        String user = "deinBenutzer"; // Windows-Username
        String password = "deinPasswort"; // Windows-Passwort
        */

public class SSHClient {
    public static void main(String[] args) {
        String host = "172.25.164.93"; // Ziel-PC
        String user = "User"; // Windows-Username
        String password = "password"; // Windows-Passwort
        String command = "powershell Remove-Item -Path \"$env:USERPROFILE\\Desktop\\*\" -Recurse -Force -Exclude 'desktop.ini', 'Thumbs.db'";

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            
            channel.connect();

            // Warte auf die Beendigung des Befehls
            while (!channel.isClosed()) {
                Thread.sleep(100);
            }
            
            channel.disconnect();
            session.disconnect();

            System.out.println("Alle Dateien auf dem Desktop wurden gelöscht (Papierkorb bleibt erhalten).");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}