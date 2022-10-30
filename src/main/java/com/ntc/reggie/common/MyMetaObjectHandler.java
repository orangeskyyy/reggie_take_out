package com.ntc.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("处理插入时的自动填充:");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        // metaObject.setValue("createUser",new Long(1));
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        // metaObject.setValue("updateUser",new Long(1));
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        // BaseContext.removeCurrentId();
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("处理更新时的自动填充");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        // BaseContext.removeCurrentId();
        // long id = Thread.currentThread().getId();
        // log.info("自动填充线程id:{}",id);
    }
}
