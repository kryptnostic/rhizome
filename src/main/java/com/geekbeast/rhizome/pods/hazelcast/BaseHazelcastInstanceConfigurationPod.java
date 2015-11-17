package com.geekbeast.rhizome.pods.hazelcast;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.geekbeast.rhizome.configuration.RhizomeConfiguration;
import com.geekbeast.rhizome.configuration.hazelcast.HazelcastConfiguration;
import com.geekbeast.rhizome.configuration.hazelcast.HazelcastConfigurationContainer;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.TcpIpConfig;

@Configuration
public class BaseHazelcastInstanceConfigurationPod {

    @Inject
    protected RhizomeConfiguration configuration;

    @Bean
    public HazelcastConfigurationContainer getHazelcastConfiguration() {
        return new HazelcastConfigurationContainer( getHazelcastServerConfiguration(), getHazelcastClientConfiguration() );
    }

    public Config getHazelcastServerConfiguration() {
        Optional<HazelcastConfiguration> maybeConfiguration = configuration.getHazelcastConfiguration();
        Preconditions.checkArgument(
                maybeConfiguration.isPresent(),
                "Hazelcast Configuration must be present to build hazelcast instance configuration." );
        HazelcastConfiguration hzConfiguration = maybeConfiguration.get();
        Config config = new Config( hzConfiguration.getInstanceName() )
                .setGroupConfig( new GroupConfig( hzConfiguration.getGroup(), hzConfiguration.getPassword() ) )
                .setSerializationConfig( new SerializationConfig().setSerializerConfigs( getSerializerConfigs() ) )
                .setMapConfigs( getMapConfigs() )
                .setNetworkConfig( getNetworkConfig( hzConfiguration ) );
        return config;
    }

    public ClientConfig getHazelcastClientConfiguration() {
        Optional<HazelcastConfiguration> maybeConfiguration = configuration.getHazelcastConfiguration();
        Preconditions.checkArgument(
                maybeConfiguration.isPresent(),
                "Hazelcast Configuration must be present to build hazelcast instance configuration." );
        HazelcastConfiguration hzConfiguration = maybeConfiguration.get();
        if ( hzConfiguration.isServer() ) {
            return null;
        }
        ClientConfig clientConfig = new ClientConfig()
            .setGroupConfig( new GroupConfig( hzConfiguration.getGroup(), hzConfiguration.getPassword() ) )
                .setSerializationConfig( new SerializationConfig().setSerializerConfigs( getSerializerConfigs() ) )
                .setNetworkConfig( getClientNetworkConfig( hzConfiguration ) );
        return clientConfig;
    }

    private static ClientNetworkConfig getClientNetworkConfig( HazelcastConfiguration hzConfiguration ) {
        return new ClientNetworkConfig().setAddresses( hzConfiguration.getHazelcastSeedNodes() );
    }

    protected static NetworkConfig getNetworkConfig( HazelcastConfiguration hzConfiguration ) {
        return new NetworkConfig().setPort( hzConfiguration.getPort() ).setJoin(
                getJoinConfig( hzConfiguration.getHazelcastSeedNodes() ) );
    }

    protected static JoinConfig getJoinConfig( List<String> nodes ) {
        return new JoinConfig().setMulticastConfig( new MulticastConfig().setEnabled( false ).setLoopbackModeEnabled(
                false ) ).setTcpIpConfig( getTcpIpConfig( nodes ) );
    }

    protected static TcpIpConfig getTcpIpConfig( List<String> nodes ) {
        return new TcpIpConfig().setMembers( nodes ).setEnabled( true );
    }

    protected static Map<String, MapConfig> getMapConfigs() {
        return ImmutableMap.of();
    }

    protected static Collection<SerializerConfig> getSerializerConfigs() {
        return ImmutableList.of();
    }

}
