package org.qi4j.entitystore.s3;

import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.entity.Queryable;
import org.qi4j.api.property.Property;
import org.qi4j.api.configuration.ConfigurationComposite;

/**
 * Configuration for the Amazon S3 EntityStore
 */
public interface S3Configuration
    extends ConfigurationComposite
{
    Property<String> accessKey();

    Property<String> secretKey();
}
