package cn.how2j.trend.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/*用于获取当前微服务实例的端口号
 * 因为这个微服务会做成集群，不同的微服务使用的是不同的端口号
 */
//由应用程序事件监听器实现-ApplicationListener

//在刷新应用程序上下文并准备好Web服务器后发布的事件，用于获取运行服务器的本地端口。--WebServerInitializedEvent
@Component
public class IpConfiguration implements ApplicationListener<WebServerInitializedEvent> {
    private int serverPort;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
    }

    public int getPort() {
        return this.serverPort;
    }
}
