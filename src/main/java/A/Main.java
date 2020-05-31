package A;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Time;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.plugin.Plugin;
import org.json.JSONObject;

public class Main extends Plugin {
    //Var
    public static JSONObject currentKicks = new JSONObject();
    public static Thread cycle;
    Boolean enabled = false;

    ///Var
    //on start
    public Main() {
        if (!byteCode.hasDir("mind_db")) {
            byteCode.mkdir("mind_db");
        }
        if (!byteCode.has("gk")) byteCode.make("gk", new JSONObject());
        
        currentKicks = byteCode.get("gk");
        byteCode.assertGK(currentKicks);
        enabled = true;
        cycle a = new cycle(Thread.currentThread());
        a.setDaemon(false);
        a.start();

        Events.on(EventType.PlayerJoin.class, event -> {
            Player player = event.player;
            currentKicks = byteCode.get("gk");
            byteCode.assertGK(currentKicks);
            if (currentKicks.has(player.con.address) && currentKicks.has(player.uuid)) { //normal user
                Call.onKick(player.con, "You've been kicked recently.");
            } else {
                if (currentKicks.has(player.con.address)) { //if using another device
                    byteCode.putInt("gk", player.uuid, currentKicks.getFloat(player.con.address));
                } else if (currentKicks.has(player.uuid)) {//if switching to data / 4G
                    byteCode.putInt("gk", player.con.address, currentKicks.getFloat(player.uuid));
                } else {
                    return;
                }
                Call.onKick(player.con, "You've been kicked recently.");
            }
        });
        Events.on(EventType.PlayerLeave.class, event -> {
            Player player = event.player;
            if (player.getInfo().lastKicked > Time.millis()) {
                currentKicks = byteCode.get("gk");
                byteCode.assertGK(currentKicks);
                currentKicks.put(player.uuid, player.getInfo().lastKicked);
                currentKicks.put(player.con.address, player.getInfo().lastKicked);
                byteCode.save("gk", currentKicks);
            }
        });
        
        
        Events.on(EventType.WaveEvent.class, event -> {
            if (enabled) {
                if (!cycle.isAlive()) {
                    cycle b = new cycle(Thread.currentThread());
                    b.setDaemon(false);
                    b.start();
                }
            }
        });
    }

    public void registerServerCommands(CommandHandler handler) {
        handler.register("gk-clear", "generates the default gk.cn file", arg -> {
            if (byteCode.save("gk", new JSONObject()) != null) Log.info("Successfully created " + System.getProperty("user.home") + "/mind_db/gk.cn");
        });
    }
}