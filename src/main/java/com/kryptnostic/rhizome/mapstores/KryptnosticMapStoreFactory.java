package com.kryptnostic.rhizome.mapstores;

/**
 *
 * @author Drew Bailey
 *
 */
public interface KryptnosticMapStoreFactory {

    <K, V> MapStoreBuilder<K, V> build( Class<K> keyType, Class<V> valType );

}