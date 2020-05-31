package A;

import arc.util.Log;
import arc.util.Time;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class cycle extends Thread {
    private Thread MainT;
    private JSONObject currentKicks;

    public cycle(Thread main) {
        MainT = main;
    }

    public void run() {
        Log.info("cycle started - Waiting 60 Seconds");
        Main.cycle = Thread.currentThread();
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (Exception ignored) {
        }
        Log.info("cycle running");
        //Var
        int seconds = 0;
        int nex15Seconds = 0;
        int nexMinute = 0;
        //
        while (MainT.isAlive()) {
            try {
                TimeUnit.SECONDS.sleep(1);
                seconds++;
            } catch (Exception ignored) {
            }
            //run

            //
            if (seconds >= nex15Seconds) {
                nex15Seconds = seconds + 15;
                //run
            }
            if (seconds >= nexMinute) {
                nexMinute = seconds + 60;
                //run
                currentKicks = byteCode.get("gk");
                byteCode.assertGK(currentKicks);
                if (!currentKicks.isEmpty()) {
                    HashMap<String, String> remove = new HashMap<>();
                    for (String uuidp : currentKicks.keySet()) {
                        long lastKicked = currentKicks.getLong(uuidp);
                        if (lastKicked < Time.millis()) {
                            remove.put(uuidp, "");
                        }
                    }
                    if (!remove.isEmpty()) {
                        remove.forEach((k,v) -> {
                            currentKicks.remove(k);
                        });
                        byteCode.save("gk", currentKicks);
                    }
                }
            }
        }
    }
}
