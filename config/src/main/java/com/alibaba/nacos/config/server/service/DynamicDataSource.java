/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.nacos.config.server.service;

import static com.alibaba.nacos.core.utils.SystemUtils.STANDALONE_MODE;

import com.alibaba.nacos.config.server.utils.PropertyUtil;
import java.io.IOException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * datasource adapter
 *
 * @author Nacos
 */
@Component
public class DynamicDataSource implements ApplicationContextAware, DataSourceService {

    private DataSourceService selectedDatasource;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        //require PropertyUtil post construct
        applicationContext.getBeansOfType(PropertyUtil.class);
        if (STANDALONE_MODE && !PropertyUtil.isStandaloneUseMysql()) {
            this.selectedDatasource = (DataSourceService)applicationContext.getBean("localDataSourceService");
        } else {
            this.selectedDatasource = (DataSourceService)applicationContext.getBean("basicDataSourceService");
        }
    }

    protected DataSourceService getDataSource() {
        return this.selectedDatasource;
    }

    @Override
    public void reload() throws IOException {
        selectedDatasource.reload();
    }

    @Override
    public boolean checkMasterWritable() {
        return selectedDatasource.checkMasterWritable();
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return selectedDatasource.getJdbcTemplate();
    }

    @Override
    public TransactionTemplate getTransactionTemplate() {
        return selectedDatasource.getTransactionTemplate();
    }

    @Override
    public String getCurrentDBUrl() {
        return selectedDatasource.getCurrentDBUrl();
    }

    @Override
    public String getHealth() {
        return selectedDatasource.getHealth();
    }
}
