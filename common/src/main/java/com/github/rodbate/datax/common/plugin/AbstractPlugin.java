package com.github.rodbate.datax.common.plugin;

import com.github.rodbate.datax.common.base.BaseObject;
import com.github.rodbate.datax.common.util.Configuration;

public abstract class AbstractPlugin extends BaseObject implements Pluginable {
    //作业的config
    private Configuration pluginJobConf;

    //插件本身的plugin
    private Configuration pluginConf;

    // by qiangsi.lq。 修改为对端的作业configuration
    private Configuration peerPluginJobConf;

    private String peerPluginName;

    @Override
    public String getPluginName() {
        assert null != this.pluginConf;
        return this.pluginConf.getString("name");
    }

    @Override
    public String getDeveloper() {
        assert null != this.pluginConf;
        return this.pluginConf.getString("developer");
    }

    @Override
    public String getDescription() {
        assert null != this.pluginConf;
        return this.pluginConf.getString("description");
    }

    @Override
    public Configuration getPluginJobConf() {
        return pluginJobConf;
    }

    @Override
    public void setPluginJobConf(Configuration pluginJobConf) {
        this.pluginJobConf = pluginJobConf;
    }

    @Override
    public void setPluginConf(Configuration pluginConf) {
        this.pluginConf = pluginConf;
    }

    @Override
    public Configuration getPeerPluginJobConf() {
        return peerPluginJobConf;
    }

    @Override
    public void setPeerPluginJobConf(Configuration peerPluginJobConf) {
        this.peerPluginJobConf = peerPluginJobConf;
    }

    @Override
    public String getPeerPluginName() {
        return peerPluginName;
    }

    @Override
    public void setPeerPluginName(String peerPluginName) {
        this.peerPluginName = peerPluginName;
    }

    public void preCheck() {
    }

    public void prepare() {
    }

    public void post() {
    }

    public void preHandler(Configuration jobConfiguration) {

    }

    public void postHandler(Configuration jobConfiguration) {

    }
}
