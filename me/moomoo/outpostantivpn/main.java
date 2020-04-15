package me.moomoo.outpostantivpn;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Thread.sleep;

public class main extends JavaPlugin implements Listener {
    public void onEnable() {
        System.out.println("[ENABLED] moomoo's antivpn plugin, originally made for outpost");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");


    }
    public void onDisable() {
        System.out.println("[DISABLED] moomoo's antivpn plugin, originally made for outpost. Goodnight.");
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent evt){
        evt.setQuitMessage("");
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt){
        evt.setCancelled(true);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) throws IOException, ParseException {
        evt.setJoinMessage("");
        Player craftplayer = evt.getPlayer();
        if(evt.getPlayer().getName() == "moooomoooo"){
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> sendPlayerToServer(craftplayer), 15);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> evt.getPlayer().sendMessage("§7There is something suspicious with your connection..."), 80);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> evt.getPlayer().sendMessage("§7You are required to complete a §fMAPCHA."), 80);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> evt.getPlayer().sendMessage("§7Click the map in your hotbar and type the letters you see into chat."), 80);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> evt.getPlayer().sendMessage("§cMAKE SURE YOU DO NOT HAVE ANY CHAT MODS ENABLED. THINGS LIKE FANCY CHAT AND SUFFIXES MAY PREVENT YOU FROM COMPLETING THE CAPTCHA."), 80);

        String ip = evt.getPlayer().getAddress().toString().replace("/", "").replaceAll(":[0-9][0-9][0-9][0-9][0-9]", "");
        String player = evt.getPlayer().getName();



        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + player + " joined");
        URL url = new URL("https://proxycheck.io/v2/" + ip + "?key=420");
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.toString());
//        URL url = new URL("http://v2.api.iphub.info/ip/" + ip);
//        URLConnection connection = url.openConnection();
//        HttpURLConnection httpConn = (HttpURLConnection) connection;
//        httpConn.setRequestProperty ("X-Key", "420==");
//        BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//        JSONParser parser = new JSONParser();
//        JSONObject json = (JSONObject) parser.parse(response.toString());
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    sleep(800);
                    System.out.println(response.toString());
                    System.out.println(json);
                    JSONObject proxyinfo = (JSONObject) json.get(ip);
                    System.out.println(proxyinfo);
                    System.out.println(proxyinfo.get("proxy").toString());
                    if(proxyinfo.get("proxy").toString().equals("yes")) {
                        System.out.println(player + " was required to complete a captcha because " + ip + " is a vpn.");
                        System.out.println(response.toString());
                    } else {

                        startsendPlayerToServer(craftplayer);
                        System.out.println(response.toString());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
        });
        t.start();

    }

    String successServer = "main";
    private void startsendPlayerToServer(Player player) {

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> sendPlayerToServer(player), 15);
    }
    private void sendPlayerToServer(Player player) {
        if (successServer != null && !successServer.isEmpty()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(successServer);
            player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
            System.out.println("Sent player to bungee server since not a vpn");
        }
    }

}
