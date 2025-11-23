package org.gz.imserver.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gz.imserver.manager.UserInstanceBindComponent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContextClosedListener implements ApplicationListener<ContextClosedEvent> {
    @Resource
    private UserInstanceBindComponent userInstanceBindComponent;
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // 容器正常关闭时，清理当前实例的所有用户绑定关系
        userInstanceBindComponent.clearAllUserBind(null);
        log.info("【容器关闭】已清理当前实例的用户-实例绑定关系");
    }
}
