package yusama125718_208292ihcuobust.man10pvphunger;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Man10PVPHunger extends JavaPlugin implements Listener, CommandExecutor, TabCompleter
{
    JavaPlugin mpvph;
    boolean system;
    boolean onadd = false;
    int respawnfood;
    double respawnhealth;
    List<HashMap<String, List<Location>>> arealist = new ArrayList<>();
    List<Location> addlocation = new ArrayList<>();
    List<UUID> exsitplayer = new ArrayList<>();
    List<Integer> exsitHunger = new ArrayList<>();
    HashMap<UUID, Integer> pvpplayer = new HashMap<>();

    @Override
    public void onEnable()
    {
        pvpplayer.clear();
        arealist.clear();
        this.mpvph = this;
        saveDefaultConfig();
        system = mpvph.getConfig().getBoolean("System");
        respawnhealth = mpvph.getConfig().getInt("respawnhealth");
        respawnfood = mpvph.getConfig().getInt("respawnfood");
        if (respawnhealth<1)
        {
            respawnhealth = 1;
        }
        if (respawnhealth>20)
        {
            respawnhealth = 20;
        }
        if (respawnfood<0)
        {
            respawnfood = 0;
        }
        if (respawnfood>20)
        {
            respawnfood = 20;
        }
        try
        {
            for (int i = 0; i < Objects.requireNonNull(mpvph.getConfig().getList("exitplayerlist")).size(); i++)
            {
                exsitplayer.add((UUID) (Objects.requireNonNull(mpvph.getConfig().getList("exitplayerlist"))).get(i));
            }
        }
        catch (NullPointerException e)
        {
            Bukkit.broadcast("§l[§fMan10Spawn§f§l]§途中退出したプレイヤーのロードに失敗しました","mpvph.op");
        }
        exsitHunger.addAll(mpvph.getConfig().getIntegerList("exithungerlist"));
        for (int i = 0;i<exsitplayer.size();i++)
        {
            pvpplayer.put(exsitplayer.get(i),exsitHunger.get(i));
        }
        List<Double> areax = new ArrayList<>(mpvph.getConfig().getDoubleList("areax"));
        List<Double> areay = new ArrayList<>(mpvph.getConfig().getDoubleList("areay"));
        List<Double> areaz = new ArrayList<>(mpvph.getConfig().getDoubleList("areaz"));
        List<String> areaworld = new ArrayList<>(mpvph.getConfig().getStringList("areaworld"));
        List<String> areanamep = new ArrayList<>(mpvph.getConfig().getStringList("areaname"));
        int j = 0;
        for (int i = 0;i<areaworld.size();i++)
        {
            World world = Bukkit.getServer().getWorld(areaworld.get(i));
            Location Location1 = null;
            assert false;
            Location1.set(areax.get(j),areay.get(j),areaz.get(j));
            Location1.setWorld(world);
            j++;
            Location Location2 = null;
            Location2.set(areax.get(j),areay.get(j),areaz.get(j));
            Location2.setWorld(world);
            j++;
            List<Location> Locationlist = new ArrayList<>();
            Locationlist.add(Location1);
            Locationlist.add(Location2);
            HashMap<String, List<Location>> addhash = new HashMap<>();
            addhash.put(areanamep.get(i), Locationlist);
            arealist.set(i,addhash);
        }
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!sender.hasPermission("mpvph.op"))
        {
            sender.sendMessage("§c[Man10PVPHunger]You don't have permissions!");
            return true;
        }
        switch (args.length)
        {
            case 1:
            {
                if (!sender.hasPermission("mpvph.op"))
                {
                    sender.sendMessage("§c[Man10PVPHunger]You don't have permissions!");
                    return true;
                }
                if (args[0].equals("help"))
                {
                    sender.sendMessage("§b[Man10PVPHunger]§7 /mpvph start §e: 自分をPVPモードにします");
                    sender.sendMessage("§b[Man10PVPHunger]§7 /mpvph [on/off] §e: システムをon/offします");
                    sender.sendMessage("§b[Man10PVPHunger]§7 /mpvph add [エリア名] §e: 現在地をPVPエリアの角にセットします。移動してもう一回実行するとエリアを作ります。");
                    sender.sendMessage("§b[Man10PVPHunger]§7 /mpvph delete [エリア名] §e: PVPエリアを消去します");
                }
                if (args[0].equals("on"))
                {
                    if (system)
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§cすでに有効です");
                        return true;
                    }
                    system = true;
                    mpvph.getConfig().set("system",true);
                    saveConfig();
                    sender.sendMessage("§b[Man10PVPHunger]§eONにしました");
                    return true;
                }
                if (args[0].equals("off"))
                {
                    if (!system)
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§cすでに無効です");
                        return true;
                    }
                    system = false;
                    mpvph.getConfig().set("system",false);
                    saveConfig();
                    sender.sendMessage("§b[Man10PVPHunger]§eOFFにしました");
                    return true;
                }
                if (args[0].equals("start"))
                {
                    if ((pvpplayer.containsKey(((Player) sender).getUniqueId()))&&!system)
                    {
                        return true;
                    }
                    pvpplayer.put(((Player) sender).getUniqueId(),((Player) sender).getFoodLevel());
                    return true;
                }
            }
            case 2:
            {
                if (args[0].equals("add"))
                {
                    String listname = null;
                    if (onadd)
                    {
                        if (!(((Player) sender).getLocation().getWorld() == addlocation.get(0).getWorld()))
                        {
                            sender.sendMessage("§b[Man10PVPHunger]§cワールドが変わっています");
                            return true;
                        }
                        if (!listname.equals(args[1]))
                        {
                            sender.sendMessage("§b[Man10PVPHunger]§c名前が変わっています");
                            return true;
                        }
                        Location setlocation1 = addlocation.get(0);
                        Location setlocation2 = addlocation.get(0);
                        if (((Player) sender).getLocation().getX() < addlocation.get(0).getX())
                        {
                            setlocation1.setX(((Player) sender).getLocation().getX());
                        }
                        else
                        {
                            setlocation2.setX(((Player) sender).getLocation().getX());
                        }
                        if (((Player) sender).getLocation().getY() < addlocation.get(0).getY())
                        {
                            setlocation1.setY(((Player) sender).getLocation().getY());
                        }
                        else
                        {
                            setlocation2.setY(((Player) sender).getLocation().getY());
                        }
                        if (((Player) sender).getLocation().getZ() < addlocation.get(0).getZ())
                        {
                            setlocation1.setZ(((Player) sender).getLocation().getZ());
                        }
                        else
                        {
                            setlocation2.setZ(((Player) sender).getLocation().getZ());
                        }
                        setlocation2.setWorld(setlocation1.getWorld());
                        addlocation.set(0, setlocation1);
                        addlocation.set(1, setlocation2);
                        HashMap<String,List<Location>> addlist = new HashMap<>();
                        addlist.put(args[1],addlocation);
                        arealist.add(addlist);
                        onadd = false;
                        List<Double> savex = new ArrayList<>();
                        List<Double> savey = new ArrayList<>();
                        List<Double> savez = new ArrayList<>();
                        List<String> saveworld = new ArrayList<>();
                        List<String> savename = new ArrayList<>();
                        for (int i = 0;i<arealist.size();i++)
                        {
                            for (String key : arealist.get(i).keySet())
                            {
                                for (int j = 0;j<arealist.get(i).get(key).size();j++)
                                {
                                    Location location = null;
                                    assert false;
                                    savex.set(j,arealist.get(i).get(key).get(j).getX());
                                    savey.set(j,arealist.get(i).get(key).get(j).getY());
                                    savez.set(j,arealist.get(i).get(key).get(j).getZ());
                                    saveworld.set(j,arealist.get(i).get(key).get(j).getWorld().getName());
                                    savename.set(j,key);
                                }
                            }
                        }
                        mpvph.getConfig().set("areax",savex);
                        mpvph.getConfig().set("areay",savey);
                        mpvph.getConfig().set("areaz",savez);
                        mpvph.getConfig().set("areaworld",saveworld);
                        mpvph.getConfig().set("areaname",savename);
                        mpvph.saveConfig();
                        sender.sendMessage("§b[Man10PVPHunger]§e追加しました");
                        return true;
                    }
                    else
                    {
                        listname = args[1];
                        addlocation.add(((Player) sender).getLocation());
                        onadd = true;
                        Addtimer addtimer = new Addtimer();
                        addtimer.start();
                        try
                        {
                            addtimer.join();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        onadd = false;
                        addlocation.clear();
                    }
                    return true;
                }
                if (args[0].equals("delete"))
                {
                    String listname = args [1];
                    int i = 0;
                    aaa: for (i=0;i<arealist.size();i++)
                    {
                        if (arealist.get(i).containsKey(listname))
                        {
                            break aaa;
                        }
                    }
                    if (i > arealist.size())
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§cそのエリアは存在しません");
                        return true;
                    }
                    arealist.remove(i);
                    List<Double> savex = new ArrayList<>();
                    List<Double> savey = new ArrayList<>();
                    List<Double> savez = new ArrayList<>();
                    List<String> saveworld = new ArrayList<>();
                    List<String> savename = new ArrayList<>();
                    for (int k = 0;k<arealist.size();k++)
                    {
                        for (String key : arealist.get(k).keySet())
                        {
                            for (int j = 0;j<arealist.get(k).get(key).size();j++)
                            {
                                Location location = null;
                                assert false;
                                savex.set(j,arealist.get(k).get(key).get(j).getX());
                                savey.set(j,arealist.get(k).get(key).get(j).getY());
                                savez.set(j,arealist.get(k).get(key).get(j).getZ());
                                saveworld.set(j,arealist.get(k).get(key).get(j).getWorld().getName());
                                savename.set(j,key);
                            }
                        }
                    }
                    mpvph.getConfig().set("areax",savex);
                    mpvph.getConfig().set("areay",savey);
                    mpvph.getConfig().set("areaz",savez);
                    mpvph.getConfig().set("areaworld",saveworld);
                    mpvph.getConfig().set("areaname",savename);
                    mpvph.saveConfig();
                    sender.sendMessage("§b[Man10PVPHunger]§e削除しました");
                    return true;
                }
            }
            default:
            {
                if (sender.hasPermission("mpvph.op"))
                {
                    sender.sendMessage("§b[Man10PVPHunger]§r/mpvph help でコマンドを確認できます");
                }
                return true;
            }
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event)
    {
        if (system&&pvpplayer.containsKey(event.getPlayer().getUniqueId()))
        {
            int i=0;
            aaa: for (i=0;i<arealist.size();i++)
            {
                for (String key : arealist.get(1).keySet())
                {
                    if ((arealist.get(i).get(key).get(0).getWorld()).equals(event.getPlayer().getLocation().getWorld())&&(arealist.get(i).get(key).get(0).getX() < event.getTo().getX()&&event.getTo().getX() < arealist.get(i).get(key).get(1).getX()&&(arealist.get(i).get(key).get(0).getY()<event.getTo().getY()&&event.getTo().getY() <arealist.get(i).get(key).get(1).getY())&&(arealist.get(i).get(key).get(0).getZ()<event.getTo().getZ()&&event.getTo().getZ() <arealist.get(i).get(key).get(1).getZ())))
                    {
                        break aaa;
                    }
                }
            }
            if (i<arealist.size())
            {
                event.getPlayer().setFoodLevel(pvpplayer.get(event.getPlayer().getUniqueId()));
                pvpplayer.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void PlayerRespawnEvent(PlayerRespawnEvent event)
    {
        Location playerlocation = event.getPlayer().getLocation();
        if (pvpplayer.containsKey(event.getPlayer().getUniqueId())&&system)
        {
            Bukkit.getScheduler().runTaskLater(this, new Runnable()
            {
                @Override
                public void run()
                {
                    event.getPlayer().setHealth(respawnhealth);
                    event.getPlayer().setFoodLevel(respawnfood);
                }
            }, 10);
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event)
    {
        int i=0;
        aaa: for (i=0;i<arealist.size();i++)
        {
            for (String key : arealist.get(1).keySet())
            {
                if ((arealist.get(i).get(key).get(0).getWorld()).equals(event.getPlayer().getLocation().getWorld())&&(arealist.get(i).get(key).get(0).getX() < event.getPlayer().getLocation().getX()&&event.getPlayer().getLocation().getX() < arealist.get(i).get(key).get(0).getX()&&(arealist.get(i).get(key).get(0).getY()<event.getPlayer().getLocation().getY()&&event.getPlayer().getLocation().getY() <arealist.get(i).get(key).get(0).getY())&&(arealist.get(i).get(key).get(0).getZ()<event.getPlayer().getLocation().getZ()&&event.getPlayer().getLocation().getZ() <arealist.get(i).get(key).get(0).getZ())))
                {
                    break aaa;
                }
            }
        }
        if (i<arealist.size())
        {
            exsitplayer.add(event.getPlayer().getUniqueId());
            exsitHunger.add(pvpplayer.get(event.getPlayer().getUniqueId()));
            pvpplayer.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event)
    {
        if (exsitplayer.contains(event.getPlayer().getUniqueId())&&system)
        {
            int i = 0;
            aaa: for (i=0;i<exsitplayer.size();i++)
            {
                if (exsitplayer.get(i).equals(event.getPlayer().getUniqueId()))
                {
                    break aaa;
                }
            }
            if (i<exsitplayer.size())
            {
                event.getPlayer().setFoodLevel(exsitHunger.get(i));
                exsitplayer.remove(i);
                exsitHunger.remove(i);
            }
        }
    }

    @Override
    public void onDisable()
    {
        int i=0;
        for (UUID key : pvpplayer.keySet())
        {
            exsitplayer.set(i,key);
            exsitHunger.set(i,pvpplayer.get(key));
            i++;
        }
        mpvph.getConfig().set("exitplayerlist",exsitplayer);
        mpvph.getConfig().set("exithungerlist",exsitHunger);
        mpvph.saveConfig();
    }
}
