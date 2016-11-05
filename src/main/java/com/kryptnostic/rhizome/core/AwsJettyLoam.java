package com.kryptnostic.rhizome.core;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.base.Preconditions;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.kryptnostic.rhizome.configuration.amazon.AmazonLaunchConfiguration;
import com.kryptnostic.rhizome.configuration.jetty.JettyConfiguration;
import com.kryptnostic.rhizome.keystores.Keystores;

public class AwsJettyLoam extends JettyLoam {
    private static final Logger             logger = LoggerFactory.getLogger( AwsJettyLoam.class );
    private final AmazonLaunchConfiguration awsConfig;
    private final AmazonS3                  s3     = new AmazonS3Client();

    public AwsJettyLoam( JettyConfiguration config, AmazonLaunchConfiguration awsConfig ) throws IOException {
        super( config );
        this.awsConfig = Preconditions.checkNotNull( awsConfig, "AwsConfig cannot be null." );
    }

    @Override
    protected void configureSslStores( SslContextFactory contextFactory ) throws IOException {
        String truststoreKey = awsConfig.getFolder() + config.getTruststoreConfiguration().get().getStorePath();
        String keystoreKey = awsConfig.getFolder() + config.getKeystoreConfiguration().get().getStorePath();

        logger.info( "Trust store key: {}", truststoreKey );
        logger.info( "Keystore key: {}", keystoreKey );
        String truststorePassword = config.getTruststoreConfiguration().get().getStorePassword();
        String keystorePassword = config.getKeystoreConfiguration().get().getStorePassword();
        S3Object truststoreObj = s3.getObject( awsConfig.getBucket(), truststoreKey );
        S3Object keystoreObj = s3.getObject( awsConfig.getBucket(), keystoreKey );
        InputStream ksStream = keystoreObj.getObjectContent();
        InputStream tsStream = truststoreObj.getObjectContent();

        try {
            contextFactory.setKeyStore( Keystores.loadKeystoreFromStream( ksStream, keystorePassword.toCharArray() ) );
            contextFactory
                    .setTrustStore( Keystores.loadKeystoreFromStream( tsStream, truststorePassword.toCharArray() ) );
        } catch ( NoSuchAlgorithmException | CertificateException | KeyStoreException e ) {
            throw new IOException( "Unable to load keystores from S3.", e );
        }

        contextFactory.setTrustStorePassword( truststorePassword );
        contextFactory.setKeyStorePassword( keystorePassword );
    }
}