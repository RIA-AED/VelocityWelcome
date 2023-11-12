package ink.magma.velocitywelcome;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

@Plugin(
        id = "velocity-welcome",
        name = "VelocityWelcome",
        version = BuildConstants.VERSION,
        authors = {"MagmaBlock"},
        description = "Velocity Zth Proxy 的登入消息和 Title 展现插件."
)

public class VelocityWelcome {

    private ProxyServer server;
    private Logger logger;
    private final ArrayList<DisconnectEvent.LoginStatus> acceptedStatus = new ArrayList<DisconnectEvent.LoginStatus>();

    @Inject
    public VelocityWelcome(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        acceptedStatus.add(DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN);
        acceptedStatus.add(DisconnectEvent.LoginStatus.PRE_SERVER_JOIN);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }

    @Subscribe
    public void onPlayerConnected(ServerPostConnectEvent event) {
        if (event.getPreviousServer() == null && event.getPlayer() != null) {
            MiniMessage miniMessage = MiniMessage.miniMessage();
            Player player = event.getPlayer();

            // 登入消息
            if (!player.hasPermission("zth.silentjoin")) {
                Component joinMessage = miniMessage.deserialize(
                        MessageFormat.format("<color:#27ae60>[+]</color> {0}", player.getUsername())
                );
                server.sendMessage(joinMessage);
            }


            // Title
            Component mainTitle = miniMessage.deserialize("<gray>Hi,</gray> ")
                    .append(Component.text(player.getUsername()));
            Component subTitle = miniMessage.deserialize("<gradient:gray:white>欢迎回到</gradient> <gradient:#e42d3e:#e46040>RIA</gradient> <gradient:#94e6eb:white>Zeroth, Fifth</gradient>");

            Title title = Title.title(mainTitle, subTitle);

            player.showTitle(title);
        }
    }

    @Subscribe
    public void onPlayerDisconnectProxy(DisconnectEvent event) {
        Player player = event.getPlayer();
        MiniMessage miniMessage = MiniMessage.miniMessage();

        if (player == null) return;
        if (!acceptedStatus.contains(event.getLoginStatus())) return;
        if (player.hasPermission("zth.silentjoin")) return;

        // 登出消息
        Component leaveMessage = miniMessage.deserialize(
                MessageFormat.format("<color:#a4b0be>[-] <gradient:#a4b0be:#717c7d>{0}</gradient>", player.getUsername())
        );
        server.sendMessage(leaveMessage);
    }
}
