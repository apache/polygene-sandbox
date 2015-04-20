/*  Copyright 2008 Rickard �berg.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.entitystore.s3;

import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.security.AWSCredentials;
import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.entity.EntityReference;
import org.qi4j.api.injection.scope.This;
import org.qi4j.io.Input;
import org.qi4j.io.Output;
import org.qi4j.spi.entitystore.EntityStoreException;

import java.io.IOException;
import java.io.Reader;
import org.qi4j.api.service.ServiceActivation;
import org.qi4j.spi.entitystore.helpers.MapEntityStore;

/**
 * Amazon S3 implementation of SerializationStore.
 * <p/>
 * To use this you must supply your own access key and secret key for your Amazon S3 account.
 */
public class S3SerializationStoreMixin
    implements ServiceActivation, MapEntityStore
{

    @This
    private Configuration<S3Configuration> configuration;

    private S3Service s3Service;
    private S3Bucket entityBucket;

    // Activatable implementation
    public void activateService()
        throws Exception
    {
        String awsAccessKey = configuration.get().accessKey().get();
        String awsSecretKey = configuration.get().secretKey().get();

        if( awsAccessKey == null || awsSecretKey == null )
        {
            throw new IllegalStateException( "No S3 keys configured" );
        }

        AWSCredentials awsCredentials =
            new AWSCredentials( awsAccessKey, awsSecretKey );
        s3Service = new RestS3Service( awsCredentials );

        S3Bucket[] s3Buckets = s3Service.listAllBuckets();
        System.out.println( "How many buckets do I have in S3? " + s3Buckets.length );

        if( s3Buckets.length == 0 )
        {
            entityBucket = s3Service.createBucket( "entity-bucket" );
            System.out.println( "Created entity bucket: " + entityBucket.getName() );
        }
        else
        {
            entityBucket = s3Buckets[ 0 ];
        }
    }

    public void passivateService()
        throws Exception
    {
    }

    public Reader get( EntityReference entityReference )
        throws EntityStoreException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Input<Reader, IOException> entityStates()
    {
        return new Input<Reader, IOException>()
        {
            public <ReceiverThrowableType extends Throwable> void transferTo( Output<? super Reader, ReceiverThrowableType> output )
                throws IOException, ReceiverThrowableType
            {
                // TODO Implement this
                throw new UnsupportedOperationException( "Not supported yet." );
            }
        };
    }

    public void applyChanges( MapChanges changes )
        throws IOException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}