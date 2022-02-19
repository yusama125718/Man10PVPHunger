package yusama125718_208292ihcuobust.man10pvphunger;

import com.sun.org.apache.xerces.internal.xs.StringList;
import jdk.javadoc.internal.tool.Start;
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
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import sun.util.resources.ext.CalendarData_da;

import java.util.*;

public final class Man10PVPHunger extends JavaPlugin implements Listener, CommandExecutor, TabCompleter
{
    JavaPlugin mpvph;
    boolean system;
    int respawnfood;
    double respawnhealth;
    HashMap<UUID, Integer> pvpplayer = new HashMap<>();
    List<String> targetworld = new ArrayList<>();

    @Override
    public void onEnable()
    {
        this.mpvph = this;
        saveDefaultConfig();
        system = mpvph.getConfig().getBoolean("system");
        respawnhealth = mpvph.getConfig().getInt("respawnhealth");
        respawnfood = mpvph.getConfig().getInt("respawnfood");
        if (respawnhealth<1)
        {
            respawnhealth = 1;
        }
        else if (respawnhealth>20)
        {
            respawnhealth = 20;
        }
        if (respawnfood<0)
        {
            respawnfood = 0;
        }
        else if (respawnfood>20)
        {
            respawnfood = 20;
        }
        targetworld.addAll(mpvph.getConfig().getStringList("targetworld"));
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
                if (args[0].equals("help"))
                {
                    sender.sendMessage("§b[Man10PVPHunger]§7 /mpvph [on/off] §e: システムをon/offします");
                    sender.sendMessage("§b[Man10PVPHunger]§7 /mpvph sethealth [体力] §e: PVP中のリスポーン時の体力を設定します");
                    sender.sendMessage("§b[Man10PVPHunger]§7 /mpvph setfood [満腹度] §e: PVP中のリスポーン時の満腹度を設定します");
                    sender.sendMessage("§b[Man10PVPHunger]§7 /mpvph add [ワールド名] §e: 指定したワールドをPVPワールドにします");
                    sender.sendMessage("§b[Man10PVPHunger]§7 /mpvph delete [ワールド名] §e: PVPエリアを消去します");
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
                break;
            }
            case 2:
            {
                if (args[0].equals("add"))
                {
                    String addworld = args[1];
                    List<String> worlds = new ArrayList<>();
                    for (int i = 0; i<Bukkit.getWorlds().size(); i++)
                    {
                        worlds.add(Bukkit.getWorlds().get(i).getName());
                    }
                    if (!worlds.contains(addworld))
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§cそのワールドは存在しません");
                        return true;
                    }
                    if (targetworld.contains(addworld))
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§cそのワールドはすでに対象です");
                        return true;
                    }
                    targetworld.add(addworld);
                    mpvph.getConfig().set("targetworld",targetworld);
                    saveConfig();
                    sender.sendMessage("§b[Man10PVPHunger]§eワールドを対象にしました");
                    return  true;
                }
                if (args[0].equals("delete"))
                {
                    String deleteworld = args[1];
                    List<String> worlds = new ArrayList<>();
                    for (int i = 0; i<Bukkit.getWorlds().size(); i++)
                    {
                        worlds.add(Bukkit.getWorlds().get(i).getName());
                    }
                    if (!worlds.contains(deleteworld))
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§cそのワールドは存在しません");
                        return true;
                    }
                    if (!targetworld.contains(deleteworld))
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§cそのワールドは追加されていません");
                        return true;
                    }
                    targetworld.remove(deleteworld);
                    mpvph.getConfig().set("targetworld",targetworld);
                    saveConfig();
                    sender.sendMessage("§b[Man10PVPHunger]§eワールドを対象から削除しました");
                    return true;
                }
                if (args[0].equals("sethealth"))
                {
                    boolean isNumeric = args[1].matches("-?\\d+");
                    if (!isNumeric)
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§c体力は整数にしてください");
                        return true;
                    }
                    int sethealth = Integer.parseInt(args[1]);
                    if (sethealth>20||sethealth<1)
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§c体力は1以上20以下の数字にしてください");
                        return true;
                    }
                    respawnhealth = sethealth;
                    mpvph.getConfig().set("respawnhealth",sethealth);
                    saveConfig();
                    sender.sendMessage("§b[Man10PVPHunger]§e設定しました");
                    return true;
                }
                if (args[0].equals("setfood"))
                {
                    boolean isNumeric = args[1].matches("-?\\d+");
                    if (!isNumeric)
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§c満腹度は整数にしてください");
                        return true;
                    }
                    int setfood = Integer.parseInt(args[1]);
                    if (setfood>20||setfood<0)
                    {
                        sender.sendMessage("§b[Man10PVPHunger]§c満腹度は0以上20以下の数字にしてください");
                        return true;
                    }
                    respawnfood = setfood;
                    mpvph.getConfig().set("respawnfood",setfood);
                    saveConfig();
                    sender.sendMessage("§b[Man10PVPHunger]§e設定しました");
                    return true;
                }
                break;
            }
            default:
            {
                sender.sendMessage("§b[Man10PVPHunger]§r/mpvph help でコマンドを確認できます");
                return true;
            }
        }
        return true;
    }

    @EventHandler
    public void PlayerRespawnEvent(PlayerRespawnEvent event)
    {
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
            }, 3);
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event)
    {
        if (!system)
        {
            return;
        }
        if (pvpplayer.containsKey(event.getPlayer().getUniqueId()))
        {
            event.getPlayer().setFoodLevel(pvpplayer.get(event.getPlayer().getUniqueId()));
            pvpplayer.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event)
    {
        if (!system)
        {
            return;
        }
        Player targetPlayer = event.getPlayer();
        if (targetworld.contains(targetPlayer.getLocation().getWorld().getName()))
        {
            if (!pvpplayer.containsKey(targetPlayer.getUniqueId()))
            {
                pvpplayer.put(targetPlayer.getUniqueId(),targetPlayer.getFoodLevel());
                targetPlayer.setHealth(respawnhealth);
                targetPlayer.setFoodLevel(respawnfood);
                targetPlayer.sendMessage("§b[Man10PVPHunger]§ePVPモードを有効化します");
            }
        }
        else if (pvpplayer.containsKey(targetPlayer.getUniqueId()))
        {
            targetPlayer.setFoodLevel(pvpplayer.get(targetPlayer.getUniqueId()));
            pvpplayer.remove(targetPlayer.getUniqueId());
            targetPlayer.sendMessage("§b[Man10PVPHunger]§ePVPモードを無効化します");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!sender.hasPermission("mpvph.op"))
        {
            return null;
        }
        if(command.getName().equalsIgnoreCase("mpvph"))
        {
            if (args.length == 1)
            {
                if (args[0].length() == 0)
                {
                    return Arrays.asList("add","delete","off","on","setfood","sethealth");
                }
                else if ("on".startsWith(args[0]) && "off".startsWith(args[0]))
                {
                    return Arrays.asList("on","off");
                }
                else if ("on".startsWith(args[0]))
                {
                    return Collections.singletonList("on");
                }
                else if ("off".startsWith(args[0]))
                {
                    return Collections.singletonList("off");
                }
                else if ("add".startsWith(args[0]))
                {
                    return Collections.singletonList("add");
                }
                else if ("delete".startsWith(args[0]))
                {
                    return Collections.singletonList("delete");
                }
                else if ("sethealth".startsWith(args[0])&&"setfood".startsWith(args[0]))
                {
                    return Arrays.asList("sethealth","setfood");
                }
                else if ("sethealth".startsWith(args[0]))
                {
                    return Collections.singletonList("sethealth");
                }
                else if ("setfood".startsWith(args[0]))
                {
                    return Collections.singletonList("setfood");
                }
            }
            else if (args.length==2)
            {
                ArrayList<String> list = new ArrayList<>();
                for (World world : Bukkit.getWorlds())
                {
                    list.add(world.getName());
                }
                switch (args[0])
                {
                    case "add":
                    case "delete":
                        return list;
                    case "setfood":
                        return Collections.singletonList("<満腹度>");
                    case "sethealth":
                        return Collections.singletonList("<体力>");
                }
            }
        }
        return null;
    }

    @Override
    public void onDisable(){}
}
