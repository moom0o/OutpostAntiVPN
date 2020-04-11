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

public class main extends JavaPlugin implements Listener {
    public void onEnable() {
        System.out.println("[ENABLED] moomoo's antivpn plugin, originally made for outpost https://discord.gg/htQYBj2");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");


    }
    public void onDisable() {
        System.out.println("[DISABLED] moomoo's antivpn plugin, originally made for outpost. https://discord.gg/htQYBj2 Goodnight.");
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
        String ip = evt.getPlayer().getAddress().toString().replace("/", "").replaceAll(":[0-9][0-9][0-9][0-9][0-9]", "");
        String player = evt.getPlayer().getName();
        Player craftplayer = evt.getPlayer();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + player + " joined");
        URL url = new URL("http://v2.api.iphub.info/ip/" + ip);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        httpConn.setRequestProperty ("X-Key", "x");
        BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.toString());
        if(json.get("block").toString().equals("1")) {
            evt.getPlayer().sendMessage("§7There is something suspicious with your connection...");
            evt.getPlayer().sendMessage("§7You are required to complete a §fMAPCHA.");
            evt.getPlayer().sendMessage("§7Click the map in your hotbar and type the letters you see into chat.");
            evt.getPlayer().sendMessage("§cMAKE SURE YOU DO NOT HAVE ANY CHAT MODS ENABLED. THINGS LIKE FANCY CHAT AND SUFFIXES MAY PREVENT YOU FROM COMPLETING THE CAPTCHA.");
            System.out.println(player + " was required to complete a captcha because " + ip + " is a vpn.");
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> sendPlayerToServer(craftplayer), 15);
        }
    }

    String successServer = "outpost";
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
