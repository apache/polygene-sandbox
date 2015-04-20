/*
 * Copyright 2009 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.entitystore.jndi;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;
import org.qi4j.api.common.Visibility;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;
import org.qi4j.test.AbstractQi4jTest;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.qi4j.api.value.ValueSerialization;
import org.qi4j.test.EntityTestAssembler;
import org.qi4j.valueserialization.orgjson.OrgJsonValueSerializationService;

public class JndiReadEntityStoreTest extends AbstractQi4jTest
{
    public void assemble( ModuleAssembly module ) throws AssemblyException
    {
        module.services( JndiEntityStoreService.class, UuidIdentityGeneratorService.class );
        module.services( OrgJsonValueSerializationService.class ).taggedWith( ValueSerialization.Formats.JSON );
        ModuleAssembly config = module.layer().module( "config" );
        config.entities( JndiConfiguration.class ).visibleIn( Visibility.layer );
        new EntityTestAssembler().assemble( config );

        module.entities( UserEntity.class, GroupEntity.class );
    }

    @Test
    @Ignore( "Requires connection to LDAP server on OPS4J that is now unavailable" )
    public void findSaslSupportTypes()
        throws Exception
    {
        // Create initial context
        DirContext ctx = new InitialDirContext();

        // Read supportedSASLMechanisms from root DSE
        Attributes attrs = ctx.getAttributes(
            "ldap://srv07.ops4j.org:389", new String[]{ "supportedSASLMechanisms" } );

        System.out.println( attrs );
    }

    @Test
    @Ignore
    public void testReadNiclasFromLdap()
        throws Exception
    {
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            User user = uow.get( User.class, "niclas.hedhman" );
            System.out.println( user.givenName().get() + " " + user.sn().get() );
            Assert.assertEquals( "Niclas", user.givenName().get() );
        }
        finally
        {
            uow.discard();
        }
    }
}
