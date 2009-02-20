package org.qi4j.entitystore.rmi;

import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.entity.Queryable;
import org.qi4j.api.property.Property;

/**
 * JAVADOC
 */
@Queryable( false )
public interface RegistryConfiguration
    extends EntityComposite
{
    Property<Integer> port();
}
