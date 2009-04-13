/*  Copyright 2008 Jan Kronquist.
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
package org.qi4j.entitystore.javaspaces;

import net.jini.security.policy.DynamicPolicyProvider;
import org.junit.After;
import org.junit.Test;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.library.http.JettyServiceAssembler;
import org.qi4j.library.jini.javaspaces.JiniJavaSpacesServiceAssembler;
import org.qi4j.library.jini.lookup.JiniLookupServiceAssembler;
import org.qi4j.library.jini.transaction.JiniTransactionServiceAssembler;
import org.qi4j.library.spaces.javaspaces.JavaSpacesClientAssembler;
import org.qi4j.spi.entity.helpers.UuidIdentityGeneratorService;
import org.qi4j.test.entity.AbstractEntityStoreTest;

import java.io.File;
import java.security.*;

/**
 * JavaSpaces EntityStore test
 */
public class JavaSpacesEntityStoreTest extends AbstractEntityStoreTest
{
    static
    {
        Policy basePolicy = new AllPolicy();
        DynamicPolicyProvider policyProvider = new DynamicPolicyProvider( basePolicy );
        Policy.setPolicy( policyProvider );

    }

    @SuppressWarnings( "unchecked" )
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        super.assemble( module );
        module.addServices( MemoryEntityStoreService.class, UuidIdentityGeneratorService.class );
        module.addAssembler( new JettyServiceAssembler() );
        module.addAssembler( new JavaSpacesClientAssembler() );
        module.addAssembler( new JiniJavaSpacesServiceAssembler() );
        module.addAssembler( new JiniLookupServiceAssembler() );
        module.addAssembler( new JiniTransactionServiceAssembler() );
    }

    @Override @After public void tearDown()
        throws Exception
    {
        super.tearDown();
        delete( new File( "qi4jtemp" ) );
    }

    @Test
    @Override public void givenConcurrentUnitOfWorksWhenUoWCompletesThenCheckConcurrentModification() throws UnitOfWorkCompletionException
    {
        super.givenConcurrentUnitOfWorksWhenUoWCompletesThenCheckConcurrentModification();
    }

    private void delete( File dir )
    {
        for( File file : dir.listFiles() )
        {
            if( file.isDirectory() )
            {
                delete( file );
            }
            file.delete();
        }
        dir.delete();
    }

    @Test
    public void enableTests()
    {
    }

    public static class AllPolicy extends Policy
    {

        public AllPolicy()
        {
        }

        public PermissionCollection getPermissions( CodeSource codeSource )
        {
            Permissions allPermission;
            allPermission = new Permissions();
            allPermission.add( new AllPermission() );
            return allPermission;
        }

        public void refresh()
        {
        }
    }

}