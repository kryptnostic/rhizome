package com.kryptnostic.rhizome.mapstores.cassandra;

import java.util.Set;

import com.datastax.driver.core.Session;
import com.geekbeast.rhizome.configuration.cassandra.CassandraConfiguration;
import com.geekbeast.rhizome.pods.RegistryBasedMappersPod;
import com.google.common.base.Preconditions;
import com.kryptnostic.rhizome.mappers.SelfRegisteringKeyMapper;
import com.kryptnostic.rhizome.mappers.SelfRegisteringValueMapper;
import com.kryptnostic.rhizome.mapstores.AbstractMapStoreBuilder;
import com.kryptnostic.rhizome.mapstores.KryptnosticMapStoreFactory;
import com.kryptnostic.rhizome.mapstores.MapStoreBuilder;
import com.kryptnostic.rhizome.mapstores.TestableSelfRegisteringMapStore;

public class CassandraMapStoreFactory implements KryptnosticMapStoreFactory {

    final Session                session;
    final CassandraConfiguration config;
    final RegistryBasedMappersPod mappers;

    CassandraMapStoreFactory( Builder builder ) {
        super();
        this.session = builder.getSession();
        this.config = builder.getConfig();
        this.mappers = builder.getMappers();
    }

    @Override
    public <K, V> MapStoreBuilder<K, V> build( Class<K> keyType, Class<V> valType ) {
        SelfRegisteringKeyMapper<K> keyMapper = (SelfRegisteringKeyMapper<K>) mappers.getKeyMapper( keyType );
        SelfRegisteringValueMapper<V> valueMapper = (SelfRegisteringValueMapper<V>) mappers.getValueMapper( valType );
        Preconditions.checkNotNull( keyMapper, "No keymapper found for type %s ", keyType );
        Preconditions.checkNotNull( valueMapper, "No valuemapper found for type %s ", valType );
        return new CassandraMapStoreBuilder<>( keyMapper, valueMapper );
    }

    @Override
    public <K, C extends Set<V>, V> MapStoreBuilder<K, C> buildSetProxy( Class<K> keyType, Class<V> valType ) {
        SelfRegisteringKeyMapper<K> keyMapper = (SelfRegisteringKeyMapper<K>) mappers.getKeyMapper( keyType );
        SelfRegisteringValueMapper<V> valueMapper = (SelfRegisteringValueMapper<V>) mappers.getValueMapper( valType );
        Preconditions.checkNotNull( keyMapper, "No keymapper found for type %s ", keyType );
        Preconditions.checkNotNull( valueMapper, "No valuemapper found for type %s ", valType );
        return new ProxiedCassandraMapStoreBuilder<>( keyMapper, valueMapper, valType );
    }

    public class ProxiedCassandraMapStoreBuilder<K, C extends Set<V>, V> extends CassandraMapStoreBuilder<K, C> {

        private final SelfRegisteringValueMapper<V> innerValueMapper;
        private final Class<V>       valueType;

        public ProxiedCassandraMapStoreBuilder(
                SelfRegisteringKeyMapper<K> keyMapper,
                SelfRegisteringValueMapper<V> valueMapper,
                Class<V> valueType ) {
            super( keyMapper, new SetProxyAwareValueMapper<C, V>( valueMapper ) );
            this.innerValueMapper = valueMapper;
            this.valueType = valueType;
        }

        @Override
        public TestableSelfRegisteringMapStore<K, C> build() {
            return new SetProxyBackedCassandraMapStore<K, C, V>(
                    tableName,
                    mapName,
                    keyMapper,
                    innerValueMapper,
                    config,
                    session,
                    valueType );
        }
    }

    public class CassandraMapStoreBuilder<K, C> extends AbstractMapStoreBuilder<K, C> {

        public CassandraMapStoreBuilder(
                SelfRegisteringKeyMapper<K> keyMapper,
                SelfRegisteringValueMapper<C> valueMapper ) {
            super( keyMapper, valueMapper );
        }

        @Override
        public TestableSelfRegisteringMapStore<K, C> build() {
            return new DefaultCassandraMapStore<K, C>(
                    tableName,
                    mapName,
                    keyMapper,
                    valueMapper,
                    config,
                    session) {
                @Override
                public K generateTestKey() {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException( "THIS METHOD HAS NOT BEEN IMPLEMENTED, BLAME drew" );
                }

                @Override
                public C generateTestValue() throws Exception {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException( "THIS METHOD HAS NOT BEEN IMPLEMENTED, BLAME drew" );
                }
            };
        }

    }

    public static class Builder {

        private CassandraConfiguration config;
        private Session                session;
        private RegistryBasedMappersPod mappers;

        public Builder() {}


        public Builder withConfiguration( CassandraConfiguration config ) {
            this.config = config;
            return this;
        }

        public Builder withSession( Session cluster ) {
            this.session = cluster;
            return this;
        }

        public Builder withMappers( RegistryBasedMappersPod mappers) {
            this.mappers = mappers;
            return this;
        }


        public CassandraMapStoreFactory build() {
            return new CassandraMapStoreFactory( this );
        }

        public RegistryBasedMappersPod getMappers() {
            return mappers;
        }

        /**
         * @return the config
         */
        public CassandraConfiguration getConfig() {
            return config;
        }

        /**
         * @return the session
         */
        public Session getSession() {
            return session;
        }
    }

}
